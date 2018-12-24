package com.digitaldan.harmony.shell;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

import com.digitaldan.harmony.HarmonyClient;

public class ListCommand extends ShellCommand {
    @Argument(handler = SubCommandHandler.class)
    @SubCommands({ @SubCommand(name = "devices", impl = ListDevicesCommand.class),
            @SubCommand(name = "activities", impl = ListActivitiesCommand.class) })
    private ShellCommand command;

    @Override
    public void execute(HarmonyClient harmonyClient) {
        command.execute(harmonyClient);
    }

}
