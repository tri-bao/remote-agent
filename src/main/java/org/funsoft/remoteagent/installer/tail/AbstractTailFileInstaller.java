/**
 * 
 */
package org.funsoft.remoteagent.installer.tail;

import org.funsoft.remoteagent.gui.component.ReadOnlyTextPane;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.installer.tail.config.TailFileParamInputPanel;

/**
 * @author htb
 *
 */
public abstract class AbstractTailFileInstaller extends AbstractInstaller {

	protected AbstractTailFileInstaller(String installerName) {
		super(installerName);
	}

	protected void tail() throws Exception {
		TailFileParamInputPanel pnl = new TailFileParamInputPanel(getFilePath());
		TailFileParamInputPanel.showInputExitIfCancel(pnl,
				"FILE VÀ SỐ DÒNG CUỐI CÙNG MUỐN XEM",
				600, 180);
		
		String txt = tailAsString(pnl.getNumberOfLine(), pnl.getFilePath());
		
		showTextInReadOnlyPane(pnl.getNumberOfLine() + " last lines from file: " + pnl.getFilePath(), txt);
	}

	private void showTextInReadOnlyPane(
			String title,
			String txt) {
		ReadOnlyTextPane.showText(title, txt, true);
	}
	
	protected abstract String getFilePath();
}
