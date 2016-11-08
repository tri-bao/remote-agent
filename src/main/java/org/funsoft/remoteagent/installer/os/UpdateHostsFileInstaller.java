/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

/**
 * @author htb
 *
 */
public class UpdateHostsFileInstaller extends ChangeHostNameAndUpdateHostsFileInstaller {
	private static UpdateHostsFileInstaller instance;
	public static UpdateHostsFileInstaller getInstance() {
		if (instance == null) {
			instance = new UpdateHostsFileInstaller();
		}
		return instance;
	}
	
	private UpdateHostsFileInstaller() {
		super("OS - update hosts file");
	}

	@Override
	public String getDescription() {
		return "Chỉ update host file thôi";
	}

	@Override
	protected void performInternal() throws Exception {
		updateHostsFile();
	}
	
}
