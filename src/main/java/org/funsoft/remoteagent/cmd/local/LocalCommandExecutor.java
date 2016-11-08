/*
 * Copyright ROCO 2012. All rights reserved.
 */

package org.funsoft.remoteagent.cmd.local;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.funsoft.remoteagent.cmd.core.AbstractCommandExecutor;
import org.funsoft.remoteagent.cmd.core.CmdUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Ho Tri Bao
 */
public class LocalCommandExecutor extends AbstractCommandExecutor {

    @Override
    public CommandResult exec(String command) {
        return execInternal(command, null, null, null);
    }

    private CommandResult execInternal(String command, String[] args,
                                       Map<String, String> environment, String workingDir) {
        try {
            final Process exec = createProcess(command, args, environment, workingDir);

            return getCommandResult(exec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandResult getCommandResult(Process exec) throws IOException, InterruptedException {
        final StringBuilder bdEs = new StringBuilder();
        final StringBuilder bdIs = new StringBuilder();

        Thread printOsThread = printStream(exec.getInputStream(), bdIs);
        Thread printEsThread = printStream(exec.getErrorStream(), bdEs);

        // Otherwise, If the external process expects something on its stdin, it will hang forever
        exec.getOutputStream().close();

        int exitCode = exec.waitFor();

        printOsThread.join(10000);
        printEsThread.join(10000);

        CommandResult r = new CommandResult(bdEs.toString(), bdIs.toString(), exitCode);
        r.printResult();
        return r;
    }

    private Process createProcess(String command, String[] args, Map<String, String> environment,
                                  String workingDir) throws Exception {
        List<String> cmdWithArgs = new ArrayList<>();
        cmdWithArgs.add(command);
        if (args != null) {
            cmdWithArgs.addAll(Arrays.asList(args));
        }

        traceCommand(cmdWithArgs);

        if (ArrayUtils.isEmpty(args) && (environment == null) && (workingDir == null)) {
            // simple command
            return Runtime.getRuntime().exec(command);
        }

        ProcessBuilder pb = new ProcessBuilder(cmdWithArgs);
        if (MapUtils.isNotEmpty(environment)) {
            Map<String, String> env = pb.environment();
            env.putAll(environment);
        }
        if (workingDir != null) {
            CmdUtils.enforceDirExist(workingDir);
            pb.directory(new File(workingDir));
        }
        return pb.start();
    }

    public CommandResult execNoFailed(String command, String[] args,
                                      Map<String, String> environment, String workingDir) {
        CommandResult commandResult = exec(command, args, environment, workingDir);
        assertResult(commandResult);
        return commandResult;
    }

    public CommandResult exec(String command, String[] args,
                              Map<String, String> environment, String workingDir) {
        return execInternal(command, args, environment, workingDir);
    }

    private static Thread printStream(final InputStream is, final StringBuilder msgCollector) {
        Runnable errorReader = new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        System.out.println(CommandResult.INDENTATION + line);
                        if (msgCollector != null) {
                            msgCollector.append(line).append("\n");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Thread t = new Thread(errorReader);
        t.start();
        return t;
    }

}
