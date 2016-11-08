/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

/**
 * @author htb
 *
 */
public class InstallSendemailCmdInstaller extends AbstractOsInstaller {
	private static InstallSendemailCmdInstaller instance;
	
	public static InstallSendemailCmdInstaller getInstance() {
		if (instance == null) {
			instance = new InstallSendemailCmdInstaller();
		}
		return instance;
	}

	protected InstallSendemailCmdInstaller() {
		super("Install sendemail command");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		sshSudoStrict("apt-get -y install libio-socket-ssl-perl libnet-ssleay-perl sendemail");
	}

}
