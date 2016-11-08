/**
 * 
 */
package org.funsoft.remoteagent.file;

import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.util.FileChooser;
import org.funsoft.remoteagent.util.FileUtils;

import java.io.File;

/**
 * @author htb
 *
 */
public class FileUploader extends AbstractInstaller {
	private static FileUploader instance;
	public static FileUploader getInstance() {
		if (instance == null) {
			instance = new FileUploader();
		}
		return instance;
	}
	
	protected FileUploader() {
		super("Đẩy file lên máy chủ");
	}

	@Override
	public String getDescription() {
		return "Đẩy file từ máy local lên máy chủ";
	}

	@Override
	protected void performInternal() throws Exception {
		File chooseFile = FileChooser.chooseFile("CHỌN FILE CẦN ĐẨY LÊN SERVER");
		if (chooseFile == null) {
			return;
		}
		String remoteAbsolutePath = FileUtils.askRemotePath();
		if (remoteAbsolutePath == null) {
			return;
		}
		
		scpToRemote(chooseFile.getAbsolutePath(), remoteAbsolutePath);
	}

}
