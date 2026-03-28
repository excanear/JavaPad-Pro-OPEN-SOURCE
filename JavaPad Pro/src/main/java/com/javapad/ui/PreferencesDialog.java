package com.javapad.ui;

import com.javapad.core.Controller;
import com.javapad.utils.ConfigManager;
import com.javapad.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class PreferencesDialog extends JDialog {
    public PreferencesDialog(JFrame owner, Controller controller) {
        super(owner, "Preferências", true);
        setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(0,2,6,6));

        JCheckBox autosaveChk = new JCheckBox("Habilitar Auto-save");
        autosaveChk.setSelected(ConfigManager.getBoolean("autosave.enabled", false));
        p.add(autosaveChk);
        p.add(new JLabel());

        p.add(new JLabel("Intervalo auto-save (s):"));
        JSpinner interval = new JSpinner(new SpinnerNumberModel(ConfigManager.getInt("autosave.interval.seconds", 60), 5, 3600, 5));
        p.add(interval);

        p.add(new JLabel("Tema:"));
        JComboBox<String> theme = new JComboBox<>(new String[] {"LIGHT","DARK"});
        theme.setSelectedItem(ThemeManager.getCurrent().name());
        p.add(theme);

        p.add(new JLabel("Tamanho da fonte (editor):"));
        JSpinner fontSize = new JSpinner(new SpinnerNumberModel(ConfigManager.getInt("editor.font.size", 14), 10, 36, 1));
        p.add(fontSize);

        p.add(new JLabel("Atalho Buscar (ex: ctrl F):"));
        JTextField findKey = new JTextField(ConfigManager.get("shortcut.find", "ctrl F"));
        p.add(findKey);

        p.add(new JLabel("Atalho Substituir (ex: ctrl H):"));
        JTextField replaceKey = new JTextField(ConfigManager.get("shortcut.replace", "ctrl H"));
        p.add(replaceKey);

        add(p, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Salvar");
        JButton cancel = new JButton("Cancelar");
        btns.add(ok); btns.add(cancel);
        add(btns, BorderLayout.SOUTH);

        ok.addActionListener(e -> {
            boolean as = autosaveChk.isSelected();
            int sec = (Integer) interval.getValue();
            ConfigManager.set("autosave.enabled", String.valueOf(as));
            ConfigManager.set("autosave.interval.seconds", String.valueOf(sec));
            ConfigManager.set("editor.font.size", String.valueOf(fontSize.getValue()));
            String t = (String) theme.getSelectedItem();
            ThemeManager.setTheme(ThemeManager.Theme.valueOf(t));
            // shortcuts
            String fk = findKey.getText().trim();
            String rk = replaceKey.getText().trim();
            if (!fk.isEmpty()) ConfigManager.set("shortcut.find", fk);
            if (!rk.isEmpty()) ConfigManager.set("shortcut.replace", rk);
            ConfigManager.save();
            // apply autosave via controller
            controller.setAutosaveEnabled(as);
            controller.setAutosaveInterval(sec);
            // update UI theme
            SwingUtilities.updateComponentTreeUI(owner);
            // reapply shortcuts in main frame
            if (owner instanceof MainFrame) ((MainFrame) owner).applyShortcuts();
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
