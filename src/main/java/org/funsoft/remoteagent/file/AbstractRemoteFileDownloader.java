package org.funsoft.remoteagent.file;

import org.funsoft.remoteagent.cmd.local.LocalCommandExecutor;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.util.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Ho Tri Bao
 *
 */
public abstract class AbstractRemoteFileDownloader extends AbstractInstaller {

	protected AbstractRemoteFileDownloader(String installerName) {
		super(installerName);
	}

	@Override
	public String getDescription() {
		if (getRemoteFileParentPath() == null) {
			return "";
		}
		return "(" + getRemoteFileAbsolutePath() + ")";
	}

	private String getRemoteFileAbsolutePath() {
		if (getRemoteFileName() != null) {
			if (getRemoteFileParentPath().endsWith("/")) {
				return getRemoteFileParentPath() + getRemoteFileName();
			}
			return getRemoteFileParentPath() + "/" + getRemoteFileName();
		}
		return getRemoteFileParentPath();
	}
	
	protected abstract String getRemoteFileParentPath();
	protected abstract String getRemoteFileName();

	protected void copy(String remoteFileName, String remotePath) throws Exception {
		remoteFileName = remoteFileName == null ? null : remoteFileName.trim();
		remotePath = remotePath.trim();
		if (remoteFileName == null) {
			int idx = remotePath.lastIndexOf('/');
			if (idx < 0) {
				throw new ExitInstallerRuntimeException(
						"Không thể xác định được tên file/folder trong đường dẫn");
			}
			remoteFileName = remotePath.substring(idx + 1).trim();
			remotePath = remotePath.substring(0, idx).trim();
		}
		
		if (remotePath.equals("/")) {
			throw new ExitInstallerRuntimeException(
					"Không được zip top level mount point /");
		}
		
		File choosenFolder = FileChooser.chooseFolder("CHỌN NƠI LƯU FILE");
		if (choosenFolder == null) {
			return;
		}
		
		
		String outputFileName = remoteFileName  + ".tar.gz";
		File outFile = new File(choosenFolder, outputFileName);
		if (outFile.exists()) {
			if (!confirm("File " + outputFileName
					+ " đã tồn tại ở thư mục được chọn. Bạn muốn ghi đè lên nó?")) {
				return;
			}
		}
		
		String remoteFile;
		if (remotePath.endsWith("/")) {
			remoteFile = remotePath + remoteFileName;
		} else {
			remoteFile = remotePath + "/" + remoteFileName;
		}
		if (!doesFileOrFolderExist(remoteFile)) {
			RemoteAgentGui.showErrorMsg("File " + remoteFile
					+ " không tồn tại");
			return;
		}
		
		String tmpZipFile = getStagingFolder() + "/" + outputFileName;
		// zip
		sshSudoStrict("tar -C " + remotePath + " -czf " + tmpZipFile + " " + remoteFileName);

		// copy to /tmp
		//sshSudoStrict("cp " + remoteFile + " " + getStagingFolder());

		sshSudoStrict("chown " + hostInfo.getUsername() + " " + tmpZipFile);

		scpFromRemote(tmpZipFile, outFile.getAbsolutePath());
		
		try {
			// doesn't work on ubuntu
			Desktop.getDesktop().open(choosenFolder);
		} catch (IOException e) {
			if ((System.getProperty("os.name", "unknown").toLowerCase().indexOf("linux") >= 0)) {
				new LocalCommandExecutor().exec("nautilus", new String[] {choosenFolder.getAbsolutePath()}, null, null);
			}
		}
	}
}