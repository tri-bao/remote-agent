/**
 * 
 */
package org.funsoft.remoteagent.tag.view;

import org.funsoft.remoteagent.gui.component.table.LineWrapTableCellRenderer;
import org.funsoft.remoteagent.gui.component.table.ScrollableSortablePanel;
import org.funsoft.remoteagent.gui.component.table.SortableTable;
import org.funsoft.remoteagent.gui.component.table.TextAreaCellEditor;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.painlessgridbag.LayoutUtils;
import org.painlessgridbag.PainlessGridBag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author htb
 *
 */
public class TagManagementPanel extends JPanel {
	private final static Color READONLY_COLOR = new JLabel().getBackground();

	private final TagTableModel modelTag = new TagTableModel(true) {
		@Override
		public int[] getSortableColumns() {
			return new int[0]; // disable sorting for management screen
		}
	};
	private final ScrollableSortablePanel<TagDto, TagTableModel> tblTag =
			new ScrollableSortablePanel<>(
					modelTag,
					"Tags",
					true,
					new SortableTable<>(-1, false));
	private final JButton btnAddTag = new JButton("Add tag");
	private final JButton btnDeleteTag = new JButton("Delete tag");
	
	private final JButton btnSave = new JButton("Save");
	
	public TagManagementPanel() {
		initState();
		initAction();
		initLayout();
	}

	private void initState() {
		btnDeleteTag.setEnabled(false);
		tblTag.getTable().setColumnSelectionAllowed(true);
		tblTag.getTable().setRowSelectionAllowed(true);
		tblTag.getTable().setRowHeight(32);
		
		tblTag.getTable().getColumnModel().getColumn(0).setCellEditor(new TextAreaCellEditor());
		tblTag.getTable().getColumnModel().getColumn(1).setCellEditor(new TextAreaCellEditor());

		LineWrapTableCellRenderer tagCellRenderer = new LineWrapTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				TagDto rowData = tblTag.getTable().getSortableModel().getRowData(row);
				comp.setBackground(Color.WHITE);

				return comp;
			}
		};
	
		tblTag.getTable().getColumnModel().getColumn(0).setCellRenderer(tagCellRenderer);
		tblTag.getTable().getColumnModel().getColumn(1).setCellRenderer(tagCellRenderer);
	}

	private void initAction() {
		tblTag.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				btnDeleteTag.setEnabled((tblTag.getSelectedRowData() != null));
			}
		});
		btnAddTag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblTag.addData(new TagDto());
				tblTag.getTable().changeSelection(modelTag.getRowCount() - 1, 0, false, false);
				tblTag.getTable().editCellAt(modelTag.getRowCount() - 1, 0);
				tblTag.getTable().transferFocus();
			}
		});
	}

	private void initLayout() {
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		gbl.row().cellXRemainder(tblTag).fillXY();
		gbl.row().cell(btnAddTag).cell(btnDeleteTag);
		
		gbl.constraints(btnAddTag).anchor = GridBagConstraints.WEST;
		gbl.constraints(btnDeleteTag).anchor = GridBagConstraints.EAST;
		
		LayoutUtils.addButtonPanel(gbl, btnSave);
		
		gbl.done();
	}
	
	public JButton getBtnDeleteTag() {
		return btnDeleteTag;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	public void removeSelectedTag() {
		tblTag.getTable();
		tblTag.removeSelectedRow();
	}
	public TagDto getSelectedTag() {
		return tblTag.getSelectedRowData();
	}
	public void showTags(List<TagDto> tags) {
		tblTag.showResult(tags);
	}
	public List<TagDto> getAllTags() {
		stopEditing();
		return modelTag.getAllRowData();
	}

	protected void stopEditing() {
		if (tblTag.getTable().isEditing()) {
			tblTag.getTable().getCellEditor().stopCellEditing();
		}
	}
}
