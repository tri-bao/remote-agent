/**
 *
 */
package org.funsoft.remoteagent.installer.core;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.installer.InstallerRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author htb
 */
public abstract class AbstractGuidedCompositeInstaller implements IGuidedCompositeInstaller {
    private final String name;

    private int currentIndex = 0;
    private Map<Integer, String> installerIdsToName;
    private Map<String, Integer> nameToIds;

    protected AbstractGuidedCompositeInstaller(String name) {
        this.name = name;
    }

    @Override
    public String buildGuide(Map<Integer, String> installerIdsToName) {
        currentIndex = 0;
        this.installerIdsToName = installerIdsToName;
        nameToIds = new HashMap<>();

        return buildGuide();
    }

    protected abstract String buildGuide();

    protected String mentionInstaller(IInstaller installer) {
        InstallerRepository.INSTANCE.addInstaller(installer);

        String installerName = installer.getName();
        Integer id = nameToIds.get(installerName);
        if (id == null) {
            currentIndex++;
            installerIdsToName.put(currentIndex, installerName);
            nameToIds.put(installerName, currentIndex);
            id = currentIndex;
        }
        return "<a href=#\"" + id + "\">" + installerName + "</a>"
                + (StringUtils.isNotBlank(installer.getDescription())
                ? " (" + installer.getDescription() + ")"
                : "");
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public void execute() throws Exception {
    }

    @Override
    public String getDescription() {
        return null;
    }

}
