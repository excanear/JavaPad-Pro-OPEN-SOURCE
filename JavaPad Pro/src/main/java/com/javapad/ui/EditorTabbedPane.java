package com.javapad.ui;

import com.javapad.core.Controller;
import javax.swing.*;
import java.awt.*;

public class EditorTabbedPane extends JTabbedPane {
    private final Controller controller;

    public EditorTabbedPane(Controller controller) {
        super();
        this.controller = controller;
    }

    public void addEditorTab(EditorTab tab) {
        addTab(tab.getTitle(), tab.getIcon(), tab);
        setSelectedComponent(tab);
    }

    public EditorTab getCurrentTab() {
        Component c = getSelectedComponent();
        if (c instanceof EditorTab) return (EditorTab)c;
        return null;
    }

    public java.util.List<EditorTab> getAllTabs() {
        java.util.List<EditorTab> list = new java.util.ArrayList<>();
        for (int i = 0; i < getTabCount(); i++) {
            Component c = getComponentAt(i);
            if (c instanceof EditorTab) list.add((EditorTab)c);
        }
        return list;
    }

    public void removeEditorTab(EditorTab tab) {
        for (int i = 0; i < getTabCount(); i++) {
            Component c = getComponentAt(i);
            if (c == tab) {
                removeTabAt(i);
                // select adjacent tab if any
                if (getTabCount() > 0) setSelectedIndex(Math.max(0, i-1));
                return;
            }
        }
    }
}
