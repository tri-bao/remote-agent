/**
 * 
 */
package org.funsoft.remoteagent.tag.view;

import org.funsoft.remoteagent.gui.component.AbstractDoubleListSelectionPanel;
import org.funsoft.remoteagent.gui.component.table.ScrollableSortablePanel;
import org.funsoft.remoteagent.gui.component.table.SortableTable;
import org.funsoft.remoteagent.tag.dto.TagDto;

/**
 * @author htb
 *
 */
public class TagSelectionPanel extends AbstractDoubleListSelectionPanel<
	ScrollableSortablePanel<TagDto, TagTableModel>, TagDto, ScrollableSortablePanel<TagDto, TagTableModel>> {

	@Override
	protected ScrollableSortablePanel<TagDto, TagTableModel> createAvailabelPanel() {
		return new ScrollableSortablePanel<>(
				new TagTableModel(false),
				"Tags",
				true,
				new SortableTable<TagDto, TagTableModel>(-1, false));
	}

	@Override
	protected ScrollableSortablePanel<TagDto, TagTableModel> createSelectedPanel() {
		return new ScrollableSortablePanel<>(
				new TagTableModel(false),
				"Tags",
				true,
				new SortableTable<TagDto, TagTableModel>(-1, false));
	}

	@Override
	protected ScrollableSortablePanel<TagDto, TagTableModel> getScrollableTable(
			ScrollableSortablePanel<TagDto, TagTableModel> tblPnl) {
		return tblPnl;
	}

}
