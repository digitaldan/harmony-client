package com.digitaldan.harmony.shell;

import java.util.Map.Entry;

import com.digitaldan.harmony.HarmonyClient;

public class ListDevicesCommand extends ShellCommand {
    @Override
    public void execute(HarmonyClient harmonyClient) {
        harmonyClient.getConfig().thenAccept(config -> {
            for (Entry<Integer, String> e : config.getDeviceLabels().entrySet()) {
                println("%d: %s", e.getKey(), e.getValue());
            }
        });
    }
}
