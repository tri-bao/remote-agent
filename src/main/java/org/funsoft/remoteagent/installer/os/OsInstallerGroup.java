/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.installer.core.AbstractCompositeInstaller;
import org.funsoft.remoteagent.installer.core.IInstaller;

import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 *
 */
public class OsInstallerGroup extends AbstractCompositeInstaller {
	private static OsInstallerGroup instance;
	public static OsInstallerGroup getInstance() {
		if (instance == null) {
			instance = new OsInstallerGroup();
		}
		return instance;
	}
	
	protected OsInstallerGroup() {
		super("OS", "Nhóm các chức năng liên quan đến hệ điều hành");
	}

	@Override
	public List<IInstaller> getSubInstaller() {
		return (List) Arrays.asList(
				// Machine preparation, more or less specific for EC2 environment
				CreatePasswordlessSshUserInstaller.getInstance(),
				SshServerInstaller.getInstance(),
				ChangeTimezoneInstaller.getInstance(),
				SetupNtpDeamonInstaller.getInstance(),
				ChangeHostNameAndUpdateHostsFileInstaller.getInstance(),
				ConfigureNetworkInterfaceInstaller.getInstance(),
				BringNetworkInterfaceUpInstaller.getInstance(),
				AddNewHardDriveInstaller.getInstance(),
				InstallIoTopInstaller.getInstance(),
				ListUpgradeSoftwareInstaller.getInstance(),
				InstallSoftwareUpdatesInstaller.getInstance(),
				
				ChangeOsUserPasswordInstaller.getInstance(),
				UpdateHostsFileInstaller.getInstance(),
				
				CheckPortInstaller.getInstance(),
				InstallSendemailCmdInstaller.getInstance(),
				InstallTimerEntropydInstaller.getInstance(),
				FirewallManagementInstaller.getInstance()
				);
	}

}
