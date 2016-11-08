package org.funsoft.remoteagent.file;

import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.util.FileUtils;

/**
 * @author Ho Tri Bao
 *
 */
public class AnyFileDownloader extends AbstractRemoteFileDownloader {
	private static AnyFileDownloader instance;
    public static AnyFileDownloader getInstanse() {
        if (instance == null) {
            instance = new AnyFileDownloader();
        }
        return instance;
    }

	protected AnyFileDownloader() {
		super("Copy 1 file bất kỳ từ remote server");
	}

	@Override
	protected String getRemoteFileParentPath() {
		return null;
	}

	@Override
	protected String getRemoteFileName() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		String remoteAbsolutePath = FileUtils.askRemotePath();
		int idx = remoteAbsolutePath.lastIndexOf('/');
		if (idx < 0) {
			throw new ExitInstallerRuntimeException("Không xác định được tên file (không xác định được "
					+ "ký tự / trong đường dẫn)");
		}

		String remoteFileName = remoteAbsolutePath.substring(idx + 1);
		String remotePath = remoteAbsolutePath.substring(0, idx);
		
		if (!confirm("Bạn muốn copy file: " + remoteFileName
				+ "\n\n từ thư mục: " + remotePath
				+ "\n\n trên máy chủ?")) {
			return;
		}
		
		copy(remoteFileName, remotePath);
	}

}
