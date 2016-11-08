/**
 * 
 */
package org.funsoft.remoteagent.gui.component;

import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;

/**
 * @author htb
 *
 */
public class TimeField extends JPanel {
	private final JComboBox<String> cbxHour = new JComboBox<>();
	private final JComboBox<String> cbxMinute = new JComboBox<>();
	
	public TimeField() {
		initState();
		initAction();
		initLayout();
	}
	@Override
	public void requestFocus() {
		cbxHour.requestFocus();
	}
	private void initState() {
		for (int i = 0; i < 24; i++) {
			if (i < 10) {
				cbxHour.addItem("0" + i);
			} else {
				cbxHour.addItem("" + i);
			}
		}
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				cbxMinute.addItem("0" + i);
			} else {
				cbxMinute.addItem("" + i);
			}
		}
	}

	private void initAction() {
	}

	private void initLayout() {
		JLabel sep = new JLabel(":");
		PainlessGridBag gbl = new PainlessGridBag(this, GUIUtils.getZeroSurroundingGridBagConfig(), false);
		gbl.row().cell(cbxHour).cell(sep).cell(cbxMinute);
		gbl.constraints(sep).insets.left = 0;
		gbl.constraints(cbxMinute).insets.left = 0;
		gbl.done();
	}
	public int getHour() {
		return Integer.parseInt((String) cbxHour.getSelectedItem());
	}
	public void setHour(int hour) {
		if (hour < 10) {
			cbxHour.setSelectedItem("0" + hour);
		} else {
			cbxHour.setSelectedItem("" + hour);
		}
	}
	public int getMinute() {
		return Integer.parseInt((String) cbxMinute.getSelectedItem());
	}
	public void setMinute(int minute) {
		if (minute < 10) {
			cbxMinute.setSelectedItem("0" + minute);
		} else {
			cbxMinute.setSelectedItem("" + minute);
		}
	}
	@Override
	public void setEnabled(boolean e) {
		cbxHour.setEnabled(e);
		cbxMinute.setEnabled(e);
	}
}
