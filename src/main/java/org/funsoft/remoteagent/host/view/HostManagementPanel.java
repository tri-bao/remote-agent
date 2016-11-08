/**
 * 
 */
package org.funsoft.remoteagent.host.view;

import org.funsoft.remoteagent.host.dto.HostDto;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author htb
 *
 */
public class HostManagementPanel extends JPanel {
	private final HostTablePanel<HostTableModel> pnlHost =
			new HostTablePanel<>(new HostTableModel());
	
	private final JButton btnAdd = new JButton("Add");
	private final JButton btnEdit = new JButton("Edit");
	private final JButton btnDelete = new JButton("Delete");
	
	public HostManagementPanel() {
		initState();
		initAction();
		initLayout();
	}

	private void initState() {
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
		pnlHost.getBtnManageTags().setVisible(true);
	}

	private void initAction() {
		pnlHost.getPnlHost().getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				HostDto selectedRowData = pnlHost.getPnlHost().getSelectedRowData();
				btnEdit.setEnabled(selectedRowData != null);
				btnDelete.setEnabled(selectedRowData != null);
			}
		});
		pnlHost.getPnlHost().getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					if (btnEdit.isVisible()) {
						btnEdit.doClick();
					}
				}
			}
		});
	}

	private void initLayout() {
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		gbl.row().cellXRemainder(pnlHost).fillXY();
		LayoutUtils.addButtonPanel(gbl, btnAdd, btnEdit, btnDelete);
		gbl.done();
	}

	public void showHostList(List<HostDto> hosts) {
		pnlHost.showHostList(hosts);
	}
	public HostDto getSelectedHost() {
		return pnlHost.getSelectedHost();
	}
	public void addHost(HostDto host) {
		pnlHost.addHost(host);
	}
	public void updateSelectedHost(HostDto host) {
		pnlHost.updateSelectedHost(host);
	}
	public void deleteSelectedHost() {
		pnlHost.deleteSelectedHost();
	}

	public JButton getBtnAdd() {
		return btnAdd;
	}

	public JButton getBtnEdit() {
		return btnEdit;
	}

	public JButton getBtnDelete() {
		return btnDelete;
	}
	public JButton getBtnManageTags() {
		return pnlHost.getBtnManageTags();
	}
}
