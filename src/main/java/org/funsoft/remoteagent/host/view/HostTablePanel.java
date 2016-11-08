/**
 *
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.collections.CollectionUtils;
import org.funsoft.remoteagent.gui.component.table.AbstractTableModel;
import org.funsoft.remoteagent.gui.component.table.ScrollableSortablePanel;
import org.funsoft.remoteagent.gui.component.table.SortableTable;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author htb
 */
public class HostTablePanel<M extends AbstractTableModel<HostDto>> extends JPanel {
    private static TagDto previousTag = null;

    private final JLabel lblTotalHost = new JLabel();
    private final JComboBox<TagDto> cbxTags = new JComboBox<>();
    private final ScrollableSortablePanel<HostDto, M> pnlHost;
    private final ActionListener tableFilterAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterByTags();
        }
    };
    private final JButton btnManageTags = new JButton("Manage tag list");

    private final boolean supportFiltering;

    public HostTablePanel(M tableModel) {
        this(tableModel, true);
    }

    public HostTablePanel(M tableModel, boolean supportFiltering) {
        this.supportFiltering = supportFiltering;
        pnlHost = new ScrollableSortablePanel<>(
                tableModel,
                "hosts",
                true,
                new SortableTable<>(-1, false));
        initState();
        initAction();
        initLayout();
    }

    public ScrollableSortablePanel<HostDto, M> getPnlHost() {
        return pnlHost;
    }

    private void initState() {
        btnManageTags.setVisible(false);
        lblTotalHost.setForeground(Color.BLUE);

        cbxTags.addActionListener(tableFilterAction);
    }

    private void initAction() {
    }

    private void initLayout() {
        PainlessGridBag gbl = new PainlessGridBag(this, false);
        if (supportFiltering) {
            gbl.row().cellXRemainder(lblTotalHost);
            gbl.row().cell(new JLabel("Filter tag")).cell(cbxTags).cell(btnManageTags);
        }
        gbl.row().cellXRemainder(pnlHost).fillXY();
        gbl.done();
    }

    public void showHostList(List<HostDto> hosts) {
        pnlHost.showResult(hosts);
        reinitTagCbx();
        updateTotalHost();
    }

    public void clearHostSelection() {
        pnlHost.getTable().getSelectionModel().clearSelection();
    }

    public HostDto getSelectedHost() {
        return pnlHost.getSelectedRowData();
    }

    public void addHost(HostDto host) {
        pnlHost.addData(host);
        reinitTagCbx();
        updateTotalHost();
    }

    public void updateSelectedHost(HostDto host) {
        pnlHost.updateData(host, pnlHost.getTable().getSelectedRowConvertedToModelIndex());
        reinitTagCbx();
        updateTotalHost();
    }

    public void deleteSelectedHost() {
        pnlHost.removeData(pnlHost.getTable().getSelectedRowConvertedToModelIndex());
        reinitTagCbx();
        updateTotalHost();
    }

    private void updateTotalHost() {
        lblTotalHost.setText("Total: " + pnlHost.getSortableModel().getRowCount() + " hosts");
    }

    private void reinitTagCbx() {
        if (!supportFiltering) {
            return;
        }
        List<HostDto> allRowData = pnlHost.getSortableModel().getAllRowData();
        Set<TagDto> items = new TreeSet<>();
        for (HostDto hostDto : allRowData) {
            if (CollectionUtils.isEmpty(hostDto.getTags())) {
                continue;
            }
            for (TagDto tag : hostDto.getTags()) {
                items.add(tag);
            }
        }

        cbxTags.removeActionListener(tableFilterAction);

        cbxTags.removeAllItems();
        cbxTags.addItem(new TagDto("<All>"));
        for (TagDto tag : items) {
            cbxTags.addItem(tag);
        }

        if (previousTag != null) {
            cbxTags.setSelectedItem(previousTag);
        }

        cbxTags.addActionListener(tableFilterAction);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                filterByTags();
            }
        });
    }

    private void filterByTags() {
        if (!supportFiltering) {
            return;
        }
        pnlHost.getTable().setTableFilter(new RowFilter<M, Integer>() {
            @Override
            public boolean include(
                    RowFilter.Entry<? extends M, ? extends Integer> entry) {
                int row = entry.getIdentifier();
                HostDto host = entry.getModel().getRowData(row);

                if (cbxTags.getSelectedIndex() == 0) {
                    return true;
                }
                TagDto selectedTag = (TagDto) cbxTags.getSelectedItem();

                previousTag = selectedTag;

                return (host.getTags() != null) && host.getTags().contains(selectedTag);
            }
        });
    }

    public JButton getBtnManageTags() {
        return btnManageTags;
    }
}
