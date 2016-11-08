package org.funsoft.remoteagent.installer;

import org.funsoft.remoteagent.file.FileUploader;
import org.funsoft.remoteagent.file.RemoteFileDownloader;
import org.funsoft.remoteagent.installer.cnffile.RemoteConfigFileEditorInstaller;
import org.funsoft.remoteagent.installer.connection.ConnectionGroupInstaller;
import org.funsoft.remoteagent.installer.core.ICompositeInstaller;
import org.funsoft.remoteagent.installer.core.IInstaller;
import org.funsoft.remoteagent.installer.os.OsInstallerGroup;
import org.funsoft.remoteagent.installer.tail.TailFileInstallerGroup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Ho Tri Bao
 */
public enum InstallerRepository {
    INSTANCE;

    private final LinkedHashMap<String, IInstaller> installers = new LinkedHashMap<>();
    private final LinkedHashMap<String, IInstaller> subinstallers = new LinkedHashMap<>();

    private InstallerRepository() {
    }

    public void addDefaultInstallers() {
        addInstaller(ConnectionGroupInstaller.getInstance());
        addInstaller(OsInstallerGroup.getInstance());
        addInstaller(RemoteFileDownloader.getInstanse());
        addInstaller(FileUploader.getInstance());
        addInstaller(TailFileInstallerGroup.getInstance());
        addInstaller(RemoteConfigFileEditorInstaller.getIntance());
    }

    public void addInstaller(IInstaller is) {
        if (installers.containsKey(is.getName())) {
            return;
        }
        installers.put(is.getName(), is);
        if (is instanceof ICompositeInstaller) {
            ICompositeInstaller cis = (ICompositeInstaller) is;
            List<IInstaller> subs = cis.getSubInstaller();
            for (IInstaller s : subs) {
                addSubInstaller(s);
            }
        }
    }

    private void addSubInstaller(IInstaller is) {
        subinstallers.put(is.getName(), is);
    }

    public Set<String> getInstallerNames() {
        return installers.keySet();
    }

    public IInstaller getInstaller(String name) {
        IInstaller installer = installers.get(name);
        if (installer == null) {
            installer = subinstallers.get(name);
        }
        if (installer == null) {
            throw new RuntimeException("no installer with name: " + name
                    + ". Forgot to add it to InstallerRepository/Installer group?");
        }
        return installer;
    }
}
