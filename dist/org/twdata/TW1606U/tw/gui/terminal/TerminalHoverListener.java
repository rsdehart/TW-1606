package org.twdata.TW1606U.tw.gui.terminal;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.log4j.Logger;
import org.twdata.TW1606U.ActionManager;
import org.twdata.TW1606U.DefaultActionResolver;
import org.twdata.TW1606U.StreamFilter;
import org.twdata.TW1606U.gui.RadialMenu;
import org.twdata.TW1606U.gui.RadialMenuItem;
import org.twdata.TW1606U.gui.Terminal;

import de.mud.terminal.SwingTerminal;

public class TerminalHoverListener implements MouseMotionListener, MouseListener {

    private SwingTerminal terminal;
    private final String sectorWarpsStart = "Warps to Sector(s) :";
    protected static final Pattern warpPtn = Pattern.compile("\\(?([0-9]+)");
    private JFrame frame;
    private Link currentLink;
    
    private RadialMenu warpMenu;
    private WarpMenuListener warpMenuListener;
    private ActionManager manager = null;
    private RadialMenuItem defaultMenuItem;
    private boolean isDragging = false;
    private String stardockStart = "The StarDock is located in sector";
    
    public TerminalHoverListener() {
        this(null, null, null);
    }
    
    public TerminalHoverListener(SwingTerminal terminal, JFrame frame, ActionManager mgr) {
        setSwingTerminal(terminal);
        this.frame = frame;
        this.manager = mgr;
        try {
            warpMenu = new RadialMenu(200, 30);
            defaultMenuItem = new RadialMenuItem(manager.getAction("twxproxy_move"));
            warpMenu.addMenuItem(defaultMenuItem);
            warpMenu.addMenuItem(new RadialMenuItem(manager.getAction("twxproxy_trade")));
            warpMenu.addMenuItem(new RadialMenuItem(manager.getAction("twxproxy_ssm")));
            warpMenu.addMenuItem("Scan");
            warpMenu.setBackground(Color.WHITE);
            warpMenu.setForeground(Color.DARK_GRAY);
            warpMenu.setFont(new Font("Arial", Font.BOLD, 12));
            warpMenu.setHighlightColor(Color.YELLOW);
            warpMenu.setBackgroundTransparency(.1f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        warpMenuListener = new WarpMenuListener();
        warpMenu.addMenuListener(warpMenuListener);
    }
    
    public void setSwingTerminal(SwingTerminal terminal) {
        this.terminal = terminal;
        if (terminal != null) {
            terminal.addMouseMotionListener(this);
            terminal.addMouseListener(this);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (warpMenu.isVisible()) {
            int x = frame.getLayeredPane().getMousePosition().x - warpMenu.getLocation().x;
            int y = frame.getLayeredPane().getMousePosition().y - warpMenu.getLocation().y;
            MouseEvent ev = new MouseEvent(warpMenu, MouseEvent.MOUSE_MOVED, e.getWhen(), e.getModifiers(), x, y, e.getClickCount(), false);
            
            warpMenu.mouseMoved(ev);
            isDragging = true;
        }
    }

    public void mouseMoved(MouseEvent e) {
        
        
        terminal.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {
                Point pos = terminal.mouseGetPos(e.getPoint());
                //System.out.println("buffer pos:"+pos);
                StringBuilder sb = new StringBuilder();
                for (int x=0; x<terminal.getVDUBuffer().getColumns(); x++) {
                    sb.append(terminal.getVDUBuffer().getChar(x, pos.y));
                }
                String line = sb.toString();
                //System.out.println("finding links in line:"+line+":");
                List<Link> links = findLinks(line);
                //System.out.println("found :"+links.size());
                Cursor cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
                currentLink = null;
                for (Link link : links) {
                    if (pos.x >= link.getStart() && pos.x <= link.getEnd()) {
                        currentLink = link;
                        cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                        break;
                    }
                }
                
                if (terminal.getCursor() != cursor) {
                    terminal.setCursor(cursor);
                }
                //String line = getCurrentLine(e.getPoint());
                //Warps to Sector(s) :  2 - (3) - (4) - (5) - (6) - (7)
                //System.out.println("line:"+getCurrentLine(e.getPoint()));
            }
        });
    }
    
    List<Link> findLinks(String line) {
        
        List<Link> links = new ArrayList<Link>();
        if (line != null && line.length() > 0) {
            if (line.startsWith(sectorWarpsStart) || line.indexOf(stardockStart) > -1) {
                Matcher m = warpPtn.matcher(line);
                while (m.find()) {
                    Link link = new Link(m.group(1), m.start(), m.end());
                    links.add(link);
                }
            } 
        }
        return links;
    }

    public void mouseClicked(MouseEvent e) {
        
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        if (currentLink != null) {
            if (SwingUtilities.isRightMouseButton(e)) {
                frame.getLayeredPane().remove(warpMenu);
                frame.getLayeredPane().add(warpMenu,
                        new Integer(JLayeredPane.POPUP_LAYER));
                warpMenu.setBounds(frame.getLayeredPane().getMousePosition().x - 130, frame.getLayeredPane().getMousePosition().y - 130, 261, 261);
                warpMenu.setVisible(true);
                
                frame.validate();
            } else {
                warpMenuListener.menuSelected(new MenuEvent(defaultMenuItem));
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (warpMenu.isVisible() && isDragging) {
            int x = frame.getLayeredPane().getMousePosition().x - warpMenu.getLocation().x;
            int y = frame.getLayeredPane().getMousePosition().y - warpMenu.getLocation().y;
            MouseEvent ev = new MouseEvent(warpMenu, MouseEvent.MOUSE_CLICKED, e.getWhen(), e.getModifiers(), x, y, e.getClickCount(), false);
            
            warpMenu.mouseClicked(ev);
        }
        isDragging = false;
        
    }
    
    public void destroy() {
        frame.getLayeredPane().remove(warpMenu);
        terminal.removeMouseListener(this);
        terminal.removeMouseMotionListener(this);
    }
    
    class WarpMenuListener implements MenuListener {
        public void menuCanceled(MenuEvent e) {
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuSelected(MenuEvent e) {
            Object src = e.getSource();
            if (src instanceof RadialMenuItem) {
                ((LinkAction)((RadialMenuItem)src).getAction()).setLink(currentLink);
                ((RadialMenuItem)src).getAction().invoke();
            }
        }
    }
    
}
