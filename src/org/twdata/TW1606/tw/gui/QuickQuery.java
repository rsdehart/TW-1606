package org.twdata.TW1606.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606.script.flow.*;
import org.twdata.TW1606.script.flow.javascript.*;
import org.twdata.TW1606.signal.*;
import org.mozilla.javascript.*;
import org.twdata.TW1606.*;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
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
import org.apache.log4j.Logger;
import org.twdata.TW1606.gui.SessionFieldEnabler;
import javax.swing.text.*;

/**
 *@created    October 18, 2003
 */
public abstract class QuickQuery extends JTextField {
    
    protected static Logger log;

    /**  Constructor for the QuickQuery object */
    public QuickQuery() { 
        super();
        log = Logger.getLogger(getClass());
        if (log.isDebugEnabled()) {
            log.debug("Initializing quick query");
        }
        
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = QuickQuery.this.getText().trim();
                    if (text.length() > 0) {
                        QuickQuery.this.handleTyped(text);
                        //QuickQuery.this.setText("");
                    }
                }
            }
            
        });
    }
    
    public void setSessionFieldEnabler(SessionFieldEnabler enabler) {
        enabler.addField(this);
    }
    
    protected abstract void handleTyped(String query);
}

