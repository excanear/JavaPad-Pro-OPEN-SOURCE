package com.javapad.plugins;

import com.javapad.ui.MainFrame;

/**
 * Simple plugin interface. Plugins can be loaded by PluginManager.
 */
public interface Plugin {
    void init(MainFrame frame);
    String getName();
}
