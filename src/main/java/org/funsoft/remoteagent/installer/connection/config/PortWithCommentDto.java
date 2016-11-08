/**
 * 
 */
package org.funsoft.remoteagent.installer.connection.config;

/**
 * @author htb
 *
 */
public class PortWithCommentDto {
	private final int port;
	private final String comment;
	public PortWithCommentDto(int port, String comment) {
		this.port = port;
		this.comment = comment;
	}
	public int getPort() {
		return port;
	}
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((comment == null) ? 0 : comment.hashCode());
		result = (prime * result) + port;
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
		PortWithCommentDto other = (PortWithCommentDto) obj;
		if (comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!comment.equals(other.comment)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return port + " - " + comment;
	}
	
}
