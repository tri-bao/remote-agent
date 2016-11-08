/**
 * 
 */
package org.funsoft.remoteagent.host.dto;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.tag.dto.TagDto;

import java.io.Serializable;
import java.util.List;


/**
 * @author htb
 *
 */
public class HostDto implements Serializable {
	private String uuid;
	private String displayName;
	private String host;
	private int port;
	private String username;
	
	// either password or privateKyeFilePath are filled. Never both
	private String password;
	private String privateKeyFilePath;
	
	private String internalIp;
	private List<String> internalDns;
	private List<TagDto> tags; // category
	private String description;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPrivateKeyFilePath() {
		return privateKeyFilePath;
	}
	public void setPrivateKeyFilePath(String privateKeyFilePath) {
		this.privateKeyFilePath = privateKeyFilePath;
	}
	public List<String> getInternalDns() {
		return internalDns;
	}
	public void setInternalDns(List<String> internalDns) {
		this.internalDns = internalDns;
	}
	public List<TagDto> getTags() {
		return tags;
	}
	public void setTags(List<TagDto> tags) {
		this.tags = tags;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getInternalIp() {
		return internalIp;
	}
	public void setInternalIp(String internalIp) {
		this.internalIp = internalIp;
	}

	@Override
	public String toString() {
		return host;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HostDto other = (HostDto) obj;
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}
	
	public String getDisplayInfo() {
		if (StringUtils.isNotBlank(displayName)) {
			return displayName + " - " + host;
		}
		return host;
	}
}
