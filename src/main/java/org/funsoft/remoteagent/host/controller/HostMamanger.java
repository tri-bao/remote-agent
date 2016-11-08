/**
 * 
 */
package org.funsoft.remoteagent.host.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.host.dto.HostDto;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;
import org.funsoft.remoteagent.tag.dto.TagDto;
import org.funsoft.remoteagent.util.CryptoUtil;
import org.funsoft.remoteagent.util.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 *
 */
public class HostMamanger {
	private static final String ENCRYPT_KEY = "adda!@fdaf$#@DA";
	private static final String HOST_FILE = Preferences.getInstance().getPreferenceHome() + ".hosts.xml";

	private static HostMamanger instance;
	
	private final List<HostDto> knownHosts;
	
	private HostMamanger() {
		knownHosts = loadKnownHosts();
	}

	public static HostMamanger getInstance() {
		if (instance == null) {
			instance = new HostMamanger();
		}
		return instance;
	}
	
	private List<HostDto> loadKnownHosts() {
		File f = new File(HOST_FILE);
		if (!f.exists()) {
			return new ArrayList<>();
		}
		
		XStream xstream = new XStream(new DomDriver());
		List<HostDto> hosts = (List<HostDto>) xstream.fromXML(f);

		if (hosts == null) {
			hosts = new ArrayList<>();
		}

		decryptUsernamePassword(hosts);
		
		return hosts;
	}

	protected void decryptUsernamePassword(List<HostDto> hosts) {
		for (HostDto hostDto : hosts) {
			hostDto.setUsername(CryptoUtil.decrypt(hostDto.getUsername(), ENCRYPT_KEY));
			hostDto.setPassword(StringUtils.stripToNull(hostDto.getPassword()));
			if (hostDto.getPassword() != null) {
				hostDto.setPassword(CryptoUtil.decrypt(hostDto.getPassword(), ENCRYPT_KEY));
			}
		}
	}
	
	private void saveKnownHosts() {
		XStream xstream = new XStream(new DomDriver());
		
		// encrypt username, password before save
		for (HostDto hostDto : knownHosts) {
			hostDto.setUsername(CryptoUtil.encrypt(hostDto.getUsername(), ENCRYPT_KEY));
			hostDto.setPassword(StringUtils.stripToNull(hostDto.getPassword()));
			if (hostDto.getPassword() != null) {
				hostDto.setPassword(CryptoUtil.encrypt(hostDto.getPassword(), ENCRYPT_KEY));
			}
		}
		
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(
					new FileOutputStream(new File(HOST_FILE), false), "UTF-8");
			xstream.toXML(knownHosts, osw);
		} catch (Exception e) {
			throw new ExitInstallerRuntimeException(e);
		} finally {
			IOUtils.closeQuietly(osw);
		}
		
		decryptUsernamePassword(knownHosts);
	}

	public void addOrUpdate(HostDto hostInfo) {
		boolean found = false;
		for (int i = 0; i < knownHosts.size(); i++) {
			HostDto hostDto = knownHosts.get(i);
			if (StringUtils.equalsIgnoreCase(hostDto.getUuid(), hostInfo.getUuid())) {
				knownHosts.set(i, hostInfo);
				found = true;
				break;
			}
		}
		if (!found) {
			knownHosts.add(hostInfo);
		}
		
		saveKnownHosts();
	}
	
	public List<HostDto> getAllHosts() {
		return knownHosts;
	}

	public void removeHost(HostDto host) {
		for (int i = 0; i < knownHosts.size(); i++) {
			if (StringUtils.equalsIgnoreCase(host.getUuid(), knownHosts.get(i).getUuid())) {
				knownHosts.remove(i);
				saveKnownHosts();
				return;
			}
		}
	}
	
	public List<HostDto> getHostsByTags(TagDto... tags) {
		List<HostDto> result = new ArrayList<>();
		if (ArrayUtils.isEmpty(tags)) {
			return result;
		}
		
		List<TagDto> tagsToCheck = Arrays.asList(tags);
		for (HostDto h : knownHosts) {
			if ((h.getTags() != null) && CollectionUtils.containsAny(h.getTags(), tagsToCheck)) {
				result.add(h);
			}
		}
		return result;
	}
	public void addTagIfNotExisted(HostDto host, TagDto... tags) {
		if (host.getTags() == null) {
			host.setTags(new ArrayList<>());
		}
		for (TagDto tagDto : tags) {
			if (!host.getTags().contains(tagDto)) {
				host.getTags().add(tagDto);
			}
		}
	}
}
