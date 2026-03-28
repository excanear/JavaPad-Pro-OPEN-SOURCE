package com.javapad.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Simple line number component for a text component.
 */
public class TextLineNumber extends JComponent implements DocumentListener {
    private final JTextPane textPane;

    public TextLineNumber(JTextPane textPane) {
        this.textPane = textPane;
        textPane.getDocument().addDocumentListener(this);
        setFont(textPane.getFont());
        setPreferredWidth();
    }

    private void setPreferredWidth() {
        int lines = Math.max(1, textPane.getDocument().getDefaultRootElement().getElementCount());
        int digits = String.valueOf(lines).length();
        FontMetrics fm = getFontMetrics(getFont());
        int width = 5 + fm.charWidth('0') * digits;
        setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle clip = g.getClipBounds();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        g.setColor(Color.BLACK);
        int start = textPane.viewToModel2D(new Point(0, clip.y));
        int end = textPane.viewToModel2D(new Point(0, clip.y + clip.height));
        int startLine = textPane.getDocument().getDefaultRootElement().getElementIndex(start);
        int endLine = textPane.getDocument().getDefaultRootElement().getElementIndex(end);
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int y = -textPane.getVisibleRect().y + fm.getAscent();
        for (int i = startLine; i <= endLine; i++) {
            String text = String.valueOf(i + 1);
            int x = getWidth() - 5 - fm.stringWidth(text);
            g.drawString(text, x, y + i * lineHeight);
        }
    }

    @Override public void insertUpdate(DocumentEvent e) { setPreferredWidth(); }
    @Override public void removeUpdate(DocumentEvent e) { setPreferredWidth(); }
    @Override public void changedUpdate(DocumentEvent e) { setPreferredWidth(); }
}
