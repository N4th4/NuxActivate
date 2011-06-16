package com.bukkit.N4th4.NuxActivate;

import java.util.logging.Logger;
import java.util.logging.Level;

public class NALogger {
    private static Logger log;

    public static void initialize() {
        log = Logger.getLogger("Minecraft");
    }

    public static void info(String message) {
        log.log(Level.INFO, "[NuxActivate] " + message);
    }

    public static void severe(String message) {
        log.log(Level.SEVERE, "[NuxActivate] " + message);
    }
}
