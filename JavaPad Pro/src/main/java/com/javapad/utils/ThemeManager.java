package com.javapad.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Very small theme manager supporting light / dark toggling.
 */
public class ThemeManager {
    public enum Theme { LIGHT, DARK }

    private static Theme current = Theme.LIGHT;

    public static void init() {
        String t = ConfigManager.get("theme", "LIGHT");
        current = Theme.valueOf(t);
        applyGlobal();
    }

    public static Theme getCurrent() { return current; }

    public static void setTheme(Theme theme) {
        current = theme;
        ConfigManager.set("theme", current.name());
        ConfigManager.save();
        applyGlobal();
    }

    public static void toggleTheme() {
        current = (current == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
        ConfigManager.set("theme", current.name());
        ConfigManager.save();
        applyGlobal();
    }

    private static void applyGlobal() {
        if (current == Theme.DARK) {
            UIManager.put("Panel.background", Color.DARK_GRAY);
            UIManager.put("TextPane.background", Color.BLACK);
            UIManager.put("TextPane.foreground", Color.WHITE);
        } else {
            UIManager.put("Panel.background", Color.LIGHT_GRAY);
            UIManager.put("TextPane.background", Color.WHITE);
            UIManager.put("TextPane.foreground", Color.BLACK);
        }
    }

    public static void applyTo(JComponent c) {
        if (current == Theme.DARK) {
            c.setBackground(Color.BLACK);
            c.setForeground(Color.WHITE);
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }
    }
}
