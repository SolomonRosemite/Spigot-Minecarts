package com.rosemite.minecarts.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Log {
    private static ConsoleCommandSender consoleSender;

    public static void setConsoleSender(ConsoleCommandSender cs) {
        if (cs != null && consoleSender == null) {
            consoleSender = cs;
        }
    }

    public static void d(Object message) {
        consoleSender.sendMessage( ChatColor.YELLOW + "[Log]:" + ChatColor.WHITE + message);
    }
}
