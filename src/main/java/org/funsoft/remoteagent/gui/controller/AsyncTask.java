package org.funsoft.remoteagent.gui.controller;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Ho Tri Bao
 *
 */
public abstract class AsyncTask <Return> extends SwingWorker<Return, Void> {
    
    @Override
    protected final Return doInBackground() throws Exception {
        try {
            return doLengthlyTask();
        } catch (Exception e) {
            unhandledExceptionOccurs(e);
            throw e;
        }
    }
    
    protected abstract Return doLengthlyTask();
    
    @Override
    protected final void done() {
        try {
            // call get to make sure any exceptions
            // thrown during doInBackground() are
            // thrown again
            try {
            	doneInternal(get());
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (ExecutionException ex) {
                if (ex.getCause() instanceof Error) {
                    throw (Error) ex.getCause();
                }
                // otherwise, already handled
            }
        } finally {
            cleanUp();
        }
    }

    protected abstract void doneInternal(Return taskResult);
    
    /**
     * The default implementation will invoke the DefaultUncaughtExceptionHandler installed (if any)
     * 
     * @param ex any exception thrown by the method <code>doInBackgroundInternal</code>.
     */
    protected void unhandledExceptionOccurs(Exception ex) {
        if (Thread.getDefaultUncaughtExceptionHandler() != null) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(
                    Thread.currentThread(), ex);
        }
    }
    
    protected abstract void cleanUp();
}
