package com.rosemite.minecarts.helpers;

import org.bukkit.Location;

public class Common {
    public static Location getNextLocation(Location location, int x, int z) {
        return location.clone().add(x, 0, z);
    }

    public static Location getNextLocationReverse(Location location, int x, int z) {
        return location.clone().add(x * -1, 0, z * -1);
    }
}
