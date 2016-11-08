package org.funsoft.remoteagent.installer.os.config;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;

/**
 * @author Ho Tri Bao
 *
 */
public class FirewallRuleInputPanel extends AbstractConfigInputPanel {
	private final JTextArea txeNICs = GUIUtils.newTextArea();
	private final JTextField txtNic = GUIUtils.newTextField();
	private final JTextField txtPort = GUIUtils.newTextField();
	private final JTextField txtProtocal = GUIUtils.newTextField();
	private final JTextField txtFromIP = GUIUtils.newTextField();
	public FirewallRuleInputPanel() {
		init();
	}
	@Override
	protected void initState() {
		txeNICs.setEditable(false);
		txeNICs.setRows(8);
		txtProtocal.setText("tcp");
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cellXRemainder(new JLabel("Các card mạng hiện có"));
		gbl.row().cellXRemainder(new JScrollPane(txeNICs)).fillX();
		
		gbl.row().cell(new JLabel("Đặt rule trên card")).cellX(txtNic, 3).fillX()
			.cell(new JLabel("để trống tương ứng với tất cả card"));
		gbl.row()
			.cell(new JLabel("Cho phép port")).cell(txtPort).fillX()
			.cell(new JLabel("protocal")).cell(txtProtocal).fillX();
		gbl.row().cell(new JLabel("Truy cập từ IP")).cellX(txtFromIP, 3).fillX()
			.cell(new JLabel("để trống tương ứng với mọi IP"));
	}

	@Override
	protected boolean checkValid() {
		if (GUIUtils.getText(txtNic) != null) {
			boolean b = NetworkCardPublicPrivateSelectionPanel.validateNicName(
					GUIUtils.getText(txtNic), txeNICs.getText());
			if (!b) {
				return false;
			}
		}
		boolean b =
				GUIUtils.checkInteger(txtPort, "port", true)
				&& GUIUtils.requireMandatory(txtProtocal, "protocal");
		if (!b) {
			return false;
		}
		if (GUIUtils.getText(txtFromIP) == null) {
			if (!RemoteAgentGui.showConfirmationYesNo("chưa nhập IP, nghĩa là cho phép tất cả?")) {
				return false;
			}
		}
		return true;
	}

	public String getNIC() {
		return GUIUtils.getText(txtNic);
	}
	public int getPort() {
		return GUIUtils.getInteger(txtPort);
	}
	public String getProtocal() {
		return GUIUtils.getText(txtProtocal);
	}
	public String getIP() {
		return GUIUtils.getText(txtFromIP);
	}
	public void displayNICs(String nics) {
		txeNICs.setText(nics);
	}
}
