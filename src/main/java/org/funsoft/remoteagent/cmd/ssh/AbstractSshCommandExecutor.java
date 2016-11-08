package org.funsoft.remoteagent.cmd.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.funsoft.remoteagent.cmd.core.AbstractCommandExecutor;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;

import java.io.*;

/**
 * @author Ho Tri Bao
 *
 */
public abstract class AbstractSshCommandExecutor extends AbstractCommandExecutor {
	private static final int MAX_WAIT_BEFORE_ASK_ABORT = 45; // second

    private final Session session;
    private final HostDto host;
    public AbstractSshCommandExecutor(HostDto host, Session session) {
    	this.host = host;
        this.session = session;
    }

    @Override
	public CommandResult exec(String command) {
        try {
			return executeCommandInternal(command);
		} catch (Exception e) {
			throw new ExitInstallerRuntimeException(e);
		}
    }
    
    private CommandResult executeCommandInternal(String command)
            throws JSchException, IOException {
        traceCommand(command);
        
        Channel channel = session.openChannel("exec");

        setCommand((ChannelExec) channel, command);

        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();
        
        ByteArrayOutputStream bErrS = new ByteArrayOutputStream();
        ((ChannelExec) channel).setErrStream(new PrintStream(bErrS));
        
        channel.connect();

        try {
            writeCommandOutput(out);
            out.flush();

            String m;
            StringBuilder message = new StringBuilder();
            byte[] tmp = new byte[1024];
            int sleepCountWithoutMessage = 0;
            while (true) {
                while (in.available() > 0) {
                	sleepCountWithoutMessage = 0;
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    m = new String(tmp, 0, i);
                    message.append(m);
                    System.out.print(m);
                }
                if (channel.isClosed()) {
                    break;
                }
                
            	// this is to prevent entering sudo password
            	if ((message.length() == 0)
            			&& (sleepCountWithoutMessage >= MAX_WAIT_BEFORE_ASK_ABORT)) {
            		if (RemoteAgentGui.showConfirmationYesNo(
            				"Command đang chờ quá lâu (hơn "
            				+ MAX_WAIT_BEFORE_ASK_ABORT + "s) trên máy: " + host.getDisplayInfo() + "\n\n"
            				+ "Có khả năng sudo password đã bị nhập trật.\n"
            				+ "Muốn dừng lại không?")) {
            			throw new ExitInstallerRuntimeException();
            		}
            		sleepCountWithoutMessage = 0;
            	}
            	
                try {
					Thread.sleep(1000);
				} catch (Exception e) {
					throw new ExitInstallerRuntimeException(e);
				}
				sleepCountWithoutMessage++;
            }
            
            String errorStr = null;
            if (bErrS.size() > 0) {
                errorStr = new String(bErrS.toByteArray());
                System.out.print("    " + errorStr);
            }
            System.out.println("    exit-status: " + channel.getExitStatus());
            
            return new CommandResult(errorStr, message.toString(), channel.getExitStatus());
        } finally {
            channel.disconnect();
        }
    }
    
    protected abstract void setCommand(ChannelExec channel, String cmd);
    protected abstract void writeCommandOutput(OutputStream out) throws IOException;
}
