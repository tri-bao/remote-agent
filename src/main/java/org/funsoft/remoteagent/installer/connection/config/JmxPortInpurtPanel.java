/**
 * 
 */
package org.funsoft.remoteagent.installer.connection.config;

import org.funsoft.remoteagent.cnf.AbstractConfigInputPanel;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author htb
 *
 */
public class JmxPortInpurtPanel extends AbstractConfigInputPanel {
	private final JComboBox<PortWithCommentDto> cbxPort = new JComboBox<>();
	private final JTextField txtCustomPort = GUIUtils.newTextField();
	private final JRadioButton radPredefined = new JRadioButton("Predefined port");
	private final JRadioButton radCustom = new JRadioButton("Custom port");
	
	private final PortWithCommentDto[] predefinedPorts;
	public JmxPortInpurtPanel(PortWithCommentDto... predefinedPorts) {
		this.predefinedPorts = predefinedPorts;
		init();
	}
	
	@Override
	protected void initState() {
		ButtonGroup gr = new ButtonGroup();
		gr.add(radCustom);
		gr.add(radPredefined);
		radPredefined.setSelected(true);
		txtCustomPort.setEditable(false);
		GUIUtils.fixWidth(txtCustomPort, 50);
		
		for (PortWithCommentDto p : predefinedPorts) {
			cbxPort.addItem(p);
		}
	}

	@Override
	protected void initAction() {
		super.initAction();
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtCustomPort.setEditable(radCustom.isSelected());
				cbxPort.setEnabled(radPredefined.isSelected());
				if (!radCustom.isSelected()) {
					txtCustomPort.setText(null);
				}
			}
		};
		radCustom.addActionListener(al);
		radPredefined.addActionListener(al);
	}

	@Override
	protected void initLayout(PainlessGridBag gbl) {
		gbl.row().cell(radPredefined).cell(cbxPort);
		gbl.row().cell(radCustom).cell(txtCustomPort);
	}

	@Override
	protected boolean checkValid() {
		return !radCustom.isSelected() || GUIUtils.checkPort(txtCustomPort, "Port", true);
	}

	public PortWithCommentDto getSelectedPort() {
		if (radPredefined.isSelected()) {
			return (PortWithCommentDto) cbxPort.getSelectedItem();
		}
		return new PortWithCommentDto(GUIUtils.getInteger(txtCustomPort), "");
	}
}
