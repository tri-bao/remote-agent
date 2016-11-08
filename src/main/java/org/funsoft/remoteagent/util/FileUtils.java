package org.funsoft.remoteagent.util;

import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.StringTokenizer;

/**
 * @author Ho Tri Bao
 *
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
	public static String getCurrentDir() {
		if (FileUtils.class.getResource("FileUtils.class").getProtocol().equals("jar")) {
			return new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
		}
		return ".";
	}
	
    public static String askRemotePath() {
        JTextField txtPath = new JTextField();
        txtPath.setPreferredSize(new Dimension(300, 25));
        txtPath.setSize(txtPath.getPreferredSize());
        do {
            JPanel pnl = new JPanel();
            pnl.setLayout(new GridLayout(1, 2, 5, 5));
            pnl.add(new JLabel("Đường dẫn tuyệt đối"));
            pnl.add(txtPath);
            int option = JOptionPane.showConfirmDialog(null, new Object[] { pnl },
                    "Đường dẫn tuyệt đối của file/folder trên máy chủ", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                if ((txtPath.getText() == null) || txtPath.getText().trim().equals("")) {
                    RemoteAgentGui.showErrorMsg("Chưa nhập đường dẫn");
                    continue;
                }
                if (txtPath.getText().endsWith("/")) {
                	RemoteAgentGui.showErrorMsg(
        					"Đường dẫn đến không được kết thúc bằng /");
                	continue;
        		}
                if (!txtPath.getText().startsWith("/")) {
                	RemoteAgentGui.showErrorMsg(
        					"Đường dẫn phải bắt đầu bằng /");
                	continue;
        		}
                
        		String remoteAbsolutePath = txtPath.getText().trim();
        		
        		return remoteAbsolutePath;
            } else {
                throw new ExitInstallerRuntimeException();
            }
        } while (true);
    }
    
    public static String asPath(String linuxPath) {
        if (File.separatorChar == '/') {
            return linuxPath;
        }
        StringTokenizer tkr = new StringTokenizer(linuxPath, "/");
        
        StringBuilder bd = new StringBuilder();
        while (tkr.hasMoreElements()) {
            String s = tkr.nextToken();
            if (bd.length() > 0) {
                bd.append(File.separatorChar);
            }
            bd.append(s);
        }
        return bd.toString();
    }

}
