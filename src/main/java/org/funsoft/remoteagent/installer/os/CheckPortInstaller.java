/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.cnf.InputUtils;
import org.funsoft.remoteagent.gui.component.ReadOnlyTextPane;
import org.funsoft.remoteagent.main.RemoteAgentGui;

/**
 * @author htb
 *
 */
public class CheckPortInstaller extends AbstractOsInstaller {

	private static CheckPortInstaller instance;
	
	public static CheckPortInstaller getInstance() {
		if (instance == null) {
			instance = new CheckPortInstaller();
		}
		return instance;
	}

	protected CheckPortInstaller() {
		super("OS - check port availablity");
	}
	
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		String port;
		do {
			port = InputUtils.askSingleTextExitIfNull("Port to check", null);
		} while (!StringUtils.isNumeric(port));
		
        CommandResult res = listInusedPort(Integer.parseInt(port));
        if (doesResultSayPortAvailable(res)) {
        	RemoteAgentGui.showInfoMsg("Port " + port + " is avaialble");
        } else {
        	CommandResult fuser = sshSudoStrict("fuser -vf " + port + "/tcp");
        	ReadOnlyTextPane.showText("PORT IN-USED",
        			res.getOkStr() + "\n"
        			+ "PID: " + fuser.getOkStr() + "\n"
        			+ fuser.getErrorStr(),
        			true);
        }
	}

}
