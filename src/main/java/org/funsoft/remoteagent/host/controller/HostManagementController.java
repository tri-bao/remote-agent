/**
 * 
 */
package org.funsoft.remoteagent.host.controller;

import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AbstractController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.host.view.HostDetailPanel;
import org.funsoft.remoteagent.host.view.HostManagementPanel;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.tag.controller.TagManagementController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

/**
 * @author htb
 *
 */
public class HostManagementController extends AbstractController<HostManagementPanel> {
	public void show() {
		setView(new HostManagementPanel());
		
		getView().showHostList(HostMamanger.getInstance().getAllHosts());
		
		initAction();
		
		GUIUtils.showInDialog(getView(), "MANAGE HOSTS");
	}
	
	private void initAction() {
		getView().getBtnAdd().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAdd();
			}
		});
		getView().getBtnEdit().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onEdit();
			}
		});
		getView().getBtnDelete().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		getView().getBtnManageTags().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onManageTags();
			}
		});
	}
	private void onAdd() {
		HostDetailPanel pnl = new HostDetailPanel();
		GUIUtils.showInDialog(pnl, "ADD NEW HOST", 600, 600);
		if (pnl.isCancel()) {
			return;
		}
		HostDto host = new HostDto();
		host.setUuid(UUID.randomUUID().toString());
		pnl.collectData(host);
		
		HostMamanger.getInstance().addOrUpdate(host);
		
		getView().addHost(host);
	}
	private void onEdit() {
		HostDto selectedHost = getView().getSelectedHost();
		if (selectedHost == null) {
			return;
		}
		
		HostDetailPanel pnl = new HostDetailPanel();
		pnl.fillDataToScreen(selectedHost);
		GUIUtils.showInDialog(pnl, "EDIT HOST", 600, 600);
		if (pnl.isCancel()) {
			return;
		}
		pnl.collectData(selectedHost);
		
		HostMamanger.getInstance().addOrUpdate(selectedHost);
		
		getView().updateSelectedHost(selectedHost);
	}
	private void onDelete() {
		HostDto selectedHost = getView().getSelectedHost();
		if (selectedHost == null) {
			return;
		}

		if (RemoteAgentGui.showConfirmationYesNo("Có chắc delete host: " + selectedHost.getHost() + "?")) {
			HostMamanger.getInstance().removeHost(selectedHost);
			getView().deleteSelectedHost();
		}
	}
	private void onManageTags() {
		TagManagementController ctrl = new TagManagementController();
		ctrl.show();
	}
}
