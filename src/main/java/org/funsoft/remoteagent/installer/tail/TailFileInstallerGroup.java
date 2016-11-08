/**
 * 
 */
package org.funsoft.remoteagent.installer.tail;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.installer.core.AbstractGuidedCompositeInstaller;

/**
 * @author htb
 *
 */
public class TailFileInstallerGroup extends AbstractGuidedCompositeInstaller {

	private static TailFileInstallerGroup instance;
	public static TailFileInstallerGroup getInstance() {
		if (instance == null) {
			instance = new TailFileInstallerGroup();
		}
		return instance;
	}
	
	protected TailFileInstallerGroup() {
		super("Tail file");
	}
	
	@Override
	protected String buildGuide() {
		// mention them
		String[] lines = new String[] {
				"<ul>",
					"<li>", mentionInstaller(new TailAnyFileInstaller("syslog", "/var/log/syslog")),
					"<li>", mentionInstaller(new TailAnyFileInstaller("haproxy", "/var/log/haproxy.log")),
				"</ul>",
		};
		return StringUtils.join(lines, "");
	}
}
