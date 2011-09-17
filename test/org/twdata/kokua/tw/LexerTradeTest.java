package org.twdata.TW1606.tw;

import org.twdata.TW1606.tw.model.Sector;
import org.twdata.TW1606.tw.model.Ship;
import org.twdata.TW1606.tw.model.Port;
import org.twdata.TW1606.tw.model.Player;
import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class LexerTradeTest extends LexerBase {

    public LexerTradeTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { LexerTradeTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void testCreditsAndHolds() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Commerce report for Daimio Primus: 01:03:29 PM Sun Aug 07, 2016\r\n");
        sb.append("\r\n");
        sb.append("You have 769,914 credits and 20 empty cargo holds\r\n");
        
        Player trader = playerDao.get("test", Player.TRADER, true);
        Ship ship = shipDao.create("foo");
        Sector sector = sectorDao.create(10);
        Port port = portDao.create(10);
        sector.setPort(port);
        session.setSector(sector);
        trader.setCurShip(ship);
        session.setTrader(trader);
        playerDao.update(trader);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("Credits not right:"+trader.getCredits(), 769914==trader.getCredits());
    }
    
    public void testBust() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("He arrives shortly and fines you 6 Cargo Holds and all your cargo for your\r\n");
        sb.append("audacity. \"Don't let me find you around here again\", he spits.\r\n");
        
        Player trader = playerDao.get("test", Player.TRADER, true);
        Ship ship = shipDao.create("foo");
        ship.setHolds(100);
        Sector sector = sectorDao.create(10);
        Port port = portDao.create(10);
        sector.setPort(port);
        session.setSector(sector);
        trader.setCurShip(ship);
        session.setTrader(trader);
        playerDao.update(trader);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertNotNull(port.getLastBust());
        assertEquals(94, ship.getHolds());
    }
    
    public void testCommerceReport() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Docking...\r\n");
        sb.append("One turn deducted, 9994 turns left.\r\n");
        sb.append("\r\n");
        sb.append("Commerce report for Chadwick: 08:22:55 PM Sun Aug 07, 2016\r\n");
        sb.append("\r\n");
        sb.append("-=-=-        Docking Log        -=-=-\r\n");
        sb.append("asd docked 5 minutes ago.\r\n");
        sb.append("\r\n");
        sb.append("Items     Status  Trading % of max OnBoard\r\n");
        sb.append("-----     ------  ------- -------- -------\r\n");
        sb.append("Fuel Ore   Buying    1210    100%       0\r\n");
        sb.append("Organics   Buying    1670    100%       0\r\n");
        sb.append("Equipment  Selling    950    100%       0\r\n");
        
        Player trader = playerDao.get("test", Player.TRADER, true);
        Ship ship = shipDao.create("foo");
        Sector sector = sectorDao.create(10);
        Port port = portDao.create(10);
        port.setPortClass(1);
        sector.setPort(port);
        session.setSector(sector);
        trader.setCurShip(ship);
        session.setTrader(trader);
        playerDao.update(trader);
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Player last = port.getLastVisitor();
        assertNotNull(last);
        assertTrue("asd should have just left", "asd".equals(last.getName()));
        assertTrue("should be buying fuel", port.buysProduct(port.FUEL_ORE));
        assertTrue("should be buying org", port.buysProduct(port.ORGANICS));
        assertTrue("should be selling equip", !port.buysProduct(port.EQUIPMENT));
        
        assertTrue("buying 1210 fuel, was "+port.getCurProduct(port.FUEL_ORE),1210==port.getCurProduct(port.FUEL_ORE));
        assertTrue("buying 1670 org, was "+port.getCurProduct(port.ORGANICS), 1670==port.getCurProduct(port.ORGANICS));
        assertTrue("selling 950 equip, was "+port.getCurProduct(port.EQUIPMENT), 950==port.getCurProduct(port.EQUIPMENT));
        
        assertTrue("buying max 1210 fuel, was "+port.getMaxProduct(port.FUEL_ORE),1210==port.getMaxProduct(port.FUEL_ORE));
        assertTrue("buying 1670 max org, was "+port.getMaxProduct(port.ORGANICS), 1670==port.getMaxProduct(port.ORGANICS));
        assertTrue("selling 950 max equip, was "+port.getMaxProduct(port.EQUIPMENT), 950==port.getMaxProduct(port.EQUIPMENT));
    }
}    


