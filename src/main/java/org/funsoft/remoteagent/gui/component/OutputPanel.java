package org.funsoft.remoteagent.gui.component;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * @author Ho Tri Bao
 *
 */
public class OutputPanel extends JPanel {
    private final JScrollPane scrOutput = new JScrollPane();
    private final JTextPane txpOutput = new JTextPane();
    private final StyledDocument document = (StyledDocument) txpOutput.getDocument();
    private Style errorStyle;
    
    public OutputPanel() {
        initState();
        initAction();
        initLayout();
    }

    private void initState() {
        txpOutput.setEditable(false);
        txpOutput.setBackground(Color.BLACK);
        txpOutput.setForeground(Color.WHITE);
        errorStyle = document.addStyle("error", null);
        StyleConstants.setForeground(errorStyle, Color.RED);
    }

    private void initAction() {
    }

    private void initLayout() {
        scrOutput.setViewportView(txpOutput);
        setLayout(new BorderLayout());
        add(scrOutput, BorderLayout.CENTER);
    }

    public void displayMessage(Object... msgToJoin) {
        try {
            StringBuilder bd = new StringBuilder();
            for (Object object : msgToJoin) {
                if (object instanceof Object[]) {
                    for (Object subOnj : (Object[]) object) {
                        bd.append(subOnj);
                    }
                } else {
                    bd.append(object);
                }
            }
            final String msg = bd.toString();
            
            document.insertString(document.getLength(), msg + "\n", null);
            forceVirticalScrollDown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void displayError(Object... msgToJoin) {
        try {
            StringBuilder bd = new StringBuilder();
            for (Object object : msgToJoin) {
                if (object instanceof Object[]) {
                    for (Object subOnj : (Object[]) object) {
                        bd.append(subOnj);
                    }
                } else {
                    bd.append(object);
                }
            }
            final String error = bd.toString();
            
            document.insertString(document.getLength(), error + "\n", errorStyle);
            forceVirticalScrollDown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void flush() {
        System.out.flush();
    }
    private void forceVirticalScrollDown() {
        // run in invokeLater in order to avoid
        //  java.lang.ClassCastException: sun.java2d.NullSurfaceData cannot be cast to sun.java2d.d3d.D3DSurfaceData
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrOutput.getVerticalScrollBar().setValue(scrOutput.getVerticalScrollBar().getMaximum());
            }
        });
    }
}
