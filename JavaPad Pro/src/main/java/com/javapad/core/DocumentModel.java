package com.javapad.core;

import java.nio.file.Path;

/**
 * Model representing an open document.
 */
public class DocumentModel {
    private Path path;
    private String displayName;
    private boolean modified = false;
    private int caretLine = 1;
    private int caretColumn = 1;

    public DocumentModel(Path path, String displayName) {
        this.path = path;
        this.displayName = displayName;
    }

    public Path getPath() { return path; }
    public void setPath(Path p) { this.path = p; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String name) { this.displayName = name; }
    public boolean isModified() { return modified; }
    public void setModified(boolean m) { this.modified = m; }
    public void setCaretPosition(int line, int col) { this.caretLine = line; this.caretColumn = col; }
    public int getCaretLine() { return caretLine; }
    public int getCaretColumn() { return caretColumn; }
}
