package com.digitaldan.harmony;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitaldan.harmony.config.Action;
import com.digitaldan.harmony.config.Activity;
import com.digitaldan.harmony.config.Activity.Status;
import com.digitaldan.harmony.config.ControlGroup;
import com.digitaldan.harmony.config.Device;
import com.digitaldan.harmony.config.Discovery;
import com.digitaldan.harmony.config.Function;
import com.digitaldan.harmony.config.HarmonyConfig;
import com.digitaldan.harmony.config.Ping;
import com.digitaldan.harmony.messages.ActivityFinishedMessage;
import com.digitaldan.harmony.messages.ConfigMessage;
import com.digitaldan.harmony.messages.DigestMessage;
import com.digitaldan.harmony.messages.DiscoveryMessage.DiscoveryRequestMessage;
import com.digitaldan.harmony.messages.DiscoveryMessage.DiscoveryResponseMessage;
import com.digitaldan.harmony.messages.ErrorResponseMessage;
import com.digitaldan.harmony.messages.GetCurrentActivityMessage;
import com.digitaldan.harmony.messages.HoldActionMessage;
import com.digitaldan.harmony.messages.HoldActionMessage.HoldStatus;
import com.digitaldan.harmony.messages.Message;
import com.digitaldan.harmony.messages.MessageDeserializer;
import com.digitaldan.harmony.messages.PingMessage;
import com.digitaldan.harmony.messages.RequestMessage;
import com.digitaldan.harmony.messages.ResponseMessage;
import com.digitaldan.harmony.messages.StartActivityMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HarmonyClient {
    private static final int ONE_MB = 1024 * 1024;
    private final Logger logger = LoggerFactory.getLogger(HarmonyClient.class);
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageDeserializer()).create();
    private final Map<String, CompletableFuture<ResponseMessage>> responseFutures = new HashMap<>();
    private final Set<HarmonyClientListener> listeners = new HashSet<>();
    private WebSocketClient client;
    private Session session;
    private HarmonyConfig cachedConfig;
    private Activity currentActivity;
    private long connectedTime = System.currentTimeMillis();
    private HttpClient httpClient;
    private ScheduledExecutorService timeoutService;

    /*
     * Public Methods
     */
    public HarmonyClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        timeoutService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Adds a {@link HarmonyClientListener}
     *
     * @param listener HarmonyClientListener
     */
    public void addListener(HarmonyClientListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        if (currentActivity != null) {
            listener.activityStarted(currentActivity);
        }
    }

    /**
     * Removes a {@link HarmonyClientListener}
     *
     * @param listener HarmonyClientListener
     */
    public void removeListener(HarmonyClientListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Connect to a HarmonyHub
     *
     * @param host host or IP to connect to
     * @throws IOException if unable to connect to host
     */
    public void connect(String host) throws IOException {
        if (session != null && session.isOpen()) {
            throw new IOException("Can not call connect on already connected session");
        }
        if (!httpClient.isStarted()) {
            try {
                httpClient.start();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
        }
        Discovery discovery = getDiscoveryFromHost(host);
        if (discovery == null) {
            throw new IOException(String.format("Could not discover host %s", host));
        }
        connectWebsocket(host, discovery.getActiveRemoteId());
    }

    /**
     * Disconnects from a HarmonyHub
     */
    public void disconnect() {
        if (isConnected()) {
            session.close();
        }
    }

    /**
     * Is there an active connection to a HarmonyHub
     *
     * @return is connected or not
     */
    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    /**
     * Queries a HarmmonyHub for its discovery information
     *
     * @param host host or IP to query
     * @return {@link Discovery}
     * @throws IOException if unable to communicate to host
     */
    public Discovery getDiscoveryFromHost(String host) throws IOException {
        URI uri;
        try {
            uri = new URI(String.format("http://%s:8088", host));
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
        DiscoveryRequestMessage drm = new DiscoveryRequestMessage();
        Request request = httpClient.POST(uri);

        logger.trace("Sending {}", drm.toJSON());
        request.content(new StringContentProvider(drm.toJSON()), "application/json");
        request.header(HttpHeader.ORIGIN, "http://sl.dhg.myharmony.com");
        request.header(HttpHeader.ACCEPT, "text/plain");

        try {
            ContentResponse response = request.send();
            String res = new String(response.getContent());
            logger.trace("Discovery response for hos {} : {}", host, res);
            DiscoveryResponseMessage dr = DiscoveryResponseMessage.fromJSON(res);
            if (dr == null) {
                throw new IOException("Could not serialize discovery response");
            }

            return dr.getDiscovery();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Sends a ping message to a HarmonyHub
     *
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Ping> sendPing() {
        final CompletableFuture<Ping> future = new CompletableFuture<Ping>();
        sendMessage(new PingMessage.PingRequestMessage()).thenAccept(m -> {
            future.complete(((PingMessage.PingResponseMessage) m).getPing());
        }).exceptionally(e -> {
            future.completeExceptionally(e);
            return null;
        });
        return future;
    }

    /**
     * Get the current running activity
     *
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Activity> getCurrentActivity() {
        final CompletableFuture<Activity> future = new CompletableFuture<Activity>();
        if (this.currentActivity == null) {
            sendMessage(new GetCurrentActivityMessage.GetCurrentActivityRequestMessage()).thenAccept(m -> {
                int activityId = ((GetCurrentActivityMessage.GetCurrentActivityResponseMessage) m).getActivityId();
                this.currentActivity = this.cachedConfig.getActivityById(activityId);
                future.complete(this.currentActivity);
            });
        } else {
            future.complete(this.currentActivity);
        }
        return future;
    }

    /**
     * Starts an activity by it's numeric ID
     *
     * @param activityId numeric ID of the activity
     * @return {@link CompletableFuture}
     * @throws IllegalArgumentException if activity does not exist
     */
    public CompletableFuture<?> startActivity(int activityId) throws IllegalArgumentException {
        if (cachedConfig != null) {
            if (cachedConfig.getActivityById(activityId) == null) {
                throw new IllegalArgumentException(String.format("Unknown activity '%d'", activityId));
            }
            return sendMessage(new StartActivityMessage.StartActivityRequestMessage(activityId,
                    System.currentTimeMillis() - connectedTime));
        }
        return null;

    }

    /**
     * Starts and activity by it's string label/name
     *
     * @param label string name or label of the activity
     * @return {@link CompletableFuture}
     * @throws IllegalArgumentException if activity does not exist
     */
    public CompletableFuture<?> startActivityByName(String label) throws IllegalArgumentException {
        if (cachedConfig != null) {
            Activity activity = cachedConfig.getActivityByName(label);
            if (activity == null) {
                throw new IllegalArgumentException(String.format("Unknown activity '%s'", label));
            }
            return sendMessage(new StartActivityMessage.StartActivityRequestMessage(activity.getId(),
                    System.currentTimeMillis() - connectedTime));
        }
        return null;
    }

    /**
     * Sends a button press command to a device in the current activity that is is registered to handle the command
     *
     * @param buttonName name of the button to press
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<?> pressButtonCurrentActivity(String buttonName) {
        return pressButtonCurrentActivity(buttonName, 200);
    }

    /**
     * Sends a button press command to a device in the current activity that is is registered to handle the command
     *
     * @param buttonName name of the button to press
     * @param timeMillis time in milliseconds to hold the press for
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<?> pressButtonCurrentActivity(String buttonName, int timeMillis) {
        final CompletableFuture<?> future = new CompletableFuture<>();
        getCurrentActivity().thenAccept(currentActivity -> {
            // control group, function, name/label
            String activityLabel = currentActivity.getLabel();
            for (Activity activity : cachedConfig.getActivities()) {
                if (activity.getLabel().equalsIgnoreCase(activityLabel)) {
                    for (ControlGroup controlGroup : activity.getControlGroup()) {
                        for (Function function : controlGroup.getFunction()) {
                            if (function.getName().equalsIgnoreCase(buttonName)
                                    || function.getLabel().equalsIgnoreCase(buttonName)) {
                                Action action = gson.fromJson(function.getAction(), Action.class);
                                pressButton(action.getDeviceId(), action.getCommand(), timeMillis).thenAccept(mm -> {
                                    future.complete(null);
                                });
                                return;
                            }
                        }
                    }
                }
            }
            if (!future.isDone()) {
                future.completeExceptionally(new Exception("Could not find device in activity for button press"));
            }
        });
        return future;
    }

    /**
     * Sends a button press command to a device, depressed for a given time
     *
     * @param deviceId numeric ID of the button to press
     * @param button string name or label of the button to press
     * @param timeMillis time in milliseconds to hold the press for
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<?> pressButton(int deviceId, String button, int timeMillis) {
        final CompletableFuture<?> future = new CompletableFuture<>();
        sendNoReplyMessage(new HoldActionMessage.HoldActionRequestMessage(deviceId, button, HoldStatus.PRESS,
                System.currentTimeMillis() - connectedTime)).thenAccept(m -> {
                    try {
                        Thread.sleep(timeMillis);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendNoReplyMessage(new HoldActionMessage.HoldActionRequestMessage(deviceId, button,
                            HoldStatus.RELEASE, System.currentTimeMillis() - connectedTime)).thenAccept(mm -> {
                                future.complete(null);

                            });
                });
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return future;
    }

    /**
     * Sends a button press command to a device, depressed for 200ms
     *
     * @param deviceId numeric ID of the device
     * @param button string name or label of the button to press
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<?> pressButton(int deviceId, String button) {
        return pressButton(deviceId, button, 200);
    }

    /**
     * Sends a button press command to a device, depressed for 200ms
     *
     * @param deviceName string name or label of the device
     * @param button string name or label of the button
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<?> pressButton(String deviceName, String button) {
        Device device = cachedConfig.getDeviceByName(deviceName);
        if (device == null) {
            throw new IllegalArgumentException(String.format("Unknown device '%s'", deviceName));
        }
        return pressButton(device.getId(), button);
    }

    /**
     * Sends a button press command to a device, depressed for a given time
     *
     * @param deviceName string name or label of the device
     * @param button string name or label of the button
     * @param pressTime time in milliseconds to hold the button for
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<?> pressButton(String deviceName, String button, int pressTime) {
        Device device = cachedConfig.getDeviceByName(deviceName);
        if (device == null) {
            throw new IllegalArgumentException(String.format("Unknown device '%s'", deviceName));
        }
        return pressButton(device.getId(), button, pressTime);
    }

    /**
     * Gets the current configuration for a HarmonyHub
     *
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<HarmonyConfig> getConfig() {
        final CompletableFuture<HarmonyConfig> future = new CompletableFuture<HarmonyConfig>();
        if (cachedConfig != null) {
            future.complete(cachedConfig);
        } else {
            sendMessage(new ConfigMessage.ConfigRequestMessage()).thenAccept(m -> {
                this.cachedConfig = ((ConfigMessage.ConfigResponseMessage) m).getHarmonyConfig();
                future.complete(this.cachedConfig);
            });
        }
        return future;
    }

    /*
     * Private Methods
     */

    private ScheduledFuture<?> scheduleTimeout(String msgId) {
        return timeoutService.schedule(() -> {
            CompletableFuture<?> f = responseFutures.remove(msgId);
            if (f != null) {
                f.completeExceptionally(new Exception("response timeout"));
            }
        }, 2, TimeUnit.MINUTES);
    }

    private CompletableFuture<ResponseMessage> sendMessage(RequestMessage message) {
        final CompletableFuture<ResponseMessage> future = new CompletableFuture<>();
        if (!isConnected()) {
            future.completeExceptionally(new IOException("Not Connected"));
            return future;
        }
        final String id = message.getId();
        String json = message.toJson();

        logger.debug("Sending: {}", json);
        session.getRemote().sendString(json, new WriteCallback() {
            @Override
            public void writeSuccess() {
                logger.trace("writeSuccess for id {}", id);
                responseFutures.put(id, future);
                // TODO we need to remove these timeouts when responses are handled normally
                scheduleTimeout(message.getId());
            }

            @Override
            public void writeFailed(Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    private CompletableFuture<ResponseMessage> sendNoReplyMessage(RequestMessage message) {
        if (!isConnected()) {
            return null;
        }
        final CompletableFuture<ResponseMessage> future = new CompletableFuture<>();
        String json = message.toJson();

        logger.debug("Sending: {}", json);
        session.getRemote().sendString(json, new WriteCallback() {
            @Override
            public void writeSuccess() {
                logger.trace("writeSuccess for message {}", message.getId());
                future.complete(null);
            }

            @Override
            public void writeFailed(Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    private void connectWebsocket(String host, String hubId) throws IOException {
        URI uri;
        try {
            uri = new URI(String.format("ws://%s:8088/?domain=svcs.myharmony.com&hubId=%s", host, hubId));
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }

        client = new WebSocketClient(httpClient);
        client.getPolicy().setMaxTextMessageSize(ONE_MB);

        try {
            client.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Could not start web socket connection to hub {}", e);
            throw new IOException(e.getMessage());
        }

        client.connect(new MyWebSocketListener(), uri);
    }

    private class MyWebSocketListener implements WebSocketListener {
        @Override
        public void onWebSocketClose(int code, String reason) {
            logger.debug("onWebSocketClose {} {}", code, reason);
            hubDisconected(reason);
        }

        @Override
        public void onWebSocketConnect(Session wssession) {
            logger.debug("onWebSocketConnect {}", wssession);
            connectedTime = System.currentTimeMillis();
            session = wssession;
            getConfig().thenAccept(m -> {
                synchronized (listeners) {
                    Iterator<HarmonyClientListener> clientIter = listeners.iterator();
                    while (clientIter.hasNext()) {
                        HarmonyClientListener listener = clientIter.next();
                        if (listener != null) {
                            listener.hubConnected();
                        }
                    }
                }
            });
            getCurrentActivity();
        }

        @Override
        public void onWebSocketError(Throwable error) {
            logger.debug("onWebSocketError", error);
            hubDisconected(error.getMessage());
        }

        @Override
        public void onWebSocketBinary(byte[] data, int offset, int len) {
            logger.trace("onWebSocketBinary {} {} {}", data, offset, len);
        }

        @Override
        public void onWebSocketText(String message) {
            logger.trace("onWebSocketText {}", message);
            Message m = gson.fromJson(message, Message.class);

            if (m == null) {
                return;
            }

            if (m instanceof ResponseMessage) {
                ResponseMessage rm = (ResponseMessage) m;
                logger.trace("Looking for future for ID {}", rm.getId());
                CompletableFuture<ResponseMessage> future = responseFutures.remove(rm.getId());
                if (future != null) {
                    logger.trace("Calling for future for ID {}", rm.getId());
                    future.complete(rm);
                }
            }

            if (m instanceof ErrorResponseMessage) {
                ResponseMessage rm = (ResponseMessage) m;
                logger.trace("Error Response: Looking for future for ID {}", rm.getId());
                CompletableFuture<ResponseMessage> future = responseFutures.remove(rm.getId());
                if (future != null) {
                    logger.trace("Error Response:  Calling for future for ID {}", rm.getId());
                    future.completeExceptionally(
                            new Exception(String.format("Error Code %d : %s", rm.getCode(), rm.getMsg())));
                }
            }

            if (m instanceof ActivityFinishedMessage) {
                if (cachedConfig != null) {
                    ActivityFinishedMessage af = (ActivityFinishedMessage) m;
                    logger.debug("ActivityFinishedMessage {}", af.getActivityFinished().getActivityId());
                    Activity activity = cachedConfig.getActivityById(af.getActivityFinished().getActivityId());

                    if (currentActivity != activity) {
                        currentActivity = activity;
                        synchronized (listeners) {
                            Iterator<HarmonyClientListener> clientIter = listeners.iterator();
                            while (clientIter.hasNext()) {
                                HarmonyClientListener listener = clientIter.next();
                                if (listener != null) {
                                    listener.activityStarted(currentActivity);
                                }
                            }
                        }
                    }
                }
            }

            if (m instanceof DigestMessage) {
                boolean newStatus = false;
                DigestMessage dm = (DigestMessage) m;
                Status status = dm.getDigest().getActivityStatus();
                Activity activity = cachedConfig.getActivityById(dm.getDigest().getActivityId());
                logger.trace("DigestMessage {}", activity.getId());
                if (status == Status.HUB_IS_OFF) {
                    // HUB_IS_OFF is a special status received on PowerOff activity only,
                    // but it affects the status of all activities
                    for (Activity act : cachedConfig.getActivities()) {
                        if (act.getStatus() != status) {
                            newStatus = true;
                            act.setStatus(status);
                        }
                    }
                } else if (status != Status.UNKNOWN && status != activity.getStatus()) {
                    newStatus = true;
                    activity.setStatus(status);
                }
                // inform listeners only if status was changed - avoid duplicate notifications
                if (newStatus) {
                    synchronized (listeners) {
                        Iterator<HarmonyClientListener> clientIter = listeners.iterator();
                        while (clientIter.hasNext()) {
                            HarmonyClientListener listener = clientIter.next();
                            logger.debug("status listener[{}] notified: {} - {}", listener, activity, status);
                            listener.activityStatusChanged(activity, status);
                        }
                    }
                }
            }
        }
    }

    private void hubDisconected(String reason) {
        session.close();
        logger.debug("notifyClose {} {}", reason);
        synchronized (listeners) {
            Iterator<HarmonyClientListener> clientIter = listeners.iterator();
            while (clientIter.hasNext()) {
                HarmonyClientListener listener = clientIter.next();
                if (listener != null) {
                    listener.hubDisconnected(reason);
                }
            }
        }
        synchronized (responseFutures) {
            Iterator<Map.Entry<String, CompletableFuture<ResponseMessage>>> responseIter = responseFutures.entrySet()
                    .iterator();
            while (responseIter.hasNext()) {
                responseIter.next().getValue().completeExceptionally(new IOException("Connection Closed"));
                responseIter.remove();
            }
        }
    }
}
