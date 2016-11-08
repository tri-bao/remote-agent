package org.funsoft.remoteagent.main;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.AvailableInstallerPanel;
import org.funsoft.remoteagent.gui.component.CurrentInstallerPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.component.OutputPanel;
import org.funsoft.remoteagent.gui.controller.AsyncTask;
import org.funsoft.remoteagent.host.controller.HostFileManagementController;
import org.funsoft.remoteagent.host.controller.HostManagementController;
import org.funsoft.remoteagent.installer.InstallerRepository;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.core.IInstaller;
import org.funsoft.remoteagent.installer.core.NormalExitInstallerRuntimeException;
import org.funsoft.remoteagent.util.InstallerPrintStream;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ho Tri Bao
 */
public class RemoteAgentGui extends JFrame implements HyperlinkListener {
    private static final PrintWriter OUTPUT_WRITER = getOutputFile();
    private final JSplitPane splitRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
    private final JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

    private final AvailableInstallerPanel pnlAvailableInstaller = new AvailableInstallerPanel();
    private final CurrentInstallerPanel pnlCurrentInstaller = new CurrentInstallerPanel();
    private final OutputPanel pnlOutput = new OutputPanel();
    private final InstallerPrintStream installerPrintStream = new InstallerPrintStream(pnlOutput, OUTPUT_WRITER);

    private final String stagingFolder = "/tmp/remoteagent";

    private static RemoteAgentGui mainFrame;

    private static PrintWriter getOutputFile() {
        File dir = new File("output");
        if (dir.exists() && !dir.isDirectory()) {
            RemoteAgentGui.showErrorMsg(
                    "Output will be written to folder " + dir.getAbsolutePath()
                            + ". However, there is currently a FILE at that location");
            System.exit(1);
        }
        if (!dir.exists()) {
            dir.mkdir();
        }

        final String prefix = "remote-agent-output-" + getCurrrentTimeAsString() + "-";
        final String suffix = ".txt";
        for (int i = 1; i <= 1000; i++) {
            File f = new File(dir, prefix + i + suffix);
            if (!f.exists()) {
                try {
                    return new PrintWriter(new BufferedWriter(new FileWriter(f)), true);
                } catch (IOException e) {
                    RemoteAgentGui.showErrorMsg("Cannot write to file " + f.getAbsolutePath());
                    System.exit(1);
                }
            }
        }
        RemoteAgentGui.showErrorMsg("Cannot create output file");
        System.exit(1);
        return null;
    }

    private static String getCurrrentTimeAsString() {
        return new SimpleDateFormat("yyyyddMM_HHmmss").format(new Date());
    }

