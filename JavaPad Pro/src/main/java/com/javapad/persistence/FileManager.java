package com.javapad.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple file manager using java.nio for safe read/write operations.
 */
public class FileManager {
    public static String readAll(Path p) throws IOException {
        byte[] bytes = Files.readAllBytes(p);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeAll(Path p, String content) throws IOException {
        Files.createDirectories(p.getParent());
        Files.write(p, content.getBytes(StandardCharsets.UTF_8));
    }

    public static void delete(Path p) throws IOException {
        // delete the file if it exists, throw IOException on failure
        Files.delete(p);
    }

    /**
     * Read up to maxBytes from the start of the file as UTF-8. Useful for
     * previewing very large files without loading everything into memory.
     */
    public static String readPartial(Path p, int maxBytes) throws IOException {
        long size = Files.size(p);
        int toRead = (int) Math.min(size, (long) maxBytes);
        byte[] buf = new byte[toRead];
        try (java.io.InputStream in = Files.newInputStream(p)) {
            int read = 0;
            while (read < toRead) {
                int r = in.read(buf, read, toRead - read);
                if (r < 0) break;
                read += r;
            }
        }
        return new String(buf, StandardCharsets.UTF_8);
    }
}
