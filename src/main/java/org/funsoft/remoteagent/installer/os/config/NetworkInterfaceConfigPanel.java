/**
 * 
 */
package org.funsoft.remoteagent.installer.os.config;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.*;

/**
 * @author htb
 *
 */
public class NetworkInterfaceConfigPanel extends AbstractConfigInputPanel {
	private final JTextField txtRouteTableIndex = GUIUtils.newTextField();
	private final JTextField txtRoutetableName = GUIUtils.newTextField();
	private final JTextField txtRoutingRulePriority = GUIUtils.newTextField();
	private final JTextField txtInterfaceIndex = GUIUtils.newTextField();
	private final JTextField txtInterfaceIP = GUIUtils.newTextField();
	private final JTextField txtNetmask = GUIUtils.newTextField();
	private final JTextField txtNetwork = GUIUtils.newTextField();
	private final JTextField txtBroadcast = GUIUtils.newTextField();
	private final JTextField txtGateway = GUIUtils.newTextField();
	private final JTextField txtSubnet = GUIUtils.newTextField();
	
	public NetworkInterfaceConfigPanel(HostDto currentHost) {
		super(currentHost);
		init();
	}
	
	@Override
	protected void initState() {
		txtRouteTableIndex.setText("2");
		txtRoutetableName.setText("eth1_table");
		txtInterfaceIndex.setText("1");
		txtRoutingRulePriority.setText("1000");
		txtInterfaceIP.setText("172.31.32.4");
		txtNetmask.setText("255.255.240.0");
		txtNetwork.setText("172.31.32.0");
		txtBroadcast.setText("172.31.47.255");
		txtGateway.setText("172.31.32.1");
		txtSubnet.setText("172.31.32.0/20");
		
		GUIUtils.fixWidth(txtRouteTableIndex, 50);
		GUIUtils.fixWidth(txtRoutingRulePriority, 50);
		GUIUtils.fixWidth(txtInterfaceIndex, 50);
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		JLabel lblWarn = lbl("CẢNH BÁO: NHẬP CÁC ĐỊA CHỈ CẨN THẬN, KHÔNG CÓ VALIDATION CHECK ĐÂU NHA");
		lblWarn.setForeground(Color.RED);
		
		GUIUtils.addSeparator("Route table", gbl);
		gbl.row().cell(lbl("Routable name")).cellXRemainder(txtRoutetableName).fillX();
		gbl.row().cell(lbl("Route table index")).cellXRemainder(txtRouteTableIndex);
		gbl.row().cell(lbl("Routing rule priorty")).cellXRemainder(txtRoutingRulePriority);
		note("priority for defining the routing rule for the subnet", gbl.row().cell(), gbl);
		note("ex ip rule add from 172.31.32.0/20 lookup eth1_table prio 1000", gbl.row().cell(), gbl);
		
		GUIUtils.addSeparator("NIC", gbl);
		gbl.row().cell(lbl("Interface name")).cell(lbl("eth")).cell(txtInterfaceIndex);
		note("1, 2, 3", gbl.row().cell().cell(), gbl);
		gbl.row().cellXRemainder(lblWarn);
		gbl.row().cell(lbl("Interface address")).cellXRemainder(txtInterfaceIP).fillX();
		gbl.row().cell(lbl("Netmask")).cellXRemainder(txtNetmask).fillX();
		gbl.row().cell(lbl("Network")).cellXRemainder(txtNetwork).fillX();
		gbl.row().cell(lbl("Broadcast")).cellXRemainder(txtBroadcast).fillX();
		gbl.row().cell(lbl("Gateway")).cellXRemainder(txtGateway).fillX();
		gbl.row().cell(lbl("Subnet")).cellXRemainder(txtSubnet).fillX();
	}

	@Override
	protected boolean checkValid() {
		return GUIUtils.requireMandatory(txtRoutetableName, "Routable name")
				&& GUIUtils.checkInteger(txtRouteTableIndex, "Route table index", true)
				&& GUIUtils.checkInteger(txtRoutingRulePriority, "Routing rule priority", true)
				
				&& GUIUtils.checkInteger(txtInterfaceIndex, "Interface index", true)
				&& GUIUtils.requireMandatory(txtInterfaceIP, "")
				&& GUIUtils.requireMandatory(txtNetmask, "Netmask")
				&& GUIUtils.requireMandatory(txtNetwork, "Network")
				&& GUIUtils.requireMandatory(txtBroadcast, "Broadcast")
				&& GUIUtils.requireMandatory(txtGateway, "Gateway")
				&& GUIUtils.requireMandatory(txtSubnet, "Submet")
				;
	}

	public NetworkInterfaceConfigDto collectDto() {
		NetworkInterfaceConfigDto dto = new NetworkInterfaceConfigDto();
		dto.setRouteTableIndex(GUIUtils.getInteger(txtRouteTableIndex));
		dto.setRoutetableName(GUIUtils.getText(txtRoutetableName));
		dto.setRoutingRulePriority(GUIUtils.getInteger(txtRoutingRulePriority));
		
		dto.setInterfaceIndex(GUIUtils.getInteger(txtInterfaceIndex));
		dto.setInterfaceIP(GUIUtils.getText(txtInterfaceIP));
		dto.setNetmask(GUIUtils.getText(txtNetmask));
		dto.setNetwork(GUIUtils.getText(txtNetwork));
		dto.setBroadcast(GUIUtils.getText(txtBroadcast));
		dto.setGateway(GUIUtils.getText(txtGateway));
		dto.setSubnet(GUIUtils.getText(txtSubnet));
	
		return dto;
	}
}
