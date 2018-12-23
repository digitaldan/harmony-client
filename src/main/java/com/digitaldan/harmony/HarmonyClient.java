package com.digitaldan.harmony;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
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

import com.digitaldan.harmony.config.Activity;
import com.digitaldan.harmony.config.Activity.Status;
import com.digitaldan.harmony.config.Discovery;
import com.digitaldan.harmony.config.HarmonyConfig;
import com.digitaldan.harmony.messages.ActivityFinishedMessage;
import com.digitaldan.harmony.messages.ConfigMessage;
import com.digitaldan.harmony.messages.DigestMessage;
import com.digitaldan.harmony.messages.DiscoveryMessage.DiscoveryRequestMessage;
import com.digitaldan.harmony.messages.DiscoveryMessage.DiscoveryResponseMessage;
import com.digitaldan.harmony.messages.GetCurrentActivityMessage;
import com.digitaldan.harmony.messages.Message;
import com.digitaldan.harmony.messages.MessageDeserializer;
import com.digitaldan.harmony.messages.PingMessage;
import com.digitaldan.harmony.messages.RequestMessage;
import com.digitaldan.harmony.messages.ResponseMessage;
import com.digitaldan.harmony.messages.StartActivityMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HarmonyClient implements WebSocketListener {
    private final Logger logger = LoggerFactory.getLogger(HarmonyClient.class);
    private Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageDeserializer()).create();
    private ConcurrentHashMap<String, CompletableFuture<ResponseMessage>> responseFutures = new ConcurrentHashMap<>();
    private ArrayList<HarmonyClientListener> listeners = new ArrayList<>();
    private WebSocketClient client;
    private Session session;
    private HarmonyConfig cachedConfig;
    private Activity currentActivity;
    private long connectedTime = System.currentTimeMillis();
    private HttpClient httpClient;

    public HarmonyClient() throws Exception {
        httpClient = new HttpClient();
        httpClient.start();

    }

    public void addListener(HarmonyClientListener listener) {
        listeners.add(listener);
        if (currentActivity != null) {
            listener.activityStarted(currentActivity);
        }
    }

    public void removeListener(HarmonyClientListener listener) {
        listeners.remove(listener);
    }

    public void connect(String host) throws IOException {
        Discovery discovery = getDiscoveryFromHost(host);
        connectWebsocket(host, discovery.getRemoteId());
    }

    public Discovery getDiscoveryFromHost(String host) throws IOException {
        URI uri;
        try {
            uri = new URI(String.format("http://%s:8088", host));
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
        DiscoveryRequestMessage drm = new DiscoveryRequestMessage();
        Request request = httpClient.POST(uri);

        logger.info("Sending {}", drm.toJSON());
        request.content(new StringContentProvider(drm.toJSON()), "application/json");
        request.header(HttpHeader.ORIGIN, "http//:localhost.nebula.myharmony.com");
        request.header(HttpHeader.ACCEPT, "text/plain");

        try {
            ContentResponse response = request.send();
            String res = new String(response.getContent());
            DiscoveryResponseMessage dr = DiscoveryResponseMessage.fromJSON(res);
            if (dr == null) {
                throw new IOException("Could not serialize discovery response");
            }

            return dr.getDiscovery();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void connectWebsocket(String host, String hubId) throws IOException {
        URI uri;
        try {
            uri = new URI(String.format("ws://%s:8088/?domain=svcs.myharmony.com&hubId=%s", host, hubId));
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }

        client = new WebSocketClient(httpClient);

        try {
            client.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Could not start web socket connection to hub {}", e);
            throw new IOException(e.getMessage());
        }

        client.connect(this, uri);
    }

    @Override
    public void onWebSocketClose(int code, String reason) {
        logger.info("onWebSocketClose {} {} ", code, reason);
        for (HarmonyClientListener listener : listeners) {
            if (listener != null) {
                listener.hubDisconnected(reason);
            }
        }
        Iterator<String> it = responseFutures.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            responseFutures.remove(key).completeExceptionally(new IOException("Connection Closed"));
        }
    }

    @Override
    public void onWebSocketConnect(Session session) {
        logger.info("onWebSocketConnect {}", session);
        connectedTime = System.currentTimeMillis();
        this.session = session;
        getHarmonyConfig().thenAccept(m -> {
            for (HarmonyClientListener listener : listeners) {
                if (listener != null) {
                    listener.hubConnected();
                }
            }
        });
        getCurrentActivity();
    }

    @Override
    public void onWebSocketError(Throwable error) {
        logger.error("onWebSocketError", error);
    }

    @Override
    public void onWebSocketBinary(byte[] data, int offset, int len) {
        logger.info("onWebSocketBinary {} {} {} ", data, offset, len);
    }

    @Override
    public void onWebSocketText(String message) {
        logger.info("onWebSocketText {}", message);
        Message m = gson.fromJson(message, Message.class);

        if (m == null) {
            return;
        }

        if (m instanceof ResponseMessage) {
            ResponseMessage rm = (ResponseMessage) m;
            logger.info("Looking for future for ID {} ", rm.getId());
            CompletableFuture<ResponseMessage> future = responseFutures.remove(rm.getId());
            if (future != null) {
                logger.info("Calling for future for ID {} ", rm.getId());
                future.complete(rm);
            }
        }

        if (m instanceof ActivityFinishedMessage) {
            if (cachedConfig != null) {
                ActivityFinishedMessage af = (ActivityFinishedMessage) m;
                logger.info("ActivityFinishedMessage {}", af.getActivityFinished().getActivityId());
                Activity activity = cachedConfig.getActivityById(af.getActivityFinished().getActivityId());

                if (currentActivity != activity) {
                    currentActivity = activity;
                    for (HarmonyClientListener listener : listeners) {
                        if (listener != null) {
                            listener.activityStarted(this.currentActivity);
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
            logger.info("DigestMessage {}", activity.getId());
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
                for (HarmonyClientListener listener : listeners) {
                    logger.debug("status listener[{}] notified: {} - {}", listener, activity, status);
                    listener.activityStatusChanged(activity, status);
                }
            }
        }
    }

    CompletableFuture<ResponseMessage> sendMessage(RequestMessage message) {
        if (session == null) {
            return null;
        }
        final String id = message.getId();
        final CompletableFuture<ResponseMessage> future = new CompletableFuture<>();
        session.getRemote().sendString(message.toJson(), new WriteCallback() {
            @Override
            public void writeSuccess() {
                logger.debug("writeSuccess for id {}", id);
                responseFutures.put(id, future);
            }

            @Override
            public void writeFailed(Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public CompletableFuture<?> sendPing() {
        return sendMessage(new PingMessage.PingRequestMessage());
    }
    //
    // public CompletableFuture<PingResponseMessage> pressButton(int deviceId, String button) {
    // return null;
    // }
    //
    // public CompletableFuture<PingResponseMessage> pressButton(int deviceId, String button, int pressTime) {
    // return null;
    // }
    //
    // public CompletableFuture<PingResponseMessage> pressButton(String deviceName, String button) {
    // return null;
    // }
    //
    // public CompletableFuture<PingResponseMessage> pressButton(String deviceName, String button, int pressTime) {
    // return null;
    // }

    public CompletableFuture<Activity> getCurrentActivity() {
        final CompletableFuture<Activity> future = new CompletableFuture<Activity>();
        if (currentActivity == null) {
            sendMessage(new GetCurrentActivityMessage.GetCurrentActivityRequestMessage()).thenAccept(m -> {
                this.currentActivity = ((GetCurrentActivityMessage.GetCurrentActivityResponseMessage) m).getActivity();
                future.complete(this.currentActivity);
            });
        } else {
            future.complete(this.currentActivity);
        }
        return future;
    }

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

    public CompletableFuture<?> startActivityByName(String label) throws IllegalArgumentException {
        if (cachedConfig != null) {
            Activity activity = cachedConfig.getActivityByName(label);
            if (activity == null) {
                throw new IllegalArgumentException(String.format("Unknown activity '%s'", label));
            }
            // if (currentActivity == null || !label.equals(currentActivity.getLabel())) {
            return sendMessage(new StartActivityMessage.StartActivityRequestMessage(activity.getId(),
                    System.currentTimeMillis() - connectedTime));
            // }

        }
        return null;
    }

    public CompletableFuture<HarmonyConfig> getHarmonyConfig() {
        final CompletableFuture<HarmonyConfig> future = new CompletableFuture<HarmonyConfig>();
        sendMessage(new ConfigMessage.ConfigRequestMessage()).thenAccept(m -> {
            this.cachedConfig = ((ConfigMessage.ConfigResponseMessage) m).getHarmonyConfig();
            future.complete(this.cachedConfig);
        });
        return future;
    }
}
