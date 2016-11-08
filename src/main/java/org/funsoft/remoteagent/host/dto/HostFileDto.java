/**
 * 
 */
package org.funsoft.remoteagent.host.dto;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author htb
 *
 */
public class HostFileDto implements Serializable {
	private String name;
	private List<HostFileEntryDto> ipToNames;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<HostFileEntryDto> getIpToNames() {
		return ipToNames;
	}
	public void setIpToNames(List<HostFileEntryDto> ipToNames) {
		this.ipToNames = ipToNames;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
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
		HostFileDto other = (HostFileDto) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public String formatHostFile() {
		StringBuilder bd = new StringBuilder();
		if (CollectionUtils.isEmpty(ipToNames)) {
			return bd.toString();
		}
		Collections.sort(ipToNames, new Comparator<HostFileEntryDto>() {
			@Override
			public int compare(HostFileEntryDto o1, HostFileEntryDto o2) {
				int ip = o1.getIp().compareTo(o2.getIp());
				if (ip != 0) {
					return ip;
				}
				return o1.getDns().compareTo(o2.getDns());
			}
		});
		for (HostFileEntryDto et : ipToNames) {
			bd.append(et.getIp()).append('\t').append(et.getDns()).append('\n');
		}
		return bd.toString();
	}
}
