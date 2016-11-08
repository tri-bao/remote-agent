package org.funsoft.remoteagent.installer.os.util;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.cmd.core.CommandResult;
import org.funsoft.remoteagent.installer.core.AbstractInstaller;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;

import java.util.*;

/**
 * @author Ho Tri Bao
 *
 */
public class NetworkUtils {
	public static List<String> listAllNetworkInterfaces(AbstractInstaller currentInstaller) throws Exception {
		CommandResult commandResult = currentInstaller.sshSudoStrict("ifconfig -a -s");
		String okStr = commandResult.getOkStr();
		if (StringUtils.isEmpty(okStr)) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<>(3);
		String[] lines = StringUtils.split(okStr, '\n');
		
		// skip title line
		for (int i = 1; i < lines.length; i++) {
			StringBuilder nic = new StringBuilder();
			for (int j = 0; j < lines[i].length(); j++) {
				char c = lines[i].charAt(j);
				if (Character.isWhitespace(c)) {
					if (nic.length() > 0) {
						result.add(nic.toString());
					}
					break;
				} else {
					nic.append(c);
				}
			}
		}
		return Collections.unmodifiableList(result);
	}
	
	public static List<String> listFirewalRules(AbstractInstaller currentInstaller) {
		CommandResult commandResult;
		List<String> allNics;
		try {
			allNics = NetworkUtils.listAllNetworkInterfaces(currentInstaller);
			commandResult = currentInstaller.sshSudoStrict("ufw status");
		} catch (Exception e) {
			throw new ExitInstallerRuntimeException(e);
		}
		
		if (commandResult.getOkStr().contains("inactive")) {
			return null;
		}
		
		final Map<String, String> ruleToNicMap = new HashMap<>();
		List<String> result = new ArrayList<>();
		
		String[] lines = StringUtils.split(commandResult.getOkStr(), "\n");
		for (String line : lines) {
			if (StringUtils.isBlank(line)
					|| line.startsWith("Status")
					|| (line.contains("To") && line.contains("Action") && line.contains("From"))
					|| line.startsWith("--")
					) {
				continue;
			}
			
			ruleToNicMap.put(line, getFirstOccurNic(allNics, line));
			
			result.add(line);
		}
		
		// sort by to-NIC
		Collections.sort(result, new Comparator<String>() {
			@Override
			public int compare(String rule1, String rule2) {
				return ruleToNicMap.get(rule1).compareTo(ruleToNicMap.get(rule2));
			}
		});
		return result;
	}
	
	private static String getFirstOccurNic(List<String> nics, String line) {
		int idx = Integer.MAX_VALUE;
		String result = "";
		for (String nic : nics) {
			int i = line.indexOf(nic);
			if ((i >= 0) && (i < idx)) {
				idx = i;
				result = nic;
			}
		}
		return result;
	}
}
