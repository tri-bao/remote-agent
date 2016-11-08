/**
 *
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.host.controller.HostMamanger;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.tag.controller.TagSelectionController;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.funsoft.remoteagent.util.FileChooser;
import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 */
public class HostDetailPanel extends AbstractConfigInputPanel {
    private final JTextField txtDisplayName = GUIUtils.newTextField();
    private final JTextArea txeHost = GUIUtils.newTextArea();
    private final JTextField txtPort = GUIUtils.newTextField();
    private final JTextField txtUsername = GUIUtils.newTextField();

    private final JPasswordField txtPassword = GUIUtils.newPwdField();
    private final JTextField txtPrivateKeyFilePath = GUIUtils.newTextField();
    private final JButton btnChooseFile = new JButton("Select file");
    private final JButton btnClearFile = new JButton("Clear file");

    private final JTextField txtInternalIp = GUIUtils.newTextField();
    private final JTextArea txeInternalDns = GUIUtils.newTextArea();
    private final JTextArea txeDescription = GUIUtils.newTextArea();
    private final JButton btnTags = new JButton("Tag");
    private final JPanel pnlTags = new JPanel();
    private List<TagDto> selectedTags;

    private String uuid;

    public HostDetailPanel() {
        init();
    }

    @Override
    protected void initState() {
        txeHost.setRows(3);
        txtPrivateKeyFilePath.setEnabled(false);
        txtPort.setText("22");
        GUIUtils.fixWidth(txtPort, 50);
        showTags(Arrays.asList(new TagDto("No tag")));
    }