    protected RemoteAgentGui(String title) {
        setTitle("REMOTE AGENT");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if (Toolkit.getDefaultToolkit().getScreenSize().width > 1024) {
            setMinimumSize(new Dimension(1024, 600));
        } else {
            setMinimumSize(new Dimension(500, 350));
        }

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setOpaque(true);
        setContentPane(contentPane);

        setJMenuBar(createMenuBar());

        splitRight.setDividerSize(5);
        splitRight.setTopComponent(pnlCurrentInstaller);
        pnlCurrentInstaller.setPreferredSize(new Dimension(290, 440));
        splitRight.setBottomComponent(pnlOutput);

        splitMain.setLeftComponent(pnlAvailableInstaller);
        pnlAvailableInstaller.setPreferredSize(new Dimension(290, 400));
        splitMain.setRightComponent(splitRight);
        splitMain.setDividerSize(5);

        contentPane.add(splitMain, BorderLayout.CENTER);

        pnlAvailableInstaller.getJlist().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                String name = (String) pnlAvailableInstaller.getJlist().getSelectedValue();
                if (name != null) {
                    showInstaller(name);
                }
            }
        });
        pnlCurrentInstaller.addHyperlinkListener(this);

        pnlAvailableInstaller.showInstallers(InstallerRepository.INSTANCE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

        mainFrame = this;
    }

    @Override
    public void toFront() {
        super.toFront();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitMain.setDividerLocation(0.25);
                splitRight.setDividerLocation(0.75);
            }
        });
    }

    public void showInstaller(String name) {
        pnlCurrentInstaller.displayInstaller(InstallerRepository.INSTANCE.getInstaller(name));
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }
        String installerName = pnlCurrentInstaller.getInstallerName(event);
        final IInstaller installer = InstallerRepository.INSTANCE.getInstaller(installerName);

        GUIUtils.maskWindow(this);

        try {
            AsyncTask<Void> worker = new AsyncTask<Void>() {
                @Override
                protected Void doLengthlyTask() {
                    try {
                        System.out.println("=============[" + getCurrrentTimeAsString() + "] launch: "
                                + installer.getName());
                        installer.execute();
                        System.out.println("xxxxxxxxxxxxx[" + getCurrrentTimeAsString() + "] done: "
                                + installer.getName());
                        onExitInstaller(null);
                    } catch (Exception e) {
                        onExitInstaller(e);
                    }
                    return null;
                }

                @Override
                protected void doneInternal(Void taskResult) {
                }

                @Override
                protected void cleanUp() {
                    GUIUtils.unmaskWindow(RemoteAgentGui.this);
                }
            };

            worker.execute();
        } catch (Exception e) {
            onExitInstaller(e);
        }
    }

    private void onExitInstaller(Exception e) {
        GUIUtils.unmaskWindow(this);
        if ((e == null) || (e instanceof NormalExitInstallerRuntimeException)) {
            System.out.println("Terminated successfully");
        } else if (e instanceof ExitInstallerRuntimeException) {
            if (e.getCause() != null) {
                System.out.println(RemoteAgentGui.getStacktraceAsString(e));
            }
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else {
                showErrorMsg("Terminated unsuccessfully");
            }
        } else {
            if (Thread.getDefaultUncaughtExceptionHandler() != null) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(
                        Thread.currentThread(), e);
            }
        }
    }

    public static RemoteAgentGui getInstance() {
        if (mainFrame == null) {
            throw new RuntimeException(String.format("%s is not initializes. Please use %s to init it",
                    RemoteAgentGui.class.getSimpleName(), RemoteAgent.class.getSimpleName()));
        }
        return mainFrame;
    }

    public void showIntoContentPane(final JPanel pnl) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getContentPane().invalidate();
                getContentPane().removeAll();
                getContentPane().add(pnl, BorderLayout.CENTER);
                getContentPane().validate();
            }
        });
    }

    public JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu mnu = new JMenu("Settings");
        mb.add(mnu);

        addMenuItem("Manage remote hosts", mnu, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HostManagementController ctrl = new HostManagementController();
                ctrl.show();
            }
        });

        addMenuItem("Manage HOSTS file", mnu, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HostFileManagementController ctrl = new HostFileManagementController();
                ctrl.show();
            }
        });

        addMenuItem("Manage passwords", mnu, null);

        return mb;
    }

    private void addMenuItem(String label, JMenu into, ActionListener action) {
        JMenuItem mit = new JMenuItem(label);
        if (action != null) {
            mit.addActionListener(action);
        }
        into.add(mit);
    }

    public static void showErrorMsg(String msg) {
        JPanel pnl = createOptionPaneContent(msg);
        JOptionPane.showMessageDialog(RemoteAgentGui.getInstance(),
                pnl, "ERROR", JOptionPane.ERROR_MESSAGE);
        System.out.println(msg);
    }

    public static void showInfoMsg(String msg) {
        JPanel pnl = createOptionPaneContent(msg, null);
        JOptionPane.showMessageDialog(RemoteAgentGui.getInstance(),
                pnl, "INFO", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String getStacktraceAsString(Throwable e) {
        StringWriter strWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(strWriter);
        e.printStackTrace(printWriter);
        String result = strWriter.toString();
        printWriter.close();
        return result;
    }

    public static boolean showConfirmationYesNo(String msg) {
        JCheckBox chk = new JCheckBox("I've read the above message");
        JPanel pnl = createOptionPaneContent(msg, chk);
        do {
            int res = JOptionPane.showConfirmDialog(RemoteAgentGui.getInstance(),
                    pnl,
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if ((res == JOptionPane.YES_OPTION) && chk.isSelected()) {
                return true;
            }
        } while (!chk.isSelected());

        return false;
    }

    private static JPanel createOptionPaneContent(String msg) {
        return createOptionPaneContent(msg, null);
    }

    private static JPanel createOptionPaneContent(String msg, final JCheckBox chk) {
        String[] lines = StringUtils.splitPreserveAllTokens(msg, "\n");

        JPanel pnl = new JPanel();
        PainlessGridBag gbl = new PainlessGridBag(pnl, false);
        for (String line : lines) {
            gbl.row().cell(new JLabel(StringUtils.isBlank(line) ? " " : line));
        }
        GUIUtils.addSeparator("", gbl);
        if (chk != null) {
            chk.setMnemonic(KeyEvent.VK_X);
            gbl.row().cell(chk);
        }
        gbl.done();
        return pnl;
    }
}
