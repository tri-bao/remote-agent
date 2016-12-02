/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.cnf.InputUtils;
import org.funsoft.remoteagent.gui.component.ReadOnlyTextPane;
import org.funsoft.remoteagent.installer.cnffile.RemoteConfigFileEditor;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;


/**
 * @author htb
 *
 */
public class AddNewHardDriveInstaller extends AbstractOsInstaller {
	private static AddNewHardDriveInstaller instance;
	public static AddNewHardDriveInstaller getInstance() {
		if (instance == null) {
			instance = new AddNewHardDriveInstaller();
		}
		return instance;
	}
	
	private AddNewHardDriveInstaller() {
		super("OS - Lắp đĩa cứng mới");
	}
	
	@Override
	public String getDescription() {
		return "Attaching a new hard disk (formated or not yet) to a machine. "
				+ "If the disk is partition and formated, this can be used to "
				+ "mount it";
	}

	@Override
	protected void performInternal() throws Exception {
		// Here is the procedure for doing this manually
		//  df -h
		//  sudo lsblk
		//	==> determine logical name of the new disk (ex: /dev/sdb)
		// If this is a completely new disk
		//  sudo sfdisk /dev/sdb
		//		enter: 0,
		//		just press enter for the rest of partition
		//		answer y for the following question
		//			Warning: no primary partition is marked bootable (active)
		//			This does not matter for LILO, but the DOS MBR will not boot this disk.
		//			Do you want to write this to disk? [ynq] y
		//	sudo mkfs.ext4 /dev/sdb1
		//	sudo tune2fs -m 0 /dev/sdb1 (reclaim all reserved blocks - 5%)
		// Create mount point
		//	sudo mkdir -m 000 /vol1
		// Mount auto on reboot
		//	sudo nano /etc/fstab
		//	/dev/sdb1    /vol1   auto    defaults     0        2
		// Mount now
		//	sudo mount /vol1
		
		String logicalName = determineDiskLogicalName();
		
		boolean isNewDisk = isNewUnformatedDisk(logicalName);
		
		if (isNewDisk) {
			// make a partition for the entire disk
			//executeAsBatchSudo("echo \",,L\" | sudo sfdisk " + logicalName);
			executeAsBatchSudo(
					"set -e",
					"sudo sfdisk " + logicalName + " << EOF",
					"0,",
					"EOF");
			
			logicalName += "1";// there is only one partition on this disk
			// format
			sshSudoStrict("mkfs.ext4 " + logicalName);
			// reclaim all reserved blocks - 5%
			sshSudoStrict("tune2fs -m 1 " + logicalName);
		}
		
		// make sure this is a partition logical name, not disk name
		if (!isPartitionName(logicalName)) {
			throw new ExitInstallerRuntimeException("\"" + logicalName
					+ "\" phải là 1 partion. Logical name "
					+ "này là tên 1 đĩa cứng chứ không phải 1 partition");
		}
		
		String mountPoint = askMountPoint();
		
		addToFsTab(logicalName, mountPoint);
		
		// mount for immediately used
		sshSudoStrict("mount " + mountPoint);
	}

	private boolean isPartitionName(String name) {
		char lastChar = name.charAt(name.length() - 1);
		return (lastChar >= '0') && (lastChar <= '9');
	}
	
	private String determineDiskLogicalName() throws Exception {
		CommandResult cmdResult = sshStrict("df -h");
		String dfResult = cmdResult.getOkStr();
		
		cmdResult = sshSudoStrict("lsblk");
		
		ReadOnlyTextPane.showText("LIST OF DISKS",
				"Hãy xác định logical name (vd: /dev/sdb) của đĩa mới lắp. Copy it\n"
				+ "\n\n"
				+ "============result of command: df -h (all in-used devices) ================\n"
				+ dfResult
						+ "\n\n========result of command: lsblk (all disks)====================\n"
				+ cmdResult.getOkStr(),
				false);
		
		return InputUtils.askSingleTextExitIfNull("Logical name of new disk", null);
	}

