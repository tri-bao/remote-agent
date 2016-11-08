package org.funsoft.remoteagent.util;

import java.io.*;
import java.util.Properties;

/**
 * @author Ho Tri Bao
 */
public class Preferences {
    private final String preferenceFile;

    private final String preferenceHome;

    private Properties preferences = null;

    private String remoteStaginFolder;

    private static Preferences instance;

    /**
     * @param configDirName name of the folder (will be appended with a '.' character) to be created in the user home
     *                      to store application preferences.
     */
    public static void init(String configDirName) {
        if (instance != null) {
            return;
        }
        instance = new Preferences(configDirName);
        instance.load();
    }

    public static Preferences getInstance() {
        if (instance == null) {
            throw new RuntimeException("Not yet initialized! Please call Preferences.init(...) first");
        }
        return instance;
    }

    private Preferences(String configDirName) {
        preferenceHome = System.getProperty("user.home")
                + File.separatorChar + "." + configDirName + File.separatorChar;
        preferenceFile = preferenceHome + ".remote-agent-preferences";
    }

    public Properties load() {
        File dir = new File(preferenceHome);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (preferences != null) {
            return preferences;
        }
        BufferedInputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(preferenceFile));
            Properties tmp = new Properties();
            tmp.load(inStream);
            preferences = tmp;
        } catch (Exception ignore) {
            preferences = new Properties();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception ignore) {
                }
            }
        }
        return preferences;
    }

    public void store() {
        BufferedOutputStream outStream = null;
        try {
            File file = new File(preferenceFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            outStream = new BufferedOutputStream(new FileOutputStream(preferenceFile, false));
            if (preferences != null) {
                preferences.store(outStream, "");
            }
        } catch (Exception ignore) {
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public void set(String key, String value) {
        load().setProperty(key, value);
        store();
    }

    public String get(String key) {
        String val = load().getProperty(key);
        if (val == null) {
            return null;
        }
        val = val.trim();
        if (val.equals("")) {
            return null;
        }
        return val;
    }

    public String getPreferenceHome() {
        return preferenceHome;
    }

    public String getRemoteStaginFolder() {
        return remoteStaginFolder;
    }

    public void setRemoteStagingFolder(String stagingFolder) {
        if (stagingFolder == null) {
            throw new IllegalArgumentException("stagingFolder param must not be null");
        }
        stagingFolder = stagingFolder.trim();
        if (stagingFolder.endsWith("/")) {
            stagingFolder = stagingFolder.substring(0, stagingFolder.length() - 1);
        }
        this.remoteStaginFolder = stagingFolder;
    }
}
