/**
 * 
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author htb
 * 
 */
public class HostSelectionPanel extends JPanel {

	private final HostTablePanel<HostSelectionTableModel> pnlHost = new HostTablePanel<>(
			new HostSelectionTableModel());

	private final HostDetailPanel pnlConnection = new HostDetailPanel();

	private final String installerName;

	public HostSelectionPanel() {
		this(null);
	}

	public HostSelectionPanel(String installerName) {
		this.installerName = installerName;
		initState();
		initAction();
		initLayout();
	}

	private void initState() {
		pnlConnection.setForMakingConnection();
		pnlConnection.getBtnOK().setEnabled(false);
		pnlConnection.getBtnOK().setText("Connect");
	}

	private void initAction() {
		ActionListener[] als = pnlConnection.getBtnOK().getActionListeners();
		for (ActionListener actionListener : als) {
			pnlConnection.getBtnOK().removeActionListener(actionListener);
		}
		pnlHost.getPnlHost().getTable().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) {
							return;
						}
						HostDto sel = pnlHost.getPnlHost().getSelectedRowData();
						pnlConnection.getBtnOK().setEnabled(sel != null);
						displayHost(sel);
					}
				});
	}

	private void initLayout() {
		JPanel pnlConn = pnlConn();
		pnlConn.setMinimumSize(new Dimension(450, 1));

		PainlessGridBag gbl = new PainlessGridBag(this, false);
		gbl.row().cell(pnlHost).fillXY().cell(pnlConn).fillY();
		gbl.constraints(pnlConn).anchor = GridBagConstraints.CENTER;
		gbl.done();
	}

	private JPanel pnlConn() {
		if (StringUtils.isBlank(installerName)) {
			return pnlConnection;
		}
		JLabel lblName = new JLabel(installerName);
		lblName.setForeground(Color.RED);
		GUIUtils.makeFontBold(lblName);

		JPanel pnl = new JPanel();
		PainlessGridBag gbl = new PainlessGridBag(pnl, false);
		gbl.row().cell(lblName);
		gbl.row().cell(pnlConnection).fillXY();
		gbl.done();
		return pnl;
	}

	public boolean checkValid() {
		return pnlConnection.checkValid();
	}

	public HostDto getSelectedHost() {
		HostDto host = pnlHost.getPnlHost().getSelectedRowData();
		// let changes persisted during session. host = (HostDto) SerializationUtils.clone(host);
		pnlConnection.collectData(host);
		return host;
	}

	public JButton getBtnConnect() {
		return pnlConnection.getBtnOK();
	}

	public boolean isCancel() {
		return pnlConnection.isCancel();
	}

	public void displayHosts(List<HostDto> knownHosts) {
		pnlHost.showHostList(knownHosts);
		pnlHost.clearHostSelection(); // to prevent unanted host selected due to default selection
	}

	public void setSelectedHost(HostDto h) {
		List<HostDto> allRowData = pnlHost.getPnlHost().getSortableModel().getAllRowData();

		if (allRowData.size() == 1) {
			pnlHost.getPnlHost().getTable().getSelectionModel().setSelectionInterval(0, 0);
		}
		if (h == null) {
			return;
		}
		for (int i = 0; i < allRowData.size(); i++) {
			HostDto host = allRowData.get(i);
			if (StringUtils.equalsIgnoreCase(host.getUuid(), h.getUuid())) {
				int viewId = pnlHost.getPnlHost().getTable().convertRowIndexToView(i);
				pnlHost.getPnlHost().getTable().getSelectionModel().setSelectionInterval(viewId, viewId);
				break;
			}
		}
	}

	private void displayHost(HostDto host) {
		pnlConnection.fillDataToScreen(host);
	}
}
