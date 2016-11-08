/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.cnf.InputUtils;

/**
 * @author htb
 *
 */
public class ChangeTimezoneInstaller extends AbstractOsInstaller {

	private static ChangeTimezoneInstaller instance;
	public static ChangeTimezoneInstaller getInstance() {
		if (instance == null) {
			instance = new ChangeTimezoneInstaller();
		}
		return instance;
	}

	protected ChangeTimezoneInstaller() {
		super("OS - change timezone");
	}
	
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		String zone = InputUtils.askSingleTextExitIfNull("Timezone",
				"Asia/Ho_Chi_Minh", "Timezone name: see /usr/share/zoneinfo");
		
		executeAsBatchSudo(
				"echo \"" + zone + "\" > /etc/timezone",
				"dpkg-reconfigure -f noninteractive tzdata");
		
		sshSudoStrict("service cron restart");
	}

}
