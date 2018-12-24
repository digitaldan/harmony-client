package com.digitaldan.harmony.shell;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

import com.digitaldan.harmony.HarmonyClient;

public class ShellCommandWrapper {
    @Argument(handler = SubCommandHandler.class)
    @SubCommands({ @SubCommand(name = "get_config", impl = GetConfigCommand.class),
            @SubCommand(name = "press", impl = PressButtonCommand.class),
            @SubCommand(name = "start", impl = StartActivityCommand.class),
            @SubCommand(name = "list", impl = ListCommand.class),
            @SubCommand(name = "show", impl = ShowCommand.class), })
    private ShellCommand command;

    public void execute(HarmonyClient harmonyClient) {
        command.execute(harmonyClient);
    }
}
