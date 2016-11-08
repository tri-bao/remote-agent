/**
 * 
 */
package org.funsoft.remoteagent.gui.component;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.controller.AsyncTask;
import org.funsoft.remoteagent.main.RemoteAgentGui;
import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;
import org.painlessgridbag.engine.IGridCell;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;

/**
 * @author htb
 * 
 */
public class GUIUtils {
	public static boolean checkPasswordPair(JPasswordField p1, JPasswordField p2, String lbl) {
		return checkPasswordPair(p1, p2, null, lbl);
	}

	public static boolean checkPasswordPair(JPasswordField p1, JPasswordField p2, Integer minLength,
			String lbl) {
		boolean man = requireMandatory(p1, lbl) && requireMandatory(p2, lbl + " again");
		if (!man) {
			return false;
		}

		String ps1 = getText(p1);
		if (!StringUtils.equals(ps1, getText(p2))) {
			RemoteAgentGui.showErrorMsg("2 Password không khớp");
			p1.requestFocus();
			return false;
		}
		if (minLength != null) {
			if (ps1.length() < minLength.intValue()) {
				RemoteAgentGui.showErrorMsg("Password phải có ít nhất " + minLength + " ký tự");
				p1.requestFocus();
				return false;
			}
		}
		return true;
	}

	public static boolean requireMandatory(JPasswordField txtField, String fieldName) {
		if (ArrayUtils.isEmpty(txtField.getPassword())) {
			RemoteAgentGui.showErrorMsg("Chưa nhập " + fieldName);
			txtField.requestFocus();
			return false;
		}
		return true;
	}

	public static boolean requireMandatory(JTextComponent txtField, String fieldName) {
		if (StringUtils.isBlank(txtField.getText())) {
			RemoteAgentGui.showErrorMsg("Chưa nhập " + fieldName);
			txtField.requestFocus();
			return false;
		}
		return true;
	}

	public static void showInDialog(JComponent content, String title) {
		showInDialogInternal(content, title, -1, -1, null);
	}

	public static void showInDialog(JComponent content, String title, int width, int height) {
		showInDialogInternal(content, title, width, height, null);
	}

	public static void showInDialog(JComponent content, String title, int width, int height,
			WindowListener dialogListener) {
		showInDialogInternal(content, title, width, height, dialogListener);
	}

	private static void showInDialogInternal(JComponent content, String title, int width, int height,
			WindowListener dialogListener) {
		JDialog dlg = new JDialog();
		if ((width > 0) && (height > 0)) {
			dlg.setPreferredSize(new Dimension(width, height));
			dlg.setMinimumSize(dlg.getPreferredSize());
		} else {
			Rectangle max = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			dlg.setPreferredSize(new Dimension(max.width, max.height));
		}
		dlg.setModal(true);
		dlg.setResizable(true);
		dlg.setTitle(title);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dlg.setContentPane(content);

		if (dialogListener != null) {
			dlg.addWindowListener(dialogListener);
		}

		dlg.pack();

		alignDialogInMiddleOfScreen(dlg);

		dlg.setVisible(true);
	}

	private static void alignDialogInMiddleOfScreen(JDialog dlg) {
		Rectangle frameBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int x = frameBounds.x + ((frameBounds.width - dlg.getSize().width) / 2);
		int y = frameBounds.y + ((frameBounds.height - dlg.getSize().height) / 2);
		dlg.setLocation(x, y);
	}

	public static String getText(JTextComponent f) {
		return StringUtils.stripToNull(f.getText());
	}

	public static String getText(JPasswordField f) {
		return new String(f.getPassword());
	}

