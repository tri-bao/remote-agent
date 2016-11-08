/**
 * 
 */
package org.funsoft.remoteagent.installer.connection;

import org.funsoft.remoteagent.installer.connection.config.PortWithCommentDto;

/**
 * @author htb
 *
 */
public class JmxTunnelingConsoleInstaller extends AbstractConnectionInstaller {

	private static JmxTunnelingConsoleInstaller instance;
	
	public static JmxTunnelingConsoleInstaller getInstance() {
		if (instance == null) {
			instance = new JmxTunnelingConsoleInstaller();
		}
		return instance;
	}
	
	protected JmxTunnelingConsoleInstaller() {
		super("Open SSH tunnel");
	}

	@Override
	public String getDescription() {
		return "use case: java remote debuging";
	}

	@Override
	protected void performInternal() throws Exception {
		openSslTunneling(
				confirm("Start as SOCKS server?\n"
						+ "(to debug java remoting, DON'T use SOCKS)\n\n"
						+ "- Yes : start as SOCKS (for jmx)\n"
						+ "- No : no SOCKS (for java remoting)"),
				new PortWithCommentDto(2001, "Java remote debuging")
				);
	}

}
