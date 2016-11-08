/**
 * 
 */
package org.funsoft.remoteagent.tag.controller;

import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AbstractController;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.funsoft.remoteagent.tag.view.TagSelectionPanel;

import java.util.List;

/**
 * @author htb
 *
 */
public class TagSelectionController extends AbstractController<TagSelectionPanel> {
	public List<TagDto> select(List<TagDto> selectedTags) {
		setView(new TagSelectionPanel());
		getView().setAvailableRows(TagManager.getInstance().getAllTags(), selectedTags);
		
		GUIUtils.showInDialog(getView(), "SELECT TAGS", 900, 600);
		
		if (getView().isCancel()) {
			return null;
		}
		return getView().getSelectedRows();
	}

}
