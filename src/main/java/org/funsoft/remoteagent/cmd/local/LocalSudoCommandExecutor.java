/*
 * Copyright ROCO 2012. All rights reserved.
 */

package org.funsoft.remoteagent.cmd.local;

import org.apache.commons.io.IOUtils;
import org.funsoft.remoteagent.cmd.core.AbstractCommandExecutor;
import org.funsoft.remoteagent.cmd.core.CommandResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Ho Tri Bao
 *
 */
public class LocalSudoCommandExecutor extends AbstractCommandExecutor {
    private final String sudoPass;

	public LocalSudoCommandExecutor(String sudoPass) {
		this.sudoPass = sudoPass;
	}

	@Override
	public CommandResult exec(String command) {
		try {
			return exectInternal(command);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CommandResult exectInternal(String command) throws IOException,
			InterruptedException {
		if (command.startsWith("sudo ")) {
			throw new IllegalArgumentException("Command must not start with 'sudo': " + command);
		}
		traceCommand(command);
		
		String[] cmd = {"bash", "-c", "echo " + sudoPass + "| sudo -k -S " + command};
		
		final Process exec = Runtime.getRuntime().exec(cmd);
		BufferedReader er = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
		BufferedReader ir = new BufferedReader(new InputStreamReader(exec.getInputStream()));
		
		int exitCode = exec.waitFor();
		
		CommandResult r = new CommandResult(IOUtils.toString(er), IOUtils.toString(ir), exitCode);
		r.printResult();
		if (r.exitWithErrorCode()) {
			System.out.println("Failed to execute the above command with sudo, maybe incorrect password");
		}
		return r;
	}

}
