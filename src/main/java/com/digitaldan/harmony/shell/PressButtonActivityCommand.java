package com.digitaldan.harmony.shell;

import org.kohsuke.args4j.Argument;

import com.digitaldan.harmony.HarmonyClient;

public class PressButtonActivityCommand extends ShellCommand {
    @Argument(required = true, index = 0)
    private String button;

    @Override
    public void execute(HarmonyClient harmonyClient) {
        harmonyClient.pressButtonCurrentActivity(button);
    }
}
