package org.funsoft.remoteagent.util;

import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.main.RemoteAgentGui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Ho Tri Bao
 *
 */
public class FileChooser {
    private static final String CHOOSEN_DIR_KEY = "last.choosen.folder";

	public static File chooseOneFileExactName(String warFileName) {
		do {
			File file = FileChooser.chooseFileExitIfNull(
					"CHỌN " + warFileName + " FILE", warFileName);
			if (!file.getName().equals(warFileName)) {
				RemoteAgentGui.showErrorMsg("File phải có tên là " + warFileName);
			} else {
				return file;
			}
		} while(true);
	}

    public static File chooseFileWithPrefixExitIfNull(
    		String title, String fileNamePrefix, final String... fileExtensions) {
    	do {
    		File f = chooseFileExitIfNull(title, fileExtensions);
    		if (f.getName().startsWith(fileNamePrefix)) {
    			return f;
    		}
    		RemoteAgentGui.showErrorMsg("Tên file phải bắt đầu bằng: " + fileNamePrefix);
    	} while (true);
    }
    public static File chooseFileExitIfNull(String title, final String... fileExtensions) {
    	File f = choose(title, fileExtensions, JFileChooser.FILES_ONLY);
    	if (f == null) {
    		throw new ExitInstallerRuntimeException();
    	}
    	return f;
    }
    public static File chooseFolderExitIfNull(String title) {
        File f = choose(title, null, JFileChooser.DIRECTORIES_ONLY);
        if (f == null) {
    		throw new ExitInstallerRuntimeException();
    	}
    	return f;
    }
	
    public static File chooseFile(String title, final String... fileExtensions) {
        return choose(title, fileExtensions, JFileChooser.FILES_ONLY);
    }
    public static File chooseFolder(String title) {
        return choose(title, null, JFileChooser.DIRECTORIES_ONLY);
    }

    private static File choose(String title, final String[] fileExtensions, int mode) {
        JFileChooser fc = new JFileChooser();
        fc.setPreferredSize(new Dimension(880, 435));
        
        fc.setFileSelectionMode(mode);
        fc.setDialogTitle(title);
        
        String lastDir = Preferences.getInstance().get(CHOOSEN_DIR_KEY);
        if (lastDir != null) {
        	File file = new File(lastDir);
            if (file.exists() && file.isDirectory()) {
                fc.setCurrentDirectory(file);
            }
        }
        
        if ((fileExtensions != null) && (fileExtensions.length > 0)) {
            fc.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    String des = null;
                    for (String ex : fileExtensions) {
                        if (des != null) {
                            des = ", " + ex;
                        } else {
                            des = ex;
                        }
                    }
                    return des;
                }
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    for (String ex : fileExtensions) {
                        if (f.getName().toLowerCase().endsWith(ex.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            if (selectedFile.isDirectory()) {
            	Preferences.getInstance().set(
            			CHOOSEN_DIR_KEY, selectedFile.getAbsolutePath());
            } else {
            	Preferences.getInstance().set(
            			CHOOSEN_DIR_KEY, selectedFile.getParent());
            }
            return selectedFile;
        }
        return null;
    }
    
    /**
     * @param [0] selected file name; [1] calling name of selected file; [2] selected file name ...
     */
	public static boolean confirmSelectedFiles(String[] fileNameAndFileTypeNames) {
		if ((fileNameAndFileTypeNames.length % 2) != 0) {
			throw new RuntimeException("fileNameAndFileTypeNames.length must be an even number");
		}
		
		String fileList = "XÁC NHẬN FILE ĐÃ CHỌN LÀ ĐÚNG:\n\n";
		for (int i = 0; i < fileNameAndFileTypeNames.length;) {
			fileList += fileNameAndFileTypeNames[i + 1] + ": " + fileNameAndFileTypeNames[i] + "\n";
			i += 2;
		}
		return RemoteAgentGui.showConfirmationYesNo(fileList);
	}
	
	/**
	 * @param files [key: selected file; value: file description]
	 */
	public static boolean confirmSelectedFiles(Map<File, String> files) {
		String fileList = "XÁC NHẬN FILE ĐÃ CHỌN LÀ ĐÚNG:\n\n";
		for (Entry<File, String> et : files.entrySet()) {
			fileList += et.getValue() + ": " + et.getKey().getName() + "\n";
		}
		return RemoteAgentGui.showConfirmationYesNo(fileList);
	}
}
