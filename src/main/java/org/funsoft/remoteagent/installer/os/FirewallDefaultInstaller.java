package org.funsoft.remoteagent.installer.os;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.os.config.NetworkCardPublicPrivateSelectionPanel;

/**
 * @author Ho Tri Bao
 *
 */
public class FirewallDefaultInstaller extends AbstractFirewallInstaller {
	private static final Integer[] PORTS_PUBLIC_FIREWALL = new Integer[] {80, 443};
	private static FirewallDefaultInstaller instanceQllt;
	private static FirewallDefaultInstaller instanceCsdldc;

	private final Integer[] privatePorts;
	
	protected FirewallDefaultInstaller(Integer[] privatePorts) {
		super("OS - cài đặt firewall với tham số mặc định");
		this.privatePorts = privatePorts;
	}

	@Override
	public String getDescription() {
		return "Thiết lập mặc định: card public: cho phép bên ngoài kết nối vào các port tcp "
				+ StringUtils.join(PORTS_PUBLIC_FIREWALL, ", ")
				+ ". Card private: kết nối vào các port tcp "
				+ StringUtils.join(privatePorts, ", ");
	}

	@Override
	protected void performInternal() throws Exception {
		// 1. check firewall status. if it is active, exit
		CommandResult commandResult = sshSudoStrict("ufw status");
		if (!commandResult.getOkStr().contains("inactive")) {
			throw new ExitInstallerRuntimeException("Firewall đang hoạt động. Hãy sử dụng chức năng quản lý firewall."
					+ "\nViệc cấu hình firewall mặc đinh chỉ được tiến hành trên các máy vừa mới cài đặt.");
		}
		
		String nics = listNICs();
		NetworkCardPublicPrivateSelectionPanel pnl = new NetworkCardPublicPrivateSelectionPanel(this);
		pnl.displayNICs(nics);
		NetworkCardPublicPrivateSelectionPanel.showInputExitIfCancel(
				pnl,
				"NHẬP CÁC CARD MẠNG", 700, 300);
		
		if (pnl.getPublicNIC() != null) {
			for (Integer port : PORTS_PUBLIC_FIREWALL) {
				addFirewallRuleAllowPort(port, "tcp", pnl.getPublicNIC(), "any");
			}
		}
		if (pnl.getPrivateNIC() != null) {
			for (Integer port : privatePorts) {
				addFirewallRuleAllowPort(port, "tcp", pnl.getPrivateNIC(), "any");
			}
		}
		
		enableUfwFirewall();
	}
}
