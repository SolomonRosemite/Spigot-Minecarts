package com.rosemite.minecarts.models;

import com.rosemite.minecarts.helpers.Convert;
import org.bukkit.Location;
import org.bukkit.Material;

public class RailEntry {
    public Material prevMaterial;
    public Location location;
    public boolean canMoveUp;

    public RailEntry(Location location, Material prevMaterial, boolean canMoveUp) {
        this.location = location;
        this.canMoveUp = canMoveUp;
        this.prevMaterial = prevMaterial;
    }

//    public String toString() {
//        return Convert.toJson(location);
//    }
}
