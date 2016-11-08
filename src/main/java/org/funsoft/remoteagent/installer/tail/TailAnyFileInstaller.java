/**
 * 
 */
package org.funsoft.remoteagent.installer.tail;

import org.funsoft.remoteagent.host.dto.HostDto;

/**
 * @author htb
 *
 */
public class TailAnyFileInstaller extends AbstractTailFileInstaller {
	private final String filePath;
	public TailAnyFileInstaller(HostDto host, String filePath) {
		super("Tail file " + filePath);
		this.hostInfo = host;
		this.filePath = filePath;
	}
	public TailAnyFileInstaller(String name, String filePath) {
		super(name + " (" + filePath + ")");
		this.filePath = filePath;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		tail();
	}

	public void doTail() throws Exception {
		connectAndPerform();
	}
	
	@Override
	protected String getFilePath() {
		return filePath;
	}

}
