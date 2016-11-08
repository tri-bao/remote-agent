package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.cnf.ConfigFileUtils;
import org.funsoft.remoteagent.installer.connection.SshConsoleInstaller;

import java.io.IOException;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public abstract class AbstractFirewallInstaller extends AbstractOsInstaller {

	protected AbstractFirewallInstaller(String installerName) {
		super(installerName);
	}

	/**
	 * added rules are store in /lib/ufw/user6.rules (IPv6) user.rules (IPv4)
	 */
	protected void addFirewallRuleAllowPort(int port, String protocal, String nic, String fromIP) throws Exception {
		// sudo ufw allow in on eth1 to any port 8888 proto tcp from 192.168.56.1
		sshSudoStrict("ufw allow"
				+ " in on " + nic
				+ " to any port " + port + " proto " + protocal
				+ " from " + (fromIP == null ? "any" : fromIP));
	}
	

	protected void enableUfwFirewall() throws IOException, Exception {
		if (confirm("Nên mở sẵn 1 ssh console, tránh trường hợp bị khóa sau khi cài firewall.\n"
				+ "Bạn có muốn mở luôn không?")) {
			SshConsoleInstaller.openSshConsole(hostInfo);
		}

		AbstractRemoteConfigFileModifier configUfw = new AbstractRemoteConfigFileModifier() {
			@Override
			protected void placeNewConfig(List<String> lines) throws Exception {
				for (int i = 0; i < lines.size(); i++) {
					if (ConfigFileUtils.cnfKeyOccursUnComment("ENABLED", lines.get(i))) {
						// no whitespace around =
						lines.set(i, "ENABLED=yes");
					}
				}
			}
		};
		configUfw.modify("/etc/ufw/ufw.conf");
		
		executeAsBatchSudo("yes | ufw enable");
	}

}
