/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.gui.component.ReadOnlyTextPane;

/**
 * @author htb
 *
 */
public class ListUpgradeSoftwareInstaller extends AbstractOsInstaller {
	private static ListUpgradeSoftwareInstaller instance;
	
	public static ListUpgradeSoftwareInstaller getInstance() {
		if (instance == null) {
			instance = new ListUpgradeSoftwareInstaller();
		}
		return instance;
	}

	protected ListUpgradeSoftwareInstaller() {
		super("OS - list available updates");
	}

	@Override
	public String getDescription() {
		return "Just list them, not install them";
	}

	@Override
	protected void performInternal() throws Exception {
		sshSudoStrict("apt-get update");
		CommandResult list = sshSudo("apt-get -u --assume-no upgrade");
		ReadOnlyTextPane.showText("Available updates", list.getOkStr(), false);
	}

}
