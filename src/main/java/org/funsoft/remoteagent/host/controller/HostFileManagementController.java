/**
 * 
 */
package org.funsoft.remoteagent.host.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cnf.InputUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AbstractController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.host.dto.HostFileDto;
import org.funsoft.remoteagent.host.dto.HostFileEntryDto;
import org.funsoft.remoteagent.host.view.ConfirmHostFileContentPanel;
import org.funsoft.remoteagent.host.view.HostFileManagementPanel;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.util.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author htb
 *
 */
public class HostFileManagementController extends AbstractController<HostFileManagementPanel> {
	private static final String HOSTS_FILE = Preferences.getInstance().getPreferenceHome() + ".etc-hosts.xml";
	private List<HostFileDto> files;
	private HostFileDto selectedFile = null;
	public HostFileDto selectAHost() {
		setView(new HostFileManagementPanel(true));
		
		files = loadHostFile();
		getView().showFiles(files);
		initAction4Select();
		
		GUIUtils.showInDialog(getView(), "SELECT A HOSTS FILE");
		return selectedFile;
	}
	public void show() {
		setView(new HostFileManagementPanel(false));
		files = loadHostFile();
		getView().showFiles(files);
		initAction();
		
		GUIUtils.showInDialog(getView(), "HOSTS FILE");
	}
	private List<HostFileDto> loadHostFile() {
		File f = new File(HOSTS_FILE);
		if (!f.exists()) {
			return new ArrayList<>();
		}
		
		XStream xstream = new XStream(new DomDriver());
		List<HostFileDto> files = (List<HostFileDto>) xstream.fromXML(f);

		if (files == null) {
			files = new ArrayList<>();
		}

		return files;
	}
	private void initAction4Select() {
		initAction();
		getView().getBtnSelect().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getView().getSelectedFile() == null) {
					RemoteAgentGui.showErrorMsg("Phải chọn 1 file");
					return;
				}
				selectedFile = getView().getSelectedFile();
				SwingUtilities.getWindowAncestor(getView()).dispose();
			}
		});
	}
	private void initAction() {
		getView().getBtnAdd().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAdd();
			}
		});
		getView().getBtnDelete().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
	}
	private void onAdd() {
		MultiHostSelectionController ctrl = new MultiHostSelectionController();
		List<HostDto> selectedHosts = ctrl.select();
		if (CollectionUtils.isEmpty(selectedHosts)) {
			return;
		}
		
		String tenFile;
		do {
			tenFile = InputUtils.askSingleText("Tên file", true, null, "vd: hosts-topwat-on-ec2");
			if (StringUtils.isBlank(tenFile)) {
				return;
			}
			// check file name exist
			for (HostFileDto f : files) {
				if (StringUtils.equalsIgnoreCase(tenFile, f.getName())) {
					RemoteAgentGui.showErrorMsg("File name existed");
					continue;
				}
			}
			break;
		} while (true);
		
		HostFileDto file = new HostFileDto();
		file.setName(tenFile);
		file.setIpToNames(new ArrayList<>());
		
		Set<String> dnsSet = new HashSet<>();
		for (HostDto hostDto : selectedHosts) {
			if (StringUtils.isBlank(hostDto.getInternalIp())) {
				continue;
			}
			if (CollectionUtils.isEmpty(hostDto.getInternalDns())) {
				continue;
			}
			for (String dns : hostDto.getInternalDns()) {
				if (dnsSet.contains(dns)) {
					RemoteAgentGui.showErrorMsg("Host name \"" + dns + "\" appear twice");
					return;
				}
				dnsSet.add(dns);
				file.getIpToNames().add(new HostFileEntryDto(hostDto.getInternalIp(), dns));
			}
		}
		
		ConfirmHostFileContentPanel pnlConfirm = new ConfirmHostFileContentPanel(
				file.formatHostFile());
		GUIUtils.showInDialog(pnlConfirm, "CONFIRM FILE CONTENT", 600, 500);
		if (!pnlConfirm.isCancel()) {
			files.add(file);
			writeHostFile();
			getView().addFile(file);
		}
	}
	
	private void writeHostFile() {
		XStream xstream = new XStream(new DomDriver());
		
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(
					new FileOutputStream(new File(HOSTS_FILE), false), "UTF-8");
			xstream.toXML(files, osw);
		} catch (Exception e) {
			throw new ExitInstallerRuntimeException(e);
		} finally {
			IOUtils.closeQuietly(osw);
		}
	}
	private void onDelete() {
		HostFileDto selectedFile = getView().getSelectedFile();
		if (selectedFile == null) {
			return;
		}
		if (RemoteAgentGui.showConfirmationYesNo("Thật sự muốn delete host file:\n"
				+ selectedFile.getName())) {
			files.remove(selectedFile);
			writeHostFile();
			getView().removeSelectedFile();
		}
	}
}
