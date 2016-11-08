/**
 *
 */
package org.funsoft.remoteagent.cnf;

import org.apache.commons.collections.CollectionUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.config.HostNameField;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.engine.IGridCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author htb
 */
public abstract class AbstractConfigInputPanel extends JPanel {
    protected final JButton btnOK = new JButton("OK");
    protected final JButton btnCancel = new JButton("Cancel");

    private boolean isCancel = true;

    protected final HostDto currentHost;

    public AbstractConfigInputPanel() {
        this(null);
    }

    public AbstractConfigInputPanel(HostDto currentHost) {
        this.currentHost = currentHost;
    }

    // subclasses must call this method in their constructor
    protected void init() {
        initState();
        initAction();
        initLayout();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        getRootPane().setDefaultButton(btnOK);
    }

    protected abstract void initState();

    protected void initAction() {
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!checkValid()) {
                    return;
                }
                isCancel = false;
                SwingUtilities.getWindowAncestor(btnOK).dispose();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(btnOK).dispose();
            }
        });
    }

    private void initLayout() {
        JPanel pnl = new JPanel();
        PainlessGridBag gbl = new PainlessGridBag(pnl, false);
        if (currentHost != null) {
            JLabel lblHost = lbl("SERVER TO BE WORKING ON: " + currentHost.getDisplayInfo());
            lblHost.setForeground(Color.RED);
            GUIUtils.makeFontBold(lblHost);
            gbl.row().cellXRemainder(lblHost);
            gbl.getConfig().addLabelAnchor(lblHost, GridBagConstraints.CENTER);

            GUIUtils.addSeparator("", gbl);
        }
        initLayout(gbl);
        doneLayout(gbl);

        addToMainLayout(pnl);
    }

    protected void addToMainLayout(JPanel pnl) {
        JScrollPane scr = new JScrollPane(pnl);
        scr.getVerticalScrollBar().setUnitIncrement(15);

        PainlessGridBag gblMain = new PainlessGridBag(this, false);
        gblMain.row().cell(scr).fillXY();
        LayoutUtils.addButtonPanel(gblMain, btnOK, btnCancel);
        gblMain.done();
    }

    protected void doneLayout(PainlessGridBag gbl) {
        gbl.doneAndPushEverythingToTop();
    }

    protected abstract void initLayout(PainlessGridBag gbl);

    // layout utils
    protected JLabel lbl(String txt) {
        return new JLabel(txt);
    }

    protected void note(String txt, IGridCell afterCell, PainlessGridBag gbl) {
        GUIUtils.noteRow(txt, afterCell, gbl);
    }

    protected void passwordPair(String lbl, JPasswordField pwd, JPasswordField pwdAgain,
                                PainlessGridBag gbl) {
        gbl.row().cell(lbl(lbl)).cell(pwd).fillX().cell(lbl("again")).cell(pwdAgain).fillX();
    }

    protected abstract boolean checkValid();

    protected boolean requireInternalDnsExistInCurrentHost(HostNameField txtHost) {
        if (CollectionUtils.isNotEmpty(currentHost.getInternalDns())
                && (currentHost != null)
                && !currentHost.getInternalDns().contains(txtHost.getHostName())) {
            RemoteAgentGui.showErrorMsg("Internal DNS: \"" + txtHost.getHostName() + "\""
                    + " is not in selected internal DNS host");
            txtHost.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public static <P extends AbstractConfigInputPanel> P showInputExitIfCancel(
            Class<P> pnlClass,
            String title, int width, int height) {
        return showInputExitIfCancel(null, pnlClass, title, width, height);
    }

    public static <P extends AbstractConfigInputPanel> P showInputExitIfCancel(
            HostDto currentHost,
            Class<P> pnlClass,
            String title, int width, int height) {
        try {
            P pnl;
            try {
                pnl = pnlClass.getConstructor(HostDto.class).newInstance(currentHost);
            } catch (NoSuchMethodException e) {
                pnl = pnlClass.newInstance();
            }
            showInputExitIfCancel(pnl, title, width, height);
            return pnl;
        } catch (Exception e) {
            if (e instanceof ExitInstallerRuntimeException) {
                throw (ExitInstallerRuntimeException) e;
            }
            throw new ExitInstallerRuntimeException(e);
        }
    }

    public static <P extends AbstractConfigInputPanel> void showInputExitIfCancel(P pnl, String title, int width, int height) {
        GUIUtils.showInDialog(pnl, title, width, height);
        if (pnl.isCancel()) {
            throw new ExitInstallerRuntimeException("Cancel dialog: " + title);
        }
    }

    public JButton getBtnOK() {
        return btnOK;
    }
}
