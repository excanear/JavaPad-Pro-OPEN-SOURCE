package com.javapad.ui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final JLabel posLabel = new JLabel("Ln 1, Col 1");
    private final JLabel infoLabel = new JLabel("Ready");

    public StatusBar(EditorTabbedPane tabs) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        add(posLabel, BorderLayout.WEST);
        add(infoLabel, BorderLayout.EAST);

        Timer t = new Timer(300, e -> {
            EditorTab tab = tabs.getCurrentTab();
            if (tab != null) {
                posLabel.setText(tab.getTitle());
            } else {
                posLabel.setText("No file");
            }
        });
        t.start();
    }
}
