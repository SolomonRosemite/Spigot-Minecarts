package com.rosemite.minecarts.main;

import com.rosemite.minecarts.commands.CommandCreateRails;
import com.rosemite.minecarts.helpers.Convert;
import com.rosemite.minecarts.helpers.Log;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class Minecarts extends JavaPlugin {
    private CommandCreateRails commandCreateRails;
    private Location l;

    @Override
    public void onEnable() {
        super.onEnable();
        Log.setConsoleSender(getServer().getConsoleSender());
        Log.d("Started Minecarts plugin!");

        l = Convert.fromJson("{ \"world\": \"world\", \"x\": 541.3000000119209, \"y\": 64.0, \"z\": -669.3424957397692, \"yaw\": 272.16498, \"pitch\": 2.430829 }", Location.class);
        commandCreateRails = new CommandCreateRails(l);

        //noinspection ConstantConditions
        getCommand("rails").setExecutor(commandCreateRails);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        commandCreateRails.clearPlacedBlocks(l);
    }
}
