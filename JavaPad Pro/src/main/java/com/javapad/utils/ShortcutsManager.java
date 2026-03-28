package com.javapad.utils;

import javax.swing.KeyStroke;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage customizable shortcuts persisted via ConfigManager.
 */
public class ShortcutsManager {
    private static final String PREFIX = "shortcut.";
    private final Map<String, KeyStroke> map = new HashMap<>();

    public ShortcutsManager() {
        loadDefaults();
    }

    private void loadDefaults() {
        // load from config or use defaults
        map.put("find", KeyStroke.getKeyStroke(ConfigManager.get("shortcut.find", "ctrl F")));
        map.put("replace", KeyStroke.getKeyStroke(ConfigManager.get("shortcut.replace", "ctrl H")));
        map.put("delete", KeyStroke.getKeyStroke(ConfigManager.get("shortcut.delete", "ctrl DELETE")));
    }

    public KeyStroke get(String id) { return map.get(id); }

    public void set(String id, KeyStroke ks) {
        map.put(id, ks);
        if (ks != null) ConfigManager.set(PREFIX + id, ks.toString());
        ConfigManager.save();
    }

    public void persistAll() {
        for (Map.Entry<String, KeyStroke> e : map.entrySet()) {
            if (e.getValue() != null) ConfigManager.set(PREFIX + e.getKey(), e.getValue().toString());
        }
        ConfigManager.save();
    }
}
