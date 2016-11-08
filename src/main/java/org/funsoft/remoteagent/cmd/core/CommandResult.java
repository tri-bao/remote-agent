package org.funsoft.remoteagent.cmd.core;

import org.apache.commons.lang.StringUtils;


/**
 * @author Ho Tri Bao
 *
 */
public class CommandResult implements ICommandResult {
	public static final String INDENTATION = "    ";

    private final String errorStr;
    private final String okStr;
    private final int exitStatus;
    public CommandResult(String errorStr, String okStr, int exitStatus) {
        this.errorStr = errorStr;
        this.okStr = okStr;
        this.exitStatus = exitStatus;
    }
    @Override
	public boolean containErrorString(String str) {
        return (errorStr != null) && StringUtils.containsIgnoreCase(errorStr, str);
    }
    @Override
	public boolean exitWithErrorCode() {
        return exitStatus != 0;
    }
    @Override
	public String getErrorStr() {
        return errorStr;
    }
    @Override
	public String getOkStr() {
        return okStr;
    }
    @Override
	public int getExitStatus() {
        return exitStatus;
    }

    public void printResult() {
        System.out.println(INDENTATION + "exit-status: " + exitStatus);
    }
}
