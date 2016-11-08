/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.installer.cnffile.RemoteConfigFileEditor;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.os.config.NetworkInterfaceConfigDto;
import org.funsoft.remoteagent.installer.os.config.NetworkInterfaceConfigPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 *
 */
public class ConfigureNetworkInterfaceInstaller extends AbstractOsInstaller {
	private static ConfigureNetworkInterfaceInstaller instance;
	public static ConfigureNetworkInterfaceInstaller getInstance() {
		if (instance == null) {
			instance = new ConfigureNetworkInterfaceInstaller();
		}
		return instance;
	}
	
	protected ConfigureNetworkInterfaceInstaller() {
		super("OS - configure Network interface");
	}

	@Override
	public String getDescription() {
		return "Add/update network interface configuration and update route table";
	}

	@Override
	protected void performInternal() throws Exception {
		// precheck, load route table
		RemoteConfigFileEditor editor = new RemoteConfigFileEditor(this);
		String routeTables = editor.readRemoteTextFileAsString("/etc/iproute2/rt_tables", false);
		if (StringUtils.isBlank(routeTables)) {
			throw new ExitInstallerRuntimeException("Route table (/etc/iproute2/rt_tables) does not exist.\n"
					+ "iproute program must be installed first");
		}
		
		NetworkInterfaceConfigPanel pnl = NetworkInterfaceConfigPanel.showInputExitIfCancel(hostInfo,
				NetworkInterfaceConfigPanel.class,
				"NETWORK INTERFACE CONFIG", 574, 600);
		NetworkInterfaceConfigDto nicDto = pnl.collectDto();
		
		String infName = "eth" + nicDto.getInterfaceIndex();
		String infConfig = editor.readRemoteTextFileAsString("/etc/network/interfaces.d/" + infName + ".cfg", false);
		if (StringUtils.isNotBlank(infConfig)) {
			if (!confirm("Config của interface " + infName + " đã tồn tại.\n\n"
					+ "Muốn update nó không?")) {
				throw new ExitInstallerRuntimeException();
			}
		}
		
		updateRouteTable(routeTables, nicDto);
		configureInterface(nicDto);
	}

	private void updateRouteTable(String routeTables, NetworkInterfaceConfigDto nicDto) throws Exception {
		// check for exiting table
		int foundIndex = -1;
		String[] lines = StringUtils.split(routeTables, "\n");
		for (int i = 0; i < lines.length; i++) {
			String line = StringUtils.stripToEmpty(lines[i]);
			if (StringUtils.isBlank(line)) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			String[] parts = StringUtils.split(line);
			if (parts.length < 2) {
				continue;
			}
			if (StringUtils.equals(StringUtils.stripToEmpty(parts[1]), nicDto.getRoutetableName())) {
				foundIndex = i;
				break;
			}
		}
		
		if (foundIndex >= 0) {
			if (!confirm("Routable " + nicDto.getRoutetableName() + " đã tồn tại rồi.\n\n"
					+ "Giờ muốn gì?\n"
					+ "- Yes: Cập nhật table index"
					+ "- No: dừng lại, không làm nữa")) {
				
				throw new ExitInstallerRuntimeException();
			}
		}
		
		List<String> result = new ArrayList<>(lines.length);
		if (foundIndex >= 0) {
			for (int i = 0; i < lines.length; i++) {
				if (i != foundIndex) {
					result.add(lines[i]);
				} else {
					result.add(nicDto.getRouteTableIndex() + "\t" +  nicDto.getRoutetableName());
				}
			}
		} else {
			result.addAll(Arrays.asList(lines));
			result.add(nicDto.getRouteTableIndex() + "\t" +  nicDto.getRoutetableName());
		}
		
		routeTables = StringUtils.join(result, "\n");
		RemoteConfigFileEditor editor = new RemoteConfigFileEditor(this);
		editor.show(routeTables, "/etc/iproute2/rt_tables");
	}
	
	private void configureInterface(NetworkInterfaceConfigDto nicDto) throws Exception {
		String infName = "eth" + nicDto.getInterfaceIndex();
		String[] lines = new String[] {
				"auto " + infName,
				"iface " + infName + " inet static",
					"\taddress " + nicDto.getInterfaceIP(),
					"\tnetmask " + nicDto.getNetmask(),
					"\tnetwork " + nicDto.getNetwork(),
					"\tbroadcast " + nicDto.getBroadcast(),
					"\tup ip route add default via " + nicDto.getGateway() + " dev " + infName + " table " + nicDto.getRoutetableName(),
					"\tup ip rule add from " + nicDto.getSubnet() + " lookup " + nicDto.getRoutetableName()
						+ " prio " + nicDto.getRoutingRulePriority(),
		};
		RemoteConfigFileEditor editor = new RemoteConfigFileEditor(this);
		editor.show(StringUtils.join(lines, "\n"), "/etc/network/interfaces.d/" + infName + ".cfg");
	}
}
