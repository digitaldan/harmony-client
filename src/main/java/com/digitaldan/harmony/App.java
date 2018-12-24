package com.digitaldan.harmony;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitaldan.harmony.config.Activity;
import com.digitaldan.harmony.config.Activity.Status;
import com.digitaldan.harmony.config.HarmonyConfig;
import com.digitaldan.harmony.shell.ShellCommandWrapper;
import com.martiansoftware.jsap.CommandLineTokenizer;

/**
 * Hello world!
 *
 */
public class App implements HarmonyClientListener {
    private final Logger logger = LoggerFactory.getLogger(App.class);
    private HarmonyClient hc;

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
        } catch (Exception e) {
            logger.error("error connecting to Hub", e);
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
                ShellCommandWrapper command = new ShellCommandWrapper();
                new CmdLineParser(command).parseArgument(lineArgs);
                command.execute(hc);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.println("\n");
            }
        }

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
