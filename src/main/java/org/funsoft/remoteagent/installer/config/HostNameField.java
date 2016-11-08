/**
 * 
 */
package org.funsoft.remoteagent.installer.config;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;

import javax.swing.*;
import java.awt.*;

/**
 * @author htb
 *
 */
public class HostNameField extends JPanel {
	private final JTextField txtPrefix = GUIUtils.newTextField();
	private final String domainSuffix;
	private final String prefix;
	
	public HostNameField() {
		this(null);
	}
	public HostNameField(String domainSuffix) {
		this(null, domainSuffix);
	}
	public HostNameField(String prefix, String domainSuffix) {
		this.prefix = prefix;
		this.domainSuffix = domainSuffix;
		initState();
		initLayout();
	}

	private void initState() {
		GUIUtils.fixWidth(txtPrefix, 70);
		if (domainSuffix != null) {
			txtPrefix.setHorizontalAlignment(JTextField.RIGHT);
		}
	}
	public void setEditable(boolean b) {
		txtPrefix.setEditable(b);
	}
	public boolean isEditable() {
		return txtPrefix.isEditable();
	}
	@Override
	public boolean isEnabled() {
		return txtPrefix.isEnabled();
	}
	@Override
	public void setEnabled(boolean e) {
		txtPrefix.setEnabled(e);
	}
	@Override
	public void requestFocus() {
		txtPrefix.requestFocus();
	}

	private void initLayout() {
		JLabel lblSuffix = null;
		if (domainSuffix != null) {
			lblSuffix = new JLabel(domainSuffix);
			GUIUtils.makeFontBold(lblSuffix);
		}
		
		PainlessGridBag gbl = new PainlessGridBag(this, zeroSpacingConfig(), false);
		if (domainSuffix != null) {
			gbl.row().cell(getPrefixComponent()).cell(lblSuffix);
			GUIUtils.noteRow("(vd: s1, s2, s3,...)", gbl.row(), gbl);
		} else {
			gbl.row().cell(getPrefixComponent()).fillX();
		}
		
		gbl.done();
	}

	private JComponent getPrefixComponent() {
		if (StringUtils.isBlank(prefix)) {
			return txtPrefix;
		}
		JPanel p = new JPanel();
		JTextField tf = GUIUtils.newTextField();
		tf.setText(prefix);
		tf.setEnabled(false);
		tf.setBackground(Color.WHITE);
		
		GUIUtils.fixWidth(tf, 38);
		p.setLayout(new BorderLayout());
		p.add(tf, BorderLayout.WEST);
		p.add(txtPrefix, BorderLayout.EAST);
		return p;
	}
	
	private PainlessGridbagConfiguration zeroSpacingConfig() {
		PainlessGridbagConfiguration cnf = GUIUtils.getZeroSurroundingGridBagConfig();
		cnf.setHorizontalSpacing(0);
		cnf.setVirticalSpacing(0);
		
		return cnf;
	}
	
	public void wireWithLabel(String txt, PainlessGridBag gbl) {
		JLabel lbl = new JLabel(txt);
		if (domainSuffix != null) {
			gbl.row().cell(lbl).cellXRemainder(this);
		} else {
			gbl.row().cell(lbl).cellXRemainder(this).fillX();
		}
		gbl.getConfig().addLabelAnchor(lbl, GridBagConstraints.NORTHWEST);
		gbl.constraints(lbl).insets.top = 10;
	}
	
	public boolean checkValid(String lbl) {
		return GUIUtils.requireMandatory(txtPrefix, lbl);
	}
	public String getHostName() {
		if (GUIUtils.getText(txtPrefix) == null) {
			return null;
		}
		return StringUtils.stripToEmpty(prefix) + GUIUtils.getText(txtPrefix)
				+ StringUtils.stripToEmpty(domainSuffix);
	}
	public void setHostName(String host) {
		if (host == null) {
			txtPrefix.setText(host);
		} else {
			String tmp = StringUtils.removeEnd(host, StringUtils.stripToEmpty(domainSuffix));
			tmp = StringUtils.removeStart(tmp, prefix);
			txtPrefix.setText(tmp);
		}
		
	}
}
