/**
 * 
 */
package org.funsoft.remoteagent.host.dto;

import com.jcraft.jsch.Session;

/**
 * @author htb
 *
 */
public class HostWithSessionDto {
	private final HostDto host;
	private final Session session;
	public HostWithSessionDto(HostDto host, Session session) {
		super();
		this.host = host;
		this.session = session;
	}
	public HostDto getHost() {
		return host;
	}
	public Session getSession() {
		return session;
	}
}
