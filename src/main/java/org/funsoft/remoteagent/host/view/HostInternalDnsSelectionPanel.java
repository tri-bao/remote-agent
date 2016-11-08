/**
 *
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.collections.CollectionUtils;
import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.DefaultJList;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.util.List;

/**
 * @author htb
 */
public class HostInternalDnsSelectionPanel extends AbstractConfigInputPanel {
    private final HostDto host;
    private final boolean multi;
    private final DefaultJList<String> lstDns = new DefaultJList<>();

    public static String selectSignleExistIfCancel(String title, HostDto host) {
        HostInternalDnsSelectionPanel pnl = new HostInternalDnsSelectionPanel(host, false);
        HostInternalDnsSelectionPanel.showInputExitIfCancel(pnl, title,
                700, 300);
        return pnl.lstDns.getSelectedValue();
    }

    public static List<String> selectMultiExistIfCancel(String title, HostDto host) {
        HostInternalDnsSelectionPanel pnl = new HostInternalDnsSelectionPanel(host, true);
        HostInternalDnsSelectionPanel.showInputExitIfCancel(pnl, title,
                700, 300);
        return pnl.lstDns.getSelectedValuesList();
    }


    private HostInternalDnsSelectionPanel(HostDto host, boolean multi) {
        this.host = host;
        this.multi = multi;
        init();
    }

    @Override
    protected void initState() {
        DefaultListModel<String> model = new DefaultListModel<>();
        lstDns.setModel(model);
        if (CollectionUtils.isNotEmpty(host.getInternalDns())) {
            for (String d : host.getInternalDns()) {
                model.addElement(d);
            }
        }
        if (multi) {
            lstDns.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        } else {
            lstDns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        if (model.getSize() == 1) {
            lstDns.setSelectedIndex(0);
        }
    }

    @Override
    protected void initLayout(PainlessGridBag gbl) {
        String hostName = host.getDisplayInfo();
        gbl.row().cell(lbl("From host: " + hostName));
        gbl.row().cell(new JScrollPane(lstDns)).fillXY();
    }

    @Override
    protected boolean checkValid() {
        List<String> selectedDns = lstDns.getSelectedValuesList();
        if (CollectionUtils.isEmpty(selectedDns)) {
            RemoteAgentGui.showErrorMsg("No DNS name selected");
            return false;
        }
        return true;
    }
}
