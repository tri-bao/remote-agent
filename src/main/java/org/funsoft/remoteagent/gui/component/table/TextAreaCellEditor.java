/**
 * 
 */
package org.funsoft.remoteagent.gui.component.table;

import org.funsoft.remoteagent.gui.component.GUIUtils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author htb
 *
 */
public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
	private final JTextArea txe = GUIUtils.newTextArea();
	public TextAreaCellEditor() {
		GUIUtils.makeFontBold(txe);
		GUIUtils.preventInputingNewLineOnTextArea(txe, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
			}
		});
	}
	
	@Override
	public Object getCellEditorValue() {
		return GUIUtils.getText(txe);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column) {
		txe.setText(value == null ? null : value.toString());
		return txe;
	}

}
