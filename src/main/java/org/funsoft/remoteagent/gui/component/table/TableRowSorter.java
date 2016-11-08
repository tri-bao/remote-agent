/*
 * HTBTableRowSorter.java
 *
 * Project: qlhk
 *
 * Margin: 100 characters.
 *
 * $Source: $
 * $Revision:  $
 * ----------------------------------------------------------------------------
 * WHEN           WHO     VER     DESCRIPTION
 * Oct 25, 2011     HTB     1.0     Creation
 * ----------------------------------------------------------------------------
 */
package org.funsoft.remoteagent.gui.component.table;

import javax.swing.table.TableModel;

/**
 * @author Ho Tri Bao
 */
public class TableRowSorter<M extends TableModel> extends javax.swing.table.TableRowSorter<M> {

    public TableRowSorter() {
        super();
    }

    public TableRowSorter(M model) {
        super(model);
    }

    @Override
    public void rowsDeleted(int firstRow, int endRow) {
        // to avoid index out of bound exception when a row is remvoved from
        // model.
        if ((firstRow >= getModelWrapper().getRowCount())
                || (endRow >= getModelWrapper().getRowCount())) {
            return;
        }
        super.rowsDeleted(firstRow, endRow);
    }
}