    @Override
    protected void initAction() {
        super.initAction();
        AbstractAction enterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton defaultButton = getRootPane().getDefaultButton();
                if (defaultButton != null) {
                    defaultButton.doClick();
                }
            }
        };
        GUIUtils.preventInputingNewLineOnTextArea(txeHost, enterAction);

        btnClearFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPrivateKeyFilePath.setText(null);
            }
        });
        btnChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File chooseFile = FileChooser.chooseFile("CHOOSE A PRIVATE KEY FILE", ".pem");
                if (chooseFile != null) {
                    txtPrivateKeyFilePath.setText(chooseFile.getAbsolutePath());
                }
            }
        });
        btnTags.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TagSelectionController c = new TagSelectionController();
                List<TagDto> select = c.select(selectedTags);
                if (select == null) { // cancel
                    return;
                }
                selectedTags = select;
                showTags(selectedTags);
            }
        });
    }

    private void showTags(List<TagDto> tags) {
        pnlTags.removeAll();
        pnlTags.invalidate();
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }
        PainlessGridBag gbl = new PainlessGridBag(pnlTags, GUIUtils.getZeroSurroundingGridBagConfig(), false);
        for (TagDto tagDto : tags) {
            JLabel lbl = lbl("- " + tagDto.toString());
            GUIUtils.fixWidth(lbl, 450);
            gbl.row().cell(lbl);
        }
        gbl.done();
        validate();
    }

    @Override
    protected void initLayout(PainlessGridBag gbl) {
        gbl.row().cell(lbl("Display name*")).cell(txtDisplayName).fillX();
        note("for EC2, this should match instance name", gbl.row().cell(), gbl);

        GUIUtils.addSeparator("SSH connection", gbl);
        gbl.row().cell(lbl("DNS name or IP*")).cell(new JScrollPane(txeHost)).fillX();
        gbl.row().cell(lbl("Port*")).cell(txtPort);
        gbl.row().cell(lbl("Username*")).cell(txtUsername).fillX();
        gbl.row().cell(lbl("Password")).cell(txtPassword).fillX();
        gbl.row().cell(lbl("Private key")).cell(pnlPrivateKey()).fillX();

        GUIUtils.addSeparator("Internal addresses", gbl);
        gbl.row().cell(lbl("Internal IP")).cell(txtInternalIp).fillX();
        gbl.row().cell(lbl("Internal DNS")).cell(new JScrollPane(txeInternalDns)).fillXY();
        note("1 DNS / line", gbl.row().cell(), gbl);

        GUIUtils.addSeparator("Misc.", gbl);
        gbl.row().cell(btnTags).cell(pnlTags);
        gbl.row().cell(lbl("Description")).cell(new JScrollPane(txeDescription)).fillXY();
    }

    @Override
    protected void doneLayout(PainlessGridBag gbl) {
        gbl.done();
    }

    private JPanel pnlPrivateKey() {
        JPanel pnl = new JPanel();
        PainlessGridbagConfiguration cfg = GUIUtils.getZeroSurroundingGridBagConfig();

        PainlessGridBag gbl = new PainlessGridBag(pnl, cfg, false);
        gbl.row().cell(btnChooseFile).cell(txtPrivateKeyFilePath).fillX().cell(btnClearFile);
        gbl.done();
        return pnl;
    }

    @Override
    protected boolean checkValid() {
        boolean b =
                GUIUtils.requireMandatory(txtDisplayName, "Display name")
                        && GUIUtils.requireMandatory(txeHost, "DNS name or IP")
                        && GUIUtils.checkPort(txtPort, "Port", true)
                        && GUIUtils.requireMandatory(txtUsername, "Username");
        if (!b) {
            return false;
        }
        List<HostDto> knownHosts = HostMamanger.getInstance().getAllHosts();
        for (HostDto hostDto : knownHosts) {
            if (StringUtils.equalsIgnoreCase(hostDto.getUuid(), uuid)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(hostDto.getHost(), GUIUtils.getText(txeHost))) {
                RemoteAgentGui.showErrorMsg("Host " + hostDto.getHost() + " exists in the list");
                return false;
            }

            // check duplicated display name
            if ((hostDto.getDisplayName() != null)
                    && StringUtils.equalsIgnoreCase(hostDto.getDisplayName(), GUIUtils.getText(txtDisplayName))) {
                boolean confirmed = RemoteAgentGui.showConfirmationYesNo("Host with display name \""
                        + hostDto.getDisplayName() + "\" has already existed\n"
                        + "You still want to use this name?");
                if (!confirmed) {
                    return false;
                }
            }

            // check duplicated internal IP
            if ((hostDto.getInternalIp() != null)
                    && StringUtils.equalsIgnoreCase(hostDto.getInternalIp(), GUIUtils.getText(txtInternalIp))) {
                boolean confirmed = RemoteAgentGui.showConfirmationYesNo(
                        "Internal IP \"" + hostDto.getInternalIp() + "\""
                                + " has already been assigned to host\n\""
                                + hostDto.getDisplayInfo()
                                + "\" \n\n"
                                + "You still want to use this IP?");

                if (!confirmed) {
                    return false;
                }
            }
        }
        return true;
    }

    public void collectData(HostDto dto) {
        dto.setDisplayName(GUIUtils.getText(txtDisplayName));
        dto.setHost(GUIUtils.getText(txeHost));
        dto.setPort(GUIUtils.getInteger(txtPort));
        dto.setUsername(GUIUtils.getText(txtUsername));

        dto.setPassword(GUIUtils.getText(txtPassword));
        dto.setPrivateKeyFilePath(GUIUtils.getText(txtPrivateKeyFilePath));
        dto.setInternalIp(GUIUtils.getText(txtInternalIp));
        dto.setInternalDns(collectLines(txeInternalDns));
        dto.setTags(selectedTags);
        dto.setDescription(GUIUtils.getText(txeDescription));
    }

    private List<String> collectLines(JTextArea txe) {
        String text = GUIUtils.getText(txe);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String[] split = StringUtils.split(text, "\n");
        List<String> rs = new ArrayList<>(split.length);
        for (String s : split) {
            if (StringUtils.isNotBlank(s)) {
                rs.add(StringUtils.stripToNull(s));
            }
        }
        return rs;
    }

    public void fillDataToScreen(HostDto dto) {
        uuid = dto == null ? null : dto.getUuid();
        txtDisplayName.setText(dto == null ? null : dto.getDisplayName());
        txeHost.setText(dto == null ? null : dto.getHost());
        txtPort.setText(dto == null ? null : dto.getPort() + "");
        txtUsername.setText(dto == null ? null : dto.getUsername());
        txtPassword.setText(dto == null ? null : dto.getPassword());
        txtPrivateKeyFilePath.setText(dto == null ? null : dto.getPrivateKeyFilePath());
        txtInternalIp.setText(dto == null ? null : dto.getInternalIp());
        txeInternalDns.setText(dto == null ? null : StringUtils.join(dto.getInternalDns(), "\n"));

        selectedTags = dto == null ? null : dto.getTags();
        showTags(selectedTags);

        txeDescription.setText(dto == null ? null : dto.getDescription());
    }

    public void setForMakingConnection() {
        txtDisplayName.setEditable(false);
        txtInternalIp.setEditable(false);
        txtPort.setEditable(false);
        txeInternalDns.setEnabled(false);
        btnTags.setEnabled(false);
        txeDescription.setEnabled(false);
    }
}
