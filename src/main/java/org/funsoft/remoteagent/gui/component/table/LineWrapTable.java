package org.funsoft.remoteagent.gui.component.table;

import org.funsoft.remoteagent.gui.component.GUIUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;

/**
 * This class is inspired from the post of Alessandro Rossi on
 * http://stackoverflow.com/questions/965023/how-to-wrap-lines-in-a-jtable-cell
 * @author Ho Tri Bao
 */
public class LineWrapTable extends AbstractTable {
    private Font wrapTextColFont;
    private int maxRowHeight = -1;
    protected final int autoHeightColumn;
    private boolean layedOut = false;
    
    private final boolean disableLineWrap;
    
    private boolean layoutReseting = false;
    
    private int preferredRowHeight = -1;
    
    public LineWrapTable(boolean disableLineWrap) {
        this.disableLineWrap = disableLineWrap;
        this.autoHeightColumn = -1;
    }

    public LineWrapTable(int autoHeightColumn, boolean boldFont) {
        this.disableLineWrap = false;
        // if we set to 22 (the default value of HTBTable), on QD Nhap VA form, when adding vua-ans
        // we will get a table that is taller than its total row height.
        setRowHeight(20);
        
        this.autoHeightColumn = autoHeightColumn;
        if (boldFont) {
            GUIUtils.makeFontBold(this);
        }
        Font tblFont = getFont();
        wrapTextColFont = tblFont;
        
        // When the parent resize (e.g, resize splitpane, window size), this listener is to recalculate
        // the multi-line row
        addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
            private int lastParentWidth = 0;

            @Override
            public void ancestorResized(HierarchyEvent e) {
                if (lastParentWidth == getParent().getWidth()) {
                    return;
                }
                lastParentWidth = getParent().getWidth();
                // so that the auto-height column adjusted
                doLayout();
            }
        });
    }
    
    @Override
	public void setRowHeight(int h) {
    	super.setRowHeight(h);
    	this.preferredRowHeight = h;
    }
    
    /**
     * Call this method after filling data to the table.
     */
    public synchronized void resetLayout(final Class<?> notifiedParent) {
        if (disableLineWrap || (notifiedParent == null)) {
            return;
        }
        if (layoutReseting) {
        	return;
        }
        
		layoutReseting = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					doResetLayout(notifiedParent);
				} finally {
					layoutReseting = false;
				}
			}
		});
    }

    private void doResetLayout(final Class<?> notifiedParent) {
        while ((getMaxRowHeight() < 0) && !isAttachedToNotifParent(notifiedParent)) {
        	return;
        }

        invalidate();
        Container pnl = getParent();
        do {
            if (pnl == null) {
                break;
            }
            pnl.invalidate();
            pnl.setPreferredSize(null);
            pnl = pnl.getParent();
            if (notifiedParent.isInstance(pnl)) {
                pnl.setPreferredSize(null);
                pnl.validate();
                break;
            }
        } while (true);
    }

    private boolean isAttachedToNotifParent(final Class<?> notifiedParent) {
        Container pnl = getParent();
        do {
            pnl = pnl.getParent();
            if (notifiedParent.isInstance(pnl)) {
                return true;
            }
            if (pnl == null) {
                return false;
            }
        } while (true);
    }

    
    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if (disableLineWrap) {
            return;
        }
        
        int endCol = getModel().getColumnCount();
    	int startCol = 0;
    	if (autoHeightColumn >= 0) {
    		endCol = autoHeightColumn + 1;
    		startCol = autoHeightColumn;
    	}
    	for (int i = startCol; i < endCol; i++) {
    		if (getColumnModel().getColumnCount() > i) {
    			getColumnModel().getColumn(i).setCellRenderer(
    					new LineWrapTableCellRenderer());
    		}
    	}
    }

    @Override
    public void doLayout() {
        maxRowHeight = -1;
        super.doLayout();

        if (disableLineWrap) {
            return;
        }

        if (!layedOut) {
            layedOut = true;
            return;
        }
        int rowCount = getModel().getRowCount();
        int maxHeight = preferredRowHeight;
        for (int row = 0; row < rowCount; row++) {
        	int rowViewId = convertRowIndexToView(row);
        	if (rowViewId == -1) {
        		// this row has been filtered out
        		continue;
        	}
        	
			int expectedHeight = calculateRowHeight(row);
            if (maxHeight < expectedHeight) {
                maxHeight = expectedHeight;
            }
            if (getRowHeight(row) != expectedHeight) {
            	if (expectedHeight < preferredRowHeight) {
            		setRowHeight(rowViewId, preferredRowHeight);
            	} else {
            		setRowHeight(rowViewId, expectedHeight);
            	}
            }
        }

        maxRowHeight = maxHeight;
    }

    private int calculateRowHeight(int row) {
    	int endCol = getModel().getColumnCount();
    	int startCol = 0;
    	if (autoHeightColumn >= 0) {
    		endCol = autoHeightColumn + 1;
    		startCol = autoHeightColumn;
    	}
    	int maxHeight = -1;
    	for (int i = startCol; i < endCol; i++) {
    		int h = calculateHeight(
    				this,
    				getModel().getValueAt(row, i),
    				row,
    				i);
    		if (h > maxHeight) {
    			maxHeight = h;
    		}
		}
    	return maxHeight;
    }
    
    public int getMaxRowHeight() {
        return maxRowHeight;
    }
    
    protected int calculateHeight(
            JTable table, Object value, int rowIndex, int columnIndex) {
        return GUIUtils.calculateHeightForWrapText(
                table, value, rowIndex, columnIndex, wrapTextColFont);
    }
}