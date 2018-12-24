package com.digitaldan.harmony.shell;

import static java.lang.String.format;
import static java.lang.System.out;

import com.digitaldan.harmony.HarmonyClient;

public abstract class ShellCommand {
    public abstract void execute(HarmonyClient harmonyClient);

    protected void println(String fmt, Object... args) {
        if (args.length == 0) {
            out.println(fmt);
        } else {
            out.println(format(fmt, args));
        }
    }
}
