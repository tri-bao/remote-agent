package org.funsoft.remoteagent.installer.os;

import org.apache.commons.lang.StringUtils;

import java.util.List;



/**
 * @author Ho Tri Bao
 *
 */
public class SshServerInstaller extends AbstractOsInstaller {
	private static SshServerInstaller instance;
	
	public static SshServerInstaller getInstance() {
		if (instance == null) {
			instance = new SshServerInstaller();
		}
		return instance;
	}

	protected SshServerInstaller() {
		super("OS - Cấu hình SSH server");
	}

	@Override
	public String getDescription() {
		return "Cấu hình SSH server để login với public/private key";
	}

	@Override
	protected void performInternal() throws Exception {
		if (!confirm("Lưu ý: chỉ làm bước này khi bạn đã có public/private key rồi bằng không,\n"
				+ "bạn sẽ không thể đăng nhập bằng ssh đâu.\n\n"
				+ "Nếu chưa có cặp key đó, hãy sử dụng chức năng \""
					+ CreatePasswordlessSshUserInstaller.getInstance().getName() + "\" để tạo.\n\n"
				+ "Muốn tiếp tục (YES)\n"
				+ "hay dừng lại (NO)?")) {
			return;
		}
		AbstractRemoteConfigFileModifier mod = new AbstractRemoteConfigFileModifier() {
			@Override
			protected void placeNewConfig(List<String> lines) throws Exception {
				// PermitRootLogin
				//		yes
				//		no: not allowed at all
				//		without-password: must use public/private key authentication
				setConfig("PermitRootLogin", "no", lines);
				
				// only allow public key authentication
				setConfig("PubkeyAuthentication", "yes", lines);
				setConfig("PasswordAuthentication", "no", lines);
			}
		};
		mod.modify("/etc/ssh/sshd_config");
		
		if (confirm("Resert ssh server nhé?")) {
			sshSudoStrict("service ssh restart");
		}
	}

	private static void setConfig(String key, String value, List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			if  (isNotCommented(key, lines.get(i))) {
				lines.set(i, key + " " + value);
				return;
			}
		}
		for (int i = 0; i < lines.size(); i++) {
			if  (isCommented(key, lines.get(i))) {
				// insert
				lines.add(i + 1, key + " " + value);
				return;
			}
		}
	}
	
	private static boolean isNotCommented(String key, String line) {
		String k = key + " ";
		String l = StringUtils.stripStart(line, " ") + " ";
		if (l.startsWith(k)) {
			return true;
		}
		return false;
	}
	
	private static boolean isCommented(String key, String line) {
		String k = key + " ";
		String l = StringUtils.stripStart(line, " ") + " ";
		if ((l.length() > 0) && (l.charAt(0) == '#')) {
			for (int i = 1; i < l.length(); i++) {
				if ((l.charAt(i) !=  '#') && (l.charAt(i) !=  ' ')) {
					if (l.startsWith(k, i)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
