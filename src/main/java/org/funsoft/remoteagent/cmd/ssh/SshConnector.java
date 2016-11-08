package org.funsoft.remoteagent.cmd.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.util.OSUtils;

import java.io.File;

/**
 * @author Ho Tri Bao
 */
public class SshConnector {
    private static final int CONNECTION_TIMEOUT = 30 * 1000;

    public static Session connect(HostDto host) throws Exception {
        Session s = null;
        try {
            s = sshConnect(host);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null) {
                throw e;
            } else if (message.startsWith("SSH_MSG_DISCONNECT: 2 Too many authentication failures")) {
                String msg = "Cannot login with user '" + host.getUsername() + "'. You may enter the wrong password "
                        + "\nor this user is not allowed connecting via ssh";
                System.out.println(msg);
                throw new ExitInstallerRuntimeException(msg);
            } else if (message.startsWith("java.net.ConnectException: Connection timed out")) {
                String msg = "Cannot connect to machine at '" + host + "'.\n"
                        + "The address may be incorrect or the machine may stop";
                System.out.println(msg);
                throw new ExitInstallerRuntimeException(msg);
            } else if (message.startsWith("timeout: socket is not established")) {
                String msg = "Cannot connect to machine at '" + host + "' "
                        + "after " + (CONNECTION_TIMEOUT / 1000) + " seconds";
                System.out.println(msg);
                throw new ExitInstallerRuntimeException(msg);
            } else {
                throw e;
            }
        }
        return s;
    }

    private static Session sshConnect(HostDto host) throws Exception {
        JSch jsch = new JSch();

        if (OSUtils.isLinux()) {
            JSch.setConfig("StrictHostKeyChecking", "ask");

            File knowHostsFile = new File(System.getProperty("user.home") + "/.ssh/known_hosts");
            if (!knowHostsFile.exists()) {
                knowHostsFile.createNewFile();
            }
            jsch.setKnownHosts(knowHostsFile.getAbsolutePath());
        }

        if (StringUtils.isNotBlank(host.getPrivateKeyFilePath())) {
            jsch.addIdentity(host.getPrivateKeyFilePath());
        }

//        do {
        Session session = jsch.getSession(host.getUsername(), host.getHost(), host.getPort());

        if (StringUtils.isBlank(host.getPrivateKeyFilePath())) {
            session.setUserInfo(new UserInfoProvider(host));
        }

        try {
            session.connect(CONNECTION_TIMEOUT);
        } catch (JSchException e) {
            String message = e.getMessage();
            if (StringUtils.startsWithIgnoreCase(message, "UnknownHostKey: " + host.getHost())) {
                // What does this mean:
                // http://superuser.com/questions/421074/ssh-the-authenticity-of-host-host-cant-be-established
//					if (RemoteAgentGui.showConfirmationYesNo("you've never connected to this server before\n"
//							+ host.getHost()
//							+ "\nAre you sure you want to continue connecting?")) {

                // this setting will cause JSCH to automatically add all target servers' entry
                // to the known_hosts file
                JSch.setConfig("StrictHostKeyChecking", "no");
                JSch.setConfig("HashKnownHosts", "yes");

//						continue;
//					}
            } else {
                throw e;
            }
        }

        return session;
//        } while (true);
    }

    public static class UserInfoProvider implements UserInfo {
        private final HostDto host;

        public UserInfoProvider(HostDto host) {
            this.host = host;
        }

        @Override
        public void showMessage(String s) {
            System.out.println(s);
        }

        @Override
        public boolean promptYesNo(String s) {
            return true;
        }

        @Override
        public boolean promptPassword(String s) {
            return true;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return true;
        }

        @Override
        public String getPassword() {
            return host.getPassword();
        }

        @Override
        public String getPassphrase() {
            return null;
        }
    }
}
