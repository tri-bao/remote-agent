package org.funsoft.remoteagent.gui.component;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.installer.core.ICompositeInstaller;
import org.funsoft.remoteagent.installer.core.IGuidedCompositeInstaller;
import org.funsoft.remoteagent.installer.core.IInstaller;
import org.funsoft.remoteagent.installer.core.NormalExitInstallerRuntimeException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ho Tri Bao
 *
 */
public class CurrentInstallerPanel extends JPanel {
    private final JEditorPane textEditor = new JEditorPane();
    private final Map<Integer, String> installerIds = new HashMap<>();
    
    public CurrentInstallerPanel() {
        textEditor.setEditable(false);
        textEditor.setContentType("text/html;charset=UTF-8");
        if (textEditor.getEditorKit() instanceof HTMLEditorKit) {
            Cursor handCur = new Cursor(Cursor.HAND_CURSOR);
            ((HTMLEditorKit) textEditor.getEditorKit()).setLinkCursor(handCur);
        }

        
        JScrollPane scr = new JScrollPane();
        scr.setViewportView(textEditor);
        setLayout(new BorderLayout());
        add(scr, BorderLayout.CENTER);
    }
    
    public void addHyperlinkListener(HyperlinkListener ls) {
        textEditor.addHyperlinkListener(ls);
    }
    
    public void displayInstaller(IInstaller installer) {
        textEditor.setText(null);
        installerIds.clear();
        int id = 1;
        StringBuilder bd = new StringBuilder();
        bd.append("<html><body style=\"font-family:Arial;font-size:12px\">");
        bd.append("<table>");
        if (installer instanceof ICompositeInstaller) {
            ICompositeInstaller cis = (ICompositeInstaller) installer;
            bd.append("<tr><td colspan=\"2\">")
            .append(StringUtils.stripToEmpty(installer.getDescription())).append("</td></tr>");
            for (IInstaller si : cis.getSubInstaller()) {
                buildSingleInstallerRow(si, id++, bd, true);
            }
        } else if (installer instanceof IGuidedCompositeInstaller) {
        	IGuidedCompositeInstaller cis = (IGuidedCompositeInstaller) installer;
            bd.append("<tr><td colspan=\"2\">")
            	.append(cis.buildGuide(installerIds))
            	.append("</td></tr>");
            
        } else {
            buildSingleInstallerRow(installer, id, bd, false);
        }
        bd.append("</table>");
        bd.append("</body></html>");
        textEditor.setText(bd.toString());
        textEditor.setCaretPosition(0);
    }
    
    private void buildSingleInstallerRow(IInstaller installer, int id, StringBuilder bd, boolean showNumber) {
        installerIds.put(id, installer.getName());
        bd.append("<tr><td><a href=#\"").append(id).append("\">").append(showNumber ? id + " - " : "")
            .append(installer.getName()).append("</a>")
            .append("</td>").append("<td>")
            .append(StringUtils.stripToEmpty(installer.getDescription())).append("</td></tr>");
    }
    
    public String getInstallerName(HyperlinkEvent event) {
        String url = event.getDescription();
        if (url.startsWith("http")) {
        	try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
        	throw new NormalExitInstallerRuntimeException();
        }
        url = event.getDescription().substring(1);
        return installerIds.get(Integer.parseInt(url));
    }
}
