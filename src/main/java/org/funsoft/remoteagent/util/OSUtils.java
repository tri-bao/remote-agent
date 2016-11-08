/*
 * OSUtils.java
 *
 * Project: qlhk
 *
 * Margin: 100 characters.
 *
 * $Source: $
 * $Revision:  $
 * ----------------------------------------------------------------------------
 * WHEN           WHO     VER     DESCRIPTION
 * Sep 5, 2009     HTB     1.0     Creation
 * ----------------------------------------------------------------------------
 */
package org.funsoft.remoteagent.util;

/**
 * Utilities dealing with Operating system.
 * 
 * @author Ho Tri Bao
 *
 */
public final class OSUtils {
    public static boolean isWindows() {
        return getOsName().toLowerCase().contains("windows");
    }
    
    public static boolean isLinux() {
        return getOsName().toLowerCase().contains("linux");
    }

    public static boolean isMacOs() {
        return getOsName().toLowerCase().contains("mac");
    }
    
    private static String getOsName() {
        return System.getProperty("os.name", "unknown");
    }
}
