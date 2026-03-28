package com.javapad.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Simple configuration manager using a properties file in the user's home.
 */
public class ConfigManager {
    private static final Properties props = new Properties();
    private static final File configFile = Paths.get(System.getProperty("user.home"), ".javapad.properties").toFile();

    public static void load() {
        try {
            if (configFile.exists()) {
                try (FileInputStream in = new FileInputStream(configFile)) { props.load(in); }
            }
        } catch (Exception e) {
            AppLogger.error("Failed to load config", e);
        }
    }

    public static void save() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "JavaPad Pro Settings");
        } catch (Exception e) {
            AppLogger.error("Failed to save config", e);
        }
    }

    public static String get(String key, String def) { return props.getProperty(key, def); }
    public static void set(String key, String value) { props.setProperty(key, value); }

    public static int getInt(String key, int def) {
        try { return Integer.parseInt(get(key, String.valueOf(def))); } catch (Exception e) { return def; }
    }

    public static boolean getBoolean(String key, boolean def) {
        try { return Boolean.parseBoolean(get(key, String.valueOf(def))); } catch (Exception e) { return def; }
    }
}
