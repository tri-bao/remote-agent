package org.funsoft.remoteagent.gui.controller;

import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.main.RemoteAgentGui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ho Tri Bao
 *
 */
public final class AsyncTaskWithAutoMask {
	public static abstract class AsyncTaskCallback<R> {
		protected abstract R perform() throws Exception;
		
		protected abstract void onSuccess(R taskResult);
		
		protected void onException(Exception e) {
			e.printStackTrace();
			RemoteAgentGui.showErrorMsg("Có lỗi, xem trong console");
		}
	}
	
	public static <R, W extends Component & RootPaneContainer> void execute(final W windowToMask,
			final AsyncTaskCallback<R> callback) {
		GUIUtils.maskWindow(windowToMask);
        AsyncTask<R> task = new AsyncTask<R>() {
			@Override
			protected R doLengthlyTask() {
				try {
					return callback.perform();
				} catch (Exception e) {
					callback.onException(e);
					return null;
				}
			}
			@Override
			protected void doneInternal(R taskResult) {
				callback.onSuccess(taskResult);
			}
			@Override
			protected void cleanUp() {
				GUIUtils.unmaskWindow(windowToMask);
			}
		};
        task.execute();
	}
	
	public static <R> void execute(final JPanel pnlToMask, final AsyncTaskCallback<R> callback) {
		GUIUtils.maskContainer(pnlToMask);
        AsyncTask<R> task = new AsyncTask<R>() {
			@Override
			protected R doLengthlyTask() {
				try {
					return callback.perform();
				} catch (Exception e) {
					callback.onException(e);
					return null;
				}
			}
			@Override
			protected void doneInternal(R taskResult) {
				callback.onSuccess(taskResult);
			}
			@Override
			protected void cleanUp() {
				GUIUtils.unmaskContainer(pnlToMask);
			}
		};
        task.execute();
	}
}