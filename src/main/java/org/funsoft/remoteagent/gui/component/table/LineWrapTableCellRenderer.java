package org.funsoft.remoteagent.gui.component.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.Serializable;

/**
 * @author Ho Tri Bao
 * 
 */
public class LineWrapTableCellRenderer extends JTextArea implements TableCellRenderer, Serializable {
    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
    public LineWrapTableCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());
            
        if (value != null) {
            setText(value.toString());
        } else {
            setText("");
        }
        return this;
    }
}
