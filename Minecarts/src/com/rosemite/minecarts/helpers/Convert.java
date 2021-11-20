package com.rosemite.minecarts.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rosemite.minecarts.models.LocationAdapter;
import org.bukkit.Location;

public class Convert {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .setPrettyPrinting()
            .create();

    public static String toJson(Object value) {
        return gson.toJson(value);
    }
    public static <T> T fromJson(String value, Class<T> type) {
        return gson.fromJson(value, type);
    }
}
