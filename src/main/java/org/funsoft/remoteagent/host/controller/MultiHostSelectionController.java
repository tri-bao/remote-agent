/**
 * 
 */
package org.funsoft.remoteagent.host.controller;

import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AbstractController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.host.view.MultiHostSelectionPanel;

import java.util.List;

/**
 * @author htb
 *
 */
public class MultiHostSelectionController extends AbstractController<MultiHostSelectionPanel> {
	public List<HostDto> select() {
		return select(null);
	}
	public List<HostDto> select(List<HostDto> selectedHosts) {
		setView(new MultiHostSelectionPanel());
		getView().setAvailableRows(HostMamanger.getInstance().getAllHosts(), selectedHosts);
		
		GUIUtils.showInDialog(getView(), "SELECT HOSTS");
		
		if (getView().isCancel()) {
			return null;
		}
		return getView().getSelectedRows();
	}
}
