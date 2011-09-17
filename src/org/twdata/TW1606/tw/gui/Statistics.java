package org.twdata.TW1606.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.*;
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
import org.twdata.TW1606.tw.signal.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.*;
import org.twdata.TW1606.data.DaoManager;
import org.twdata.TW1606.gui.SessionFieldEnabler;

/**
 *@created    October 18, 2003
 */
public class Statistics extends JPanel {
    
    protected static final Logger log = Logger.getLogger(Statistics.class);

    private SwingEngine swix;
    
    private JComponent statsPanel;
    private TWSession session;
    
    public JLabel profitPerTurn;
    public JLabel profitPerSession;
    
    private long initialCredits = -1;
    private int lastTurns = -1;
    private int totalTurns = 0;
    private long totalCredits = 0;
    
    private NumberFormat nf = NumberFormat.getInstance();
    
    private ResourceManager res;
    
    private DaoManager dm;
    private List fields = new ArrayList();
    private MessageBus bus;
    private SessionFieldEnabler enabler;
    
    public Statistics() {
    }
    
    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setDaoManager(DaoManager dm) {
        this.dm = dm;
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void setSrc(String xml) {
        setLayout(new BorderLayout());
        try {
            long start = System.currentTimeMillis();
            swix = new SwingEngine(this);
            statsPanel = (JComponent)swix.render(res.getResourceAsReader(xml));
            add(BorderLayout.CENTER, statsPanel);
            if (log.isInfoEnabled()) {
                log.info("Loaded StatsPanel - "+(System.currentTimeMillis() - start)+"ms");
            }
            if (profitPerTurn != null) enabler.addField(profitPerTurn);
            if (profitPerSession != null) enabler.addField(profitPerSession);  
        } catch (Exception e) {
            log.error(e, e);
        }
        
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.STOP.equals(signal.getStatus())){
            initialCredits = -1;
            lastTurns = -1;
            totalCredits = 0;
            totalTurns = 0;
        }
    }
    
    public void setSessionFieldEnabler(SessionFieldEnabler enabler) {
        this.enabler = enabler;
    }
    
    public void channel(PlayerStatusSignal signal) {
        if (log.isDebugEnabled()) {
            log.debug("catching player change signal");
        }
        
        final Player trader = signal.getPlayer();
        Player curTrader = session.getTrader();
        if (curTrader != null && trader.getName().equals(curTrader.getName())) {
            log.debug("updating statistics");
            if (initialCredits == -1 || lastTurns == -1) {
                throw new IllegalStateException("Initial credits should be initialized");
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Player trader = session.getTrader();
                    int deltaTurns = lastTurns - trader.getTurns();
                    totalTurns += deltaTurns;
                    
                    // Probably a new game
                    if (initialCredits == 0) {
                        initialCredits = trader.getCredits();
                    }
                    long profit = trader.getCredits() - initialCredits;
                    if (profitPerTurn != null && trader != null && deltaTurns > 0) {
                        int ppt = (int)((double)profit / (double)totalTurns);
                        profitPerTurn.setText(nf.format(ppt));
                    } 
                    if (profitPerSession != null && trader != null) {
                        profitPerSession.setText(nf.format(profit));
                    } 
                    lastTurns = trader.getTurns();
                }
            });
        }
    }
     
     public void channel(ChangedSessionSignal signal) {
        
        if (signal.getType() == signal.TRADER) {
            initialCredits = session.getTrader().getCredits();
            lastTurns = session.getTrader().getTurns();
        }
    }
    
   
}
