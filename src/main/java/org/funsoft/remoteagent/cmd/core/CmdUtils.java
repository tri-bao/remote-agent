/*
 * Copyright ROCO 2012. All rights reserved.
 */

package org.funsoft.remoteagent.cmd.core;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.local.LocalCommandExecutor;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.installer.core.NormalExitInstallerRuntimeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ho Tri Bao
 *
 */
public class CmdUtils {

	public static CommandResult excuteJar(String jarFile, String paramerter) {
		try {
			return new LocalCommandExecutor()
					.execNoFailed(getCurrentJava() + " -jar "
							+ jarFile + " " + (paramerter != null ? paramerter : ""));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> envWithJavaHome() {
		Map<String, String> env = new HashMap<>();
		env.put("JAVA_HOME", getCurrentJavaHome());
		return env;
	}
	
	public static String getCurrentJava() {
		return getCurrentJavaHome() + File.separatorChar
			+ "bin" + File.separatorChar + "java";
	}

	public static String getCurrentJavaHome() {
		return System.getProperty("java.home");
	}
	public static String readUserInput(String message, String[] validResponsesIgnoreCase) {
		String opt = readUserInput(message);
		if (ArrayUtils.isEmpty(validResponsesIgnoreCase)) {
			return opt;
		}
		for (String v : validResponsesIgnoreCase) {
			if (StringUtils.endsWithIgnoreCase(opt, v)) {
				return opt;
			}
		}
		return quitOnError("Invalid option: " + opt + ". Valid options are: ["
				+ StringUtils.join(validResponsesIgnoreCase, ", ") + "]");
	}
	/**
	 * @return non-blank string. If blank (nothing or all white spaces, quit program with error)
	 */
	public static String readUserInput(String message) {
		return readUserInput(message, true);
	}
	/**
	 */
	public static String readUserInputAcceptNothing(String message) {
		return readUserInput(message, false);
	}
	private static String readUserInput(String message, boolean quitIfNothing) {
		System.out.print(message + ": ");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String response = null;

		try {
			response = br.readLine();
		} catch (IOException ioe) {
			quitOnError("IO error trying to read respose!");
		}
		if (StringUtils.isBlank(response) && quitIfNothing) {
			quitOnError("No respose provided");
		}
		return StringUtils.stripToEmpty(response);
	}

	public static <R> R quitOnError(String message) {
		System.out.println(message);
		System.out.println("Quit on error");
		throw new ExitInstallerRuntimeException();
	}
	
	public static void enforceFileExist(String fullPath) {
		if (!fileExist(fullPath)) {
			quitOnError("File " + fullPath + " does not exist");
		}
	}
	public static void enforceDirExist(String fullPath) {
		if (!dirExist(fullPath)) {
			quitOnError("Directory " + fullPath + " does not exist");
		}
	}

	public static String makeDirIfNotExist(String fullPath) {
		File f = new File(fullPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		enforceDirExist(fullPath);
		return fullPath;
	}
	public static boolean fileExist(String fullPath) {
		File f = new File(fullPath);
		return f.exists() && f.isFile();
	}

	public static boolean dirExist(String fullPath) {
		File f = new File(fullPath);
		return f.exists() && f.isDirectory();
	}
	
	public static void deleteDirectory(String dir) {
		new LocalCommandExecutor().execNoFailed("rm -R " + dir);
	}
	public static void unzip(String zipFile, String fileToExtract, String toDir) {
		new LocalCommandExecutor().execNoFailed("unzip -q " + zipFile
				+ (fileToExtract != null ? " " + fileToExtract : "")
				+ (toDir != null ? " -d " + toDir : ""));
	}
	public static void untar(String tarballFile, String toDir) {
		new LocalCommandExecutor().execNoFailed("tar -zxf " + tarballFile + " -C " + toDir);
	}
	
	public static void askToDeleteFolder(String dir, String msg) {
		if (!dirExist(dir)) {
			return;
		}
		System.out.println(msg);
		String res = CmdUtils.readUserInput("Do you want to delete it? [yes | no]",
				new String[] {"yes", "no"});
		if (StringUtils.equalsIgnoreCase(res, "yes")) {
			new LocalCommandExecutor().execNoFailed("rm -R " + dir);
		} else if (StringUtils.equalsIgnoreCase(res, "no")) {
			throw new NormalExitInstallerRuntimeException();

		}
	}
}
