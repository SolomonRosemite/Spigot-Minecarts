package com.rosemite.minecarts.commands;

import com.rosemite.minecarts.helpers.Common;
import com.rosemite.minecarts.helpers.Convert;
import com.rosemite.minecarts.helpers.Log;
import com.rosemite.minecarts.main.Minecarts;
import com.rosemite.minecarts.models.RailEntry;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.bukkit.Bukkit.getScheduler;

public class CommandCreateRails implements CommandExecutor {
    private final LinkedList<RailEntry> entries;
    private final LinkedList<RailEntry> otherEntries;

    private final Material currentMaterial = Material.POLISHED_BASALT;
    private final boolean fullTest = true;
    @SuppressWarnings("FieldCanBeLocal")
//    private final int distance = 1000 * 4;
    private final int distance = 1000 * 10;
    public Location loc;
    Minecarts plugin;

    public CommandCreateRails(Minecarts plugin) {
        entries = new LinkedList<>();
        otherEntries = new LinkedList<>();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player p = (Player) sender;

        loc = p.getLocation().clone();

        if (args.length > 0)
        {
            String req = args[0].toLowerCase();

            if (req.startsWith("c")) {
                notify("Cleared all entries!", p);
                clearPlacedBlocks();
                entries.clear();
                return true;
            }

            if (req.startsWith("b")) {
                notify("Build Rails!", p);
                buildRails(p, loc, distance);
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

    private void buildRails(Player p, Location location, int distance) {
        getScheduler().runTaskAsynchronously(plugin, () -> {
            long startTime = System.currentTimeMillis();

            Location l = location.clone();
            Location prevLocation = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch());

            Vector vec = l.getDirection();
            double xAbs = Math.abs(vec.getX());
            double zAbs = Math.abs(vec.getZ());

            int[] direction = getDirection(vec, xAbs, zAbs);

            for (int i = 0; i < distance; i++) {
                RailEntry prevEntry = i != 0 ? entries.getLast() : null;
                RailEntry railEntry = calculateRailPath(prevLocation, direction, prevEntry);
                entries.add(railEntry);

                prevLocation = railEntry.location.clone();

                sendActionBar(p, i / (double)distance, "calculating track");
            }

            finalizeTrack(p);

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            notify("Calculating track took: " + elapsedTime + " ms", p);
        });
    }

    private void finalizeTrack(Player p) {
        new BukkitRunnable() {
            public void run() {
                long startTime = System.currentTimeMillis();
                sendActionBar(p, -1, "Verifying track");

                int invalidCount = verifyAndFix();
                Log.d("Count: " + invalidCount);

                sendActionBar(p, -1, "Building track");

                if (fullTest)
                    buildRoute();
                else
                    testBuild();

                sendActionBar(p, -1, "Completed track!");

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                Log.d("Time: " +elapsedTime);
            }
        }.runTask(plugin);
    }

    private RailEntry calculateRailPath(Location startingLocation, int[] direction, RailEntry prevEntry) {
        Location nextLocation = Common.getNextLocation(startingLocation, direction[0], direction[1]);

        if (nextLocation.getBlock().isEmpty()) {
            // Check if we can go one down
            Location l = nextLocation.clone().add(0, -1, 0);
            if (l.getBlock().isEmpty() && l.add(0, -1, 0).getBlock().isEmpty()) {
                return new RailEntry(nextLocation.add(0, -1, 0), nextLocation.getBlock().getType(), prevEntry);
            }

            return new RailEntry(nextLocation, nextLocation.getBlock().getType(), prevEntry);
        }

        int heightDifference = getNextEmptyLocationHeight(nextLocation);

        nextLocation.add(0, heightDifference, 0);

        if (prevEntry != null) {
            RailEntry newEntry = prevEntry.moveUp(heightDifference, direction);

            if (newEntry != null) {
                entries.addFirst(newEntry);
            }
        }

        return new RailEntry(nextLocation, nextLocation.getBlock().getType(), prevEntry);
    }

    private int verifyAndFix() {
        int invalidCount = 0;

        // Repeat until height verification succeeds
        while (!verifyHeight()) {
            invalidCount++;
        }

        verifyAndFixInvalidTrackPattern();

        return invalidCount;
    }

    private boolean verifyHeight() {
        Location prevLocation = entries.getFirst().location.clone();

        for (RailEntry entry : entries) {
            int heightDifference = Math.abs(entry.location.getBlockY() - prevLocation.getBlockY());

            if (heightDifference > 1) {
                prevLocation = entry.location.clone();
                entry.moveUpChildByOne();
                return false;
            }

            prevLocation = entry.location.clone();
        }

        return true;
    }

    private void verifyAndFixInvalidTrackPattern() {
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

            if (e1.location.getBlockY() == e3.location.getBlockY() && e2.location.getBlockY() < e1.location.getBlockY()) {
                e2.location.add(0, 1, 0);
                e2.location.getBlock().setType(Material.DIAMOND_BLOCK);
            }
        }
    }

    public void clearPlacedBlocks() {
        if (fullTest)
        {
            //noinspection ConstantConditions
            otherEntries.forEach(entry -> loc.getWorld().getBlockAt(entry.location.getBlockX(), entry.location.getBlockY(), entry.location.getBlockZ()).setType(entry.prevMaterial));
        }

        //noinspection ConstantConditions
        entries.forEach(entry -> loc.getWorld().getBlockAt(entry.location.getBlockX(), entry.location.getBlockY(), entry.location.getBlockZ()).setType(entry.prevMaterial));
    }

    private void buildRoute() {
        entries.forEach(entry -> {
            entry.location.getBlock().setType(Material.REDSTONE_BLOCK);

            Location l = entry.location.clone().add(0, 1, 0);
            otherEntries.add(new RailEntry(l, l.getBlock().getType(), null));
            l.getBlock().setType(Material.POWERED_RAIL);

            Location l2 = entry.location.clone().add(0, 2, 0);
            otherEntries.add(new RailEntry(l2, l2.getBlock().getType(), null));
            l2.getBlock().setType(Material.AIR);
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

    public void sendActionBar(Player p, double progress, String title) {
        if (progress == -1) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + title));
            return;
        }

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + "Progress " + title + ": " + Math.round(progress * 100)+ "%"));
    }
}
