package com.javapad.core;

import com.javapad.ui.EditorTab;
import com.javapad.ui.EditorTabbedPane;
import com.javapad.ui.MainFrame;
import com.javapad.utils.AppLogger;
import com.javapad.utils.RecentFilesManager;
import com.javapad.persistence.FileManager;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import com.javapad.utils.ConfigManager;

/**
 * Application controller - mediates between UI and persistence.
 */
public class Controller {
    private final MainFrame frame;
    private final ExecutorService bg = Executors.newSingleThreadExecutor();
    private final RecentFilesManager recentFiles = new RecentFilesManager();

    public Controller(MainFrame frame) {
        this.frame = frame;
        startAutosaveIfEnabled();
    }

    public void newFile() {
        DocumentModel model = new DocumentModel(null, "Untitled");
        EditorTab tab = new EditorTab(model);
        frame.getTabbedPane().addEditorTab(tab);
    }

    public void openFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int res = chooser.showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            java.io.File[] files = chooser.getSelectedFiles();
            for (java.io.File f : files) {
                openFileAsync(f.toPath());
                recentFiles.add(f.toPath());
            }
        }
    }

    public void openFileAsync(Path p) {
        bg.submit(() -> {
            try {
                // read file in background with progress; handle large files by partial load
                long size = java.nio.file.Files.size(p);
                final long LARGE_THRESHOLD = 50L * 1024L * 1024L; // 50MB
                if (size > LARGE_THRESHOLD) {
                    // load partial preview
                    String preview = FileManager.readPartial(p, 2 * 1024 * 1024); // 2MB preview
                    SwingUtilities.invokeLater(() -> {
                        DocumentModel model = new DocumentModel(p, p.getFileName().toString() + " (preview)");
                        EditorTab tab = new EditorTab(model);
                        tab.setContent(preview);
                        tab.markAsPartial(true);
                        try { tab.setSyntaxByPath(p); } catch (Throwable ignored) {}
                        frame.getTabbedPane().addEditorTab(tab);
                        int opt = JOptionPane.showConfirmDialog(frame, "Arquivo grande (" + (size/1024/1024) + " MB). Carregar tudo na memória?", "Arquivo grande", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            bg.submit(() -> {
                                try {
                                    String content = FileManager.readAll(p);
                                    SwingUtilities.invokeLater(() -> {
                                        tab.setContent(content);
                                        tab.markAsPartial(false);
                                    });
                                } catch (Exception ex) {
                                    AppLogger.error("Failed to load full file " + p, ex);
                                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Falha ao carregar arquivo completo: " + ex.getMessage()));
                                }
                            });
                        }
                    });
                } else {
                    // normal small file
                    String content = FileManager.readAll(p);
                    SwingUtilities.invokeLater(() -> {
                        DocumentModel model = new DocumentModel(p, p.getFileName().toString());
                        EditorTab tab = new EditorTab(model);
                        tab.setContent(content);
                        // set syntax highlighting by file type when possible
                        try { tab.setSyntaxByPath(p); } catch (Throwable ignored) {}
                        frame.getTabbedPane().addEditorTab(tab);
                    });
                }
            } catch (Exception ex) {
                AppLogger.error("Failed to open " + p, ex);
                JOptionPane.showMessageDialog(frame, "Erro ao abrir arquivo: " + ex.getMessage());
            }
        });
    }

    public java.util.List<java.nio.file.Path> getRecentFiles() { return recentFiles.getRecent(); }

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> autosaveFuture;

    private void startAutosaveIfEnabled() {
        boolean enabled = Boolean.parseBoolean(ConfigManager.get("autosave.enabled", "false"));
        int interval = Integer.parseInt(ConfigManager.get("autosave.interval.seconds", "60"));
        if (enabled) startAutosave(interval);
    }

    public void startAutosave(int seconds) {
        stopAutosave();
        autosaveFuture = scheduler.scheduleAtFixedRate(() -> {
            try { autosaveOnce(); } catch (Throwable t) { AppLogger.error("Autosave failed", t); }
        }, seconds, seconds, TimeUnit.SECONDS);
        AppLogger.log("Autosave started: " + seconds + "s");
    }

    public void setAutosaveEnabled(boolean enabled) {
        ConfigManager.set("autosave.enabled", String.valueOf(enabled));
        ConfigManager.save();
        if (enabled) {
            int interval = ConfigManager.getInt("autosave.interval.seconds", 60);
            startAutosave(interval);
        } else {
            stopAutosave();
        }
    }

    public void setAutosaveInterval(int seconds) {
        ConfigManager.set("autosave.interval.seconds", String.valueOf(seconds));
        ConfigManager.save();
        // restart if running
        boolean enabled = Boolean.parseBoolean(ConfigManager.get("autosave.enabled", "false"));
        if (enabled) startAutosave(seconds);
    }

    public void stopAutosave() {
        if (autosaveFuture != null) autosaveFuture.cancel(false);
    }

    private void autosaveOnce() {
        java.util.List<com.javapad.ui.EditorTab> tabs = frame.getTabbedPane().getAllTabs();
        for (com.javapad.ui.EditorTab tab : tabs) {
            com.javapad.core.DocumentModel m = tab.getModel();
            if (m.isModified() && m.getPath() != null) {
                String content = tab.getContent();
                bg.submit(() -> {
                    try { FileManager.writeAll(m.getPath(), content); SwingUtilities.invokeLater(() -> m.setModified(false)); }
                    catch (Exception e) { AppLogger.error("Autosave write failed: " + m.getPath(), e); }
                });
            }
        }
    }

    public void requestExit() {
        // check modified
        java.util.List<com.javapad.ui.EditorTab> tabs = frame.getTabbedPane().getAllTabs();
        boolean hasModified = false;
        for (com.javapad.ui.EditorTab t : tabs) { if (t.getModel().isModified()) { hasModified = true; break; } }
        if (hasModified) {
            int opt = javax.swing.JOptionPane.showConfirmDialog(frame, "Existem arquivos não salvos. Deseja sair mesmo assim?", "Confirmar saída", javax.swing.JOptionPane.YES_NO_OPTION);
            if (opt != javax.swing.JOptionPane.YES_OPTION) return;
        }
        stopAutosave();
        scheduler.shutdownNow();
        bg.shutdownNow();
        frame.dispose();
    }

    public void saveCurrent() {
        EditorTab tab = frame.getTabbedPane().getCurrentTab();
        if (tab == null) return;
        DocumentModel m = tab.getModel();
        if (m.getPath() == null) {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showSaveDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                m.setPath(chooser.getSelectedFile().toPath());
                m.setDisplayName(chooser.getSelectedFile().getName());
            } else return;
        }
        Path p = m.getPath();
        String content = tab.getContent();
        bg.submit(() -> {
            try {
                FileManager.writeAll(p, content);
                SwingUtilities.invokeLater(() -> m.setModified(false));
            } catch (Exception ex) {
                AppLogger.error("Failed to save " + p, ex);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Erro ao salvar: " + ex.getMessage()));
            }
        });
    }

    /**
     * Delete the file associated with the current tab. If the document is unsaved
     * (no path) the tab is simply closed after confirmation. Otherwise the file
     * is removed from disk (background) and the tab closed on success.
     */
    public void deleteCurrent() {
        EditorTab tab = frame.getTabbedPane().getCurrentTab();
        if (tab == null) return;
        DocumentModel m = tab.getModel();
        if (m.getPath() == null) {
            int opt = JOptionPane.showConfirmDialog(frame, "Fechar aba não salva?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                frame.getTabbedPane().removeEditorTab(tab);
            }
            return;
        }
        Path p = m.getPath();
        int opt = JOptionPane.showConfirmDialog(frame, "Deseja excluir o arquivo " + p.getFileName() + " do disco? Esta ação não pode ser desfeita.", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;

        // perform delete in background
        bg.submit(() -> {
            try {
                FileManager.delete(p);
                recentFiles.remove(p);
                SwingUtilities.invokeLater(() -> {
                    frame.getTabbedPane().removeEditorTab(tab);
                    JOptionPane.showMessageDialog(frame, "Arquivo excluído: " + p.getFileName());
                });
            } catch (Exception ex) {
                AppLogger.error("Failed to delete " + p, ex);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Erro ao excluir: " + ex.getMessage()));
            }
        });
    }
}
