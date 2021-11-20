package com.rosemite.minecarts.helpers;

import org.bukkit.Location;

public class Common {
    public static Location getNextLocation(Location location, int x, int z) {
        return location.clone().add(x, 0, z);
    }
}
