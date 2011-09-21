package org.twdata.TW1606U.tw.gui.chat;

import org.swixml.SwingEngine;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.*;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.*;
import java.awt.Color;
import java.util.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import javax.swing.event.*;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.util.*;
import org.twdata.TW1606U.data.DaoManager;
import org.apache.log4j.Logger;
import javax.swing.text.*;
import java.io.IOException;

/**
 *@created    October 18, 2003
 */
public class ChatTab extends JPanel implements Printer {
    
    private static final Logger log = Logger.getLogger(ChatTab.class);
    
    private JTextField input;
    private JTextPane text;
    private JList users;
    private JSplitPane spTextUsers;
    private Engine engine;
    private MessageBus bus;
    private StreamFilter filter;
    private JTabbedPane parent;
    private int tabIndex = -1;

    private static MutableAttributeSet currStyle = new SimpleAttributeSet();

    /**  Constructor for the ChatTab object */
    public ChatTab() { }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setStreamFilter(StreamFilter filter) {
        this.filter = filter;
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.START.equals(signal.getStatus())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (input != null) {
                        input.setEnabled(true);
                    }
                }
            });
        } else if (signal.STOP.equals(signal.getStatus())){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (input != null) {
                        input.setEnabled(false);
                    }
                    if (text != null) {
                        Document doc = text.getDocument();
                        try {
                            doc.remove(0, doc.getLength());
                        } catch (Exception ex) {
                            log.warn("Unable to clear chat text", ex);
                        }
                    }
                }
            });
        }
    }
    
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing chat tab "+getName());
        }
        input = (JTextField)SwingUtils.find(this, "input", JTextField.class);
        text = (JTextPane)SwingUtils.find(this, "text", JTextPane.class);
        users = (JList)SwingUtils.find(this, "users", JList.class);
        spTextUsers = (JSplitPane)SwingUtils.find(this, "div", JSplitPane.class);
        if (getParent() instanceof JTabbedPane) {
            parent = (JTabbedPane)getParent();
            tabIndex = parent.indexOfTab(getName());
            final Color c = parent.getForegroundAt(tabIndex);
            parent.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    if (parent.getSelectedIndex() == tabIndex) {
                        parent.setForegroundAt(tabIndex, c);
                        if (input != null) {
                            input.requestFocus();
                        }
                    }
                }
            });
        }
        
        
        if (spTextUsers != null) {
            spTextUsers.setDividerLocation(spTextUsers.getWidth() - 175);
        }
        if (input != null) {
            input.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    if (evt.getID() == KeyEvent.KEY_PRESSED) {
                        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                            String text = input.getText().trim();
                            if (text.length() > 0) {
                                try {
                                    engine.handleTyped(text);
                                } catch (IOException ex) {
                                    log.warn("Unable to send:"+text);
                                }
                                input.setText("");
                            }
                        }
                    }
                }
                
            });
        }
    }
     
    /**
     *  Appends a message to the output area.
     *
     *@param  message  param
     */
    public synchronized void print(String message) {
        final Document doc = text.getDocument();
        
        try {
            doc.insertString(doc.getLength(), message, currStyle);
        }
        catch (BadLocationException e) {
            log.warn("Unable to append to chat text:"+message, e);
        }

        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    text.setCaretPosition(doc.getLength());
                    if (parent != null) {
                        if (tabIndex != parent.getSelectedIndex()) {
                            parent.setForegroundAt(tabIndex, Color.RED);
                            parent.repaint();
                        }
                    }
                }
            });
    }
    
    public void setType(String type) {
        if ("simple".equals(type)) {
            engine = new TestEngine();
        } else if ("fedcom".equals(type)) {
            engine = new TWEngine(ChatSignal.FEDCOM, bus);
        } else if ("subspace".equals(type)) {
            engine = new TWEngine(ChatSignal.SUBSPACE, bus);
        } else if ("private".equals(type)) {
            engine = new TWEngine(ChatSignal.PRIV, bus);
        } else {
            throw new RuntimeException("Unknown type: "+type);
        }
        engine.setPrinter(this);
        engine.setStreamFilter(filter);
    }
}

