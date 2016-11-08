package org.funsoft.remoteagent.cmd.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.funsoft.remoteagent.installer.core.ExitInstallerRuntimeException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Ho Tri Bao
 *
 */
public class ScpFromRemoteToLocal {
    private final Session session;

    public ScpFromRemoteToLocal(Session session) {
        this.session = session;
    }

	public void copy(String sourceRemote, String destLocal) throws Exception {
		FileOutputStream fos = null;
		Channel channel = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + sourceRemote;
            System.out.println("copying " + sourceRemote + " from server to " + destLocal);

            channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			out = channel.getOutputStream();
			in = channel.getInputStream();

			channel.connect();
			
			byte[] buf = new byte[4 * 1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					filesize = (filesize * 10L) + (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}
				
				System.out.println("filesize=" + filesize + ", file=" + file);
				
				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(destLocal, false);
				int foo;
				while (true) {
					if (buf.length < filesize) {
						foo = buf.length;
					} else {
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L) {
						break;
					}
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					return;
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}

			System.out.println("    Copy done");
		} finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (fos != null) {
            	fos.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
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
                throw new ExitInstallerRuntimeException(sb.toString());
            }
            if (b == 2) { // fatal error
                throw new ExitInstallerRuntimeException(sb.toString());
            }
        }
        return b;
    }

}
