/**
 * 
 */
package org.funsoft.remoteagent.installer.cnffile;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.*;

/**
 * @author htb
 *
 */
public class TextContextEditPanel extends AbstractConfigInputPanel {
	private final JTextField txtRemoteFile = GUIUtils.newTextField();
	private final JTextArea txe = GUIUtils.newTextArea();
	private final JCheckBox chkBackup = new JCheckBox("Backup file này trước khi ghi đè");
	
	public static TextContextEditPanel show(String remoteFilePath, String content) {
		TextContextEditPanel pnl = new TextContextEditPanel();
		pnl.txe.setText(content);
		pnl.txtRemoteFile.setText(remoteFilePath);
		
		showInputExitIfCancel(pnl, remoteFilePath, 1000, 700);
		return pnl;
	}
	
	private TextContextEditPanel() {
		init();
	}
	@Override
	protected void initState() {
        txe.setFont(new Font("monospaced", Font.PLAIN, 15));
        txe.setLineWrap(false);
        txe.setTabSize(4);
        
		txtRemoteFile.setEditable(false);
		chkBackup.setSelected(true);
		chkBackup.setEnabled(false); // force always create backup file
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		txe.requestFocusInWindow();
	}
	
	@Override
	protected void initLayout(PainlessGridBag gbl) {
		JScrollPane scr = new JScrollPane(txe);
		
		gbl.row().cell(txtRemoteFile).fillX();
		gbl.row().cell(chkBackup);
		gbl.row().cell(scr).fillXY();
	}
	@Override
	protected void doneLayout(PainlessGridBag gbl) {
		gbl.done();
	}
	@Override
	protected void addToMainLayout(JPanel pnl) {
		PainlessGridBag gblMain = new PainlessGridBag(this, false);
		gblMain.row().cell(pnl).fillXY();
		LayoutUtils.addButtonPanel(gblMain, btnOK, btnCancel);
		gblMain.done();
	}
	@Override
	protected boolean checkValid() {
		return GUIUtils.requireMandatory(txe, "Nội dung");
	}
	public String getNewContent() {
		// do not strip all whitespaces/newline, in some config files
		// such as fstab, new line at the end is mandatorys
		return txe.getText() == null ? "" : txe.getText();
	}
	public boolean isBackupCurrentFile() {
		return chkBackup.isSelected();
	}
}
