/**
 * 
 */
package org.funsoft.remoteagent.tag.view;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.table.AbstractTableModel;
import org.funsoft.remoteagent.tag.dto.TagDto;

/**
 * @author htb
 *
 */
public class TagTableModel extends AbstractTableModel<TagDto> {
	private final boolean editable;
	
	public TagTableModel(boolean editable) {
		this.editable = editable;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editable;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		TagDto rowData = getRowData(rowIndex);
		if (rowData == null) {
			return;
		}
		switch (columnIndex) {
		case 0:
			rowData.setName(StringUtils.stripToNull((String) aValue));
			return;
		case 1:
			rowData.setDescription(StringUtils.stripToNull((String) aValue));
			return;
		}
	}

	@Override
	protected String[] getAllColumnNames() {
		return new String[] {"Tag name", "Description"};
	}

	@Override
	protected Class<?>[] getAllColumnClasses() {
		return new Class[] {String.class, String.class};
	}

	@Override
	protected int[] getAllColumnSizes() {
		return new int[] {180, 388};
	}

	@Override
	protected Object getValueAtInternal(int rowIndex, int columnIndex) {
		TagDto rowData = getRowData(rowIndex);
		switch (columnIndex) {
		case 0:
			return rowData.getName();
		case 1:
			return rowData.getDescription();
		}
		return null;
	}

}
