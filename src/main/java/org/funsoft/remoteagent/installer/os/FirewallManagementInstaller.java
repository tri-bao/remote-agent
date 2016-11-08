package org.funsoft.remoteagent.installer.os;

import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AsyncTaskWithAutoMask;
import org.funsoft.remoteagent.gui.controller.AsyncTaskWithAutoMask.AsyncTaskCallback;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.os.config.FirewallManagementPanel;
import org.funsoft.remoteagent.installer.os.config.FirewallRuleInputPanel;
import org.funsoft.remoteagent.installer.os.util.NetworkUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class FirewallManagementInstaller extends AbstractFirewallInstaller {
	private static FirewallManagementInstaller instance;
	public static FirewallManagementInstaller getInstance() {
		if (instance == null) {
			instance = new FirewallManagementInstaller();
		}
		return instance;
	}
	
	protected FirewallManagementInstaller() {
		super("OS - manage firewall");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	protected void performInternal() throws Exception {
		final String nics = listNICs();
		
		List<String> rules = listRules();
		if (rules == null) { // inactive
			if (confirm("UFW firewall chưa được enabled, enable nhé?")) {
				enableUfwFirewall();
				rules = listRules();
			} else {
				return;
			}
		}
		
		final FirewallManagementPanel pnl = new FirewallManagementPanel();
		pnl.displayNICs(nics);
		pnl.displayRules(rules);
		pnl.getBtnThem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final FirewallRuleInputPanel pnlInput = new FirewallRuleInputPanel();
				pnlInput.displayNICs(nics);
				try {
					FirewallRuleInputPanel.showInputExitIfCancel(pnlInput, "CHO PHÉP", 790, 400);
				} catch (ExitInstallerRuntimeException e2) {
					return;
				}
				
				pnl.getBtnThem().setEnabled(false);
				try {
					AsyncTaskWithAutoMask.execute(pnl, new AsyncTaskCallback<Void>() {
						@Override
						protected Void perform() throws Exception {
							addFirewallRuleAllowPort(pnlInput.getPort(), pnlInput.getProtocal(),
									pnlInput.getNIC(), pnlInput.getIP());
							return null;
						}

						@Override
						protected void onSuccess(Void taskResult) {
							pnl.displayRules(listRules());
						}
					});
				} finally {
					pnl.getBtnThem().setEnabled(true);
				}
			}
		});
		pnl.getBtnDelete().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnl.getBtnDelete().setEnabled(false);
				try {
					deleteRule(pnl.getSelectedIndex() + 1, pnl.getSelectedRule(), pnl);
				} finally {
					pnl.getBtnDelete().setEnabled(true);
				}
			}
		});
		
		GUIUtils.showInDialog(pnl, "QUẢN LÝ FIREWALL", 700, 600);
	}
	
	/**
	 * @return null if inactive
	 */
	private List<String> listRules() {
		return NetworkUtils.listFirewalRules(this);
	}
	
	private void deleteRule(final int ruleNumer, String rule, final FirewallManagementPanel pnl) {
		if (confirm("Bạn thật sự muốn hủy firewall rule:\n\n" + rule)) {
			AsyncTaskWithAutoMask.execute(pnl, new AsyncTaskCallback<Void>() {
				@Override
				protected Void perform() throws Exception {
					executeAsBatchSudo("yes | ufw delete " + ruleNumer);
					return null;
				}
				@Override
				protected void onSuccess(Void taskResult) {
					pnl.displayRules(listRules());
				}
			});
		}
	}
}
