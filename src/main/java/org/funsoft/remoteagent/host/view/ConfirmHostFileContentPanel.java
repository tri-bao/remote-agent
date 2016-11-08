/**
 * 
 */
package org.funsoft.remoteagent.host.view;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.*;

/**
 * @author htb
 *
 */
public class ConfirmHostFileContentPanel extends AbstractConfigInputPanel {
	private final JTextArea txeContent = new JTextArea();

	public ConfirmHostFileContentPanel(String txt) {
		txeContent.setText(txt);
		init();
	}
	
	@Override
	protected void initState() {
		txeContent.setEditable(false);
		txeContent.setFont(new Font("monospaced", Font.PLAIN, 15));
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cell(new JScrollPane(txeContent)).fillXY();
	}

	@Override
	protected void doneLayout(PainlessGridBag gbl) {
		gbl.done();
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
}
