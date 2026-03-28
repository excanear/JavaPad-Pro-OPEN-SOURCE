package com.javapad.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class AppLogger {
    public static void init() {
        log("Logger initialized");
    }

    private static java.io.PrintWriter fileWriter;

    public static void log(String msg) {
        String line = LocalDateTime.now() + " - " + msg;
        System.out.println(line);
        writeToFile(line);
    }

    public static void error(String msg, Throwable t) {
        String header = LocalDateTime.now() + " - ERROR - " + msg;
        System.err.println(header);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        System.err.println(sw.toString());
        writeToFile(header + "\n" + sw.toString());
    }

    private static void writeToFile(String s) {
        try {
            if (fileWriter == null) {
                java.nio.file.Path logDir = java.nio.file.Paths.get(System.getProperty("user.home"), ".javapad", "logs");
                java.nio.file.Files.createDirectories(logDir);
                java.nio.file.Path logFile = logDir.resolve("app.log");
                fileWriter = new java.io.PrintWriter(new java.io.FileWriter(logFile.toFile(), true), true);
            }
            fileWriter.println(s);
        } catch (Exception e) {
            // fall back to stderr
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
