/**
 * 
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.table.AbstractTableModel;
import org.funsoft.remoteagent.host.dto.HostDto;

/**
 * @author htb
 *
 */
public class HostSelectionTableModel extends AbstractTableModel<HostDto> {
	@Override
	protected String[] getAllColumnNames() {
		return new String[] {"Name", "Internal DNS names", "Tags/Description"};
	}

	@Override
	protected Class<?>[] getAllColumnClasses() {
		return new Class[] {String.class, String.class, String.class, String.class};
	}

	@Override
	protected int[] getAllColumnSizes() {
		return new int[] {40, 30, 30};
	}

	@Override
	protected Object getValueAtInternal(int rowIndex, int columnIndex) {
		HostDto rowData = getRowData(rowIndex);
		switch (columnIndex) {
		case 0:
			return rowData.getDisplayName();
		case 1:
			return StringUtils.join(rowData.getInternalDns(), "\n");
		case 2:
			String des = StringUtils.join(rowData.getTags(), "\n");
			if (StringUtils.isNotBlank(des) && StringUtils.isNotBlank(rowData.getDescription())) {
				return des + "\n======\n" + rowData.getDescription();
			} else if (StringUtils.isNotBlank(des)) {
				return des;
			} else {
				return rowData.getDescription();
			}
		}
		return null;
	}

}
