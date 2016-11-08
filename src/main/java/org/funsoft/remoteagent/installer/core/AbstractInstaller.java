package org.funsoft.remoteagent.installer.core;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.cmd.ssh.*;
import org.funsoft.remoteagent.cnf.ConfigFileUtils;
import org.funsoft.remoteagent.cnf.InputUtils;
import org.funsoft.remoteagent.cnf.SimpleConfigFileModifier;
import org.funsoft.remoteagent.cnf.SimpleConfigFileModifier.PlaceHolder;
import org.funsoft.remoteagent.host.controller.HostSelectionController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.host.dto.HostWithSessionDto;
import org.funsoft.remoteagent.installer.os.dto.ProcessDto;
import org.funsoft.remoteagent.installer.os.dto.ProcessListResultDto;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.util.FileUtils;
import org.funsoft.remoteagent.util.Preferences;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * @author Ho Tri Bao
 */
public abstract class AbstractInstaller implements IInstaller {

    private static class SshSessionWraper {
        private final Session session;
        private final Map<String, Object> sessionData;

        public SshSessionWraper(Session session) {
            this.session = session;
            this.sessionData = new HashMap<>();
        }

        public Session getSession() {
            return session;
        }

        public Map<String, Object> getSessionData() {
            return sessionData;
        }

        public void disconnect() {
            session.disconnect();
            sessionData.clear();
        }
    }


    // see standard: http://www.pathname.com/fhs/pub/fhs-2.3.html

    protected static final Dimension SZ = new Dimension(150, 25);

    private final String installerName;

    private static final Map<HostDto, SshSessionWraper> SSH_SESSIONS = new HashMap<>();

    //protected Session session;
    protected HostDto hostInfo;

    protected String localTempDir = FileUtils.getCurrentDir() + File.separatorChar + "tmp";
    protected String configTemplateDir = FileUtils.getCurrentDir() + File.separatorChar + "cnf-files";

    protected AbstractInstaller(String installerName) {
        this.installerName = installerName;
    }

