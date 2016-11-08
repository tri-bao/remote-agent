/**
 * 
 */
package org.funsoft.remoteagent.installer.os;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import org.funsoft.remoteagent.cnf.InputUtils;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.os.config.UserAccountInputPanel;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.util.FileChooser;

import java.io.File;

/**
 * @author htb
 *
 */
public class CreatePasswordlessSshUserInstaller extends AbstractOsInstaller {
	private static final String GROUP = "sudo";
	
	private static CreatePasswordlessSshUserInstaller instance;
	public static CreatePasswordlessSshUserInstaller getInstance() {
		if (instance == null) {
			instance = new CreatePasswordlessSshUserInstaller();
		}
		return instance;
	}
	
	protected CreatePasswordlessSshUserInstaller() {
		super("OS - create new ssh user");
	}

	@Override
	public String getDescription() {
		return "User được tạo sẽ đăng nhập vào ssh sử dụng private key";
	}

	@Override
	protected void performInternal() throws Exception {
		boolean createNewUser = confirm("Tạo user mới (YES)?\n\n"
				+ "hay generate/upload key cho user hiện có (NO)?");
		
		String username;
		if (createNewUser) {
			username = createNewSshUser();
		} else {
			username = InputUtils.askSingleText("username", true, null);
			
			// add user to sudo group
			sshSudoStrict("usermod -aG " + GROUP + " " + username);
		}

		String publicFilePath;
		if (confirm("Generate new public/private key pair?\n\n"
				+ "Yes (generate new) / No (choose existing keys)")) {
			publicFilePath = genKeys(username);
		} else {
			File file = FileChooser.chooseFileExitIfNull("SELECT A PUBLIC KEY FILE", ".pub");
			publicFilePath = file.getAbsolutePath();
		}
		
		copyPublicKeyToServer(new File(publicFilePath), username);
	}
	
	private String createNewSshUser() throws Exception {
		UserAccountInputPanel pnl = UserAccountInputPanel.showInputExitIfCancel(
				UserAccountInputPanel.class,
				"NEW USER", 500, 250);
		
		if (!doesGroupExist(GROUP)) {
			//sshSudoStrict("sudo groupadd " + GROUP);
			throw new ExitInstallerRuntimeException("Group " + GROUP + " phải có sẵn trên hệ điều hành rồi");
		}

		sshSudoStrict("useradd --shell /bin/bash --create-home -c "
				+ pnl.getUsername() + " -g " + GROUP + " " +  pnl.getUsername());

		changeOsUserPassword(pnl.getUsername(), pnl.getPassword());
		return pnl.getUsername();
	}
	
	private String genKeys(String username) throws Exception {
	    String publicFileName = username + "-key.pub";
	    String privateFilePath;
	    String publicFilePath;

	    do {
			File folder = FileChooser.chooseFolderExitIfNull("WHERE TO SAVE KEY FILES");
	
			privateFilePath = folder.getAbsolutePath() + "/" + username + "-key.pem";
			publicFilePath = folder.getAbsolutePath() + "/" + publicFileName;
		    
		    if (new File(privateFilePath).exists()) {
		    	RemoteAgentGui.showErrorMsg("File "
		    			+ privateFilePath
		    			+ " đã tồn tại rồi. Hãy chọn lại nơi khác");
		    } else if (new File(publicFilePath).exists()) {
		    	RemoteAgentGui.showErrorMsg("File "
		    			+ publicFilePath
		    			+ " đã tồn tại rồi. Hãy chọn lại nơi khác");
		    } else {
		    	break;
		    }
	    } while (true);
	    
	    JSch jsch = new JSch();
		KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);
		try {
			kpair.setPassphrase("");
			kpair.writePrivateKey(privateFilePath);
			kpair.writePublicKey(publicFilePath, "public key for user "
					+ username);
		} finally {
			kpair.dispose();
		}

		return publicFilePath;
	}
	
	private void copyPublicKeyToServer(File localFile, String username) throws Exception {
		String destFolder = "/home/" + username + "/.ssh";
		sshMkDirIfNotExist(destFolder);
		scpToRemoteStaging(localFile.getAbsolutePath(),  localFile.getName());
		sshSudoStrict("chmod 777 " + destFolder);
		sshSudoStrict("cat " + getStagingFolder() + "/" + localFile.getName()
				+ " >> " + destFolder  + "/authorized_keys");
		sshSudoStrict("chown -R " + username + ":" + GROUP + " " + destFolder);
		sshSudoStrict("chmod 600 " + destFolder + "/*");
		sshSudoStrict("chmod 700 " + destFolder);
	}
}
