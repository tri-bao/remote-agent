/*
 * Copyright ROCO 2012. All rights reserved.
 */

package org.funsoft.remoteagent.cmd.core;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;

import java.util.List;


/**
 * @author Ho Tri Bao
 *
 */
public abstract class AbstractCommandExecutor {
	
	public abstract CommandResult exec(String command);

	public CommandResult execNoFailed(String command) {
		CommandResult commandResult = exec(command);
		assertResult(commandResult);
		return commandResult;
	}

	protected void assertResult(CommandResult commandResult) {
		if (commandResult.exitWithErrorCode()) {
			throw new ExitInstallerRuntimeException("Quit on error. Error code " + commandResult.getExitStatus());
		}
	}
	
	protected void traceCommand(String command) {
		System.out.println("===========================");
        System.out.println(command);
	}
	protected void traceCommand(List<String> command) {
		System.out.println("===========================");
        System.out.println(StringUtils.join(command, " "));
	}
}
