/**
 * 
 */
package org.funsoft.remoteagent.installer.os.dto;

/**
 * @author htb
 *
 */
public class ProcessDto {
	private int pid;
	private String uid;
	private String cmd;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
}
