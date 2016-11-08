/**
 * 
 */
package org.funsoft.remoteagent.gui.component;

import javax.swing.*;

/**
 * @author htb
 *
 */
public class DayOfWeekField extends JComboBox<String> {
	public DayOfWeekField() {
		for (int i = 2; i <= 8; i++) {
			if (i != 8) {
				addItem("" + i);
			} else {
				addItem("CN");
			}
		}
	}
	
	public int getDayOfWeek() {
		String sel = (String) getSelectedItem();
		if ("CN".endsWith(sel)) {
			return 8;
		}
		return Integer.parseInt(sel);
	}
	public void setDayOfWeek(int dow) {
		if ((dow < 2) || (dow > 8)) {
			throw new RuntimeException("Wrong day of week: " + dow);
		}
		if (dow == 8) {
			setSelectedItem("CN");
		}
		setSelectedItem(dow + "");
	}
}
