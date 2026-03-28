package com.javapad.ui;

import com.javapad.core.Controller;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuFactory {
    public static JMenuBar createMenuBar(JFrame owner, Controller controller, EditorTabbedPane tabs) {
        JMenuBar mb = new JMenuBar();

        JMenu file = new JMenu("Arquivo");
        file.setMnemonic(KeyEvent.VK_A);
        JMenuItem newIt = new JMenuItem("Novo");
        newIt.addActionListener(e -> controller.newFile());
        file.add(newIt);

        JMenuItem open = new JMenuItem("Abrir");
        open.addActionListener(e -> controller.openFiles());
        file.add(open);

        JMenu recentMenu = new JMenu("Recentes");
        file.add(recentMenu);
        // populate recent files lazily when menu is opened
        recentMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override public void menuSelected(javax.swing.event.MenuEvent e) {
                recentMenu.removeAll();
                for (java.nio.file.Path p : controller.getRecentFiles()) {
                    JMenuItem it = new JMenuItem(p.getFileName().toString());
                    it.addActionListener(a -> controller.openFileAsync(p));
                    recentMenu.add(it);
                }
            }
            @Override public void menuDeselected(javax.swing.event.MenuEvent e) {}
            @Override public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });

        JMenuItem save = new JMenuItem("Salvar");
        save.addActionListener(e -> controller.saveCurrent());
        file.add(save);

        JMenuItem delete = new JMenuItem("Excluir");
        delete.addActionListener(e -> controller.deleteCurrent());
        file.add(delete);

        file.addSeparator();
        JMenuItem exit = new JMenuItem("Sair");
        exit.addActionListener(e -> owner.dispose());
        file.add(exit);

        JMenu edit = new JMenu("Editar");
        JMenuItem find = new JMenuItem("Buscar / Substituir");
        find.addActionListener(e -> new FindReplaceDialog(owner, tabs));
        edit.add(find);

        JMenuItem prefs = new JMenuItem("Preferências");
        prefs.addActionListener(e -> new com.javapad.ui.PreferencesDialog(owner, controller));
        edit.add(prefs);

        mb.add(file);
        mb.add(edit);

        JMenu view = new JMenu("Visualizar");
        mb.add(view);

        JMenu help = new JMenu("Ajuda");
        mb.add(help);

        return mb;
    }
}
