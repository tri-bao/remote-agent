/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.installer.core.AbstractInstaller;

/**
 * @author htb
 *
 */
public abstract class AbstractOsInstaller extends AbstractInstaller {

	protected AbstractOsInstaller(String installerName) {
		super(installerName);
	}

    protected void changeOsUserPassword(String username, String newPassword) throws Exception {
    	executeAsBatchSudo("echo \"" + username + ":" + newPassword + "\" | sudo chpasswd");
    }

	protected String listNICs() throws Exception {
		// list NIC + IP
		return sshSudoStrict("ifconfig -a | grep \"Link encap\\|inet addr\"").getOkStr();
	}
}
