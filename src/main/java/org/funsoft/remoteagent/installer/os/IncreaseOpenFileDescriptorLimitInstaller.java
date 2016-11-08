/**
 * 
 */
package org.funsoft.remoteagent.installer.os;


/**
 * @author htb
 *
 */
public class IncreaseOpenFileDescriptorLimitInstaller extends AbstractOsInstaller {
	private final String OS_FILE_DESCRIPTOR_LIMIT = "1048576"; // maximum hard-coded constant in the Linux kernel

	private static IncreaseOpenFileDescriptorLimitInstaller instance;
	
	public static IncreaseOpenFileDescriptorLimitInstaller getInstance() {
		if (instance == null) {
			instance = new IncreaseOpenFileDescriptorLimitInstaller();
		}
		return instance;
	}
	
	protected IncreaseOpenFileDescriptorLimitInstaller() {
		super("OS - increate maximum file descriptor (ulimit)");
	}

	@Override
	public String getDescription() {
		return "cần cho xtrabackup";
	}

	@Override
	protected void performInternal() throws Exception {
		appendOrReplaceARemoteLine("* hard nofile ",
				"* hard nofile " + OS_FILE_DESCRIPTOR_LIMIT, "/etc/security/limits.conf");
		appendOrReplaceARemoteLine("* soft nofile ",
				"* soft nofile " + OS_FILE_DESCRIPTOR_LIMIT, "/etc/security/limits.conf");
		appendOrReplaceARemoteLine("root hard nofile ",
				"root hard nofile " + OS_FILE_DESCRIPTOR_LIMIT, "/etc/security/limits.conf");
		appendOrReplaceARemoteLine("* soft nofile ",
				"root soft nofile " + OS_FILE_DESCRIPTOR_LIMIT, "/etc/security/limits.conf");
		
		if (confirm("Lưu ý, open file descriptor limit vừa được thay đổi.\n"
				+ "Cần phải reboot server mới có tác dụng\n\n"
				+ "Muốn reboot không?")) {
			// cleanup
        	prepareLocalTempDir();
            cleanStagingFolder();
            sshSudo("reboot");
		}
	}

}
