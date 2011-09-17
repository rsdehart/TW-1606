package org.twdata.TW1606.tw;

import org.twdata.TW1606.tw.model.Sector;
import org.twdata.TW1606.tw.model.Ship;
import org.twdata.TW1606.tw.model.PlanetType;
import org.twdata.TW1606.tw.model.Player;
import org.twdata.TW1606.tw.model.Owner;
import org.twdata.TW1606.tw.data.PlayerDao;
import org.twdata.TW1606.tw.model.Planet;
import org.twdata.TW1606.tw.model.Corporation;
import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class LexerPlanetTest extends LexerBase {

    public LexerPlanetTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { LexerPlanetTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void testTwoLandings() throws Exception {
        Player cur = playerDao.get("bob", Player.TRADER, true);
        Ship ship = shipDao.create("ship");
        cur.setCurShip(ship);
        session.setTrader(((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true));
         
        runTextFile("twolandings.txt");
    }
    
    public void testPlanetDisplay() throws Exception {
        Player cur = playerDao.get("bob", Player.TRADER, true);
        Ship ship = shipDao.create("ship");
        cur.setCurShip(ship);
        session.setTrader(((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true));
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Planet #2 in sector 138: Ferrengal\r\n");
        sb.append("Class M, Earth Type\r\n");
        sb.append("Created by: jim\r\n");
        sb.append("Claimed by: jim and his friends [1]\r\n");
        sb.append("\r\n");
        sb.append("  Item    Colonists  Colonists    Daily     Planet      Ship      Planet\r\n");  
        sb.append("           (1000s)   2 Build 1   Product    Amount     Amount     Maximum\r\n"); 
        sb.append(" -------  ---------  ---------  ---------  ---------  ---------  ---------\r\n");
        sb.append("Fuel Ore        773          3        257      6,045          1    100,000\r\n");
        sb.append("Organics        774          7        110      3,518          2    100,000\r\n");
        sb.append("Equipment       772         13         59      2,037          3    100,000\r\n");
        sb.append("Fighters        N/A         55         42         42      5,090  1,000,000\r\n");
        sb.append("\r\n");
        sb.append("Planet has a level 3 Citadel, treasury contains 183,609 credits.\r\n");
        sb.append("Military reaction=40%, QCannon power=6%, AtmosLvl=30%, SectLvl=10%\r\n");
        sb.append("\r\n");
        sb.append("You have 14 free cargo holds.\r\n");
        sb.append("\r\n");
        sb.append("Planet command (?=help) [D] ?\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s = sectorDao.get(138);
        assertNotNull(s);
        Planet[] ps = s.getPlanets("Ferrengal");
        assertNotNull(ps);
        assertTrue("size not right:"+ps.length, 1==ps.length);
        Planet p = ps[0];
        assertTrue("cur sec not set", s == p.getCurSector());
        assertTrue("id not right:"+p.getTWId(), 2==p.getTWId());
        PlanetType pt = p.getPlanetType();
        assertNotNull(pt);
        assertTrue("type not right:"+pt.getType(), "M".equals(pt.getType()));
        assertTrue("name not right:"+pt.getName(), "Earth Type".equals(pt.getName()));
        Owner o = p.getCreator();
        assertNotNull(o);
        assertTrue("creator not trader:"+o.isCorporate(), !o.isCorporate());
        Player t = (Player)o;
        assertTrue("creator not right:"+t.getName(), "jim".equals(t.getName()));
        o = p.getOwner();
        assertNotNull(o);
        assertTrue("owner not corp:"+o.isCorporate(), o.isCorporate());
        Corporation c = (Corporation)o;
        assertTrue("owner not right:"+c.getName(), "jim and his friends".equals(c.getName()));
        assertTrue("copr id not right:"+c.getId(), 1==c.getId());
        
        assertTrue("fuel cols not right:"+p.getColonists(p.FUEL_ORE), 773==p.getColonists(p.FUEL_ORE));
        assertTrue("organics cols not right:"+p.getColonists(p.ORGANICS), 774==p.getColonists(p.ORGANICS));
        assertTrue("equip cols not right:"+p.getColonists(p.EQUIPMENT), 772==p.getColonists(p.EQUIPMENT));
        
        assertTrue("fuel not right:"+p.getProduct(p.FUEL_ORE), 6045==p.getProduct(p.FUEL_ORE));
        assertTrue("organics not right:"+p.getProduct(p.ORGANICS), 3518==p.getProduct(p.ORGANICS));
        assertTrue("equip not right:"+p.getProduct(p.EQUIPMENT), 2037==p.getProduct(p.EQUIPMENT));
        assertTrue("figs not right:"+p.getFighters(), 42==p.getFighters());
        
        assertTrue("fuel rate not right:"+pt.getColonistsToBuildOne(p.FUEL_ORE), 3==pt.getColonistsToBuildOne(p.FUEL_ORE));
        assertTrue("organics rate not right:"+pt.getColonistsToBuildOne(p.ORGANICS), 7==pt.getColonistsToBuildOne(p.ORGANICS));
        assertTrue("equip rate not right:"+pt.getColonistsToBuildOne(p.EQUIPMENT), 13==pt.getColonistsToBuildOne(p.EQUIPMENT));
        assertTrue("figs rate not right:"+pt.getColonistsToBuildOne(p.FIGHTERS), 55==pt.getColonistsToBuildOne(p.FIGHTERS));
        
        assertTrue("fuel max not right:"+pt.getMaxProduct(p.FUEL_ORE), 100000==pt.getMaxProduct(p.FUEL_ORE));
        assertTrue("organics max not right:"+pt.getMaxProduct(p.ORGANICS), 100000==pt.getMaxProduct(p.ORGANICS));
        assertTrue("equip max not right:"+pt.getMaxProduct(p.EQUIPMENT), 100000==pt.getMaxProduct(p.EQUIPMENT));
        assertTrue("figs max not right:"+pt.getMaxProduct(p.FIGHTERS), 1000000==pt.getMaxProduct(p.FIGHTERS));
        
        assertTrue("fuel not right:"+ship.getHoldContents(ship.FUEL_ORE), 1==ship.getHoldContents(ship.FUEL_ORE));
        assertTrue("organics not right:"+ship.getHoldContents(ship.ORGANICS), 2==ship.getHoldContents(ship.ORGANICS));
        assertTrue("equip not right:"+ship.getHoldContents(ship.EQUIPMENT), 3==ship.getHoldContents(ship.EQUIPMENT));
        assertTrue("figs not right:"+ship.getFighters(), 5090==ship.getFighters());
        
        assertTrue("cid not rigth:"+p.getCidadelLvl(), 3==p.getCidadelLvl());
        assertTrue("credits not right:"+p.getCredits(), 183609==p.getCredits());
        
        assertTrue("mil reaction not right:"+p.getMilReactionLvl(), 40==p.getMilReactionLvl());
        assertTrue("q cannon lvl not right:"+p.getQCannonLvl(), 6==p.getQCannonLvl());
        assertTrue("sec lvl not right:"+p.getSecReactionLvl(), 10==p.getSecReactionLvl());
        assertTrue("atmos lvl not right:"+p.getAtmosReactionLvl(), 30==p.getAtmosReactionLvl());
    }
    
    public void testPlanetDisplayNoCreator() throws Exception {
        Player cur = playerDao.get("bob", Player.TRADER, true);
        Ship ship = shipDao.create("ship");
        cur.setCurShip(ship);
        session.setTrader(((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true));
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Planet #2 in sector 138: Ferrengal\r\n");
        sb.append("Class M, Earth Type\r\n");
        sb.append("Created by: <UNKNOWN>\r\n");
        sb.append("Claimed by: jim and his friends [1]\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Planet p = planetDao.getByTWId(2);
        assertNotNull(p);
        assertNull(p.getCreator());
    }
    
    public void testPlanetDisplayTraderOwner() throws Exception {
        Player cur = playerDao.get("bob", Player.TRADER, true);
        Ship ship = shipDao.create("ship");
        cur.setCurShip(ship);
        session.setTrader(((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true));
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Planet #2 in sector 138: Ferrengal\r\n");
        sb.append("Class M, Earth Type\r\n");
        sb.append("Created by: jim\r\n");
        sb.append("Claimed by: bob\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Planet p = planetDao.getByTWId(2);
        assertNotNull(p);
        Owner o = p.getOwner();
        assertTrue("owner not trader:"+o.isCorporate(), !o.isCorporate());
        Player t = (Player)o;
        assertTrue("owner not right:"+t.getName(), "bob".equals(t.getName()));
    }
}    


