/**
 * 
 */
package org.funsoft.remoteagent.gui.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.funsoft.remoteagent.gui.component.table.AbstractTableModel;
import org.funsoft.remoteagent.gui.component.table.ScrollableSortablePanel;
import org.funsoft.remoteagent.gui.component.table.SortableTable;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 *
 */
public abstract class AbstractDoubleListSelectionPanel<Pnl extends JComponent,
			D,
			T extends ScrollableSortablePanel<D, ? extends AbstractTableModel<D>>> extends JPanel {
	protected final Pnl pnlAvailable = createAvailabelPanel();
	protected final Pnl pnlSelected = createSelectedPanel();

	private final JButton btnSelect = new JButton("Add >>");
	private final JButton btnRemove = new JButton("<< Remove");
	private final JButton btnOK = new JButton("OK");
	private final JButton btnCancel = new JButton("Cancel");
	private boolean isCancel = true;
	
	public AbstractDoubleListSelectionPanel() {
		initState();
		initAction();
		initLayout();
	}

	protected abstract Pnl createAvailabelPanel();
	
	protected abstract Pnl createSelectedPanel();

	protected abstract T getScrollableTable(Pnl tblPnl);
	
	protected SortableTable<D, ? extends AbstractTableModel<D>> getTable(Pnl tblPnl) {
		return getScrollableTable(tblPnl).getTable();
	}
	
	private void initState() {
		btnSelect.setEnabled(false);
		btnRemove.setEnabled(false);
		
		getTable(pnlAvailable).getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		getTable(pnlSelected).getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}

	private void initAction() {
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkValid()) {
					isCancel = false;
					SwingUtilities.getWindowAncestor(btnCancel).dispose();
				}
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.getWindowAncestor(btnCancel).dispose();
			}
		});
		getTable(pnlAvailable).getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] selectedRows = getTable(pnlAvailable).getSelectedRows();
				btnSelect.setEnabled(ArrayUtils.isNotEmpty(selectedRows));
			}
		});
		getTable(pnlSelected).getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] selectedRows = getTable(pnlSelected).getSelectedRows();
				btnRemove.setEnabled(ArrayUtils.isNotEmpty(selectedRows));
			}
		});
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSelect();
			}
		});
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRemove();
			}
		});
		getTable(pnlAvailable).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					onSelect();
				}
			}
		});
		getTable(pnlSelected).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					onRemove();
				}
			}
		});
	}

	private void onSelect() {
		addRemove(pnlAvailable, pnlSelected);
	}
	private void addRemove(Pnl pnlFrom, Pnl pnlTo) {
		int[] selectedRows = getTable(pnlFrom).getSelectedRows();
		if (ArrayUtils.isEmpty(selectedRows)) {
			return;
		}
		for (int i = 0; i < selectedRows.length; i++) {
			int rowViewId = selectedRows[i];
			int modelId = getTable(pnlFrom).convertRowIndexToModel(rowViewId);
			
			getTable(pnlTo).addData(getTable(pnlFrom).getSortableModel().getRowData(modelId));
		}
		
		Arrays.sort(selectedRows);
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			int modelId = getTable(pnlFrom).convertRowIndexToModel(selectedRows[i]);
			getTable(pnlFrom).getSortableModel().removeRowData(modelId);
		}
		getScrollableTable(pnlFrom).updateTotal();
	}
	private void onRemove() {
		addRemove(pnlSelected, pnlAvailable);
	}
	
	private void initLayout() {
		JPanel pnlButton = pnlButton();
		
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		gbl.row().cell(pnlAvailable).fillXY().cell(pnlButton).cell(pnlSelected).fillXY();
		gbl.constraints(pnlButton).anchor = GridBagConstraints.CENTER;
		LayoutUtils.addButtonPanel(gbl, btnOK, btnCancel);
		gbl.done();
	}

	private JPanel pnlButton() {
		JPanel pnl = new JPanel();
		PainlessGridBag gbl = new PainlessGridBag(pnl, false);
		gbl.row().cell(btnSelect).fillX();
		gbl.row().cell(btnRemove).fillX();
		gbl.constraints(btnRemove).insets.top = 30;
		gbl.done();
		return pnl;
	}
	
	private boolean checkValid() {
		if (getTable(pnlSelected).getSortableModel().getRowCount() == 0) {
			RemoteAgentGui.showErrorMsg("No row selected");
			return false;
		}
		return true;
	}
	
	public boolean isCancel() {
		return isCancel;
	}
	public List<D> getSelectedRows() {
		return getTable(pnlSelected).getSortableModel().getAllRowData();
	}
	public void setAvailableRows(List<D> avais) {
		setAvailableRows(avais, null);
	}
	public void setAvailableRows(List<D> avais, List<D> selected) {
		if (CollectionUtils.isEmpty(selected)) {
			getTable(pnlAvailable).showResult(avais);
			return;
		}
		List<D> a = new ArrayList<>(avais);
		a.removeAll(selected);
		getTable(pnlAvailable).showResult(a);
		
		getTable(pnlSelected).showResult(selected);
	}

}
