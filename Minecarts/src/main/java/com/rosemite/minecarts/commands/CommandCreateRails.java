package com.rosemite.minecarts.commands;

import com.rosemite.minecarts.Common.Log;
import com.rosemite.minecarts.models.FacingDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CommandCreateRails implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        buildRails((Player) sender, 5);
        return false;
    }

    private void buildRails(Player player, int distance) {
        Location prevLocation = player.getLocation();

        for (int i = 0; i < distance; i++) {
            prevLocation = buildRail(prevLocation);
        }
    }

    // If there is no air block in front, we have to go one back and go up by one

    private Location buildRail(Location l) {
        Vector vec = l.getDirection();
        double xAbs = Math.abs(vec.getX());
        double zAbs = Math.abs(vec.getZ());

        int[] direction = getDirection(vec, xAbs, zAbs);

        Location newLocation = getNextLocation(l, direction[0], direction[1]);

        // Building rail
        newLocation.clone().add(0, -1, 0).getBlock().setType(Material.REDSTONE_BLOCK);
        newLocation.getBlock().setType(Material.POWERED_RAIL);
        return newLocation;
    }

    private int[] getDirection(Vector vec, double xAbs, double zAbs) {
        if (xAbs > zAbs) {
            if (vec.getX() > 0) {
                return new int[] { 1, 0 };
            } else {
                return new int[] { -1, 0 };
            }
        } else {
            if (vec.getZ() > 0) {
                return new int[] { 0, 1 };
            } else {
                return new int[] { 0, -1 };
            }
        }
    }

    private Location getNextLocation(Location location, int x, int z) {
        return location.add(x, 0, z);
    }
}
