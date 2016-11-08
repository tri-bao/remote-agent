/**
 * 
 */
package org.funsoft.remoteagent.installer.os;


/**
 * @author htb
 *
 */
public class SetupNtpDeamonInstaller extends AbstractOsInstaller {
	private static SetupNtpDeamonInstaller instance;
	public static SetupNtpDeamonInstaller getInstance() {
		if (instance == null) {
			instance = new SetupNtpDeamonInstaller();
		}
		return instance;
	}
	protected SetupNtpDeamonInstaller() {
		super("OS - setup ntpd");
	}
	
	@Override
	public String getDescription() {
		return "Network time protocal deamon";
	}

	@Override
	protected void performInternal() throws Exception {
		sshSudoStrict("apt-get -y install ntp");
//		RemoteConfigFileEditor editor = new RemoteConfigFileEditor(this);
//		editor.showForEdit("/etc/ntp.conf");
	}

}
