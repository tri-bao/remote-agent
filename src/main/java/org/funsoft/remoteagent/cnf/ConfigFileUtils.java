/**
 * 
 */
package org.funsoft.remoteagent.cnf;

import org.funsoft.remoteagent.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author htb
 *
 */
public class ConfigFileUtils {
    public static void writeFile(String toFile, String... lines) throws Exception {
    	writeFile(Arrays.asList(lines), toFile);
    }
    public static void writeFile(List<String> lines, String toFile) throws Exception {
        Writer writer = new BufferedWriter(new FileWriter(new File(FileUtils.asPath(toFile)), false));
        try {
            String lineEnding = "\n"; // linux
            for (String line : lines) {
                if (line != null) {
                    writer.write(line.toString());
                }
                writer.write(lineEnding);
            }
        } finally {
            writer.close();
        }
    }

    public static List<String> readFileAsString(String path) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(path))));
        
        try {
            List<String> list = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            return list;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public static void searchReplaceOrAdd(String key, String value, List<String> lines) {
        if (!searchReplace(key, value, lines)) {
            lines.add(0, key + " = " + value);
        }
    }
    public static void searchReplaceStrict(String key, String value, List<String> lines, String cnfFile) {
        if (!searchReplace(key, value, lines)) {
            throw new RuntimeException("Không tìm thấy config " + key + " trong file " + cnfFile);
        }
    }
    public static boolean searchReplace(String key, String value, List<String> lines) {
    	return searchReplace(key, value, lines, false);
    }
    public static boolean searchReplace(String key, String value, List<String> lines, boolean all) {
    	boolean rs = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (cnfKeyOccursUnComment(key, line) || cnfKeyOccursCommented(key, line)) {
                lines.set(i, key + " = " + value);
                rs = true;
                if (!all) {
                	break;
                }
            }
        }
        return rs;
    }

    public static boolean cnfKeyOccursUnComment(String key, String line) {
        return cnfKeyOccurs(key, line, 0);
    }
    
    public static boolean cnfKeyOccursCommented(String key, String line) {
        String ln = line.trim();
        if (ln.length() == 0) {
            return false;
        }
        if (ln.charAt(0) == '#') {
            int idx = -1;
            for (int i = 1; i < ln.length(); i++) {
                if (ln.charAt(i) != ' ') {
                    idx = i;
                    break;
                }
            }
            if (idx > 0) {
                return cnfKeyOccurs(key, ln, idx);
            }
        }
        return false;
    }
    
    private static boolean cnfKeyOccurs(String key, String line, int startIdx) {
        String ln = line.trim();
        if (ln.length() == 0) {
            return false;
        }
        if (ln.startsWith(key, startIdx)) {
            // next character must be whitespace or =
            // #abc.def  =
            // i = 8
            for (int i = startIdx + key.length(); i < ln.length(); i++) {
                char c = ln.charAt(i);
                if (c == ' ') {
                    continue;
                }
                if (c == '=') {
                    return true;
                }
            }
        }
        return false;
    }

    
    public static void uncommentConfig(String key, List<String> lines, String cnfFile) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (cnfKeyOccursUnComment(key, line)) {
                return;
            }
            if (cnfKeyOccursCommented(key, line)) {
                for (int j = 0; j < line.length(); j++) {
                    if (line.charAt(j) == ' ') {
                        continue;
                    }
                    line = line.substring(j + 1);
                    lines.set(i, line);
                    return;
                }
            }
        }
        throw new RuntimeException("Không tìm thấy config " + key + " trong file " + cnfFile);
    }
    
    public static void commentOutConfig(String key, List<String> lines, String cnfFile) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (cnfKeyOccursCommented(key, line)) {
                return;
            }
            if (cnfKeyOccursUnComment(key, line)) {
                lines.set(i, "#" + line);
                return;
            }
        }
        throw new RuntimeException("Không tìm thấy config " + key + " trong file " + cnfFile);
    }
}
