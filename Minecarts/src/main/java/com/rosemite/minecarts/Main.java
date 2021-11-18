package com.rosemite.minecarts;

import com.rosemite.minecarts.Common.Log;
import com.rosemite.minecarts.commands.CommandCreateRails;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Log.main = this;
        getCommand("rails").setExecutor(new CommandCreateRails());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void broadcastMessage(Object message) {
        Bukkit.broadcastMessage(message.toString());
    }
}
