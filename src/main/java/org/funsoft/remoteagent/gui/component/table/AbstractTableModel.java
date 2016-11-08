/**
 * 
 */
package org.funsoft.remoteagent.gui.component.table;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author htb
 * 
 */
public abstract class AbstractTableModel<T> extends javax.swing.table.AbstractTableModel {

	private final String[] columnNames;
	private final Class<?>[] columnClasses;
	private final int[] columnSizes;
	private final List<T> dataList = new ArrayList<>();

	public AbstractTableModel() {
		columnNames = getAllColumnNames();
		columnClasses = getAllColumnClasses();
		columnSizes = getAllColumnSizes();
	}
	
	protected abstract String[] getAllColumnNames();
	protected abstract Class<?>[] getAllColumnClasses();
	/**
	 * @return sizes in percentage
	 */
	protected abstract int[] getAllColumnSizes();
	
	public int[] getSortableColumns() {
		int[] cl = new int[getColumnCount()];
		for (int i = 0; i < getColumnCount(); i++) {
			cl[i] = i;
		}
		return cl;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnClasses != null) {
			return columnClasses[columnIndex];
		} else {
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		if (columnNames != null) {
			return columnNames[column];
		} else {
			return super.getColumnName(column);
		}
	}

	@Override
	public int getRowCount() {
		if (dataList == null) {
			return 0;
		}
		return dataList.size();
	}

	@Override
	public int getColumnCount() {
		if (columnNames != null) {
			return columnNames.length;
		} else {
			return 0;
		}
	}

	@Override
	public final Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount()) {
			return getValueAtInternal(rowIndex, columnIndex);
		}
		return null;
	}

	protected abstract Object getValueAtInternal(int rowIndex, int columnIndex);

	public int getColumnSize(int columnIndex) {
		if (columnSizes != null) {
			if (columnIndex > columnSizes.length) {
				return 0;
			}
			return columnSizes[columnIndex];
		}
		return 0;
	}


	public void addRowData(T data) {
//		int lastId = dataList.size();
		dataList.add(data);
		
		// ArrayIndexOutOfBound if rows are filtered
		// fireTableRowsInserted(lastId, lastId);
		fireTableDataChanged();
	}


	public void addAllRowData(List<T> data) {
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
//		int lastId = dataList.size();
		dataList.addAll(data);
		
		// ArrayIndexOutOfBound if rows are filtered
		//fireTableRowsInserted(lastId, dataList.size() - 1);
		fireTableDataChanged();
	}

	/**
	 * Adds new data for all rows.
	 * 
	 * @param data
	 */
	public void removeAllRowData() {
		// int lastId = m_dataList.size() - 1;
		// m_dataList.clear();
		// if (lastId >= 0) {
		// fireTableRowsDeleted(0, lastId);
		// }
		int lastId = dataList.size() - 1;
		dataList.clear();
		if (lastId >= 0) {
			// use fireTableDataChanged(); to workaround the following exception
			// java.lang.NegativeArraySizeException
			// at javax.swing.SizeSequence.removeEntries(SizeSequence.java:381)
			// at javax.swing.JTable.tableRowsDeleted(JTable.java:4497)
			// at javax.swing.JTable.tableChanged(JTable.java:4396)
			fireTableDataChanged();

			// fireTableRowsDeleted(0, lastId);
		}
	}

	public void removeRowData(int idx) {
		if ((idx >= 0) && (idx < dataList.size())) {
			dataList.remove(idx);
			// ArrayIndexOutOfBound if rows are filtered
//			fireTableRowsDeleted(idx, idx);
		}
		fireTableDataChanged();
	}

	public void removeRowData(Object data) {
		int index = dataList.indexOf(data);
		removeRowData(index);
	}

	public T getRowData(int rowIdx) {
		if ((rowIdx >= 0) && (rowIdx < dataList.size())) {
			return dataList.get(rowIdx);
		} else {
			return null;
		}
	}

	public void updateDataRow(int rowIdx, T data) {
		if ((rowIdx >= 0) && (rowIdx < dataList.size())) {
			dataList.set(rowIdx, data);
			fireTableRowsUpdated(rowIdx, rowIdx);
		} else {
			return;
		}
	}

	public List<T> getAllRowData() {
		return dataList;
	}
}
