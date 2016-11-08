package org.funsoft.remoteagent.cmd.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.funsoft.remoteagent.host.dto.HostDto;

import java.io.OutputStream;

/**
 * @author Ho Tri Bao
 *
 */
public class CommandExecutor extends AbstractSshCommandExecutor {

    public CommandExecutor(HostDto host, Session session) {
        super(host, session);
    }

    @Override
    protected void setCommand(ChannelExec channel, String cmd) {
        channel.setCommand(cmd);
    }

    @Override
    protected void writeCommandOutput(OutputStream out) {
    }
    
}
