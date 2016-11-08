package org.funsoft.remoteagent.util;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.OutputPanel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Ho Tri Bao
 */
public class InstallerPrintStream extends PrintStream {
    private final OutputPanel pnlOutput;
    private final PrintStream stdOut = System.out;
    private final PrintWriter printWriter;

    public InstallerPrintStream(OutputPanel pnlOutput, PrintWriter printWriter) {
        super(new NullOutputStream());
        this.pnlOutput = pnlOutput;
        this.printWriter = printWriter;
        System.setOut(this);
    }

    @Override
    public void flush() {
        pnlOutput.flush();
    }

    @Override
    public void print(String s) {
        stdOut.print(s);
        pnlOutput.displayMessage(s);
        printWriter.print(s);
        printWriter.flush();
    }

    @Override
    public void println(String x) {
        if (StringUtils.equals("MEvent. CASE!", x)) {
            // jscrollbar will print this whenever scrolling
            return;
        }
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void print(boolean b) {
        stdOut.print(b);
        pnlOutput.displayMessage(b);
        printWriter.print(b);
        printWriter.flush();
    }

    @Override
    public void print(char c) {
        stdOut.print(c);
        pnlOutput.displayMessage(c);
        printWriter.print(c);
        printWriter.flush();
    }

    @Override
    public void print(int i) {
        stdOut.print(i);
        pnlOutput.displayMessage(i);
        printWriter.print(i);
        printWriter.flush();
    }

    @Override
    public void print(long l) {
        stdOut.print(l);
        pnlOutput.displayMessage(l);
        printWriter.print(l);
        printWriter.flush();
    }

    @Override
    public void print(float f) {
        stdOut.print(f);
        pnlOutput.displayMessage(f);
        printWriter.print(f);
        printWriter.flush();
    }

    @Override
    public void print(double d) {
        stdOut.print(d);
        pnlOutput.displayMessage(d);
        printWriter.print(d);
        printWriter.flush();
    }

    @Override
    public void print(char[] s) {
        stdOut.print(s);
        pnlOutput.displayMessage(s);
        printWriter.print(s);
        printWriter.flush();
    }

    @Override
    public void print(Object obj) {
        stdOut.print(obj);
        pnlOutput.displayMessage(obj);
        printWriter.print(obj);
        printWriter.flush();
    }

    @Override
    public void println() {
        stdOut.println();
        pnlOutput.displayMessage('\n');
        printWriter.println();
        printWriter.flush();
    }

    @Override
    public void println(boolean x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(char x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(int x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(long x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(float x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(double x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(char[] x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    @Override
    public void println(Object x) {
        stdOut.println(x);
        pnlOutput.displayMessage(x);
        printWriter.println(x);
        printWriter.flush();
    }

    public static class NullOutputStream extends OutputStream {
        @Override
        public void write(byte[] b, int off, int len) {
        }

        @Override
        public void write(int b) {
        }

        @Override
        public void write(byte[] b) throws IOException {
        }
    }

}
