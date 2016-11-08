/**
 * 
 */
package org.funsoft.remoteagent.gui.component;

import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import java.awt.*;

/**
 * @author htb
 *
 */
public class ReadOnlyTextPane extends JPanel {
	
	public static void showText(String title,
			String text, boolean scrollToBottomByDefault) {
		ReadOnlyTextPane p = new ReadOnlyTextPane(text, scrollToBottomByDefault);
		GUIUtils.showInDialog(p, title, 1000, 700);
	}
	
	private final JTextArea txe = new JTextArea();
	private JScrollPane scrollpane;
	private final boolean scrollToBottomByDefault;
	
	public ReadOnlyTextPane(String text, boolean scrollToBottomByDefault) {
		this.scrollToBottomByDefault = scrollToBottomByDefault;
		txe.setText(text);
		initState();
		initAction();
		initLayout();
	}

	private void initState() {
		txe.setEditable(false);
        txe.setFont(new Font("monospaced", Font.PLAIN, 15));
	}

	private void initAction() {
		final JScrollPane scr = new JScrollPane() {
			@Override
			public void addNotify() {
				super.addNotify();
				if (!scrollToBottomByDefault) {
					return;
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						// scroll to bottom
						getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
					}
				});
			}
		};
		scr.setViewportView(txe);
		scrollpane = scr;
	}

	private void initLayout() {
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		gbl.row().cell(scrollpane).fillXY();
		gbl.done();
	}
}
