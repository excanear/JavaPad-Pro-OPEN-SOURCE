package com.javapad.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FindReplaceDialog extends JDialog {
    public FindReplaceDialog(JFrame owner, EditorTabbedPane tabs) {
        super(owner, "Buscar/Substituir", false);
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridLayout(3,2));
        p.add(new JLabel("Buscar:"));
        JTextField txtFind = new JTextField();
        p.add(txtFind);
        p.add(new JLabel("Substituir:"));
        JTextField txtReplace = new JTextField();
        p.add(txtReplace);
        JButton findBtn = new JButton("Find");
        JButton replaceBtn = new JButton("Replace");
        p.add(findBtn);
        p.add(replaceBtn);
        add(p, BorderLayout.CENTER);

        findBtn.addActionListener((ActionEvent e) -> {
            EditorTab tab = tabs.getCurrentTab();
            if (tab != null) {
                tab.findAndSelect(txtFind.getText());
            }
        });

        replaceBtn.addActionListener((ActionEvent e) -> {
            EditorTab tab = tabs.getCurrentTab();
            if (tab != null) {
                tab.replaceSelection(txtReplace.getText());
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
