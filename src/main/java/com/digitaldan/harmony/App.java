package com.digitaldan.harmony;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitaldan.harmony.config.Activity;
import com.digitaldan.harmony.config.Activity.Status;
import com.digitaldan.harmony.config.HarmonyConfig;

/**
 * Hello world!
 *
 */
public class App implements HarmonyClientListener {
    private final Logger logger = LoggerFactory.getLogger(App.class);
    private HarmonyClient hc;
    private boolean connected = false;
    private boolean running = true;

    public static void main(String[] args) {
        new App(args[0]);
    }

    public App(String host) {
        try {
            hc = new HarmonyClient();
            hc.addListener(this);
            hc.connect(host);
            while (running) {
                if (connected) {
                    hc.sendPing().thenAccept(m -> {
                        logger.info("PONG");
                    });
                }
                Thread.currentThread();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                }
            }
        } catch (Exception e) {
            logger.error("error connecting to Hub", e);
        }

        System.exit(0);
    }

    @Override
    public void hubConnected() {
        logger.info("hubConnected");
        CompletableFuture<HarmonyConfig> configFuture = hc.getHarmonyConfig();

        /**
         * Get the current JSON config
         */
        configFuture.thenAccept(c -> {
            logger.info("CONFIG RECIEVED {}", c.toJson());
            for (Activity a : c.getActivities()) {
                logger.info("Activity {}", a.getLabel());
            }
        });

        /**
         * Get the currect Activity
         */
        CompletableFuture<Activity> activyFuture = hc.getCurrentActivity();
        activyFuture.thenAccept(a -> {
            logger.info("ACTIVITY RECIEVED {}", a);
        });
        connected = true;

        /**
         *
         * Power everything off
         */
        CompletableFuture<?> startFuture = hc.startActivity(-1);
        startFuture.thenAccept(m -> {
            logger.info("ACTIVITY Started {}", m);
        });

    }

    @Override
    public void hubDisconnected(String cause) {
        logger.info("hubDisconnected {}", cause);
        connected = false;
        running = false;
    }

    @Override
    public void activityStarted(Activity activity) {
        logger.info("Activity started {}", activity.toString());

    }

    @Override
    public void activityStatusChanged(Activity activity, Status status) {
        logger.info("Activity status changed to {}  {}", status, activity.toString());

    }
}
