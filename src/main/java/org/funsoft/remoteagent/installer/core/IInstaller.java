package org.funsoft.remoteagent.installer.core;

/**
 * @author Ho Tri Bao
 */
public interface IInstaller {
    void execute() throws Exception;

    String getName();

    String getDescription();
}
