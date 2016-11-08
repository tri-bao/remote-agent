/**
 * 
 */
package org.funsoft.remoteagent.installer.core;

import java.util.Map;

/**
 * @author htb
 *
 */
public interface IGuidedCompositeInstaller extends IInstaller {
	String buildGuide(Map<Integer, String> installerIdsToName);
}