	private boolean isNewUnformatedDisk(String logicalName) throws Exception {
		CommandResult testRs = sshSudoStrict("sfdisk -l "
				+ (isPartitionName(logicalName)
						? logicalName.substring(0, logicalName.length() - 1)
								: logicalName));
		
		boolean isNew = StringUtils.contains(testRs.getErrorStr(), "No partitions found");
		
		// cross check
		if (confirm("Đây là 1 đĩa hoàn toàn mới hay đĩa đã có dữ liệu?\n\n"
				+ "Nếu là hoàn toàn mới, nó sẽ được partition và format lại la nha ==> mọi thứ trong đó đi tong hết à\n\n"
				+ "Nếu là mới, nhấn <Yes>")) {
			// double check
			if (!isNew) {
				throw new ExitInstallerRuntimeException("Khác với bạn, hệ thống đã xác định " + logicalName
						+ " đây là 1 đĩa ĐÃ partition");
			}
		} else if (isNew) {
			throw new ExitInstallerRuntimeException("Khác với bạn, hệ thống đã xác định " + logicalName
					+ " là 1 đĩa CHƯA partition");
		}
		return isNew;
	}

	private String askMountPoint() throws Exception {
		String mountPoint;
		do {
			mountPoint = InputUtils.askSingleTextExitIfNull("Mount point", null);
			if (doesFileOrFolderExist(mountPoint)) {
				if (!confirm("Mount point \"" + mountPoint + "\" đã tồn tại rồi. Muốn dùng lại không?")) {
					continue;
				}
				// make sure this folder is empty
				if (!isFolderEmpty(mountPoint)) {
					RemoteAgentGui.showInfoMsg("Mount point \"" + mountPoint + "\" không phải là thư mục rỗng. Chọn cái khác đi");
					continue;
				}
			} else {
				// create mount point
				// use permission 000 to ensure nothing is written to that director
				// until a disk is is mounted on it
				sshSudoStrict("mkdir -m 000 " + mountPoint);
			}
			break;
		} while (true);
		return mountPoint;
	}

	private void addToFsTab(String deviceName, String mountPoint) throws Exception {
		deviceName = StringUtils.stripToEmpty(deviceName);
		mountPoint = StringUtils.stripToEmpty(mountPoint);
		
		RemoteConfigFileEditor editor = new RemoteConfigFileEditor(this);
		String fstab = editor.readRemoteTextFileAsString("/etc/fstab", true);
		// /dev/sdb    /vol1   auto    defaults     0        2
		
		// make sure this device is not yet mounted
		boolean existed = false;
		String[] lines = StringUtils.split(fstab, "\n");
		for (String line : lines) {
			if (StringUtils.isBlank(line)) {
				continue;
			}
			line = StringUtils.stripToEmpty(line);
			if (line.startsWith("#")) {
				continue;
			}
			
			String[] fields = StringUtils.split(line);
			
			if (StringUtils.stripToEmpty(fields[0]).equals(deviceName)) {
				RemoteAgentGui.showInfoMsg("Device \"" + deviceName
						+ "\" đã tồn tại trong fstab rồi, không thêm vào nữa");
				existed = true;
				break;
			}
			if (StringUtils.stripToEmpty(fields[1]).equals(mountPoint)
					|| (mountPoint.endsWith("/")
							&& StringUtils.stripToEmpty(fields[1]).equals(
									mountPoint.substring(0, mountPoint.length() - 1)))
					|| StringUtils.stripToEmpty(fields[1]).equals(mountPoint + "/")) {
				RemoteAgentGui.showInfoMsg("Mount point \"" + mountPoint
						+ "\" đã tồn tại trong fstab rồi, không thêm vào nữa");
				existed = true;
				break;
			}
		}
		
		if (!existed) {
			fstab = StringUtils.stripToEmpty(fstab) + "\n"
					+ deviceName + "\t" + mountPoint + "\t" + " auto \t defaults \t 0 \t 2"
					+ "\n"; // there must be a new line at the end of this file
		}
		
		editor.show(fstab, "/etc/fstab");
	}
	
}
