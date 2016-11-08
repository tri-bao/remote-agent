/**
 * 
 */
package org.funsoft.remoteagent.tag.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;
import org.funsoft.remoteagent.gui.controller.AbstractController;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.funsoft.remoteagent.tag.view.TagManagementPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author htb
 *
 */
public class TagManagementController extends AbstractController<TagManagementPanel> {
	public void show() {
		setView(new TagManagementPanel());
		showTags();
		initAction();
		
		GUIUtils.showInDialog(getView(), "MANAGE TAGS", 600, 600);
	}

	private void showTags() {
		List<TagDto> allTags = TagManager.getInstance().getAllTags();
		List<TagDto> clonedTags = new ArrayList<>();
		for (TagDto tagDto : allTags) {
			clonedTags.add((TagDto) SerializationUtils.clone(tagDto));
		}
		getView().showTags(clonedTags);
	}
	
	private void initAction() {
		getView().getBtnDeleteTag().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		getView().getBtnSave().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
	}
	private void onDelete() {
		TagDto selectedTag = getView().getSelectedTag();
		if (selectedTag == null) {
			return;
		}
		if (StringUtils.isBlank(selectedTag.getName())) {
			getView().removeSelectedTag();
		}
		List<HostDto> usedInHosts = TagManager.getInstance().getHostsByTag(selectedTag);
		if (CollectionUtils.isNotEmpty(usedInHosts)) {
			RemoteAgentGui.showErrorMsg("Tag này đang được sử dụng. Tháo nó ra khỏi host trước khi delete");
			return;
		}
		if (RemoteAgentGui.showConfirmationYesNo("Muốn xóa tag \"" + selectedTag.getName() + "\" hả?")) {
			getView().removeSelectedTag();
		}
	}
	
	private void onSave() {
		List<TagDto> allTags = getView().getAllTags();
		
		// check duplication + exclude empty name
		List<TagDto> tagsToSave = new ArrayList<>();
		for (TagDto tagDto : allTags) {
			if (StringUtils.isBlank(tagDto.getName())) {
				continue;
			}
			if (tagsToSave.contains(tagDto)) {
				RemoteAgentGui.showErrorMsg("Tag \"" + tagDto.getName() + "\" bị trùng, xóa tag trùng trước đi");
				return;
			}
			tagsToSave.add(tagDto);
		}
		
		TagManager.getInstance().saveTags(tagsToSave);
		SwingUtilities.getWindowAncestor(getView()).dispose();
	}
}
