/**
 * 
 */
package org.funsoft.remoteagent.host.controller;

import com.jcraft.jsch.Session;
import org.funsoft.remoteagent.cmd.ssh.SshConnector;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AbstractController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.host.dto.HostWithSessionDto;
import org.funsoft.remoteagent.host.view.HostSelectionPanel;
import org.funsoft.remoteagent.main.RemoteAgentGui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author htb
 *
 */
public class HostSelectionController extends AbstractController<HostSelectionPanel> {
	private static HostDto previousHost;
	
	private HostWithSessionDto connectedHost = null;
	
	private final String installerName;
	private boolean isCanceled = true;
	private boolean connectSsh = false;
	
	public static HostDto select() {
		HostSelectionController ctrl = new HostSelectionController();
		return ctrl.selectWithoutConnecting();
	}
	
	public HostSelectionController(String installerName) {
		this.installerName = installerName;
	}
	public HostSelectionController() {
		this(null);
	}
	
	public HostDto selectWithoutConnecting() {
		setView(new HostSelectionPanel(installerName));
		getView().getBtnConnect().setText("Select");
		
		getView().getBtnConnect().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getView().checkValid()) {
					isCanceled = false;
					SwingUtilities.getWindowAncestor(getView()).dispose();
				}
			}
		});
		
		getView().displayHosts(HostMamanger.getInstance().getAllHosts());
		getView().setSelectedHost(previousHost);
		
		GUIUtils.showInDialog(getView(), "SELECT A HOST", 1000, 700);
		if (isCanceled) {
			return null;
		}
		previousHost = getView().getSelectedHost();
		return getView().getSelectedHost();
	}
	
	public HostWithSessionDto show(boolean connectSsh) {
		this.connectSsh = connectSsh;
		setView(new HostSelectionPanel(installerName));
		getView().getBtnConnect().setText("Connect");

		getView().getBtnConnect().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		
		getView().displayHosts(HostMamanger.getInstance().getAllHosts());
		getView().setSelectedHost(previousHost);
		
		GUIUtils.showInDialog(getView(), "KẾT NỐI SSH");
		if (isCanceled) {
			return null;
		}
		if (connectedHost != null) {
			previousHost = connectedHost.getHost();
		}
		return connectedHost;
	}
	
	private void onOK() {
		if (!getView().checkValid()) {
			return;
		}
		final HostDto hostInfo = getView().getSelectedHost();
		if (hostInfo == null) {
			return;
		}
		if (!connectSsh) {
			isCanceled = false;
			connectedHost = new HostWithSessionDto(hostInfo, null);
			SwingUtilities.getWindowAncestor(getView()).dispose();
			return;
		}
		doAsync(new AsyncRemoteActionPerformer<Session>() {
			@Override
			protected Session perform() {
		        try {
		        	System.out.println("=====Connecting to "
		        			+ hostInfo.getDisplayInfo());
		            return SshConnector.connect(hostInfo);
		        } catch (Exception e) {
		        	RemoteAgentGui.showErrorMsg("Không kết nối SSH được với server. "  + e.getMessage());
		        	
		            System.out.println(RemoteAgentGui.getStacktraceAsString(e));
		            return null;
		        }
			}
			@Override
			protected void updateGui(Session taskResult) {
				if (taskResult == null) {
					return;
				}
				try {
					isCanceled = false;
					connectedHost = new HostWithSessionDto(hostInfo, taskResult);
					SwingUtilities.getWindowAncestor(getView()).dispose();
				} catch (RuntimeException e) {
					taskResult.disconnect();
					throw e;
				}
			}
		});
	}
}
