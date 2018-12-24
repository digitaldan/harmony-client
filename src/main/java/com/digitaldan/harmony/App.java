package com.digitaldan.harmony;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitaldan.harmony.config.Activity;
import com.digitaldan.harmony.config.Activity.Status;
import com.digitaldan.harmony.config.HarmonyConfig;
import com.digitaldan.harmony.shell.ShellCommandWrapper;
import com.martiansoftware.jsap.CommandLineTokenizer;

/**
 * Sample App
 *
 */
public class App implements HarmonyClientListener {
    private final Logger logger = LoggerFactory.getLogger(App.class);
    private HarmonyClient hc;
    private ScheduledFuture<?> ping;

    public static void main(String[] args) {
        try {
            new App(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public App(String host) throws IOException {
        try {
            hc = new HarmonyClient();
            hc.addListener(this);
            hc.connect(host);

        } catch (Exception e) {
            logger.error("error connecting to Hub", e);
            System.exit(-1);
        }

        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while (true) {
            line = br.readLine();
            if (line == null || line.equals("q")) {
                break;
            }

            try {
                String[] lineArgs = CommandLineTokenizer.tokenize(line);
                if (lineArgs.length == 0) {
                    continue;
                }
                ShellCommandWrapper command = new ShellCommandWrapper();
                new CmdLineParser(command).parseArgument(lineArgs);
                command.execute(hc);
            } catch (CmdLineException e) {
                System.err
                        .println(e.getMessage() + "\n" + "list devices - lists the configured devices and their id's\n"
                                + "list activities - lists the configured activities and their id's\n"
                                + "show activity - shows the current activity\n"
                                + "start <activity> - starts an activity (takes a string or id)\n"
                                + "press <device> <button> - perform a single button press\n"
                                + "get_config - Dumps the full config json, unformatted\n");

            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.println("\n");
            }
        }
        ping.cancel(true);
        br.close();
        System.exit(0);
    }

    @Override
    public void hubConnected() {
        logger.info("hubConnected");
        CompletableFuture<HarmonyConfig> configFuture = hc.getConfig();

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

        ping = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            hc.sendPing().thenAccept(m -> {
                logger.info("PONG");
            });
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void hubDisconnected(String cause) {
        logger.info("hubDisconnected {}", cause);
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
