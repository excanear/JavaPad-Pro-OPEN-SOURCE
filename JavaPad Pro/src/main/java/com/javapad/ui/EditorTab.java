package com.javapad.ui;

import com.javapad.core.DocumentModel;
import com.javapad.utils.ThemeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.lang.reflect.Method;
import java.awt.event.KeyEvent;

/**
 * Editor tab that will use RSyntaxTextArea when available, falling back to JTextPane.
 * Uses reflection to avoid hard dependency at compile time; if RSyntax is provided
 * via Gradle, enhanced editor features are enabled.
 */
public class EditorTab extends JPanel {
    private final DocumentModel model;
    private final JTextComponent editor; // may be RSyntaxTextArea or JTextPane
    private final UndoManager undoManager = new UndoManager();
    private final JTextArea minimap;
    private boolean partial = false;

    public EditorTab(DocumentModel model) {
        super(new BorderLayout());
        this.model = model;

        JTextComponent created = null;
        try {
            Class<?> cls = Class.forName("org.fife.ui.rsyntaxtextarea.RSyntaxTextArea");
            Object obj = cls.getDeclaredConstructor().newInstance();
            if (obj instanceof JTextComponent) {
                created = (JTextComponent) obj;
                // try to set some properties via reflection
                Method setCodeFolding = cls.getMethod("setCodeFoldingEnabled", boolean.class);
                setCodeFolding.invoke(obj, true);
                Method setFontMethod = cls.getMethod("setFont", Font.class);
                setFontMethod.invoke(obj, new Font(Font.MONOSPACED, Font.PLAIN, 14));
            }
        } catch (Throwable t) {
            // RSyntax not available — fallback
            created = new JTextPane();
            created.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        }

        this.editor = created;
        ThemeManager.applyTo(editor);

        JScrollPane scroll;
        try {
            // if RSyntax is present, wrap in RTextScrollPane
            Class<?> rtextScroll = Class.forName("org.fife.ui.rtextarea.RTextScrollPane");
            scroll = (JScrollPane) rtextScroll.getDeclaredConstructor(Component.class).newInstance(editor);
        } catch (Throwable t) {
            scroll = new JScrollPane(editor);
            // add simple line numbers for JTextPane only
            if (editor instanceof JTextPane) {
                TextLineNumber tln = new TextLineNumber((JTextPane) editor);
                scroll.setRowHeaderView(tln);
            }
        }

        // create a small minimap on the right
        minimap = new JTextArea();
        minimap.setEditable(false);
        minimap.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 8));
        minimap.setBackground(new Color(0xF6F6F6));
        JScrollPane mapScroll = new JScrollPane(minimap);
        mapScroll.setPreferredSize(new Dimension(140, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, mapScroll);
        split.setResizeWeight(1.0);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);

        // keep reference to editor scroll for viewport sync
        JScrollPane editorScroll = null;
        if (scroll instanceof JScrollPane) editorScroll = (JScrollPane) scroll;
        else {
            // try to find enclosing scroll pane
            java.awt.Component comp = editor.getParent();
            while (comp != null && !(comp instanceof JScrollPane)) comp = comp.getParent();
            if (comp instanceof JScrollPane) editorScroll = (JScrollPane) comp;
        }

        if (editorScroll != null) {
            editorScroll.getVerticalScrollBar().addAdjustmentListener(e -> updateViewportHighlight());
        }

        editor.addCaretListener(e -> updateViewportHighlight());

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
            private void changed() { model.setModified(true); updateMinimap(); }
        });

        editor.getDocument().addUndoableEditListener(undoManager);

        // key bindings for undo/redo
        InputMap im = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = editor.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "undo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "redo");
        am.put("undo", new AbstractAction() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { if (undoManager.canUndo()) undoManager.undo(); } });
        am.put("redo", new AbstractAction() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { if (undoManager.canRedo()) undoManager.redo(); } });
    }

    public String getTitle() { return model.getDisplayName(); }
    public Icon getIcon() { return null; }

    public void setContent(String text) { editor.setText(text); model.setModified(false); }
    public String getContent() { return editor.getText(); }

    private void updateMinimap() {
        try {
            String t = editor.getText();
            if (t.length() > 1000000) {
                // avoid huge minimap for enormous files
                minimap.setText(t.substring(0, Math.min(t.length(), 500000)));
            } else minimap.setText(t);
            updateViewportHighlight();
        } catch (Throwable ignored) { }
    }

    public void markAsPartial(boolean p) {
        this.partial = p;
        if (p) setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        else setBorder(null);
    }

    private void updateViewportHighlight() {
        try {
            javax.swing.text.Document edDoc = editor.getDocument();
            javax.swing.text.Document miniDoc = minimap.getDocument();
            int edLen = edDoc.getLength();
            int miniLen = miniDoc.getLength();
            if (edLen == 0 || miniLen == 0) return;

            Rectangle vis = editor.getVisibleRect();
            int startOff = editor.viewToModel(new Point(0, vis.y));
            int endOff = editor.viewToModel(new Point(0, vis.y + vis.height - 1));

            float sRatio = (float) startOff / Math.max(1, edLen);
            float eRatio = (float) endOff / Math.max(1, edLen);
            int miniStart = Math.max(0, Math.min(miniLen, (int) (sRatio * miniLen)));
            int miniEnd = Math.max(0, Math.min(miniLen, (int) (eRatio * miniLen)));

            javax.swing.text.Highlighter h = minimap.getHighlighter();
            h.removeAllHighlights();
            javax.swing.text.Highlighter.HighlightPainter painter = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(new Color(0x99CCCCCC, true));
            if (miniStart < miniEnd) h.addHighlight(miniStart, miniEnd, painter);
        } catch (Throwable ignored) { }
    }

    /**
     * Set syntax style based on file extension (e.g. ".java", ".json", ".html").
     * Uses reflection to call RSyntaxTextArea.setSyntaxEditingStyle when available.
     */
    public void setSyntaxByPath(java.nio.file.Path p) {
        if (p == null) return;
        String name = p.getFileName().toString().toLowerCase();
        String ext = "";
        int i = name.lastIndexOf('.');
        if (i >= 0) ext = name.substring(i+1);
        String style = null;
        switch (ext) {
            case "java": style = "text/java"; break;
            case "json": style = "application/json"; break;
            case "html": case "htm": style = "text/html"; break;
            case "xml": style = "application/xml"; break;
            case "js": style = "application/javascript"; break;
            case "css": style = "text/css"; break;
            default: style = null; break;
        }
        if (style == null) return;
        try {
            Class<?> cls = Class.forName("org.fife.ui.rsyntaxtextarea.RSyntaxTextArea");
            if (cls.isInstance(editor)) {
                Method m = cls.getMethod("setSyntaxEditingStyle", String.class);
                m.invoke(editor, style);
            }
        } catch (Throwable t) {
            // ignore — fallback editor doesn't support syntax styles
        }
    }

    // helper API used by Find/Replace dialog
    public void findAndSelect(String toFind) {
        if (toFind == null || toFind.isEmpty()) return;
        String content = getContent();
        int idx = content.indexOf(toFind);
        if (idx >= 0) {
            editor.requestFocusInWindow();
            editor.select(idx, idx + toFind.length());
        }
    }

    public void replaceSelection(String replacement) { editor.replaceSelection(replacement); }

    public DocumentModel getModel() { return model; }
    public JTextComponent getEditorComponent() { return editor; }

}
