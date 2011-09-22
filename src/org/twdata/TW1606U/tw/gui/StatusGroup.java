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
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.data.DaoManager;
import org.twdata.TW1606U.gui.SessionFieldEnabler;

/**
 *@created    October 18, 2003
 */
public class StatusGroup extends JPanel {
    
    protected static final Logger log = Logger.getLogger(StatusGroup.class);

    private SwingEngine swix;
    
    private JComponent statusPanel;
    private TWSession session;
    public JTextField alignment;
    public JTextField turnsLeft;
    public JTextField experience;
    public JTextField currentSector;
    public JTextField credits;
    public JTextField figs;
    public JTextField shields;

    public JTextField holdsTotal;
    public JTextField holdsFuel;
    public JTextField holdsOrg;
    public JTextField holdsEquip;
    public JTextField holdsCols;
    public JTextField holdsEmpty;
    
    private ResourceManager res;
    
    public JLabel generalStatus;
    private Game game;
    private DaoManager dm;
    private List fields = new ArrayList();
    private MessageBus bus;
    private SessionFieldEnabler enabler;
    
    public StatusGroup() {
    }
    
    public void setSessionFieldEnabler(SessionFieldEnabler enabler) {
        this.enabler = enabler;
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
            statusPanel = (JComponent)swix.render(res.getResourceAsReader(xml));
            add(BorderLayout.CENTER, statusPanel);
            if (log.isInfoEnabled()) {
                log.info("Loaded StatusPanel - "+(System.currentTimeMillis() - start)+"ms");
            }
            
            if (alignment != null) enabler.addField(alignment);
            if (turnsLeft != null) enabler.addField(turnsLeft);
            if (experience != null) enabler.addField(experience);
            if (currentSector != null) enabler.addField(currentSector);
            if (credits != null) enabler.addField(credits);
            if (figs != null) enabler.addField(figs);
            if (shields != null) enabler.addField(shields);
            
            if (holdsTotal != null) enabler.addField(holdsTotal);
            if (holdsFuel != null) enabler.addField(holdsFuel);
            if (holdsOrg != null) enabler.addField(holdsOrg);
            if (holdsEquip != null) enabler.addField(holdsEquip);
            if (holdsCols != null) enabler.addField(holdsCols);
            if (holdsEmpty != null) enabler.addField(holdsEmpty);
        } catch (Exception e) {
            log.error(e, e);
        }
        
    }
    
    public void setGeneralStatus(final String text) {
        if (log.isDebugEnabled()) {
            log.debug("changing general status");
        }
        if (generalStatus != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    generalStatus.setText(text);
                }
            });
        }
    }
    
    public void channel(OnlineStatusSignal signal) {
        if (signal.ONLINE.equals(signal.getCommand())) {
            setGeneralStatus("Connected");
        } else {
            setGeneralStatus("Disconnected");
        }
    }
    
    public void channel(SessionStatusSignal signal) {
        game = null;
    }
    
    public void channel(PlayerStatusSignal signal) {
        if (log.isDebugEnabled()) {
            log.debug("catching player change signal");
        }
        if (game == null) {
            game = session.getGame();
        }
        
        final Player trader = signal.getPlayer();
        Player curTrader = session.getTrader();
        if (curTrader != null && trader.getName().equals(curTrader.getName())) {
            if (turnsLeft != null && trader != null) {
                turnsLeft.setText(String.valueOf(trader.getTurns()));
            } 
            if (experience != null && trader != null) {
                experience.setText(String.valueOf(trader.getExperience()));
            } 
            if (alignment != null && trader != null) {
                alignment.setText(String.valueOf(trader.getAlignment()));
            }
            if (credits != null && trader != null) {
                credits.setText(String.valueOf(trader.getCredits()));
            }

            if (figs != null && trader != null) {
                figs.setText(String.valueOf(trader.getCurShip().getFighters()));
            }

            if (shields != null && trader != null) {
                shields.setText(String.valueOf(trader.getCurShip().getShields()));
            }

        }
    }
    
    public void channel(ShipStatusSignal signal) {
        if (log.isDebugEnabled()) {
            log.debug("catching ship change signal");
        }
        if (game == null) {
            game = session.getGame();
        }
        
        final Ship ship = signal.getShip();
        Ship curShip = session.getTrader().getCurShip();
        if (curShip != null && ship.getId() == curShip.getId()) {
            if (holdsTotal != null) {
                holdsTotal.setText(String.valueOf(ship.getHolds()));
            }
            if (holdsFuel != null) {
                holdsFuel.setText(String.valueOf(ship.getHoldContents(Ship.FUEL_ORE)));
            }
            if (holdsOrg != null) {
                holdsOrg.setText(String.valueOf(ship.getHoldContents(Ship.ORGANICS)));
            }
            if (holdsEquip != null) {
                holdsEquip.setText(String.valueOf(ship.getHoldContents(Ship.EQUIPMENT)));
            }
            if (holdsCols != null) {
                holdsCols.setText(String.valueOf(ship.getHoldContents(Ship.COLONISTS)));
            }
            if (holdsEmpty != null) {
                holdsEmpty.setText(String.valueOf(ship.getEmptyHolds()));
            }
        }
     }
     
     public void channel(ChangedSessionSignal signal) {
        
        if (signal.getType() == signal.SECTOR) {
            Sector s = session.getSector();
            if (currentSector != null && s != null) {
                currentSector.setText(String.valueOf(s.getId()));
            } 
        }
    }
    
   
}
