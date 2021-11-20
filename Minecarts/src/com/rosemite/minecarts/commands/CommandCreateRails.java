package com.rosemite.minecarts.commands;

import com.rosemite.minecarts.helpers.Common;
import com.rosemite.minecarts.helpers.Convert;
import com.rosemite.minecarts.helpers.Log;
import com.rosemite.minecarts.models.RailEntry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandCreateRails implements CommandExecutor {
    private final LinkedList<RailEntry> entries;
//    private final Material currentMaterial = Material.REDSTONE_BLOCK;
    private final Material currentMaterial = Material.POLISHED_BASALT;

    public CommandCreateRails() {
        entries = new LinkedList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player p = (Player) sender;

        if (args.length > 0)
        {
            String req = args[0].toLowerCase();

            if (req.startsWith("c")) {
                notify("Cleared all entries!", p);
                clearPlacedBlocks(p.getLocation());
                entries.clear();
                return true;
            }

            if (req.startsWith("b")) {
                notify("Build Rails!", p);
                buildRails(p, 20);
                return true;
            }

            if (req.startsWith("g") || req.startsWith("l")) {
                notify("Loaded messages!", p);
                Log.d(Convert.toJson(entries));
                return true;
            }
        }

        p.sendMessage("Couldn't process message.");
        return true;
    }

    private void buildRails(Player player, int distance) {
        Location prevLocation = player.getLocation();

        for (int i = 0; i < distance; i++) {
            RailEntry prevEntry = i != 0 ? entries.getLast() : null;
            RailEntry railEntry = calculateRailPath(prevLocation, prevEntry);
            entries.add(railEntry);

            prevLocation = railEntry.location.clone();
        }

        testBuild();
//        buildRoute();
    }

    private RailEntry calculateRailPath(Location startingLocation, RailEntry prevEntry) {
        Vector vec = startingLocation.getDirection();
        double xAbs = Math.abs(vec.getX());
        double zAbs = Math.abs(vec.getZ());

        int[] direction = getDirection(vec, xAbs, zAbs);

        RailEntry entry;

        Location nextLocation = Common.getNextLocation(startingLocation, direction[0], direction[1]);
        Material materialInFrontFirst = nextLocation.getBlock().getType();
        Material materialInFront = Common.getNextLocation(nextLocation, direction[0], direction[1]).getBlock().getType();

        if ((!materialInFront.isAir() || !materialInFrontFirst.isAir()) && materialInFront != currentMaterial) {
            nextLocation.add(0, 1, 0);
            if (nextLocation.getBlock().isEmpty())
                entry = new RailEntry(nextLocation, nextLocation.getBlock().getType(), prevEntry);
            else
            {
//                entry = new RailEntry(nextLocation.add(0, 1, 0), nextLocation.getBlock().getType(), prevEntry);
                entry = new RailEntry(nextLocation, nextLocation.getBlock().getType(), prevEntry);
                entry.moveOneUp(direction);
            }
        } else {
            // Check if we can go one down
            Location l = nextLocation.clone().add(0, -1, 0);
            if (l.getBlock().isEmpty() && l.add(0, -1, 0).getBlock().isEmpty() && Common.getNextLocation(l, direction[0] * -1, direction[1] * -1).getBlock().isEmpty()) {
                entry = new RailEntry(nextLocation.add(0, -1, 0), nextLocation.getBlock().getType(), prevEntry);
            } else {
                entry = new RailEntry(nextLocation, nextLocation.clone().getBlock().getType(), prevEntry);
            }
        }

        return entry;
    }

    private void clearPlacedBlocks(Location l) {
        //noinspection ConstantConditions
        entries.forEach(entry -> l.getWorld().getBlockAt(entry.location.getBlockX(), entry.location.getBlockY(), entry.location.getBlockZ()).setType(entry.prevMaterial));
        //noinspection ConstantConditions
        entries.forEach(entry -> l.getWorld().getBlockAt(entry.location.getBlockX(), entry.location.getBlockY() + 1, entry.location.getBlockZ()).setType(Material.AIR));
    }

    private void buildRoute() {
        entries.forEach(entry -> {
            entry.location.getBlock().setType(Material.REDSTONE_BLOCK);
            entry.location.clone().add(0, 1, 0).getBlock().setType(Material.POWERED_RAIL);
        });
    }

    private void testBuild() {
        entries.forEach(entry -> entry.location.getBlock().setType(currentMaterial));
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

    private void notify(Object o, Player p) {
        Log.d(o);
        p.sendMessage(o.toString());
    }
}
