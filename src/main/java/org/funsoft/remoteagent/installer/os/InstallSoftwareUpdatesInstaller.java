/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.gui.component.ReadOnlyTextPane;
import org.funsoft.remoteagent.main.RemoteAgentGui;

/**
 * @author htb
 *
 */
public class InstallSoftwareUpdatesInstaller extends AbstractOsInstaller {
	private static InstallSoftwareUpdatesInstaller instance;
	
	public static InstallSoftwareUpdatesInstaller getInstance() {
		if (instance == null) {
			instance = new InstallSoftwareUpdatesInstaller();
		}
		return instance;
	}

	protected InstallSoftwareUpdatesInstaller() {
		super("OS - install software updates");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		sshSudoStrict("apt-get update");
		setCurrentSessionData("apt-get-update", true);
		sshSudoStrict("apt-get -y upgrade");
		CommandResult list = sshSudo("apt-get -u --assume-no upgrade");
		boolean noUpdatePending =
				((list.getOkStr() != null)
					&& list.getOkStr().contains("0 upgraded, 0 newly installed"))
				||
				((list.getErrorStr() != null)
					&& list.getErrorStr().contains("0 upgraded, 0 newly installed"))
				;
		if (!noUpdatePending) {
			RemoteAgentGui.showInfoMsg("Vẫn còn software chưa thể update được với \"upgrade\" command.\n\n"
					+ "Hãy kiểm tra lại danh sách và sử dụng dist-upgrade nếu muốn update chúng.");
			ReadOnlyTextPane.showText("Available updates", list.getOkStr(), false);
		}
	}

}
