/**
 *
 */
package org.funsoft.remoteagent.host.view;

import org.apache.commons.collections.CollectionUtils;
import org.funsoft.remoteagent.gui.component.DefaultJList;
import org.funsoft.remoteagent.host.dto.HostFileDto;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

/**
 * @author htb
 */
public class HostFileManagementPanel extends JPanel {
    private final DefaultJList<HostFileDto> lstFiles = new DefaultJList<>();
    private final JButton btnAdd = new JButton("Add file");
    private final JTextArea txeContent = new JTextArea();
    private final JButton btnDelete = new JButton("Delete file");
    private final JButton btnSelect = new JButton("Select this file");

    private final boolean forSelection;

    public HostFileManagementPanel(boolean forSelection) {
        this.forSelection = forSelection;
        initState();
        initAction();
        initLayout();
    }

    private void initState() {
        txeContent.setEditable(false);
        txeContent.setFont(new Font("monospaced", Font.PLAIN, 15));
        lstFiles.setModel(new DefaultListModel<>());
        btnSelect.setVisible(forSelection);
        btnSelect.setEnabled(false);
    }

    private void initAction() {
        lstFiles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                HostFileDto selectedValue = lstFiles.getSelectedValue();
                if (selectedValue == null) {
                    txeContent.setText(null);
                } else {
                    txeContent.setText(selectedValue.formatHostFile());
                }
                btnSelect.setEnabled(selectedValue != null);
            }
        });
    }

    private void initLayout() {
        PainlessGridBag gbl = new PainlessGridBag(this, false);
        gbl.row().cellX(new JScrollPane(lstFiles), 2).fillXY(0.15, 1.0).cell(new JScrollPane(txeContent)).fillXY(0.85, 1.0);
        gbl.row().cell(btnAdd).cell(btnDelete).cell(btnSelect);
        gbl.constraints(btnAdd).anchor = GridBagConstraints.CENTER;
        gbl.constraints(btnDelete).anchor = GridBagConstraints.CENTER;
        gbl.constraints(btnSelect).anchor = GridBagConstraints.CENTER;
        gbl.done();
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnSelect() {
        return btnSelect;
    }

    public void showFiles(List<HostFileDto> files) {
        DefaultListModel<HostFileDto> model = (DefaultListModel<HostFileDto>) lstFiles.getModel();
        model.clear();
        if (CollectionUtils.isNotEmpty(files)) {
            for (HostFileDto hostFileDto : files) {
                model.addElement(hostFileDto);
            }
        }
    }

    public void addFile(HostFileDto file) {
        DefaultListModel<HostFileDto> model = (DefaultListModel<HostFileDto>) lstFiles.getModel();
        model.addElement(file);
        lstFiles.setSelectedIndex(model.getSize() - 1);
    }

    public HostFileDto getSelectedFile() {
        return lstFiles.getSelectedValue();
    }

    public void removeSelectedFile() {
        DefaultListModel<HostFileDto> model = (DefaultListModel<HostFileDto>) lstFiles.getModel();
        model.removeElementAt(lstFiles.getSelectedIndex());
    }
}