	public static JTextArea newTextArea() {
		final JTextArea txe = new JTextArea();
		txe.setLineWrap(true);
		txe.setWrapStyleWord(true);
		makeFontBold(txe);
		// void tab char
		KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
		KeyStroke shiftTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK);
		txe.getInputMap().put(tab, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				((Component) evt.getSource()).transferFocus();
			}
		});
		txe.getInputMap().put(shiftTab, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				((Component) evt.getSource()).transferFocusBackward();
			}
		});

		return txe;
	}

	public static JTextField newTextField() {
		JTextField f = new JTextField();
		f.setPreferredSize(new Dimension(150, 25)); // mainly to fix the height
													// (taller)
		f.setSize(f.getPreferredSize());
		f.setMinimumSize(f.getPreferredSize());
		makeFontBold(f);
		return f;
	}

	public static void makeFontBold(JComponent c) {
		Font font = c.getFont();
		c.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
	}

	public static JPasswordField newPwdField() {
		JPasswordField f = new JPasswordField();
		f.setPreferredSize(new Dimension(150, 25)); // mainly to fix the height
													// (taller)
		f.setSize(f.getPreferredSize());
		f.setMinimumSize(f.getPreferredSize());
		makeFontBold(f);
		return f;
	}

	public static <W extends Component & RootPaneContainer> void maskWindow(W window) {
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		window.setGlassPane(newMaskPane());
		window.getGlassPane().setVisible(true);
	}

	private static JPanel newMaskPane() {
		JLabel lblLoading = new JLabel(loadLoadingIcon());

		JPanel pnl = new JPanel();
		pnl.setOpaque(false);
		PainlessGridBag gbl = new PainlessGridBag(pnl, false);
		gbl.row().cell(lblLoading);
		gbl.done();

		pnl.addMouseListener(new MouseAdapter() {
		});
		pnl.addKeyListener(new KeyAdapter() {
		});
		return pnl;
	}

	public static ImageIcon loadLoadingIcon() {
		return new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("loading.gif"));
	}

	public static void maskContainer(JPanel pnl) {
		WindowConstants container = getOutermostContainer(pnl);
		if (container == null) {
			return;
		}
		if (container instanceof JDialog) {
			maskWindow((JDialog) container);
		} else if (container instanceof JFrame) {
			maskWindow((JFrame) container);
		} else if (container instanceof JInternalFrame) {
			maskWindow((JInternalFrame) container);
		}
	}

	private static WindowConstants getOutermostContainer(JPanel container) {
		Container pnl = container;
		while (pnl.getParent() != null) {
			if (pnl.getParent() instanceof JInternalFrame) {
				return (JInternalFrame) pnl.getParent();
			}
			if (pnl.getParent() instanceof JDialog) {
				return (JDialog) pnl.getParent();
			}
			if (pnl.getParent() instanceof JFrame) {
				return (JFrame) pnl.getParent();
			}
			pnl = pnl.getParent();
		}
		return null;
	}

	public static <W extends Component & RootPaneContainer> void unmaskWindow(W window) {
		if (!window.isDisplayable()) {
			return;
		}
		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if (window.getGlassPane() != null) {
			window.getGlassPane().setVisible(false);
		}
	}

	public static void unmaskContainer(JPanel pnl) {
		WindowConstants container = getOutermostContainer(pnl);
		if (container == null) {
			return;
		}
		if (container instanceof JDialog) {
			unmaskWindow((JDialog) container);
		} else if (container instanceof JFrame) {
			unmaskWindow((JFrame) container);
		} else if (container instanceof JInternalFrame) {
			unmaskWindow((JInternalFrame) container);
		}
	}

	public static boolean checkPort(JTextField txt, String label, boolean mandatory) {
		if (!checkInteger(txt, label, mandatory)) {
			return false;
		}
		String t = getText(txt);
		int p = Integer.parseInt(t);
		if ((p < 0) || (p > 65535)) {
			RemoteAgentGui.showErrorMsg(label + " không hợp lệ: nằm ngoài khoảng 0 - 65535");
			txt.requestFocus();
			return false;
		}
		return true;
	}

	public static boolean checkInteger(JTextField txt, String label, boolean mandatory) {
		String t = getText(txt);
		if (t == null) {
			if (mandatory) {
				RemoteAgentGui.showErrorMsg("Chưa nhập " + label);
				txt.requestFocus();
				return false;
			}
			return true;
		}
		try {
			Integer.parseInt(t);
		} catch (Exception e) {
			RemoteAgentGui.showErrorMsg(label + " không hợp lệ: không phải số");
			txt.requestFocus();
			return false;
		}
		return true;
	}

	public static Integer getInteger(JTextField txt) {
		String t = getText(txt);
		if (t == null) {
			return null;
		}
		return Integer.parseInt(t);
	}

	public static void addSeparator(String label, PainlessGridBag gbl) {
		JLabel lbl = new JLabel(label);
		lbl.setForeground(Color.BLUE);

		JPanel pnl = new JPanel();

		// compact separator
		PainlessGridbagConfiguration cnf = getZeroSurroundingGridBagConfig();
		cnf.setHorizontalSpacing(0);

		PainlessGridBag sepGbl = new PainlessGridBag(pnl, cnf, false);
		sepGbl.row().cell(lbl).cell(new JSeparator()).fillX();
		sepGbl.done();

		gbl.row().cellXRemainder(pnl).fillX();
	}

	public static PainlessGridbagConfiguration getZeroSurroundingGridBagConfig() {
		PainlessGridbagConfiguration cnf = new PainlessGridbagConfiguration();
		cnf.setFirstColumnLeftSpacing(0);
		cnf.setFirstRowTopSpacing(0);
		cnf.setLastColumnRightSpacing(0);
		cnf.setLastRowBottomSpacing(0);
		return cnf;
	}

	public static void fixWidth(JComponent c, int width) {
		Dimension ps = c.getPreferredSize();
		c.setPreferredSize(new Dimension(width, ps.height));
		c.setMinimumSize(c.getPreferredSize());
		c.setMaximumSize(c.getPreferredSize());
	}

	public static void fixHeight(JComponent c, int height) {
		Dimension ps = c.getPreferredSize();
		c.setPreferredSize(new Dimension(ps.width, height));
		c.setMinimumSize(c.getPreferredSize());
		c.setMaximumSize(c.getPreferredSize());
	}

	public static void preventInputingNewLineOnTextArea(JTextArea txe, AbstractAction actionOnEnter) {
		if (actionOnEnter == null) {
			actionOnEnter = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// do nothing
				}
			};
		}
		txe.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), actionOnEnter);
		txe.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK),
				actionOnEnter);
		txe.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK),
				actionOnEnter);
		txe.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK),
				actionOnEnter);
	}

	public static void noteRow(String txt, IGridCell afterCell, PainlessGridBag gbl) {
		JLabel l = new JLabel(txt);
		afterCell.cellXRemainder(l);
		gbl.getConfig().addLabelAnchor(l, GridBagConstraints.NORTHWEST);
		gbl.constraints(l).insets.top = 0;
	}

	public static int calculateHeightForWrapText(JTable table, Object value, int rowIndex, int columnIndex,
			Font displayFont) {
		String text = (value == null ? null : value.toString());
		if (StringUtils.isEmpty(text)) {
			return table.getRowHeight(rowIndex);
		}
		String[] lines = StringUtils.split(text, '\n');

		int targetWidth = table.getColumnModel().getColumn(columnIndex).getWidth();
		int totalheight = 0;
		for (String l : lines) {
			int h = calculateHeightForWrapText(l, targetWidth, (Graphics2D) table.getGraphics(), displayFont);
			totalheight += h;
		}
		return totalheight + 6; // 2x2(inset.top/bottom) +
								// 2x1(border-top/bottom)
	}

	public static int calculateHeightForWrapText(String text, int targetWidth, Graphics2D g, Font displayFont) {
		if (StringUtils.isEmpty(text)) {
			return Math.round(displayFont.getLineMetrics("", g.getFontRenderContext()).getHeight());
		}

		AttributedCharacterIterator paragraph = new AttributedString(text).getIterator();
		int paragraphStart = paragraph.getBeginIndex();
		int paragraphEnd = paragraph.getEndIndex();
		Font backupFont = g.getFont();
		g.setFont(displayFont);
		FontRenderContext frc = g.getFontRenderContext();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, BreakIterator.getWordInstance(),
				frc);
		float drawPosY = 0;
		// Set position to the index of the first character in the
		// paragraph.
		lineMeasurer.setPosition(paragraphStart);
		// Get lines until the entire paragraph has been displayed.
		while (lineMeasurer.getPosition() < paragraphEnd) {
			// Retrieve next layout. A cleverer program would also cache
			// these layouts until the component is re-sized.
			TextLayout layout = lineMeasurer.nextLayout(targetWidth);
			// float drawPosX = layout.isLeftToRight() ? 0 : breakWidth -
			// layout.getAdvance();
			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();
			// Draw the TextLayout at (drawPosX, drawPosY).
			// layout.draw(g, drawPosX, drawPosY);
			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}

		g.setFont(backupFont);
		return Math.round(drawPosY);
	}

	public static Dimension getMainFrameSizeExcludeInset() {
		Insets insets = RemoteAgentGui.getInstance().getInsets();

		return new Dimension(RemoteAgentGui.getInstance().getWidth() - insets.left - insets.right, RemoteAgentGui
				.getInstance().getHeight() - insets.top - insets.bottom);
	}

	public static TitledBorder makeTitledBorder(JComponent panel, String title, Color color) {
		TitledBorder result = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title,
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, color);
		panel.setBorder(result);
		return result;
	}

	public static void showWaitingCountdown(final int waitingTimeInSecond, String msg, int width) {
		final JLabel lblCount = new JLabel();
		lblCount.setIcon(GUIUtils.loadLoadingIcon());

		JPanel pnlContent = new JPanel();
		PainlessGridBag gbl = new PainlessGridBag(pnlContent, false);
		if (StringUtils.isNotBlank(msg)) {
			gbl.row().cellXRemainder(new JLabel(msg));
		}
		gbl.row().cell(lblCount);
		gbl.done();

		final JDialog dlg = new JDialog();
		dlg.setPreferredSize(new Dimension(width, 100));
		dlg.setModal(true);
		dlg.setResizable(true);
		dlg.setTitle("WAITING...");
		// disable closing
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setContentPane(pnlContent);
		dlg.pack();
		alignDialogInMiddleOfScreen(dlg);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AsyncTask<Void> countDownTask = new AsyncTask<Void>() {
					@Override
					protected Void doLengthlyTask() {
						int second = waitingTimeInSecond;
						while (true) {
							lblCount.setText("còn " + second + " giây");
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								break;
							}
							second--;
							if (second <= 0) {
								break;
							}
						}
						return null;
					}

					@Override
					protected void doneInternal(Void taskResult) {
						dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dlg.dispose();
					}

					@Override
					protected void cleanUp() {
						dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					}
				};
				countDownTask.execute();
			}
		});
		dlg.setVisible(true);
	}
}
