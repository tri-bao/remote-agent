package org.funsoft.remoteagent.installer.os.config;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.controller.AsyncTaskWithAutoMask;
import org.funsoft.remoteagent.gui.controller.AsyncTaskWithAutoMask.AsyncTaskCallback;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.installer.os.util.NetworkUtils;

import javax.swing.*;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class NicsCombobox extends JComboBox<String> {
	private static final String DEFAULT_TEXT = "Hãy chọn 1 card mạng...";
	private static final String WAITING_TEXT = "Đang xác định các card mạng...";
	
	private final AbstractInstaller currentInstaller;
	public NicsCombobox(AbstractInstaller installer) {
		this.currentInstaller = installer;
		setEnabled(false);
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		if (getParent() instanceof JPanel) {
			if (getItemCount() > 0) {
				return;
			}
			addItem(WAITING_TEXT);
			
			AsyncTaskWithAutoMask.execute((JPanel) getParent(), new AsyncTaskCallback<List<String>>() {
				@Override
				protected List<String> perform() throws Exception {
					return NetworkUtils.listAllNetworkInterfaces(currentInstaller);
				}

				@Override
				protected void onSuccess(List<String> nics) {
					removeAllItems();
					addItem(DEFAULT_TEXT);
					for (String nic : nics) {
						addItem(nic);
					}
					setSelectedIndex(0);
					setEnabled(true);
				}
			});
		}
	}
	public String getSelectedNic() {
		String txt = (String) getSelectedItem();
		if (StringUtils.equalsIgnoreCase(DEFAULT_TEXT, txt)
				|| StringUtils.equalsIgnoreCase(WAITING_TEXT, txt)) {
			return null;
		}
		return txt;
	}
}
