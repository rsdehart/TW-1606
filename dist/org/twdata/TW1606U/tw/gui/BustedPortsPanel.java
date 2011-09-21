package org.twdata.TW1606U.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.*;
import java.awt.Insets;
import javax.swing.*;

import java.util.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.text.NumberFormat;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.data.DaoManager;
import org.twdata.TW1606U.gui.SessionFieldEnabler;
import org.twdata.TW1606U.gui.SortedTable;

/**
 *@created    October 18, 2003
 */
public class BustedPortsPanel extends JPanel {
    
    protected static final Logger log = Logger.getLogger(BustedPortsPanel.class);

    private SortedTable table;
    private MessageBus bus;
    
    public BustedPortsPanel() {
        setLayout(new BorderLayout());
        table = new SortedTable("Sector", "Available", "Distance");
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.doLayout();
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.STOP.equals(signal.getStatus())){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    table.setData(new Object[0][3], 0);
                }
            });
        } else if (signal.START.equals(signal.getStatus())){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    table.setData(new Object[0][3], 0);
                }
            });
        }
    }
    
    public void setSessionFieldEnabler(SessionFieldEnabler enabler) {
        enabler.addField(table);
    }
    
    /*
    public void channel(PlayerStatusSignal signal) {
        if (log.isDebugEnabled()) {
            log.debug("catching player change signal");
        }
        final Player trader = signal.getPlayer();
        
        Player curTrader = session.getTrader();
        if (curTrader != null && trader.getName().equals(curTrader.getName())) {) {
        
        if (signal.getType() == signal.SECTOR) {
            Sector sec = session.getSector();
            initialCredits = session.getTrader().getCredits();
            lastTurns = session.getTrader().getTurns();
        }
    }
    */
    
   
}
