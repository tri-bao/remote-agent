/**
 * 
 */
package org.funsoft.remoteagent.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author htb
 * 
 */
public class DefaultJList<E> extends JList<E> {
	public DefaultJList() {
		// to avoid selection change when clicking on the blank area of the list
		addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList<E> list = (JList<E>) e.getSource();
                if ((list.locationToIndex(e.getPoint()) == -1) && !e.isShiftDown()
                        && !isMenuShortcutKeyDown(e)) {
                    list.clearSelection();
                }
            }
            private boolean isMenuShortcutKeyDown(InputEvent event) {
                return (event.getModifiers() & Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask()) != 0;
            }
        });
	}
	// to avoid selection change when clicking on the blank area of the list
	@Override
	public int locationToIndex(Point location) {
		int index = super.locationToIndex(location);
		if ((index != -1) && !getCellBounds(index, index).contains(location)) {
			return -1;
		} else {
			return index;
		}
	}

}
