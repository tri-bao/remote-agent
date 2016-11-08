/**
 * 
 */
package org.funsoft.remoteagent.installer.core;



/**
 * @author htb
 *
 */
public abstract class AbstractUtilInstaller extends AbstractInstaller {
	
	public AbstractUtilInstaller(AbstractInstaller mainInstaller) {
		super("");
		//session = mainInstaller == null ? null : mainInstaller.session;
		hostInfo = mainInstaller == null ? null : mainInstaller.hostInfo;
	}

	@Override
	public String getDescription() {
		throw new IllegalArgumentException("unexpected method call");
	}

	@Override
	protected void performInternal() throws Exception {
		throw new IllegalArgumentException("unexpected method call");
	}
	
}
