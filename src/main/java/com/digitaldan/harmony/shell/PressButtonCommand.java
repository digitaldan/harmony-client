package com.digitaldan.harmony.shell;

import org.kohsuke.args4j.Argument;

import com.digitaldan.harmony.HarmonyClient;

public class PressButtonCommand extends ShellCommand {
    @Argument(required = true, index = 0)
    private String deviceId;

    @Argument(required = true, index = 1)
    private String button;

    @Override
    public void execute(HarmonyClient harmonyClient) {
        try {
            harmonyClient.pressButton(Integer.parseInt(deviceId), button);
        } catch (NumberFormatException e) {
            harmonyClient.pressButton(deviceId, button);
        }
    }
}
