package org.funsoft.remoteagent.cmd.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.funsoft.remoteagent.host.dto.HostDto;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ho Tri Bao
 *
 */
public class SudoCommandExecutor extends AbstractSshCommandExecutor {
    private final String sudoPass;
    
    public SudoCommandExecutor(HostDto host, Session session) {
        super(host, session);
        this.sudoPass = host.getPassword();
    }

    @Override
    protected void setCommand(ChannelExec channel, String cmd) {
        // man sudo
        // -S The -S (stdin) option causes sudo to read the password from the
        // standard input instead of the terminal device.
        // -p The -p (prompt) option allows you to override the default
        // password prompt and use a custom one.
        channel.setCommand("sudo -S -p '' " + cmd);
    }

    @Override
    protected void writeCommandOutput(OutputStream out) throws IOException {
        out.write((sudoPass + "\n").getBytes());
    }

}
