package org.funsoft.remoteagent.gui.component;

import org.funsoft.remoteagent.installer.InstallerRepository;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ho Tri Bao
 *
 */
public class AvailableInstallerPanel extends JPanel {
    private final JList jlist = new DefaultJList();
    
    public AvailableInstallerPanel() {
        setLayout(new BorderLayout());
        
        JScrollPane scr = new JScrollPane();
        scr.setViewportView(jlist);
        add(scr, BorderLayout.CENTER);
    }
    
    public JList getJlist() {
        return jlist;
    }

    public void showInstallers(InstallerRepository repo) {
        DefaultListModel model = new DefaultListModel();
        int i = 0;
        for (String nm : repo.getInstallerNames()) {
            model.add(i++, nm);
        }
        jlist.setModel(model);
    }
}
