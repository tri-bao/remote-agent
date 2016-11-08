/**
 * 
 */
package org.funsoft.remoteagent.installer;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.core.NormalExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;

/**
 * @author htb
 *
 */
public class InstallerGuiExceptionHandler {
    public void handle(Throwable rte) {
    	if (rte instanceof NormalExitInstallerRuntimeException) {
    		return;
    	}
        System.out.println(RemoteAgentGui.getStacktraceAsString(rte));
        if ((rte instanceof ExitInstallerRuntimeException)
        		&& StringUtils.isNotBlank(rte.getMessage())) {
        	RemoteAgentGui.showErrorMsg(
                    "Exit with message: " + rte.getMessage()
                    + "\n\nSee log for stacktrace");
        } else {
        	RemoteAgentGui.showErrorMsg(
                    "Exit with exception: " + rte
                    	+ "\n\nSee log for stacktrace");
        }
    }

}
