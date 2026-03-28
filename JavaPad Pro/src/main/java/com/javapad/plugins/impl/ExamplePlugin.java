package com.javapad.plugins.impl;

import com.javapad.plugins.Plugin;
import com.javapad.ui.MainFrame;
import javax.swing.*;

/**
 * Small example plugin demonstrating Plugin API.
 */
public class ExamplePlugin implements Plugin {
    @Override
    public void init(MainFrame frame) {
        // add a sample menu item under Help
        JMenuItem it = new JMenuItem("Example: About Plugin");
        it.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Example Plugin Active"));
        // attempt to find Help menu
        JMenuBar mb = frame.getJMenuBar();
        if (mb != null) {
            for (int i = 0; i < mb.getMenuCount(); i++) {
                JMenu m = mb.getMenu(i);
                if (m != null && "Ajuda".equals(m.getText())) {
                    m.addSeparator();
                    m.add(it);
                    return;
                }
            }
            // if not found, add a Help menu
            JMenu help = new JMenu("Ajuda");
            help.add(it);
            mb.add(help);
        }
    }

    @Override
    public String getName() {
        return "ExamplePlugin";
    }
}
