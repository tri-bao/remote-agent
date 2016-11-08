/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.installer.os.config.UserAccountInputPanel;

/**
 * @author htb
 *
 */
public class ChangeOsUserPasswordInstaller extends AbstractOsInstaller {
	private static ChangeOsUserPasswordInstaller instance;
	public static ChangeOsUserPasswordInstaller getInstance() {
		if (instance == null) {
			instance = new ChangeOsUserPasswordInstaller();
		}
		return instance;
	}
	
	protected ChangeOsUserPasswordInstaller() {
		super("OS - change user password");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		UserAccountInputPanel pnl = UserAccountInputPanel.showInputExitIfCancel(
				UserAccountInputPanel.class,
				"CHANGE PASSWORD USER", 500, 250);

		changeOsUserPassword(pnl.getUsername(), pnl.getPassword());
	}

}
