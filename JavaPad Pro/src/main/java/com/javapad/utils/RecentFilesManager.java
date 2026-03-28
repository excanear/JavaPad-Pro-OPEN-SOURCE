package com.javapad.utils;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage list of recent files persisted via ConfigManager.
 */
public class RecentFilesManager {
    private static final String KEY = "recent.files";
    private static final int MAX = 10;
    private final Deque<String> recent = new ArrayDeque<>();

    public RecentFilesManager() {
        load();
    }

    private void load() {
        String v = ConfigManager.get(KEY, "");
        if (v == null || v.isEmpty()) return;
        List<String> parts = Arrays.stream(v.split(";"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        for (String p : parts) {
            recent.addLast(p);
        }
    }

    private void persist() {
        String join = String.join(";", recent);
        ConfigManager.set(KEY, join);
        ConfigManager.save();
    }

    public void add(Path p) {
        if (p == null) return;
        String s = p.toAbsolutePath().toString();
        recent.remove(s);
        recent.addFirst(s);
        while (recent.size() > MAX) recent.removeLast();
        persist();
    }

    public void remove(Path p) {
        if (p == null) return;
        String s = p.toAbsolutePath().toString();
        if (recent.remove(s)) persist();
    }

    public java.util.List<Path> getRecent() {
        return recent.stream().map(java.nio.file.Paths::get).collect(Collectors.toList());
    }
}
