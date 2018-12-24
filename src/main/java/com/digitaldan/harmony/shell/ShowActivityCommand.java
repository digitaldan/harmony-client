package com.digitaldan.harmony.shell;

import com.digitaldan.harmony.HarmonyClient;

public class ShowActivityCommand extends ShellCommand {
    @Override
    public void execute(HarmonyClient harmonyClient) {
        harmonyClient.getCurrentActivity().thenAccept(activity -> {
            println("%d: %s", activity.getId(), activity.getLabel());
        });
    }
}
