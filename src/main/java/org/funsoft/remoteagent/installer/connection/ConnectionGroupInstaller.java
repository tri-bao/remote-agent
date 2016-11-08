/**
 * 
 */
package org.funsoft.remoteagent.installer.connection;

import org.funsoft.remoteagent.installer.core.AbstractCompositeInstaller;
import org.funsoft.remoteagent.installer.core.IInstaller;

import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 *
 */
public class ConnectionGroupInstaller extends AbstractCompositeInstaller {
	private static ConnectionGroupInstaller instance;
	
	public static ConnectionGroupInstaller getInstance() {
		if (instance == null) {
			instance = new ConnectionGroupInstaller();
		}
		return instance;
	}

	protected ConnectionGroupInstaller() {
		super("SSH, JMX...");
	}

	@Override
	public List<IInstaller> getSubInstaller() {
		return (List) Arrays.asList(
				SshConsoleInstaller.getInstance(),
				JConsoleInstaller.getInstance(),
				JmxTunnelingConsoleInstaller.getInstance()
				);
	}

}
