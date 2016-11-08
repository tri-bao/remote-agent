/**
 * 
 */
package org.funsoft.remoteagent.gui.controller;

import org.funsoft.remoteagent.gui.component.GUIUtils;

import javax.swing.*;

/**
 * @author htb
 *
 */
public abstract class AbstractController<V extends JPanel> {
    private V view;

    public void mask() {
        if (view != null) {
            GUIUtils.maskContainer(view);
        }
    }
    public void unmask() {
        if (view != null) {
            GUIUtils.unmaskContainer(view);
        }
    }
    
    public V getView() {
        return view;
    }
    public void setView(V view) {
		this.view = view;
	}
	protected <R> void doAsync(final AsyncRemoteActionPerformer<R> asyncTask) {
        mask();
        AsyncTask<R> task = new AsyncTask<R>() {
			@Override
			protected R doLengthlyTask() {
				try {
					return asyncTask.perform();
				} catch (Exception e) {
					return asyncTask.onException(e);
				}
			}

			@Override
			protected void doneInternal(R taskResult) {
				asyncTask.updateGui(taskResult);
			}

			@Override
			protected void cleanUp() {
				unmask();
				asyncTask.doFinally();
			}
		};
        task.execute();
    }
    protected <R> void doAsyncNoMask(final AsyncRemoteActionPerformer<R> asyncTask) {
        AsyncTask<R> task = new AsyncTask<R>() {
			@Override
			protected R doLengthlyTask() {
				try {
					return asyncTask.perform();
				} catch (Exception e) {
					return asyncTask.onException(e);
				}
			}

			@Override
			protected void doneInternal(R taskResult) {
				asyncTask.updateGui(taskResult);
			}

			@Override
			protected void cleanUp() {
				asyncTask.doFinally();
			}
		};
        task.execute();
    }
    
    /**
     * @author Ho Tri Bao
     */
    protected static abstract class AsyncRemoteActionPerformer<R> {
        protected abstract R perform() throws Exception;
        
        protected R onException(Exception e) {
        	if (e instanceof RuntimeException) {
        		throw (RuntimeException) e;
        	}
        	throw new RuntimeException(e);
        }
        
        protected abstract void updateGui(R taskResult);
        
        protected void doFinally() {
        }
    }
}
