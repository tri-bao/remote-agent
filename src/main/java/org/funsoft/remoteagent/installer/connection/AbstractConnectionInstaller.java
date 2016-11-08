/**
 * 
 */
package org.funsoft.remoteagent.installer.connection;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.host.controller.HostSelectionController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.connection.config.JmxPortInpurtPanel;
import org.funsoft.remoteagent.installer.connection.config.PortWithCommentDto;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.main.RemoteAgentGui;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author htb
 *
 */
public abstract class AbstractConnectionInstaller extends AbstractInstaller {

	protected AbstractConnectionInstaller(String installerName) {
		super(installerName);
	}
	
	@Override
	public void execute() throws Exception {
		performInternal();
	}
	
	protected PortWithCommentDto openSslTunneling(boolean asSockServer,
			PortWithCommentDto... predefinedPorts) throws Exception {
		JmxPortInpurtPanel pnl = new JmxPortInpurtPanel(predefinedPorts);
		JmxPortInpurtPanel.showInputExitIfCancel(pnl,
				"SELECT THE SSH PORT TO CONNECT TO", 500, 200);
		PortWithCommentDto selectedPort = pnl.getSelectedPort();
		if (selectedPort == null) {
			return null;
		}

		if (isPortNotOccupiedOnLocal(selectedPort.getPort())) {
			startSshProxy(selectedPort.getPort(), asSockServer);
			RemoteAgentGui.showInfoMsg("Wait until the SSH connection made then press OK");
		}
		return selectedPort;
	}
	private boolean isPortNotOccupiedOnLocal(int port) {
		ServerSocket ss = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	            }
	        }
	    }
	    return false;
	}
	
	private void startSshProxy(int port, boolean asSockServer) throws IOException {
		HostDto hostDto = HostSelectionController.select();
		if (hostDto == null) {
			return;
		}
		
		String sshCmnd = "ssh" + (hostDto.getPort() == 22 ? "" : " -p " + hostDto.getPort());
		if (StringUtils.isNotBlank(hostDto.getPrivateKeyFilePath())) {
			sshCmnd += " -i " + hostDto.getPrivateKeyFilePath();
		}
		sshCmnd +=
				(asSockServer
					? (" -D " + port)
					: (" -L " + port + ":localhost:" + port))
				+ " " + hostDto.getUsername() + "@" + hostDto.getHost();
		
        String[] cmdArray = {
        		"gnome-terminal",
        		"--working-directory=" + System.getProperty("user.home"),
        		"--command=" + sshCmnd
        		};

		Runtime.getRuntime().exec(cmdArray);
	}
}
