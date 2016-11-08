/**
 *
 */
package org.funsoft.remoteagent.installer.connection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.cmd.local.LocalCommandExecutor;
import org.funsoft.remoteagent.host.controller.HostSelectionController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.util.OSUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author htb
 */
public class SshConsoleInstaller extends AbstractConnectionInstaller {
    private static SshConsoleInstaller instance;

    public static SshConsoleInstaller getInstance() {
        if (instance == null) {
            instance = new SshConsoleInstaller();
        }
        return instance;
    }

    protected SshConsoleInstaller() {
        super("Open SSH console");
    }

    @Override
    public String getDescription() {
        return "Only support Linux and MacOS";
    }

    @Override
    protected void performInternal() throws Exception {
        HostDto hostDto = HostSelectionController.select();
        openSshConsole(hostDto);
    }

    public static void openSshConsole(HostDto hostDto) throws IOException {
        if (hostDto == null) {
            return;
        }
        if (OSUtils.isLinux()) {
            openLinuxConsole(hostDto);
        } else if (OSUtils.isMacOs()) {
            openMacConsole(hostDto);
        } else {
            RemoteAgentGui.showErrorMsg("Only support Linux and MacOs");
        }
    }

    private static void openLinuxConsole(HostDto hostDto) throws IOException {

        String sshCmnd = buildSshCommand(hostDto);

        String[] cmdArray = {
                "gnome-terminal",
                "--working-directory=" + System.getProperty("user.home"),
                "--command=" + sshCmnd
        };

        open(cmdArray);
    }

    private static String buildSshCommand(HostDto hostDto) {
        String sshCmnd = "ssh";
        if (StringUtils.isNotBlank(hostDto.getPrivateKeyFilePath())) {

            LocalCommandExecutor chmod = new LocalCommandExecutor();
            chmod.exec("chmod 400 " + hostDto.getPrivateKeyFilePath());

            sshCmnd += " -i " + hostDto.getPrivateKeyFilePath();
        }
        sshCmnd += " " + hostDto.getUsername() + "@" + hostDto.getHost();
        if (hostDto.getPort() != 22) {
            sshCmnd += " -p " + hostDto.getPort();
        }
        return sshCmnd;
    }

    private static void openMacConsole(HostDto hostDto) throws IOException {
        File scriptFile = File.createTempFile("rmagent", "script");
        FileUtils.writeStringToFile(scriptFile, StringUtils.join(new String[]{
                "#!/usr/bin/env bash",
                "SCRIPT_FILE=$(mktemp /var/tmp/doublecmd-XXXX)",
                "echo '#!/usr/bin/env bash' > $SCRIPT_FILE",
                "echo \"trap 'rm -f $SCRIPT_FILE' INT TERM EXIT\" >> $SCRIPT_FILE",
                "echo \"clear\" >> $SCRIPT_FILE",
                "printf -v DIR \"%q\" \"$(pwd)\"",
                "echo \"cd $DIR\" >> $SCRIPT_FILE",
                "echo \"$@\" >> $SCRIPT_FILE",
                "chmod +x \"$SCRIPT_FILE\"",
                "open -b com.apple.terminal \"$SCRIPT_FILE\"",
        }, "\n"));

        scriptFile.setExecutable(true);

        String[] cmdArray = {
                scriptFile.getAbsolutePath(),
                buildSshCommand(hostDto),
        };

        open(cmdArray);
    }

    private static void open(String[] cmdArray) throws IOException {
        Process exec = Runtime.getRuntime().exec(cmdArray);
        try {
            CommandResult commandResult = LocalCommandExecutor.getCommandResult(exec);
            commandResult.printResult();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
