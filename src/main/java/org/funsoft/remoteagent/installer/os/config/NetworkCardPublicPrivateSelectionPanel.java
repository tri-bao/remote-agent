package org.funsoft.remoteagent.installer.os.config;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;

/**
 * @author Ho Tri Bao
 *
 */
public class NetworkCardPublicPrivateSelectionPanel extends AbstractConfigInputPanel {
	private final JTextArea txeNICs = GUIUtils.newTextArea();
	private final NicsCombobox txtPublicNic;
	private final NicsCombobox txtPrivateNic;
	
	public NetworkCardPublicPrivateSelectionPanel(AbstractInstaller installer) {
		txtPublicNic = new NicsCombobox(installer);
		txtPrivateNic = new NicsCombobox(installer);
		init();
	}
	
	@Override
	protected void initState() {
		txeNICs.setEditable(false);
		txeNICs.setRows(8);
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cellXRemainder(new JLabel("Các card mạng hiện có"));
		gbl.row().cellXRemainder(new JScrollPane(txeNICs)).fillX();
		gbl.row().cell(new JLabel("Public card")).cell(txtPublicNic).fillX()
			.cell(new JLabel("(kết nối với internet)"));
		gbl.row().cell(new JLabel("Internal card")).cell(txtPrivateNic).fillX()
			.cell(new JLabel("(kết nối trong nội bộ các server)"));
	}

	@Override
	protected boolean checkValid() {
		if ((txtPublicNic.getSelectedNic() == null) && (txtPrivateNic.getSelectedNic() == null)) {
			RemoteAgentGui.showErrorMsg("Cần phải nhập 1 card");
			txtPublicNic.requestFocus();
			return false;
		}
		boolean b = validateNicName(txtPublicNic.getSelectedNic(), txeNICs.getText())
				&& validateNicName(txtPrivateNic.getSelectedNic(), txeNICs.getText());
		if (!b) {
			return false;
		}
		if (StringUtils.equals(txtPublicNic.getSelectedNic(), txtPrivateNic.getSelectedNic())) {
			if (RemoteAgentGui.showConfirmationYesNo("2 card mạng public và internal là 1?")) {
				return true;
			}
			return false;
		}
		return true;
	}
	public static boolean validateNicName(String name, String nicInfo) {
		if (StringUtils.isBlank(name)) {
			return true;
		}
		String str = nicInfo;
		String[] lines = StringUtils.split(str, "\n");
		for (String line : lines) {
			int pos = StringUtils.indexOf(line, "Link encap", 0);
			if (pos > 0) {
				String cardName = StringUtils.stripToEmpty(line.substring(0, pos));
				if (name.equals(cardName)) {
					return true;
				}
			}
		}
		if (RemoteAgentGui.showConfirmationYesNo("Dường như card mạng \"" + name + "\" không tồn tại.\n"
				+ "Có muốn dừng lại để sửa?")) {
			return false;
		}
		return true;
	}
	public void displayNICs(String nicsInfo) {
		txeNICs.setText(nicsInfo);
	}
	public String getPublicNIC() {
		return txtPublicNic.getSelectedNic();
	}
	public String getPrivateNIC() {
		return txtPrivateNic.getSelectedNic();
	}
}
