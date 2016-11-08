/**
 * 
 */
package org.funsoft.remoteagent.installer.tail.config;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;

/**
 * @author htb
 *
 */
public class TailFileParamInputPanel extends AbstractConfigInputPanel {
	private final JTextField txtFile = GUIUtils.newTextField();
	private final JTextField txtLine = GUIUtils.newTextField();
	public TailFileParamInputPanel(String file) {
		init();
		txtFile.setText(file);
	}
	@Override
	public void addNotify() {
		super.addNotify();
		if (StringUtils.isBlank(txtFile.getText())) {
			txtFile.requestFocusInWindow();
		} else {
			txtLine.requestFocusInWindow();
		}
	}
	@Override
	protected void initState() {
		GUIUtils.fixWidth(txtLine, 100);
		txtLine.setText("100");
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cell(lbl("File path")).cell(txtFile).fillX();
		gbl.row().cell(lbl("Số dòng")).cell(txtLine);
	}

	@Override
	protected boolean checkValid() {
		return GUIUtils.requireMandatory(txtFile, "File path")
				&& GUIUtils.checkInteger(txtLine, "Số dòng", true);
	}

	public String getFilePath() {
		return GUIUtils.getText(txtFile);
	}
	public int getNumberOfLine() {
		return GUIUtils.getInteger(txtLine);
	}
}
