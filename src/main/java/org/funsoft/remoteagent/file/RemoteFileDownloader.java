package org.funsoft.remoteagent.file;

import org.funsoft.remoteagent.installer.core.AbstractCompositeInstaller;
import org.funsoft.remoteagent.installer.core.IInstaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class RemoteFileDownloader extends AbstractCompositeInstaller {

	private static RemoteFileDownloader instance;
    public static RemoteFileDownloader getInstanse() {
        if (instance == null) {
            instance = new RemoteFileDownloader();
        }
        return instance;
    }

    private final List<IInstaller> subInstallers = new ArrayList<>(
			Arrays.asList(
					file("/var/log/", "syslog", "syslog"),
					file("/etc/mysql/", "my.cnf", "config file: mysql"),
					AnyFileDownloader.getInstanse()));

	@Override
	public List<IInstaller> getSubInstaller() {
		return subInstallers;
	}

    private PredefinedFileDownloader file(String parentPath,
			String fileName, String shortName) {
    	return new PredefinedFileDownloader(parentPath, fileName, shortName);
    }
    
    protected RemoteFileDownloader() {
		super("Lấy file từ máy chủ");
	}
}
