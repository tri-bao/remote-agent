/**
 * 
 */
package org.funsoft.remoteagent.installer.os.config;


/**
 * @author htb
 *
 */
public class NetworkInterfaceConfigDto {
	private int routeTableIndex;
	private String routetableName;
	private int routingRulePriority;
	private int interfaceIndex;
	private String interfaceIP;
	private String netmask;
	private String network;
	private String broadcast;
	private String gateway;
	private String subnet;
	
	public int getRouteTableIndex() {
		return routeTableIndex;
	}
	public void setRouteTableIndex(int routeTableIndex) {
		this.routeTableIndex = routeTableIndex;
	}
	public String getRoutetableName() {
		return routetableName;
	}
	public void setRoutetableName(String routetableName) {
		this.routetableName = routetableName;
	}
	public int getRoutingRulePriority() {
		return routingRulePriority;
	}
	public void setRoutingRulePriority(int routingRulePriority) {
		this.routingRulePriority = routingRulePriority;
	}
	public int getInterfaceIndex() {
		return interfaceIndex;
	}
	public void setInterfaceIndex(int interfaceIndex) {
		this.interfaceIndex = interfaceIndex;
	}
	public String getInterfaceIP() {
		return interfaceIP;
	}
	public void setInterfaceIP(String interfaceIP) {
		this.interfaceIP = interfaceIP;
	}
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getBroadcast() {
		return broadcast;
	}
	public void setBroadcast(String broadcast) {
		this.broadcast = broadcast;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getSubnet() {
		return subnet;
	}
	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}
}
