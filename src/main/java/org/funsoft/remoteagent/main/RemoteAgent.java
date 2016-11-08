package org.funsoft.remoteagent.main;

import org.funsoft.remoteagent.installer.InstallerGuiExceptionHandler;
import org.funsoft.remoteagent.installer.InstallerRepository;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.util.Preferences;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.InputStream;
import java.util.Enumeration;

public class RemoteAgent {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(AbstractInstaller::closeAllSshSessions));
        setupUncaughtExceptionHandler();
        makeUIThemeBetter();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void main(String[] args) throws Exception {
        builder()
                .appTitle("ULAB REMOTE AGENT")
                .preferencesFolderName("ulab-remote-agent")
                .useArialFont()
                .addDefaultInstallers()
                .stagingFolderOnRemoteServer("/tmp/staging")
                .build();
    }

    private static class Builder {
        private boolean addDefaultInstaller = false;
        private boolean useArialFont = false;
        private String title = "REMOTE AGENT";
        private String preferencesFolderName = "remote-agent";
        private String stagingFolder = "/tmp/remote-agent-staging";

        public Builder appTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder preferencesFolderName(String name) {
            this.preferencesFolderName = name;
            return this;
        }

        public Builder addDefaultInstallers() {
            addDefaultInstaller = true;
            return this;
        }

        public Builder useArialFont() {
            useArialFont = true;
            return this;
        }

        public Builder stagingFolderOnRemoteServer(String stagingFolder) {
            this.stagingFolder = stagingFolder;
            return this;
        }

        public void build() {
            Preferences.init(preferencesFolderName);
            if (useArialFont) {
                useArialFont();
            }
            Preferences.getInstance().setRemoteStagingFolder(stagingFolder);
            if (addDefaultInstaller) {
                InstallerRepository.INSTANCE.addDefaultInstallers();
            }

            RemoteAgentGui frame = new RemoteAgentGui(title);
            frame.pack();
            frame.setVisible(true);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.toFront();
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });
        }
    }

    private static void setupUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                try {
                    (new InstallerGuiExceptionHandler()).handle(e);
                } catch (Exception e2) {
                    (new InstallerGuiExceptionHandler()).handle(e2);
                }
            }
        });
        System.setProperty("sun.awt.exception.handler",
                InstallerGuiExceptionHandler.class.getCanonicalName());
    }

    private static void makeUIThemeBetter() {
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (key.equals("FormattedTextField.inactiveForeground")) {
                // this is for JDateChooser
                UIManager.put(key, Color.BLACK);
            }
            if (key.equals("TextField.inactiveForeground")) {
                // This affects autosuggestion box
                UIManager.put(key, Color.BLACK);
            }
            if (key.equals("ComboBox.disabledForeground")) {
                UIManager.put(key, Color.BLACK);
            }
        }
    }

    private static void useArialFont() {
        String[] avaiFonts =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        boolean arialFound = false;
        for (String f : avaiFonts) {
            if (f.equalsIgnoreCase("Arial")) {
                arialFound = true;
                break;
            }
        }
        if (!arialFound) {
            installFont("Arial.ttf");
            installFont("Arial_Italic.ttf");
            installFont("Arial_Bold.ttf");
            installFont("Arial_Bold_Italic.ttf");
        }
        Font defaultFont = new Font("Arial", Font.PLAIN, 12);
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, defaultFont);
            }
        }
    }

    private static void installFont(String file) {
        Font font = installFontFromClasspathResource(file);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font.deriveFont(Font.BOLD));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font.deriveFont(Font.ITALIC));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font.deriveFont(10.0f));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font.deriveFont(9.0f));
    }

    private static Font installFontFromClasspathResource(String fontFileLocation) {
        Font font;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fontFileLocation);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        return font;
    }

}
