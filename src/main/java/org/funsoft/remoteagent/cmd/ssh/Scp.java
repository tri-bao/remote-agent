package org.funsoft.remoteagent.cmd.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;

import java.io.*;

/**
 * @author Ho Tri Bao
 * 
 */
public class Scp {
    private final Session session;

    public Scp(Session session) {
        this.session = session;
    }

    public void copy(String source, String dest) throws Exception {
        FileInputStream fis=null;
        Channel channel = null;
        OutputStream out = null;
        try {
            boolean ptimestamp = true; // preserve timestamp of source file

            // exec 'scp -t rfile' remotely
            String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + dest;
            System.out.println("copying " + source + " to server at " + dest);
            
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if (checkAck(in) != 0) {
                throw new ExitInstallerRuntimeException();
            }

            File localFile = new File(source);
            if (!localFile.exists()) {
                System.out.println("File " + source + " khong ton tai");
                throw new ExitInstallerRuntimeException();
            }
            if (ptimestamp) {
            	// the example (http://www.jcraft.com/jsch/examples/ScpTo.java.html)
            	// has a whitespace after the T. But it will failed if the dest
            	// server is ubuntu 14.04:
            	//	scp: protocol error: mtime.sec not present
                command = "T" + (localFile.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command += (" " + (localFile.lastModified() / 1000) + " 0\n");
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    throw new ExitInstallerRuntimeException();
                }
            }

            // send "C0644 filesize filename", where filename should not include
            // '/'
            long filesize = localFile.length();
            command = "C0644 " + filesize + " ";
            if (source.lastIndexOf('/') > 0) {
                command += source.substring(source.lastIndexOf('/') + 1);
            } else {
                command += source;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                throw new ExitInstallerRuntimeException();
            }

            // send a content of lfile
            String totalSize = convertToMB(filesize);
            System.out.println("prepare sending " + totalSize);
            fis = new FileInputStream(source);
            byte[] buf = new byte[5 * 1024 * 1024];
            long sentBytes = 0;
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                
                System.out.print("----sending " + convertToMB(len) + "....");
                long startTime = System.currentTimeMillis();
                
                out.write(buf, 0, len); // out.flush();
                
                sentBytes += len;
                System.out.println(" in " + convertToMinutes(startTime) + " : "
                		+ convertToMB(sentBytes) + "/" + totalSize
                		+ " sent");
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                throw new ExitInstallerRuntimeException();
            }
            
            System.out.println("    Copy done");
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (fis != null) {
                fis.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private String convertToMB(long bytes) {
    	long kb = bytes / 1024;
    	long mb = kb / 1024;
    	if (mb > 0) {
    		return mb + "MB";
    	} else if (kb > 0) {
    		return kb + "KB";
    	}
    	return bytes + "B";
    }
    private String convertToMinutes(long startMills) {
    	long elapse = System.currentTimeMillis() - startMills;
    	long seconds = elapse / 1000;
    	long minutes = seconds / 60;
    	if (minutes > 0) {
    		return minutes + "m" + (seconds - (minutes * 60)) + "s";
    	}
    	return seconds + "s";
    }
    
    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        // 1 for error,
        // 2 for fatal error,
        // -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if ((b == 1) || (b == 2)) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}
