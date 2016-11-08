/**
 * 
 */
package org.funsoft.remoteagent.tag.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.IOUtils;
import org.funsoft.remoteagent.host.controller.HostMamanger;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.funsoft.remoteagent.util.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author htb
 *
 */
public class TagManager {
	private static final String TAG_FILE = Preferences.getInstance().getPreferenceHome() + ".tags.xml";

	private static TagManager instance;
	public static TagManager getInstance() {
		if (instance == null) {
			instance = new TagManager();
		}
		return instance;
	}
	
	private final List<TagDto> allTags;
	
	private TagManager() {
		allTags = loadTags();
		Collections.sort(allTags);
	}

	private List<TagDto> loadTags() {
		File f = new File(TAG_FILE);
		if (!f.exists()) {
			return new ArrayList<>();
		}
		
		XStream xstream = new XStream(new DomDriver());
		List<TagDto> tags = (List<TagDto>) xstream.fromXML(f);

		if (tags == null) {
			tags = new ArrayList<>();
		}

		return tags;
	}

	public List<TagDto> getAllTags() {
		return allTags;
	}

	public List<HostDto> getHostsByTag(TagDto tag) {
		List<HostDto> result = new ArrayList<>();
		
		List<HostDto> allHosts = HostMamanger.getInstance().getAllHosts();
		for (HostDto hostDto : allHosts) {
			if ((hostDto.getTags() != null) && hostDto.getTags().contains(tag)) {
				result.add(hostDto);
			}
		}
		return result;
	}
	
	public void saveTags(List<TagDto> tagsToSave) {
		for (TagDto newTag : tagsToSave) {

			boolean found = false;
			for (TagDto etag : allTags) {
				if (newTag.equals(etag)) {
					etag.setDescription(newTag.getDescription());
					found = true;
					break;
				}
			}
			if (!found) {
				allTags.add(newTag);
			}
		}
		
		List<TagDto> removedTags = new ArrayList<>();
		for (TagDto etag : allTags) {
			boolean found = false;
			for (TagDto t : tagsToSave) {
				if (etag.equals(t)) {
					found = true;
					break;
				}
			}
			if (!found) {
				removedTags.add(etag);
			}
		}
		allTags.removeAll(removedTags);
		
		Collections.sort(allTags);
		
		XStream xstream = new XStream(new DomDriver());
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(
					new FileOutputStream(new File(TAG_FILE), false), "UTF-8");
			xstream.toXML(allTags, osw);
		} catch (Exception e) {
			throw new ExitInstallerRuntimeException(e);
		} finally {
			IOUtils.closeQuietly(osw);
		}
	}
}
