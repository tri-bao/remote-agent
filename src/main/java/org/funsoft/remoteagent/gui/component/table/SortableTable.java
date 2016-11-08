package org.funsoft.remoteagent.gui.component.table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class SortableTable<D, M extends AbstractTableModel<D>> extends LineWrapTable {
    private RowFilter<M, Integer> tableFilter;
    
    public SortableTable() {
        super(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }
    
    public SortableTable(int autoHeightColumn, boolean boldFont) {
        super(autoHeightColumn, boldFont);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    public RowFilter<M, Integer> getTableFilter() {
        return tableFilter;
    }

    public void setTableFilter(RowFilter<M, Integer> tableFilter) {
        this.tableFilter = tableFilter;
        TableRowSorter<M> rowSorter = ((TableRowSorter<M>) getRowSorter());
        if (rowSorter != null) {
        	rowSorter.setRowFilter(tableFilter);
        }
    }

    public M getSortableModel() {
        return (M) super.getModel();
    }

    public void setModel(M model) {
        super.setModel(model);
    }

    public void showResult(List<D> results) {
        setRowSorter(null);
        getSortableModel().removeAllRowData();
        getSortableModel().addAllRowData(results);

        reinitRowSorter();

        selectFirstRow();
    }

    private void reinitRowSorter() {
        if (ArrayUtils.isNotEmpty(getSortableModel().getSortableColumns())) {
            TableRowSorter<M> sorter =
                    new TableRowSorter<>(getSortableModel());
            for (int col = 0; col < getSortableModel().getColumnCount(); col++) {
                sorter.setSortable(col, false);
            }
            for (int col : getSortableModel().getSortableColumns()) {
                sorter.setSortable(col, true);
            }
            sorter.setRowFilter(tableFilter);
            setRowSorter(sorter);
        }
    }
    
    public void addData(D data) {
        getSortableModel().addRowData(data);
        
    }
    public void updateData(D data, int modelIndex) {
        getSortableModel().updateDataRow(modelIndex, data);
    }
    public void removeData(int modelIndex) {
        setRowSorter(null);
        getSortableModel().removeRowData(modelIndex);
        reinitRowSorter();
    }
    public void selectFirstRow() {
        if (CollectionUtils.isEmpty(getSortableModel().getAllRowData())) {
            return;
        }
        ListSelectionModel selectionModel = getSelectionModel();
        selectionModel.setSelectionInterval(0, 0);
    }

    public D getSelectedRowData() {
        int rowId = getSelectedRowConvertedToModelIndex();
        if (rowId < 0) {
            return null;
        }
        return getSortableModel().getRowData(rowId);
    }
    
    public void removeSelectedRow() {
        int rowId = getSelectedRowConvertedToModelIndex();
        if (rowId < 0) {
            return;
        }
        removeData(rowId);
    }
    public int getSelectedRowConvertedToModelIndex() {
        int viewIdx = getSelectedRow();
        if (viewIdx < 0) {
            return viewIdx;
        }
        return convertRowIndexToModel(viewIdx);
    }
}