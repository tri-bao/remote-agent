package org.funsoft.remoteagent.installer.os.config;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class FirewallManagementPanel extends AbstractConfigInputPanel {
	private final JTextArea txeNICs = GUIUtils.newTextArea();
	private final DefaultListModel<String> rulesModel = new DefaultListModel<>();
	private final JList<String> lstRules = new JList<>(rulesModel);
	
	private final JButton btnDelete = new JButton("Xóa");
	private final JButton btnThem = new JButton("Thêm");
	
	public FirewallManagementPanel() {
		init();
	}
	
	@Override
	protected void initState() {
		btnOK.setVisible(false);
		btnCancel.setText("Đóng");
		txeNICs.setEditable(false);
		txeNICs.setRows(8);
		btnDelete.setEnabled(false);
		lstRules.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				c.setFont(new Font("monospaced", Font.PLAIN, 12));
				return c;
			}
			
		});
		lstRules.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				btnDelete.setEnabled(lstRules.getSelectedValue() != null);
			}
		});
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cellXRemainder(new JLabel("Các card mạng hiện có"));
		gbl.row().cellXRemainder(new JScrollPane(txeNICs)).fillX();
		
		gbl.row().separator(new JLabel("Các firewall rules hiện có (v6) có nghĩa là IPv6"));
		
		JLabel lblFrom = new JLabel("Truy cập từ");
		JLabel lblAction = new JLabel("Action");
		JLabel lblTo = new JLabel("Truy cập vào");
		gbl.row().cell(lblTo).cell(lblAction).cell(lblFrom);
		gbl.constraints(lblAction).insets.left = 60;
		gbl.constraints(lblFrom).insets.left = 20;
		
		gbl.row().cellXRemainder(new JScrollPane(lstRules)).fillXY();
		
		LayoutUtils.addButtonPanel(gbl, new JButton[] {btnDelete}, new JButton[] {btnThem});
	}

	@Override
	protected void doneLayout(PainlessGridBag gbl) {
		gbl.done();
	}

	@Override
	protected boolean checkValid() {
		return false;
	}

	public void displayNICs(String nicInfo) {
		txeNICs.setText(nicInfo);
	}
	public void displayRules(List<String> rules) {
		rulesModel.removeAllElements();
		for (String r : rules) {
			this.rulesModel.addElement(r);
		}
	}
	public String getSelectedRule() {
		return lstRules.getSelectedValue();
	}
	public int getSelectedIndex() {
		return lstRules.getSelectedIndex();
	}
	public JButton getBtnDelete() {
		return btnDelete;
	}

	public JButton getBtnThem() {
		return btnThem;
	}
}
