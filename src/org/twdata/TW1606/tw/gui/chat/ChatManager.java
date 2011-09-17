package org.twdata.TW1606.tw.gui.chat;

import org.swixml.SwingEngine;
import java.awt.Component;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.*;
import java.awt.Insets;
import javax.swing.*;
import java.util.*;
import java.awt.Container;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import org.twdata.TW1606.tw.signal.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.*;
import org.twdata.TW1606.data.DaoManager;

/**
 *@created    October 18, 2003
 */
public class ChatManager extends JPanel {
    
    protected static final Logger log = Logger.getLogger(ChatManager.class);

    public JTextField fedInput;
    public JTextPane fedText;
    public JList fedUsers;
    public JSplitPane fedSplit;
    public Engine fedEngine;
    
    private JTabbedPane tabbedPane;
    private TWSession session;
    private ResourceManager res;
    private MessageBus bus;
    
    public ChatManager() {
        log.debug("chat manager created");
    }
    
    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void setSrc(String xml) {
        setLayout(new BorderLayout());
        try {
            long start = System.currentTimeMillis();
            SwingEngine swix = new SwingEngine(this);
            tabbedPane = (JTabbedPane)swix.render(res.getResourceAsReader(xml));
            add(BorderLayout.CENTER, tabbedPane);
            
            initTabs(this);
            if (log.isInfoEnabled()) {
                log.info("Loaded chat tabbed panel - "+(System.currentTimeMillis() - start)+"ms");
            }
            
        } catch (Throwable t) {
            log.error(t, t);
        }
    }
    
    private void initTabs(Container c) {
        Component[] kids = c.getComponents();
        for (int x=0; x<kids.length; x++) {
            if (kids[x] instanceof ChatTab) {
                ((ChatTab)kids[x]).init();
            } else if (kids[x] instanceof Container) {
                initTabs((Container)kids[x]);
            }
        }
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.START.equals(signal.getStatus())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tabbedPane.setEnabled(true);
                }
            });
        } else if (signal.STOP.equals(signal.getStatus())){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tabbedPane.setEnabled(false);
                }
            });
        }
    }
   /* 
    private class InputKeyListener extends KeyAdapter {
        
        private Engine engine;
        
        public InputKeyListener(Engine engine) {
            this.engine = engine;
        }
        
        protected void processKeyEvent(KeyEvent evt) {
            log.debug("key event:"+evt.getID());
            if (evt.getID() == KeyEvent.KEY_PRESSED) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    JTextField input = (JTextField)evt.getSource();
                    log.debug("found enter");
                    String text = input.getText().trim();
                    if (text.length() > 0) {
                        engine.handleTyped(text);
                        input.setText("");
                    }
                }
            }
        }
    }
    */
}
