/**
 * 
 */
package org.funsoft.remoteagent.cmd.core;

/**
 * @author htb
 *
 */
public interface ICommandResult {
    boolean containErrorString(String str);
    boolean exitWithErrorCode();
    String getErrorStr();
    String getOkStr();
    int getExitStatus();
}
