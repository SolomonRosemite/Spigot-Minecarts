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
        location.add(0, --heightDifference, 0);

        if (heightDifference != 0) {
            if (prevEntry != null) {
                return prevEntry.moveUp(heightDifference, direction);
            }

            Location newLocation = location.clone().add(0, -1, 0);
            newLocation = Common.getNextLocationReverse(newLocation, direction[0], direction[1]);
            return new RailEntry(newLocation, newLocation.getBlock().getType(), null);
        }

        return null;
    }
//    public void moveOneUp(int[] direction) {
//        location = location.clone().add(0, 1, 0);
//
//        if (prevEntry != null && prevEntry.location.getBlockY() == location.getBlockY() - 1) {
//            return;
//        }
//
//        if (prevEntry != null)
//            prevEntry.moveOneUp(direction);
//
//        Location l2 = location.clone().add(0, -2, 0);
//        l2 = Common.getNextLocation(l2, direction[0] * -1, direction[1] * -1);
//
//        prevEntry = new RailEntry(l2, l2.getBlock().getType(), null);
//    }

    public String toString() {
        return Convert.toJson(this);
    }
}
