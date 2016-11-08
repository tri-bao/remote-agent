/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.cnf.ConfigFileUtils;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;

/**
 * @author htb
 *
 */
public class InstallTimerEntropydInstaller extends AbstractInstaller {
	private static InstallTimerEntropydInstaller instance;
	
	public static InstallTimerEntropydInstaller getInstance() {
		if (instance == null) {
			instance = new InstallTimerEntropydInstaller();
		}
		return instance;
	}

	protected InstallTimerEntropydInstaller() {
		super("Install Timer entropyd");
	}

	@Override
	public String getDescription() {
		return "Chỉ làm cho môi trường VirtualBox thôi";
	}

	@Override
	protected void performInternal() throws Exception {
		// http://bredsaal.dk/improving-randomness-and-entropy-in-ubuntu-9-10
		aptGetUpdate();
		sshSudoStrict("apt-get -y install build-essential");
		String sourceUrl = "http://www.vanheusden.com/te/timer_entropyd-0.2.tgz";
		String tarGzFile = sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1);
		String untarFolderName = tarGzFile.substring(0, tarGzFile.length() - ".tgz".length());
		// don't download to staging folder as it will be cleaned after every run.
		// while there will be error on the way it is installed. We don't want to download again and again
		sshStrict("wget -P /tmp " + " " + sourceUrl);
		sshUntar("/tmp/" + tarGzFile, getStagingFolder());
		sshStrict("make -C " + getStagingFolder() + "/" + untarFolderName);
		sshSudoStrict("mv " + getStagingFolder() + "/" + untarFolderName + " /usr/local/timer_entropyd");
		ConfigFileUtils.writeFile(temporaryPath("entropyd"),
				"#!/bin/bash",
				"### BEGIN INIT INFO",
				"# Provides:          entropyd",
				"# Required-Start:    $local_fs $remote_fs $network $syslog",
				"# Required-Stop:     $local_fs $remote_fs $network $syslog",
				"# Default-Start:     2 3 4 5",
				"# Default-Stop:      0 1 6",
				"# Short-Description:",
				"### END INIT INFO",
				"",
				"/usr/local/timer_entropyd/timer_entropyd"
				);
		scpToRemoteUnderRoot(temporaryPath("entropyd"), "/etc/init.d/entropyd");
		sshSudoStrict("chmod +x /etc/init.d/entropyd");
		sshSudoStrict("update-rc.d entropyd defaults");
		sshSudoStrict("service entropyd start");
	}

}
