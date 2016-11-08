/**
 * 
 */
package org.funsoft.remoteagent.installer.config;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;
import org.painlessgridbag.engine.IGridCell;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author htb
 *
 */
public class MultiHostNameField extends JPanel {
	private final JTextArea txePrefix = GUIUtils.newTextArea();
	private final String domainSuffix;
	private final String prefix;
	private int rows = -1;
	public MultiHostNameField() {
		this(-1);
	}
	public MultiHostNameField(int prefixRows) {
		this(null, prefixRows);
	}
	public MultiHostNameField(String domainSuffix) {
		this(domainSuffix, -1);
	}
	public MultiHostNameField(String domainSuffix, int prefixRows) {
		this(null, domainSuffix, prefixRows);
	}
	public MultiHostNameField(String prefix, String domainSuffix) {
		this(prefix, domainSuffix, -1);
	}
	public MultiHostNameField(String prefix, String domainSuffix, int prefixRows) {
		this.prefix = prefix;
		this.domainSuffix = domainSuffix;
		this.rows = prefixRows;
		initState();
		initLayout();
	}

	private void initState() {
		if (rows > 0) {
			txePrefix.setRows(rows);
		}
	}
	public void setEditable(boolean b) {
		txePrefix.setEditable(b);
	}
	public boolean isEditable() {
		return txePrefix.isEditable();
	}
	@Override
	public boolean isEnabled() {
		return txePrefix.isEnabled();
	}
	@Override
	public void setEnabled(boolean e) {
		txePrefix.setEnabled(e);
	}
	@Override
	public void requestFocus() {
		txePrefix.requestFocus();
	}

	private void initLayout() {
		JLabel lblSuffix = null;
		if (domainSuffix != null) {
			lblSuffix = new JLabel(domainSuffix);
			GUIUtils.makeFontBold(lblSuffix);
		}
		
		PainlessGridBag gbl = new PainlessGridBag(this, zeroSpacingConfig(), false);
		IGridCell gridCell;
		if (rows <= 0) {
			gridCell = gbl.row().cell(getPrefixComponent()).fillXY();
		} else {
			gridCell = gbl.row().cell(getPrefixComponent()).fillX();
		}
		if (lblSuffix != null) {
			gridCell.cell(lblSuffix);
			GUIUtils.noteRow("Chỉ nhập phần đầu (e.g s1, s2..), mỗi địa chỉ 1 dòng", gbl.row(), gbl);
		}  else {
			GUIUtils.noteRow("Mỗi địa chỉ 1 dòng", gbl.row(), gbl);
		}
		gbl.done();
	}
	
	private JComponent getPrefixComponent() {
		if (StringUtils.isBlank(prefix)) {
			return new JScrollPane(txePrefix);
		}
		JPanel p = new JPanel();
		PainlessGridBag gbl = new PainlessGridBag(this, zeroSpacingConfig(), false);
		if (rows <= 0) {
			gbl.row().cell(new JLabel(prefix)).cell(new JScrollPane(txePrefix)).fillXY();
		} else {
			gbl.row().cell(new JLabel(prefix)).cell(new JScrollPane(txePrefix)).fillX();
		}
		gbl.done();
//		p.setLayout(new BorderLayout());
//		p.add(new JLabel(prefix), BorderLayout.WEST);
//		p.add(new JScrollPane(txePrefix), BorderLayout.EAST);
		return p;
	}
	
	private PainlessGridbagConfiguration zeroSpacingConfig() {
		PainlessGridbagConfiguration cnf = new PainlessGridbagConfiguration();
		cnf.setFirstColumnLeftSpacing(0);
		cnf.setFirstRowTopSpacing(0);
		cnf.setLastColumnRightSpacing(0);
		cnf.setLastRowBottomSpacing(0);
		cnf.setHorizontalSpacing(0);
		cnf.setVirticalSpacing(0);
		
		return cnf;
	}
	
	public void wireWithLabel(String txt, PainlessGridBag gbl) {
		JLabel lbl = new JLabel(txt);
		gbl.row().cell(lbl).cellXRemainder(this).fillXY();
		gbl.getConfig().addLabelAnchor(lbl, GridBagConstraints.NORTHWEST);
		gbl.constraints(lbl).insets.top = 10;
	}
	
	public boolean checkValid(String lbl) {
		return GUIUtils.requireMandatory(txePrefix, lbl);
	}
	public List<String> getHostNames() {
		if (GUIUtils.getText(txePrefix) == null) {
			return null;
		}
		String str = GUIUtils.getText(txePrefix);
		String[] ss = StringUtils.split(str, "\n");
		List<String> rs = new ArrayList<>(ss.length);
		for (String nm : ss) {
			if (StringUtils.isBlank(nm)) {
				continue;
			}
			rs.add(StringUtils.stripToEmpty(prefix) + nm + StringUtils.stripToEmpty(domainSuffix));
		}
		return rs;
	}
	public void setHostNames(List<String> hosts) {
		if (CollectionUtils.isEmpty(hosts)) {
			txePrefix.setText(null);
		} else {
			List<String> names = new ArrayList<>();
			for (String host : hosts) {
				String tmp = StringUtils.removeEnd(host, StringUtils.stripToEmpty(domainSuffix));
				tmp = StringUtils.removeStart(tmp, prefix);
				if (StringUtils.isNotBlank(tmp)) {
					names.add(tmp);
				}
			}
			txePrefix.setText(StringUtils.join(names, "\n"));
		}
	}
}
