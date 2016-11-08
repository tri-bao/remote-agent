/**
 *
 */
package org.funsoft.remoteagent.installer.cnffile;

import org.apache.commons.io.IOUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.installer.core.AbstractUtilInstaller;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author htb
 */
public class RemoteConfigFileEditor extends AbstractUtilInstaller {

    public RemoteConfigFileEditor(AbstractInstaller mainInstaller) {
        super(mainInstaller);
    }

    public void showForEditOrCreate(String remoteFile) throws Exception {
        showForEdit(remoteFile, false);
    }

    public void showForEdit(String remoteFile) throws Exception {
        showForEdit(remoteFile, true);
    }

    private void showForEdit(String remoteFile, boolean exitIfNotExisted) throws Exception {
        String txt = readRemoteTextFileAsString(remoteFile, exitIfNotExisted);

        show(txt, remoteFile);
    }

    public String readRemoteTextFileAsString(String remoteFile, boolean exitIfNotExisted) throws Exception {
        // zip remote file
        boolean downloadFailed = false;
        CommandResult commandResult = sshSudo("gzip -c " + remoteFile + " > " + getStagingFolder() + "/cnf.tmp.gz");
        if (commandResult.exitWithErrorCode()) {
            if (exitIfNotExisted || !commandResult.containErrorString("No such file or directory")) {
                System.out.println("Failed to download file");
                throw new ExitInstallerRuntimeException();
            }
            downloadFailed = true;
        }

        // download to local
        return downloadFailed ? "" : readRemoteGzipTextFileAsString(getStagingFolder() + "/cnf.tmp.gz");
    }

    public void show(String txt, String remoteFile) throws UnsupportedEncodingException,
            FileNotFoundException, IOException, Exception {
        TextContextEditPanel pnl = TextContextEditPanel.show(remoteFile, txt);
        String modifiedContent = pnl.getNewContent();

        String localTemp = temporaryPath("text.tmp");
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(localTemp), "UTF-8");
        try {
            IOUtils.write(modifiedContent, osw);
        } finally {
            IOUtils.closeQuietly(osw);
        }

        int idx = remoteFile.lastIndexOf('/');

        boolean backup = pnl.isBackupCurrentFile();
        if (!backup) {
            if (!confirm("Bạn thật sự không muốn backup file config hiện tại trước khi ghi đè?\n\n"
                    + "Yes = không tạo file backup\n\n"
                    + "No = tạo file backup")) {
                backup = true;
            }
        }
        if (backup) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmms");
            CommandResult commandResult = sshSudo(
                    "cp " + remoteFile + " " + remoteFile + ".bak-" + sdf.format(new Date()));
            if (commandResult.exitWithErrorCode()) {
                if (!confirm("Không backup file hiện tại được (có thể nó không tồn tại). Muốn tiếp tục không?")) {
                    throw new ExitInstallerRuntimeException();
                }
            }
        }
        scpToRemoteUnderRoot(localTemp, remoteFile.substring(0, idx), remoteFile.substring(idx + 1));
    }
}
