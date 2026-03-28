package com.javapad.utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Very small, regex-based syntax highlighter for JTextPane.
 * Supports minimal Java/JSON/HTML keyword highlighting as a demo.
 */
public class SyntaxHighlighter {
    private static final Pattern JAVA_KEYWORDS = Pattern.compile("\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while)\b");
    private static final Pattern NUMBERS = Pattern.compile("\\b\\d+\\b");

    public static void highlight(JTextPane pane) {
        StyledDocument doc = pane.getStyledDocument();
        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            AppLogger.error("Highlight read failed", e);
            return;
        }

        // clear
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, pane.getForeground());
        doc.setCharacterAttributes(0, text.length(), normal, true);

        // keywords
        SimpleAttributeSet kw = new SimpleAttributeSet();
        StyleConstants.setForeground(kw, new Color(127, 0, 85));
        Matcher m = JAVA_KEYWORDS.matcher(text);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), kw, false);
        }

        // numbers
        SimpleAttributeSet num = new SimpleAttributeSet();
        StyleConstants.setForeground(num, new Color(0, 0, 192));
        m = NUMBERS.matcher(text);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), num, false);
        }
    }
}
