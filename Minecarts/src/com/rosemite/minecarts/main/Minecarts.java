package com.rosemite.minecarts.main;

import com.rosemite.minecarts.commands.CommandCreateRails;
import com.rosemite.minecarts.helpers.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class Minecarts extends JavaPlugin {
    @Override
    public void onEnable() {
        Log.setConsoleSender(getServer().getConsoleSender());
        super.onEnable();

        Log.d("Started Minecarts plugin!");

        //noinspection ConstantConditions
        getCommand("rails").setExecutor(new CommandCreateRails());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
