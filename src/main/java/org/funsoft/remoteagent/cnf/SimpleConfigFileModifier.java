package org.funsoft.remoteagent.cnf;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class SimpleConfigFileModifier {
	private final LineNumberReader reader;
	private final Writer writer;
	
	public SimpleConfigFileModifier(String templateFile, String outputFile) {
		try {
			reader = new LineNumberReader(new InputStreamReader(
					new FileInputStream(templateFile), "UTF-8"));
			writer = new OutputStreamWriter(new FileOutputStream(outputFile),
					"UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void replace(PlaceHolder... placeHolders) {
		replace(new ArrayList<>(0), placeHolders);
	}
	public void replace(List<String> appendLines, PlaceHolder... placeHolders) {
		try {
			parse(appendLines, placeHolders);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
		}
	}

	private void parse(List<String> appendLines, PlaceHolder... placeHolders) {
		List<PlaceHolder> pls = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(placeHolders)) {
			for (PlaceHolder placeHoder : placeHolders) {
				if (placeHoder != null) {
					pls.add(placeHoder);
				}
			}
		}
		
		try {
			while (true) {
				String line = reader.readLine();
				
				if (line == null) {
					if (!pls.isEmpty()) {
						throw new RuntimeException("Cannot find config line started with: \""
								+ pls.get(0).getStartWith() + "\"");
					}
					break;
				}
				
				PlaceHolder found = null;
				for (PlaceHolder placeHoder : pls) {
					if (line.startsWith(placeHoder.getStartWith())) {
						writer.write(placeHoder.getCompleteLine());
						found = placeHoder;
						break;
					}
				}
				if (found != null) {
					pls.remove(found);
				} else {
					writer.write(line);
				}
				writer.write('\n');
			}
			
			if (CollectionUtils.isNotEmpty(appendLines)) {
				for (String line : appendLines) {
					writer.write(line);
					writer.write('\n');
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void searchStartWithAndReplace(List<String> lines, PlaceHolder... params) {
		for (PlaceHolder param : params) {
			searchStartWithAndReplace(param.getStartWith(), param.getCompleteLine(), lines);
		}
	}
	
	public static void searchStartWithAndReplace(String startWith, String completeLine, List<String> lines) {
		boolean found = false;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith(startWith)) {
				lines.set(i, completeLine);
				found = true;
				break;
			}
		}
		if (!found) {
			throw new ExitInstallerRuntimeException("Không tìm thấy config line bắt đầu bằng: " + startWith);
		}
	}
	
	/**
	 * @author htb
	 */
	public static class PlaceHolder {
		private final String startWith;
		private final String completeLine;
		public PlaceHolder(String startWith, String completeLine) {
			this.startWith = startWith;
			this.completeLine = completeLine;
		}
		public String getStartWith() {
			return startWith;
		}
		public String getCompleteLine() {
			return completeLine;
		}
		public static PlaceHolder keyValue(String key, String value) {
			return new PlaceHolder(key, key + "=" + value);
		}
	}
}
