/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.cnf.InputUtils;

/**
 * @author htb
 *
 */
public class BringNetworkInterfaceUpInstaller extends AbstractOsInstaller {
	private static BringNetworkInterfaceUpInstaller instance;
	
	public static BringNetworkInterfaceUpInstaller getInstance() {
		if (instance == null) {
			instance = new BringNetworkInterfaceUpInstaller();
		}
		return instance;
	}

	protected BringNetworkInterfaceUpInstaller() {
		super("OS - bring up a network interface");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		String infName = InputUtils.askSingleText("INTERFACE NAME", true, "eth1");
		sshSudoStrict("ifup " + infName);
	}

}