    public static void closeAllSshSessions() {
        System.out.println("Closing all ssh sessions");
        for (Entry<HostDto, SshSessionWraper> et : SSH_SESSIONS.entrySet()) {
            try {
                if (et.getValue() != null) {
                    System.out.println("Closing " + et.getKey());
                    et.getValue().disconnect();
                    et.setValue(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute() throws Exception {
        prepareLocalTempDir();
        beforeRepeatedlyPerform();
        do {
            connectAndPerform();
        } while (repeatedly());
    }

    protected boolean repeatedly() {
        return false;
    }

    protected void beforeRepeatedlyPerform() {
    }

    protected void connectAndPerform() throws Exception {
        connect();
        if (!confirm("Chuẩn bị tiến hành\n\n"
                + getName()
                + "\nTrên host: " + hostInfo.getDisplayInfo()
                + "\n\nĐồng ý không?")) {
            throw new NormalExitInstallerRuntimeException();
        }
        try {
            prepareStagingFolder();
            performInternal();
        } finally {
            System.out.println("======Cleaning up");
            prepareLocalTempDir();
            cleanStagingFolder();
            disconnect();
        }
    }

    protected abstract void performInternal() throws Exception;

    public void disconnect() {
//    	if (session == null) {
//    		return;
//    	}
//    	System.out.println("======Disconnecting");
//        session.disconnect();
//        session = null;
        // just clear current host
        hostInfo = null;
    }

    protected void executeAsBatchSudo(String... lines) throws Exception {
        String remoteFile = getStagingFolder() + "/tmp.script";
        try {
            ConfigFileUtils.writeFile(temporaryPath("tmp.script"), lines);
            scpToRemoteStaging(temporaryPath("tmp.script"), "tmp.script");
            sshSudoStrict("bash " + remoteFile);
        } finally {
            new File(temporaryPath("tmp.script")).delete();
            sshStrict("rm " + remoteFile);
        }
    }

    protected void writeStringAsRemoteTextFile(String text, String destRemoteAbsoluteFilePath) throws Exception {
        String tmp = temporaryPath("tmp.txt");
        try {
            ConfigFileUtils.writeFile(tmp, text);
            scpToRemoteUnderRoot(tmp, destRemoteAbsoluteFilePath);
        } finally {
            new File(tmp).delete();
        }
    }

    public CommandResult sshSudoStrict(String cmd) throws Exception {
        CommandResult result = sshSudo(cmd);
        if (result.exitWithErrorCode()) {
            System.out.println("Failed to execute command: " + cmd);
            throw new ExitInstallerRuntimeException();
        }
        return result;
    }

    protected void aptGetUpdate() throws Exception {
        if (getCurrentSessionData("apt-get-update") != null) {
            return;
        }
        sshSudoStrict("apt-get update");
        setCurrentSessionData("apt-get-update", true);
    }

    public CommandResult sshSudo(final String cmd) throws Exception {
        return new SshExecutor<CommandResult>() {
            @Override
            protected CommandResult doExecute() throws Exception {
                return new SudoCommandExecutor(hostInfo, getCurrentSession()).exec(cmd);
            }
        }.execute();
    }

    protected Session getCurrentSession() {
        return getCurrentSessionWraper().getSession();
    }

    protected Object getCurrentSessionData(String key) {
        return getCurrentSessionWraper().getSessionData().get(key);
    }

    protected void setCurrentSessionData(String key, Object value) {
        getCurrentSessionWraper().getSessionData().put(key, value);
    }

    private SshSessionWraper getCurrentSessionWraper() {
        if (hostInfo == null) {
            return null;
        }
        if (SSH_SESSIONS.get(hostInfo) == null) {
            try {
                System.out.println("=====Connecting to " + hostInfo.getDisplayInfo());
                SSH_SESSIONS.put(hostInfo, new SshSessionWraper(SshConnector.connect(hostInfo)));
            } catch (Exception e) {
                if (e instanceof ExitInstallerRuntimeException) {
                    throw (ExitInstallerRuntimeException) e;
                }
                if (e instanceof NormalExitInstallerRuntimeException) {
                    throw (NormalExitInstallerRuntimeException) e;
                }
                throw new ExitInstallerRuntimeException("Không kết nối SSH được với server " + hostInfo.getHost()
                        + ":" + hostInfo.getPort(), e);
            }
        }
        return SSH_SESSIONS.get(hostInfo);
    }

    public CommandResult sshStrict(String cmd) throws Exception {
        CommandResult result = ssh(cmd);
        if (result.exitWithErrorCode()) {
            System.out.println("Failed to execute command: " + cmd);
            throw new ExitInstallerRuntimeException();
        }
        return result;
    }

    protected void sshUntar(String tarFile, String destFolder) throws Exception {
        sshStrict("tar -zxf " + tarFile + " -C " + destFolder);
    }

    protected void sshSudoUntar(String tarFile, String destFolder) throws Exception {
        sshSudoStrict("tar -zxf " + tarFile + " -C " + destFolder);
    }

    protected CommandResult ssh(final String cmd) throws Exception {
        return new SshExecutor<CommandResult>() {
            @Override
            protected CommandResult doExecute() throws Exception {
                return new CommandExecutor(hostInfo, getCurrentSession()).exec(cmd);
            }
        }.execute();
    }

    protected void scpFromRemote(final String sourceRemote,
                                 final String destLocal) throws Exception {
        new SshExecutor<Void>() {
            @Override
            protected Void doExecute() throws Exception {
                new ScpFromRemoteToLocal(getCurrentSession()).copy(sourceRemote, destLocal);
                return null;
            }
        }.execute();
    }

    protected void sudoCpFromRemoteStaging(String subpathInStaging, String destPath) throws Exception {
        sshSudo("cp " + getStagingFolder() + "/" + subpathInStaging + " " + destPath);
    }

    protected void scpToRemoteStaging(final String localFile, final String subpathInStaging) throws Exception {
        scpToRemote(localFile, getStagingFolder() + "/" + subpathInStaging);
    }

    protected void scpToRemote(final String localFile, final String dest) throws Exception {
        new SshExecutor<Void>() {
            @Override
            protected Void doExecute() throws Exception {
                new Scp(getCurrentSession()).copy(localFile, dest);
                return null;
            }
        }.execute();
    }

    protected void scpToRemoteUnderRoot(String localFilePath, String destRemoteAbsoluteFilePath) throws Exception {
        int idx = destRemoteAbsoluteFilePath.lastIndexOf('/');
        String destRemoteFolder = destRemoteAbsoluteFilePath.substring(0, idx);
        String remoteFileName = destRemoteAbsoluteFilePath.substring(idx + 1);
        scpToRemoteUnderRoot(localFilePath, destRemoteFolder, remoteFileName);
    }

    protected void scpToRemoteUnderRoot(String localFilePath, String destRemoteFolder,
                                        String remoteFileName) throws Exception {
        scpToRemoteStaging(localFilePath, remoteFileName);
        // use cp so that owership of the destination file doesn't change
        String remoteStaging = getStagingFolder() + "/" + remoteFileName;
        sshSudoStrict("cp -f " + remoteStaging + " " + destRemoteFolder + "/");
        ssh("rm " + remoteStaging);
    }

    protected void modifyConfigAndCopyToServer(String localCnfTemplate, String remoteDestFullPath,
                                               PlaceHolder... params) throws Exception {
        int idx = remoteDestFullPath.lastIndexOf('/');
        String remoteCnfFileName = remoteDestFullPath.substring(idx + 1);
        String destFolder = remoteDestFullPath.substring(0, idx);
        String tempFile = temporaryPath(remoteCnfFileName);

        // modify config and save to the temporary folder
        new SimpleConfigFileModifier(configTemplatePath(localCnfTemplate), tempFile).replace(params);

        // copy to remote
        scpToRemoteUnderRoot(tempFile, destFolder, remoteCnfFileName);
    }

    public void connect() {
        if (hostInfo != null) {
            getCurrentSession();
        } else {
            HostSelectionController ctrl = new HostSelectionController(installerName);
            HostWithSessionDto hostWithSessionDto = ctrl.show(false);
            if (hostWithSessionDto != null) {
                hostInfo = hostWithSessionDto.getHost();
                try {
                    getCurrentSession(); // connect if not yet
                } catch (RuntimeException e) {
                    hostInfo = null;
                    throw e;
                }
            } else {
                throw new NormalExitInstallerRuntimeException();
            }
        }
    }

    protected void newSshSession() {
        if (hostInfo == null) {
            throw new NullPointerException();
        }
        getCurrentSession();
//        try {
//        	System.out.println("=====Connecting to " + hostInfo.getDisplayInfo());
//            session = SshConnector.connect(hostInfo);
//        } catch (Exception e) {
//        	if (e instanceof ExitInstallerRuntimeException) {
//        		throw (ExitInstallerRuntimeException) e;
//        	}
//        	if (e instanceof NormalExitInstallerRuntimeException) {
//        		throw (NormalExitInstallerRuntimeException) e;
//        	}
//            throw new ExitInstallerRuntimeException("Không kết nối SSH được với server " + hostInfo.getHost()
//            		+ ":" + hostInfo.getPort(), e);
//        }
    }

    protected boolean confirm(String msg) {
        return RemoteAgentGui.showConfirmationYesNo(msg);
    }

    public void prepareStagingFolder() throws Exception {
        cleanStagingFolder();

        if (ssh("mkdir " + getStagingFolder()).exitWithErrorCode()) {
            System.out.println("Khong the tao duoc thu muc " + getStagingFolder()
                    + " tai home folder. " + "Khong the tiep tuc");
            throw new ExitInstallerRuntimeException();
        }
    }

    public void cleanStagingFolder() throws Exception {
        ssh("rm -r " + getStagingFolder());
    }

    public boolean doesFileOrFolderExist(String file) throws Exception {
        CommandResult res = sshSudo("ls " + file);
        return !res.containErrorString("No such file or directory");
    }

    protected boolean isFolderEmpty(String folder) throws Exception {
        CommandResult lsResult = sshSudoStrict("ls -a " + folder);
        return lsResult.getOkStr().equals(".\n..\n");
    }

    /**
     * <code>dir</code> may contain <code>dataDirName</code> at the end of its
     * path. This method will enforce the parent dir (the dir above the
     * <code>dataDirName</code>) existed (otherwise, throwing an exception).
     * It also enforce the data dir (with <code>dataDirName</code> at the end)
     * being an empty folder or not yet existed.
     *
     * @return the real data dir with <code>dataDirName</code> at the end
     */
    protected String checkDataDir(String dir, String dataDirName) throws Exception {
        String realDataDir = dir;
        File tmp = new File(realDataDir);
        if (!tmp.getName().equals(dataDirName)) {
            tmp = new File(realDataDir, dataDirName);
        }
        realDataDir = tmp.getAbsolutePath();
        File rootDir = tmp.getParentFile();
        if (!doesFileOrFolderExist(rootDir.getAbsolutePath())) {
            throw new ExitInstallerRuntimeException(
                    "Root data dir does not exist: " + rootDir.getAbsolutePath());
        }
        if (doesFileOrFolderExist(realDataDir) && !isFolderEmpty(realDataDir)) {
            throw new ExitInstallerRuntimeException(
                    "Folder " + realDataDir + " phải là folder rỗng");
        }
        return realDataDir;
    }

    protected void addUserGroup(String user, String group) throws Exception {
        addUserGroup(user, false, group);
    }

    protected void addUserGroup(String user, boolean createHomeDir, String group) throws Exception {
        boolean ok = false;
        do {
            ok = true;
            if (!doesGroupExist(group)) {
                sshSudoStrict("sudo groupadd " + group);
            } else if (!confirm("group " + group + " đã tồn tại, bạn muốn sử dụng group này?")) {
                group = InputUtils.askSingleTextExitIfNull("New OS Group", group);
                ok = false;
            }
        } while (!ok);

        do {
            ok = true;
            if (!doesUserExist(user)) {
                sshSudoStrict("sudo useradd -r " + (createHomeDir ? "-m" : "") + " -g " + group + " " + user);
            } else if (!confirm("user " + user + " đã tồn tại, bạn muốn sử dụng user này?")) {
                user = InputUtils.askSingleTextExitIfNull("New OS User", group);
                ok = false;
            }
        } while (!ok);
    }

    protected boolean doesUserExist(String username) throws Exception {
        CommandResult res = ssh("id " + username);
        return !res.containErrorString("No such user")
                && !StringUtils.containsIgnoreCase(res.getOkStr(), "No such user");
    }

    protected boolean doesGroupExist(String group) throws Exception {
        CommandResult res = sshSudo("getent group " + group);
        return (res.getOkStr() != null) && !res.getOkStr().trim().isEmpty();
    }

    protected CommandResult listInusedPort(int port) throws Exception {
        return sshSudo("netstat -an | grep " + port + " | grep -i LISTEN");
    }

    public boolean isPortAvailable(int port) throws Exception {
        CommandResult res = listInusedPort(port);
        return doesResultSayPortAvailable(res);
    }

    protected boolean doesResultSayPortAvailable(CommandResult res) {
        return (res.getOkStr() == null) || res.getOkStr().trim().isEmpty();
    }

    protected void prepareLocalTempDir() throws Exception {
        File directory = new File(localTempDir);
        if (!directory.exists()) {
            directory.mkdirs();
            return;
        } else {
            FileUtils.cleanDirectory(directory);
        }
    }

    protected String configTemplatePath(String subPath) {
        if (StringUtils.isNotBlank(getSubTemplateFolder())) {
            return configTemplateDir + File.separatorChar
                    + getSubTemplateFolder() + File.separatorChar + FileUtils.asPath(subPath);
        }
        return configTemplateDir + File.separatorChar + FileUtils.asPath(subPath);
    }

    protected String getSubTemplateFolder() {
        return "";
    }

    protected String temporaryPath(String subPath) {
        return localTempDir + File.separatorChar + FileUtils.asPath(subPath);
    }

    @Override
    public String getName() {
        return installerName;
    }

    @Override
    public String toString() {
        return installerName;
    }

    protected String getStagingFolder() {
        return Preferences.getInstance().getRemoteStaginFolder();
    }

    protected void sshMkDirIfNotExist(String dir) throws Exception {
        sshSudoStrict("mkdir -p " + dir);
    }

    protected void sshMkDirAndSetOwner(String dir, String user, String group) throws Exception {
        sshMkDirIfNotExist(dir);
        sshSudoStrict("chown " + user + ":" + group + " " + dir);
    }

    protected void sshMkdirAndMakeSymbolicLink(String dest, String linkName, String placeToSaveLink) throws Exception {
        sshMkDirIfNotExist(dest);
        sshMakeSymbolicLink(dest, linkName, placeToSaveLink);
    }

    /**
     * Makes a symbolic link named "linkName" which links to "dest" and is planced in folder "placeToSaveLink"
     */
    protected void sshMakeSymbolicLink(String dest, String linkName, String placeToSaveLink) throws Exception {
        sshSudoStrict("sudo ln -sf " + dest + " " + linkName);
        sshSudoStrict("sudo mv ./" + linkName + " " + placeToSaveLink);
    }

    protected ProcessListResultDto listProcessByCommand(String... cmnds) throws Exception {
        CommandResult commandResult = sshSudo("ps --no-header -o pid,uid,cmd -C " + StringUtils.join(cmnds, ","));

        if (commandResult.exitWithErrorCode()) {
            return new ProcessListResultDto(commandResult.getErrorStr());
        } else {
            String resultStr = commandResult.getOkStr();
            if (StringUtils.isBlank(resultStr)) {
                return new ProcessListResultDto((String) null);
            }
            String[] lines = StringUtils.split(resultStr, "\n");
            List<ProcessDto> processes = new ArrayList<>(lines.length);
            for (String line : lines) {
                String[] columns = StringUtils.split(line);
                ProcessDto p = new ProcessDto();
                p.setPid(Integer.parseInt(columns[0]));
                p.setUid(columns[1]);
                p.setCmd(StringUtils.join(ArrayUtils.subarray(columns, 2, columns.length), " "));
                processes.add(p);
            }
            return new ProcessListResultDto(processes);
        }
    }

    protected void appendOrReplaceARemoteLine(String lineStart, String line, String file) throws Exception {
        // do  not write anything to standard output.  Exit immediately with zero status if any match is found
        CommandResult grepRs = sshSudo("grep -q -e '" + lineStart + "' " + file);
        boolean lineAlreadyAdded = grepRs.getExitStatus() == 0;
        if (!lineAlreadyAdded
                && (StringUtils.isNotBlank(grepRs.getErrorStr())
                || StringUtils.isNotBlank(grepRs.getOkStr()))) {
            throw new ExitInstallerRuntimeException();
        }

        if (lineAlreadyAdded) {
            sshSudoStrict("sed -i '/^" + lineStart + "/c\\" + line + "' " + file);
        } else {
            sshSudoStrict("sed -i '$ a\\" + line + "' " + file);
        }
    }

    protected String defaultJvmOpts(
            int xmx,
            int maxPermSz,
            int jmxPort) {
//		http://www.stefankrause.net/wp/?p=14
//		https://www.google.com.vn/search?q=XX%3AMaxHeapFreeRatio+meaning&oq=XX%3AMaxHeapFreeRatio+meaning&aqs=chrome..69i57j69i58.2295j0j1&sourceid=chrome&es_sm=94&ie=UTF-8
//		http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6498735

        // http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html
        return StringUtils.join(new String[]{
                        "-server ",
                        "-Xmx" + xmx + "m ",
                        (maxPermSz > 0 ? ("-XX:MaxPermSize=" + maxPermSz + "m ") : ""),

                        "-XX:+UseParNewGC",
                        "-XX:+UseConcMarkSweepGC",
                        "-XX:+UseTLAB",
                        "-XX:+CMSIncrementalMode",
                        "-XX:+CMSIncrementalPacing",
                        "-XX:CMSIncrementalDutyCycleMin=0",
                        "-XX:CMSIncrementalDutyCycle=10",
                        "-XX:MaxTenuringThreshold=0",
                        "-XX:SurvivorRatio=256",
                        "-XX:CMSInitiatingOccupancyFraction=60",
                        "-XX:+DisableExplicitGC",

                        buildJmxVmArgs(jmxPort)
                },
                " ");
    }

    protected String buildJmxVmArgs(int jmxPort) {
        return "-Dcom.sun.management.jmxremote=true "
                + "-Dcom.sun.management.jmxremote.port=" + jmxPort + " "
                + "-Dcom.sun.management.jmxremote.authenticate=false "
                + "-Dcom.sun.management.jmxremote.ssl=false "
                + "-Djava.rmi.server.hostname=localhost"; // use localhost, then connect via ssh tunneling
    }

    public String tailAsString(int nbLines, String file) throws Exception {
        sshSudoStrict("tail -n " + nbLines + " " + file
                + " | gzip > " + getStagingFolder() + "/tail.tmp");

        return readRemoteGzipTextFileAsString(getStagingFolder() + "/tail.tmp");
    }

    protected String readRemoteGzipTextFileAsString(String remoteFile) throws Exception {
        File localTmp = new File(temporaryPath("text.tmp"));
        localTmp.delete();
        scpFromRemote(remoteFile, localTmp.getAbsolutePath());

        InputStream fileStream = null;
        InputStream gzipStream = null;
        try {
            fileStream = new FileInputStream(localTmp);
            gzipStream = new GZIPInputStream(fileStream);
            Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
            BufferedReader buffered = new BufferedReader(decoder);
            return IOUtils.toString(buffered);
        } finally {
            IOUtils.closeQuietly(fileStream);
            IOUtils.closeQuietly(gzipStream);
            localTmp.delete();
        }
    }

    /**
     * @param <R>
     * @author Ho Tri Bao
     */
    private abstract class SshExecutor<R> {
        public R execute() throws Exception {
            try {
                return doExecute();
            } catch (JSchException e) {
                if (isSessionDown(e)) {
                    SSH_SESSIONS.put(hostInfo, null);
                    newSshSession();
                    return doExecute();
                }
                throw e;
            } catch (ExitInstallerRuntimeException e) {
                if (isSessionDown(e.getCause())) {
                    SSH_SESSIONS.put(hostInfo, null);
                    newSshSession();
                    return doExecute();
                }
                throw e;
            }
        }

        protected abstract R doExecute() throws Exception;

        private boolean isSessionDown(Throwable e) {
            return (e != null)
                    && (e instanceof JSchException)
                    && (e.getMessage() != null) && (e.getMessage().indexOf("session is down") >= 0);
        }
    }

    /**
     * @author htb
     */
    protected abstract class AbstractLocalConfigFileModifier {
        public void modify(String srcFile, String destFile) throws Exception {
            List<String> lines = ConfigFileUtils.readFileAsString(srcFile);

            // place new password into the config file
            placeNewConfig(lines);

            ConfigFileUtils.writeFile(lines, destFile);
        }

        protected abstract void placeNewConfig(List<String> lines) throws Exception;
    }

    /**
     * @author htb
     */
    protected abstract class AbstractRemoteConfigFileModifier {
        public void modify(String remoteFilePath) throws Exception {
            String fileName = remoteFilePath.substring(remoteFilePath.lastIndexOf('/') + 1);

            // copy to staging
            sshSudoStrict("sudo cp " + remoteFilePath + " " + getStagingFolder());
            String remoteTmpFile = getStagingFolder() + "/" + fileName;
            sshSudoStrict("chown " + hostInfo.getUsername() + " " + remoteTmpFile);
            String localTmpFile = temporaryPath(fileName);

            try {
                // download from staging
                scpFromRemote(remoteTmpFile, localTmpFile);
                sshSudoStrict("rm " + remoteTmpFile);

                List<String> lines = ConfigFileUtils.readFileAsString(localTmpFile);

                // place new password into the config file
                placeNewConfig(lines);

                ConfigFileUtils.writeFile(lines, localTmpFile);

                // push file to remote
                scpToRemote(localTmpFile, getStagingFolder());
                sshSudoStrict("cp " + remoteTmpFile + " " + remoteFilePath);
            } finally {
                FileUtils.deleteQuietly(new File(localTmpFile));
            }
        }

        protected abstract void placeNewConfig(List<String> lines) throws Exception;
    }


    /**
     * @author htb
     */
    protected abstract class TaskExecutor {
        public void exec() throws Exception {
//			boolean mustDisconnect = false;
//			if (session == null) {
//				newSshSession();
//				mustDisconnect = true;
//			}
            try {
                perform();
            } finally {
//				if (mustDisconnect) {
//					HostDto tmp = hostInfo; // preserved host info
//					disconnect();
//					hostInfo = tmp;
//				}
            }
        }

        protected abstract void perform() throws Exception;
    }
}
