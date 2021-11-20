package com.rosemite.minecarts.main;

import com.rosemite.minecarts.commands.CommandCreateRails;
import com.rosemite.minecarts.helpers.Convert;
import com.rosemite.minecarts.helpers.Log;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class Minecarts extends JavaPlugin {
    private CommandCreateRails commandCreateRails;
    @Override
    public void onEnable() {
        super.onEnable();
        Log.setConsoleSender(getServer().getConsoleSender());
        Log.d("Started Minecarts plugin!");

        commandCreateRails = new CommandCreateRails();

        //noinspection ConstantConditions
        getCommand("rails").setExecutor(commandCreateRails);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        commandCreateRails.clearPlacedBlocks();
    }
}
