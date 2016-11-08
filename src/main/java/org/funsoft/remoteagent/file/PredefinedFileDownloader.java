package org.funsoft.remoteagent.file;


/**
 * @author Ho Tri Bao
 *
 */
public class PredefinedFileDownloader extends AbstractRemoteFileDownloader {
	private final String parentPath;
	private final String fileName;
	protected PredefinedFileDownloader(String parentPath,
			String fileName, String shortName) {
		super(shortName);
		this.parentPath = parentPath;
		this.fileName = fileName;
	}

	@Override
	protected String getRemoteFileParentPath() {
		return parentPath;
	}

	@Override
	protected String getRemoteFileName() {
		return fileName;
	}

	@Override
	protected void performInternal() throws Exception {
		copy(getRemoteFileName(), getRemoteFileParentPath());
	}

}
