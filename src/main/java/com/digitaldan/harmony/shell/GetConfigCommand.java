package com.digitaldan.harmony.shell;

import com.digitaldan.harmony.HarmonyClient;

public class GetConfigCommand extends ShellCommand {
    @Override
    public void execute(HarmonyClient harmonyClient) {
        harmonyClient.getConfig().thenAccept(config -> {
            println(config.toJson());
        });

    }
}
