package com.javapad.ui;

import com.javapad.core.Controller;
import com.javapad.utils.AppLogger;
import com.javapad.utils.ConfigManager;
import com.javapad.utils.ThemeManager;
import javax.swing.*;
import java.awt.*;

/**
 * Main application window. Implements menus, toolbar, status bar and tabbed editor area.
 */
public class MainFrame extends JFrame {
    private final EditorTabbedPane tabbedPane;
    private final Controller controller;
    private final com.javapad.utils.ShortcutsManager shortcuts = new com.javapad.utils.ShortcutsManager();

    public MainFrame() {
        super("JavaPad Pro");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        AppLogger.init();
        ConfigManager.load();
        ThemeManager.init();

        controller = new Controller(this);
        // register built-in example plugin
        com.javapad.plugins.PluginManager pm = new com.javapad.plugins.PluginManager();
        pm.register(new com.javapad.plugins.impl.ExamplePlugin());
        pm.initAll(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                controller.requestExit();
            }
        });

        tabbedPane = new EditorTabbedPane(controller);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(new StatusBar(tabbedPane), BorderLayout.SOUTH);
        setJMenuBar(MenuFactory.createMenuBar(this, controller, tabbedPane));
        applyShortcuts();
    }

    public void applyShortcuts() {
        KeyStroke findKs = shortcuts.get("find");
        KeyStroke replaceKs = shortcuts.get("replace");
        KeyStroke deleteKs = shortcuts.get("delete");
        JRootPane rp = getRootPane();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rp.getActionMap();
        if (findKs != null) im.put(findKs, "showFind");
        if (replaceKs != null) im.put(replaceKs, "showReplace");
        if (deleteKs != null) im.put(deleteKs, "deleteFile");
        am.put("showFind", new AbstractAction() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { new com.javapad.ui.FindReplaceDialog(MainFrame.this, tabbedPane); } });
        am.put("showReplace", new AbstractAction() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { new com.javapad.ui.FindReplaceDialog(MainFrame.this, tabbedPane); } });
        am.put("deleteFile", new AbstractAction() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { controller.deleteCurrent(); } });
    }

    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        JButton newBtn = new JButton("New");
        newBtn.addActionListener(e -> controller.newFile());
        bar.add(newBtn);

        JButton openBtn = new JButton("Open");
        openBtn.addActionListener(e -> controller.openFiles());
        bar.add(openBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> controller.saveCurrent());
        bar.add(saveBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> controller.deleteCurrent());
        bar.add(deleteBtn);

        bar.addSeparator();
        JButton theme = new JButton("Toggle Theme");
        theme.addActionListener(e -> ThemeManager.toggleTheme());
        bar.add(theme);

        return bar;
    }

    public EditorTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
