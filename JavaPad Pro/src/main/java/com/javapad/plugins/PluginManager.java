package com.javapad.plugins;

import com.javapad.ui.MainFrame;
import com.javapad.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private final List<Plugin> plugins = new ArrayList<>();

    public void register(Plugin p) {
        plugins.add(p);
    }

    public void initAll(MainFrame frame) {
        // initialize built-in/registered plugins
        for (Plugin p : plugins) {
            try { p.init(frame); }
            catch (Exception e) { AppLogger.error("Plugin init failed: " + p.getName(), e); }
        }

        // load external plugins from plugins/ directory (optional)
        try {
            java.nio.file.Path pluginsDir = java.nio.file.Paths.get("plugins");
            if (java.nio.file.Files.exists(pluginsDir) && java.nio.file.Files.isDirectory(pluginsDir)) {
                loadExternalPlugins(pluginsDir, frame);
            }
        } catch (Exception e) {
            AppLogger.error("Failed to scan plugins directory", e);
        }
    }

    private void loadExternalPlugins(java.nio.file.Path dir, MainFrame frame) {
        try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(dir)) {
            s.filter(p -> p.toString().toLowerCase().endsWith(".jar")).forEach(jar -> {
                try {
                    java.net.URL jarUrl = jar.toUri().toURL();
                    java.net.URLClassLoader cl = new java.net.URLClassLoader(new java.net.URL[]{jarUrl}, this.getClass().getClassLoader());
                    try (java.util.jar.JarFile jf = new java.util.jar.JarFile(jar.toFile())) {
                        java.util.Enumeration<java.util.jar.JarEntry> entries = jf.entries();
                        while (entries.hasMoreElements()) {
                            java.util.jar.JarEntry je = entries.nextElement();
                            String name = je.getName();
                            if (name.endsWith(".class")) {
                                String className = name.replace('/', '.').substring(0, name.length() - 6);
                                try {
                                    Class<?> cls = Class.forName(className, true, cl);
                                    if (Plugin.class.isAssignableFrom(cls) && !java.lang.reflect.Modifier.isAbstract(cls.getModifiers())) {
                                        Plugin p = (Plugin) cls.getDeclaredConstructor().newInstance();
                                        register(p);
                                        try { p.init(frame); } catch (Exception ex) { AppLogger.error("Plugin init failed: " + p.getName(), ex); }
                                    }
                                } catch (Throwable t) {
                                    // ignore class load issues for unrelated classes
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    AppLogger.error("Failed to load plugin jar: " + jar, e);
                }
            });
        } catch (Exception ex) {
            AppLogger.error("Error scanning plugin directory: " + dir, ex);
        }
    }
}
