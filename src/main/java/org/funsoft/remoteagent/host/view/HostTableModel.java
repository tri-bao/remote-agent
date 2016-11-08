/**
 *
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.table.AbstractTableModel;
import org.funsoft.remoteagent.host.dto.HostDto;

/**
 * @author htb
 */
public class HostTableModel extends AbstractTableModel<HostDto> {
    @Override
    protected String[] getAllColumnNames() {
        return new String[]{"Name", "Public IP/DNS", "Internal DNS names", "Tags", "Description"};
    }

    @Override
    protected Class<?>[] getAllColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, String.class};
    }

    @Override
    protected int[] getAllColumnSizes() {
        return new int[]{40, 30, 30, 10, 20};
    }

    @Override
    protected Object getValueAtInternal(int rowIndex, int columnIndex) {
        HostDto rowData = getRowData(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowData.getDisplayName();
            case 1:
                return rowData.getHost();
            case 2:
                return StringUtils.join(rowData.getInternalDns(), "\n");
            case 3:
                return StringUtils.join(rowData.getTags(), "\n");
            case 4:
                return rowData.getDescription();
        }
        return null;
    }

}
