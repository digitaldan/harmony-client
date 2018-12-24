package com.digitaldan.harmony.shell;

import com.digitaldan.harmony.HarmonyClient;
import com.digitaldan.harmony.config.Activity;

public class ListActivitiesCommand extends ShellCommand {
    @Override
    public void execute(HarmonyClient harmonyClient) {
        harmonyClient.getConfig().thenAccept(config -> {
            for (Activity activity : config.getActivities()) {
                println("%s: %s", activity.getId(), activity.getLabel());
            }
        });

    }
}
