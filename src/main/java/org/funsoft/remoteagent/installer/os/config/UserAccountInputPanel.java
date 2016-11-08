/**
 * 
 */
package org.funsoft.remoteagent.installer.os.config;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;

/**
 * @author htb
 *
 */
public class UserAccountInputPanel extends AbstractConfigInputPanel {
	private final JTextField txtUsername = GUIUtils.newTextField();
	private final JPasswordField txtPw1 = GUIUtils.newPwdField();
	private final JPasswordField txtPw2 = GUIUtils.newPwdField();
	
	public UserAccountInputPanel() {
		init();
	}
	
	@Override
	protected void initState() {
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cell(lbl("Username")).cell(txtUsername).fillX();
		gbl.row().cell(lbl("Password")).cell(txtPw1).fillX();
		gbl.row().cell(lbl("Confirm")).cell(txtPw2).fillX();
	}

	@Override
	protected boolean checkValid() {
		return GUIUtils.requireMandatory(txtUsername, "Username")
				&& GUIUtils.checkPasswordPair(txtPw1, txtPw2, "password");
	}
	public void setUsername(String username) {
		txtUsername.setText(username);
	}
	public String getUsername() {
		return GUIUtils.getText(txtUsername);
	}
	public String getPassword() {
		return GUIUtils.getText(txtPw1);
	}
}
