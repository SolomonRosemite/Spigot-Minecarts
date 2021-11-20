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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandCreateRails implements CommandExecutor {
    private final LinkedList<RailEntry> entries;
    private final Material currentMaterial = Material.POLISHED_BASALT;
    private final boolean fullTest = true;

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
                buildRails(p, 100);
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

//        verifyAndFix();

        if (fullTest)
            buildRoute();
        else
            testBuild();
    }

    private RailEntry calculateRailPath(Location startingLocation, RailEntry prevEntry) {
        Vector vec = startingLocation.getDirection();
        double xAbs = Math.abs(vec.getX());
        double zAbs = Math.abs(vec.getZ());

        int[] direction = getDirection(vec, xAbs, zAbs);

        Location nextLocation = Common.getNextLocation(startingLocation, direction[0], direction[1]);

        if (nextLocation.getBlock().isEmpty()) {
            // Check if we can go one down
            Location l = nextLocation.clone().add(0, -1, 0);

//            if (l.getBlock().isEmpty() && l.add(0, -1, 0).getBlock().isEmpty() && Common.getNextLocation(l, direction[0] * -1, direction[1] * -1).getBlock().isEmpty()) {

            if (l.getBlock().isEmpty() && l.add(0, -1, 0).getBlock().isEmpty()) {
                return new RailEntry(nextLocation.add(0, -1, 0), nextLocation.getBlock().getType(), prevEntry);
            }

            return new RailEntry(nextLocation, nextLocation.getBlock().getType(), prevEntry);
        }

        int heightDifference = getNextEmptyLocationHeight(nextLocation);

        nextLocation.add(0, heightDifference, 0);

        RailEntry newEntry = prevEntry.moveUp(heightDifference, direction);
        if (newEntry != null) {
            entries.addFirst(newEntry);
        }

        return new RailEntry(nextLocation, nextLocation.getBlock().getType(), prevEntry);
    }

    private void verifyAndFix() {
        LinkedHashMap<Integer, RailEntry> queue = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, RailEntry> eldest) {
                return this.size() > 3;
            }
        };

        RailEntry e1;
        RailEntry e2;
        RailEntry e3;
        for (int i = 0; i < entries.size(); i++) {
            RailEntry entry = entries.get(i);
            queue.put(i, entry);

            if (queue.size() != 3) {
                continue;
            }

            e1 = queue.get(i-2);
            e2 = queue.get(i-1);
            e3 = queue.get(i);

            if (e1.location.getBlockY() == e3.location.getBlockY() && e2.location.getBlockY() > e1.location.getBlockY()) {
                e2.location.add(0, 1, 0);
            }
        }
    }

    private void clearPlacedBlocks(Location l) {
        //noinspection ConstantConditions
        entries.forEach(entry -> l.getWorld().getBlockAt(entry.location.getBlockX(), entry.location.getBlockY(), entry.location.getBlockZ()).setType(entry.prevMaterial));

        if (fullTest)
        {
            //noinspection ConstantConditions
            entries.forEach(entry -> l.getWorld().getBlockAt(entry.location.getBlockX(), entry.location.getBlockY() + 1, entry.location.getBlockZ()).setType(Material.AIR));
        }
    }

    private void buildRoute() {
        entries.forEach(entry -> {
            entry.location.getBlock().setType(Material.REDSTONE_BLOCK);
            entry.location.clone().add(0, 1, 0).getBlock().setType(Material.POWERED_RAIL);
        });
    }

    private int getNextEmptyLocationHeight(Location startingLocation) {
        Location l = startingLocation.clone();

        while (!l.getBlock().isEmpty()) {
            l.add(0, 1, 0);
        }

        return l.getBlockY() - startingLocation.getBlockY();
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
