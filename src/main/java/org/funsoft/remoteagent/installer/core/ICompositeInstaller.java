package org.funsoft.remoteagent.installer.core;

import java.util.List;



/**
 * @author Ho Tri Bao
 *
 */
public interface ICompositeInstaller extends IInstaller {
    List<IInstaller> getSubInstaller();
}
