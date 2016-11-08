package org.funsoft.remoteagent.installer.core;

/**
 * @author Ho Tri Bao
 *
 */
public class ExitInstallerRuntimeException extends RuntimeException {

    public ExitInstallerRuntimeException() {
    }

    public ExitInstallerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExitInstallerRuntimeException(String message) {
        super(message);
    }

    public ExitInstallerRuntimeException(Throwable cause) {
        super(cause);
    }

}
