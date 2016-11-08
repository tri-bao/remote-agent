/**
 * 
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.collections.CollectionUtils;
import org.funsoft.remoteagent.gui.component.AbstractDoubleListSelectionPanel;
import org.funsoft.remoteagent.gui.component.table.ScrollableSortablePanel;
import org.funsoft.remoteagent.host.dto.HostDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author htb
 *
 */
public class MultiHostSelectionPanel
extends AbstractDoubleListSelectionPanel<HostTablePanel<HostSelectionTableModel>,
			HostDto, ScrollableSortablePanel<HostDto, HostSelectionTableModel>> {
	
	@Override
	protected HostTablePanel<HostSelectionTableModel> createAvailabelPanel() {
		return new HostTablePanel<>(new HostSelectionTableModel());
	}
	@Override
	protected HostTablePanel<HostSelectionTableModel> createSelectedPanel() {
		return new HostTablePanel<>(new HostSelectionTableModel(), false);
	}
	@Override
	protected ScrollableSortablePanel<HostDto, HostSelectionTableModel> getScrollableTable(
			HostTablePanel<HostSelectionTableModel> tblPnl) {
		return tblPnl.getPnlHost();
	}
	
	@Override
	public void setAvailableRows(List<HostDto> avais, List<HostDto> selected) {
		if (CollectionUtils.isEmpty(selected)) {
			pnlAvailable.showHostList(avais);
			return;
		}
		List<HostDto> a = new ArrayList<>(avais);
		a.removeAll(selected);
		pnlAvailable.showHostList(a);
		
		pnlSelected.showHostList(selected);
	}

}
