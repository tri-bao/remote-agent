/**
 * 
 */
package org.funsoft.remoteagent.host.dto;

/**
 * @author htb
 *
 */
public class HostFileEntryDto {
	private String ip;
	private String dns;
	
	public HostFileEntryDto() {
	}
	public HostFileEntryDto(String ip, String dns) {
		this.ip = ip;
		this.dns = dns;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDns() {
		return dns;
	}
	public void setDns(String dns) {
		this.dns = dns;
	}
}
