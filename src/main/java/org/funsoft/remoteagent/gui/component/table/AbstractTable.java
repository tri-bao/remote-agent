/**
 * 
 */
package org.funsoft.remoteagent.gui.component.table;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * @author htb
 *
 */
public abstract class AbstractTable extends JTable {
	public AbstractTable() {
        setRowHeight(22);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFocusable(false);
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        resetPreferredColumnSize();
    }

    /**
     * Resets preferred size of all columns. The size is get from the model.
     * If there is a size of zero, this size is ignored.
     */
    private void resetPreferredColumnSize() {
        if (!(dataModel instanceof AbstractTableModel)) {
            return;
        }
        TableColumn column = null;
        AbstractTableModel<?> tableModel = (AbstractTableModel<?>)getModel();
        int numCol = getColumnCount();
        for (int i = 0; i < numCol; i++) {
            column = getColumnModel().getColumn(i);
            if (tableModel.getColumnSize(i) > 0) {
                column.setPreferredWidth(tableModel.getColumnSize(i));
            }
        }
    }
}
