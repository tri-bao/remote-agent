/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

/**
 * @author htb
 *
 */
public class InstallIoTopInstaller extends AbstractOsInstaller {
	private static InstallIoTopInstaller instance;
	
	public static InstallIoTopInstaller getInstance() {
		if (instance == null) {
			instance = new InstallIoTopInstaller();
		}
		return instance;
	}

	protected InstallIoTopInstaller() {
		super("OS - installer iotop");
	}

	@Override
	public String getDescription() {
		return "iotop likes top but for monitoring disk access";
	}

	@Override
	protected void performInternal() throws Exception {
		sshSudoStrict("apt-get -y install iotop");
	}

}
