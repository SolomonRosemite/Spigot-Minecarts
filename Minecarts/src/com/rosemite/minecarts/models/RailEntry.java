package com.rosemite.minecarts.models;

import com.rosemite.minecarts.helpers.Common;
import com.rosemite.minecarts.helpers.Convert;
import org.bukkit.Location;
import org.bukkit.Material;

public class RailEntry {
    public Material prevMaterial;
    public Location location;
    public RailEntry prevEntry;

    public RailEntry(Location location, Material prevMaterial, RailEntry prevEntry) {
        this.location = location;
        this.prevMaterial = prevMaterial;
        this.prevEntry = prevEntry;
    }

    public RailEntry moveUp(int heightDifference, int[] direction) {
        location.add(0, heightDifference--, 0);

        if (heightDifference > 0) {
            if (prevEntry != null) {
                return prevEntry.moveUp(heightDifference, direction);
            }

            Location newLocation = location.clone().add(0, -1, 0);
            newLocation = Common.getNextLocationReverse(newLocation, direction[0], direction[1]);
            return new RailEntry(newLocation, newLocation.getBlock().getType(), null);
        } else if (prevEntry != null) {
            int prevY = prevEntry.location.getBlockY();
            int currentY = location.getBlockY();

            int res = Math.abs(currentY - prevY);
            if (res > 1) {
                prevEntry.moveUp(1, direction);
            }
        }

        return null;
    }

    public void moveUpChildByOne() {
        if (prevEntry != null) {
            prevEntry.moveUpByOne();
        }
    }

    private void moveUpByOne() {
        location.add(0, 1, 0);
    }

    public String toString() {
        return Convert.toJson(this);
    }
}
