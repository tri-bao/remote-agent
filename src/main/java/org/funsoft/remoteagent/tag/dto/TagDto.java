/**
 * 
 */
package org.funsoft.remoteagent.tag.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author htb
 *
 */
public class TagDto implements Serializable, Comparable<TagDto> {
	private String name;
	private String description;
	
	public TagDto() {
	}
	public TagDto(String name) {
		this.name = name;
	}
	public TagDto(String name, String description) {
		this.name = name;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((name == null) ? 0 : StringUtils.lowerCase(name).hashCode());
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
		TagDto other = (TagDto) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!StringUtils.lowerCase(name).equals(StringUtils.lowerCase(other.name))) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return name + (StringUtils.isBlank(description) ? "" : " - " + description);
	}
	
	@Override
	public int compareTo(TagDto o) {
		return name.compareTo(o.name);
	}
}
