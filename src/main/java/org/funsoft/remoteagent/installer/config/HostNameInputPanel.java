/**
 * 
 */
package org.funsoft.remoteagent.installer.config;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.util.List;

/**
 * @author htb
 *
 */
public class HostNameInputPanel extends AbstractConfigInputPanel {
	private final HostNameField txtHostName;
	private final MultiHostNameField txtMultiHostName;
	private final String label;
	private final String note;
	
	public static String askHostName(HostDto host, String label, String note) {
		return askHostName(host, null, label, note);
	}
	public static String askHostName(HostDto host,
			String suffix, String label, String note) {
		HostNameInputPanel pnl = new HostNameInputPanel(host, true,
				suffix, label, note);
		HostNameInputPanel.showInputExitIfCancel(pnl, "HOST NAME", 670, 300);
		return pnl.txtHostName.getHostName();
	}
	
	public static List<String> askMultiHostName(HostDto host,
			String label, String note) {
		return askMultiHostName(host, null, label, note);
	}
	public static List<String> askMultiHostName(HostDto host,
			String suffix, String label, String note) {
		HostNameInputPanel pnl = new HostNameInputPanel(host, false,
				suffix, label, note);
		HostNameInputPanel.showInputExitIfCancel(pnl, "HOST NAME", 670, 500);
		return pnl.txtMultiHostName.getHostNames();
	}

	private HostNameInputPanel(HostDto host,
			boolean single, String suffix, String label, String note) {
		super(host);
		if (single) {
			txtHostName = new HostNameField(suffix);
			txtMultiHostName = null;
		} else {
			txtMultiHostName = new MultiHostNameField(suffix, 3);
			txtHostName = null;
		}
		this.label = label;
		this.note = note;
		init();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		if ((txtHostName != null) && txtHostName.isEditable()) {
			txtHostName.requestFocus();
		}
		if ((txtMultiHostName != null) && txtMultiHostName.isEditable()) {
			txtMultiHostName.requestFocus();
		}
	}

	@Override
	protected void initState() {
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		if (txtHostName != null) {
			txtHostName.wireWithLabel(label, gbl);
		} else {
			txtMultiHostName.wireWithLabel(label, gbl);
		}
		if (StringUtils.isNotBlank(note)) {
			gbl.row().cell().cellXRemainder(new JLabel(note)).fillX();
		}
	}

	@Override
	protected boolean checkValid() {
		boolean b = ((txtHostName == null) || txtHostName.checkValid(label))
				&& ((txtMultiHostName == null) || txtMultiHostName.checkValid(label));
		if (!b) {
			return b;
		}
		if (!requireInternalDnsExistInCurrentHost(txtHostName)) {
			return false;
		}
		return true;
	}

}
