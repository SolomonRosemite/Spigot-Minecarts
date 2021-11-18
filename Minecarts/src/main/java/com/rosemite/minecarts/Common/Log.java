package com.rosemite.minecarts.Common;

import com.rosemite.minecarts.Main;

public class Log {
    public static Main main;

    public static void d(Object message) {
        main.broadcastMessage("[Log]: " + message);
    }
}
