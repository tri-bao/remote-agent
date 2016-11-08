/**
 * 
 */
package org.funsoft.remoteagent.installer.cnffile;

import org.funsoft.remoteagent.cnf.InputUtils;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;

/**
 * @author htb
 *
 */
public class RemoteConfigFileEditorInstaller extends AbstractInstaller {
	private static RemoteConfigFileEditorInstaller instance;
	public static RemoteConfigFileEditorInstaller getIntance() {
		if (instance == null) {
			instance = new RemoteConfigFileEditorInstaller();
		}
		return instance;
	}
	private String remoteFile;
	private RemoteConfigFileEditorInstaller() {
		super("Edit remote text file");
	}
	
	public RemoteConfigFileEditorInstaller(HostDto host, String remoteFile) {
		super("Edit file " + remoteFile);
		this.hostInfo = host;
		this.remoteFile = remoteFile;
	}
	
	@Override
	public String getDescription() {
		return "";
	}

	@Override
	protected void performInternal() throws Exception {
		if (this.remoteFile != null) {
			new RemoteConfigFileEditor(this).showForEdit(this.remoteFile);
		} else {
			String rmFile = InputUtils.askSingleText("REMOTE FILE PATH", true, null);
			new RemoteConfigFileEditor(this).showForEdit(rmFile);
		}
	}

	public void connectAndEdit() throws Exception {
		connectAndPerform();
	}
}
