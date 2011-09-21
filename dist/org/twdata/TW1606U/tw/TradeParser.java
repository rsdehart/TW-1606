package org.twdata.TW1606U.tw;

import java.util.*;
import java.util.regex.*;

import org.twdata.TW1606U.tw.model.*;

public class TradeParser extends AbstractParser {

    private Pattern prodPtn;
    private Pattern dockedPtn;
    private Pattern statusPtn;
    private Pattern holdFinePtn;
    
    private int prodId;
    private Port port;
    private Ship ship;
    
    
    public TradeParser() {
        super();
        prodPtn = Pattern.compile("([BS])[a-z]+\\s+(\\d+)\\s+(\\d+)%\\s+(\\d+)");
        dockedPtn = Pattern.compile("(.*) (docked|just left)");
        statusPtn = Pattern.compile("([0-9,]+) credits and ([0-9,]+) empty");
        holdFinePtn = Pattern.compile("fines you ([0-9,]+) Cargo");
    }
    
    public void reset() {
        prodId = -1;
        Sector c = session.getSector();
        ship = session.getTrader().getCurShip();
        port = c.getPort();
        port.setVisited(true);
    }
   
    public void parseCreditsAndHolds(String line) {
        if (log.isDebugEnabled()) {
            log.debug("parsing credits and holds");
        }    
        Matcher m = statusPtn.matcher(line); 
        if (m.find()) {
            Player trader = session.getTrader();
            Ship ship = trader.getCurShip();
            int credits = parseInt(m.group(1));
            //int holds = parseInt(m.group(2));
            trader.setCredits(credits);
            playerDao.update(trader);
        }
    }    
           
    public void parseReportLine(String line) {
        if (log.isDebugEnabled()) {
            log.debug("parsing report:"+line);
        }
        Matcher m = prodPtn.matcher(line); 
        if (m.find()) {
            boolean buying = "B".equals(m.group(1));
            int amount = parseInt(m.group(2));
            int percent = parseInt(m.group(3));
            int onBoard = parseInt(m.group(4));
            
            ++prodId;
            port.setCurProduct(prodId, amount); 
            port.setMaxProduct(prodId, (amount * percent) / 100); 
            ship.setHoldContents(prodId, onBoard);
            
            if (prodId == 2) {
                portDao.update(port);
                shipDao.update(ship);
            }
        }
    }
    
    public void parseLastDocked(String line) {
        if (log.isDebugEnabled()) {
            log.debug("parsing last docked:"+line);
        }
        Matcher m = dockedPtn.matcher(line); 
        if (m.find()) {
            Player p = playerDao.get(m.group(1), Player.TRADER, true);
            port.setLastVisitor(p);
        }
    }
    
    public void parseBusted(String line) {
        Matcher m = holdFinePtn.matcher(line); 
        if (m.find()) {
            Sector sec = session.getSector();
            Port port = sec.getPort();
            if (port != null) {
                port.setLastBust(new Date());
                portDao.update(port);
            } else {
                log.warn("Missing port for sector "+sec.getId());
            }
            Player trader = session.getTrader();
            Ship ship = trader.getCurShip();
            if (ship != null) {
                ship.setHolds(ship.getHolds() - Integer.parseInt(m.group(1)));
                ship.clearHolds();
                shipDao.update(ship);
            } else {
                log.warn("Missing player ship for bust processing");
            }
        }
        
    }
}
