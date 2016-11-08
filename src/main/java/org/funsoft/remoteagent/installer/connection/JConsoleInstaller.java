/**
 * 
 */
package org.funsoft.remoteagent.installer.connection;

import org.funsoft.remoteagent.installer.connection.config.PortWithCommentDto;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.util.FileChooser;

import java.io.File;

/**
 * @author htb
 */
public class JConsoleInstaller extends AbstractConnectionInstaller {
	private static JConsoleInstaller instance;
	public static JConsoleInstaller getInstance() {
		if (instance == null) {
			instance = new JConsoleInstaller();
		}
		return instance;
	}

	protected JConsoleInstaller() {
		super("JConsole");
	}

	@Override
	public String getDescription() {
		return "No need to open port jxm to internet on the server side";
	}

	@Override
	protected void performInternal() throws Exception {
		String jConsole = getJConsole();
		
		PortWithCommentDto selectedPort = openSslTunneling(
				true,
				new PortWithCommentDto(1504, "JMX Port")
				);
		if (selectedPort == null) {
			return;
		}
		
		String plugin = null;
		if (confirm("Muốn start JConole với plugin (VD với JTop để xem Thread CPU usage)?")) {
			File f = FileChooser.chooseFileExitIfNull("CHOOSE JConsole PLUGIN JAR", ".jar");
			plugin = f.getAbsolutePath();
		}
		
		// http://simplygenius.com/2010/08/jconsole-via-socks-ssh-tunnel.html
		if (plugin == null) {
			Runtime.getRuntime().exec(new String[] {
					jConsole,
					"-J-DsocksProxyHost=localhost",
					"-J-DsocksProxyPort=" + selectedPort.getPort(),
					"service:jmx:rmi:///jndi/rmi://localhost:" + selectedPort.getPort() + "/jmxrmi"
			});
		} else {
			Runtime.getRuntime().exec(new String[] {
					jConsole,
					"-pluginpath", plugin,
					"-J-DsocksProxyHost=localhost",
					"-J-DsocksProxyPort=" + selectedPort.getPort(),
					"service:jmx:rmi:///jndi/rmi://localhost:" + selectedPort.getPort() + "/jmxrmi"
			});
		}
	}
	
	private String getJConsole() {
		String javaHome = System.getProperty("java.home");
		File jhomeDir = new File(javaHome);
		if (jhomeDir.getName().equals("jre")) {
			jhomeDir = jhomeDir.getParentFile();
		}
		File jconsole = new File(jhomeDir, "/bin/jconsole");
		if (!jconsole.exists()) {
			throw new ExitInstallerRuntimeException("Cannot determine jconsole at:\n\n"
					+ jconsole.getAbsolutePath() + "\n\n"
					+ "Try to run with jdk instead of jre");
		}
		return jconsole.getAbsolutePath();
	}
}
