package com.digitaldan.harmony.shell;

import org.kohsuke.args4j.Argument;

import com.digitaldan.harmony.HarmonyClient;

public class StartActivityCommand extends ShellCommand {
    @Argument(required = true)
    private String activity;

    @Override
    public void execute(HarmonyClient harmonyClient) {
        try {
            harmonyClient.startActivity(Integer.parseInt(activity));
        } catch (NumberFormatException e) {
            harmonyClient.startActivityByName(activity);
        }
        println("Activity %s started", activity);
    }
}
