/**
 * 
 */
package org.funsoft.remoteagent.cnf;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author htb
 *
 */
public class InputUtils {
	public static String askSingleTextExitIfNull(final String label, String defVal) {
		return askSingleTextExitIfNull(label, defVal, null);
	}
	public static String askSingleTextExitIfNull(final String label, String defVal, String note) {
		String txt = askSingleText(label, true, defVal, note);
		if (txt == null) {
			throw new ExitInstallerRuntimeException();
		}
		return txt;
	}
	public static String askSingleText(final String label, final boolean mandatory, String delVal) {
		return askSingleText(label, mandatory, delVal, null);
	}
	public static String askSingleText(final String label, final boolean mandatory, String delVal, String note) {
		final JPanel pnl = new JPanel();
		final JTextArea txe = GUIUtils.newTextArea();
		txe.setText(delVal);
		final JButton btnOK = new JButton("OK");
		JButton btnCancel = new JButton("Cancel");
		
		PainlessGridBag gbl = new PainlessGridBag(pnl, false);
		gbl.row().cell(new JLabel(label)).cell(new JScrollPane(txe)).fillXY();
		if (note != null) {
			GUIUtils.noteRow(note, gbl.row(), gbl);
		}
		LayoutUtils.addButtonPanel(gbl, btnOK, btnCancel);
		
		gbl.done();
		
		final StringBuilder isCancel = new StringBuilder();
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isCancel.append("YES");
				SwingUtilities.getWindowAncestor(pnl).dispose();
			}
		});
		
		final StringBuilder bd = new StringBuilder();
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mandatory && !GUIUtils.requireMandatory(txe, label)) {
					return;
				}
				bd.append(txe.getText());
				SwingUtilities.getWindowAncestor(pnl).dispose();
			}
		});
		
		AbstractAction actionOnEnter = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOK.doClick();
			}
		};
		GUIUtils.preventInputingNewLineOnTextArea(txe, actionOnEnter);

		GUIUtils.showInDialog(pnl, "NHẬP: " + label, 610, 250);
		
		if (isCancel.length() > 0) {
			throw new ExitInstallerRuntimeException();
		}
		
		String rs = StringUtils.replace(bd.toString(), "\n", "");
		return StringUtils.stripToNull(rs);
	}
	
	public static String askPasswordWithConfirm(final String label) {
		PasswordPairPanel pnl = new PasswordPairPanel(label);
		PasswordPairPanel.showInputExitIfCancel(pnl, label.toUpperCase(), 500, 180);
		return GUIUtils.getText(pnl.txtPassword);
	}
	
    public static int askPort(String label, int defaultPort) {
        JTextField txtPort = GUIUtils.newTextField();
        txtPort.setText(defaultPort + "");
        do {
            JPanel pnl = new JPanel();
            pnl.setLayout(new GridLayout(1, 2, 5, 5));
            pnl.add(new JLabel(label));
            pnl.add(txtPort);
            int option = JOptionPane.showConfirmDialog(null, new Object[] { pnl },
                    "Nhập số port", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                if ((txtPort.getText() == null) || txtPort.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Chưa nhập Port");
                    continue;
                }
                try {
                    return Integer.parseInt(txtPort.getText().trim());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Port không hợp lệ");
                    continue;
                }
            } else {
                throw new ExitInstallerRuntimeException();
            }
        } while (true);
    }

	
	/**
	 * @author htb
	 */
	private static class PasswordPairPanel extends AbstractConfigInputPanel {
		private final String label;
		public PasswordPairPanel(String label) {
			this.label = label;
			init();
		}
		JPasswordField txtPassword = GUIUtils.newPwdField();
		JPasswordField txtPasswordAgain = GUIUtils.newPwdField();
		@Override
		protected void initState() {
		}

		@Override
		protected void initLayout(PainlessGridBag gbl) {
			GUIUtils.addSeparator(label, gbl);
			gbl.row().cell(new JLabel("Password")).cell(txtPassword)
					.cell(new JLabel("again")).cell(txtPasswordAgain);
		}

		@Override
		protected boolean checkValid() {
			boolean b = GUIUtils.checkPasswordPair(txtPassword, txtPasswordAgain, "Password");
			if (!b) {
				return false;
			}
			String password = GUIUtils.getText(txtPassword);
			if (StringUtils.contains(password, ' ')) {
				RemoteAgentGui.showErrorMsg("Không được có ký tự khoảng trắng");
				return false;
			} else if (StringUtils.contains(password, '$')) {
				RemoteAgentGui.showErrorMsg("Không được có ký tự $");
				return false;
			}
			return true;
		}
	}
}
