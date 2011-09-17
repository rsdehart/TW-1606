package org.twdata.TW1606.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import javax.swing.text.JTextComponent;
import java.util.*;

/**
 *@created    October 18, 2003
 */
public class SessionFieldEnabler {
    
    protected static final Logger log = Logger.getLogger(SessionFieldEnabler.class);

    private List fields = new ArrayList();
    
    public void setMessageBus(MessageBus bus) {
        bus.plug(this);
    }
    
    public synchronized void addField(JComponent c) {
        fields.add(c);
    }
    
    public synchronized void removeField(JComponent c) {
        fields.remove(c);
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.STOP.equals(signal.getStatus())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    synchronized(fields) {
                        JComponent c;
                        for (Iterator i = fields.iterator(); i.hasNext(); ) {
                            c = (JComponent)i.next();
                            if (c instanceof JTextComponent) {
                                ((JTextComponent)c).setText("");
                            } else if (c instanceof JList) {
                                ((DefaultListModel)((JList)c).getModel()).clear();
                            } else if (c instanceof JComboBox) {
                                ((JComboBox)c).removeAllItems();
                            }
                            c.setEnabled(false);
                        }
                    }
                }
            });
        } else if (signal.START.equals(signal.getStatus())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    synchronized(fields) {
                        for (Iterator i = fields.iterator(); i.hasNext(); ) {
                            ((JComponent)i.next()).setEnabled(true);
                        }
                    }
                }
            });
        }
    }
}
