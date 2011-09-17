package org.twdata.TW1606.tw;

import org.twdata.TW1606.tw.model.Sector;
import org.twdata.TW1606.tw.model.Ship;
import org.twdata.TW1606.tw.model.ShipType;
import org.twdata.TW1606.tw.model.Port;
import org.twdata.TW1606.tw.model.Player;
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
import org.twdata.TW1606.LexerRunner;
import org.apache.log4j.*;

public class LexerStatusTest extends LexerBase {

    public LexerStatusTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { LexerStatusTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void testOverflow() throws Exception {
        Player p = ((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true);
        Ship s = shipDao.create("foo");
        p.setCurShip(s);
        shipTypeDao.create("Merchant Cruiser");
        shipTypeDao.create("Merchant Freighter");
        session.setTrader(p);
 
        System.out.println("running overflow.txt");
        runTextFile("overflow.txt");
        System.out.println("running overflow2.txt");
        runTextFile("overflow2.txt");
        System.out.println("running overflow3.txt");
        runTextFile("overflow3.txt");

        tryReaderOverflow("overflow.txt");

        System.out.println("running overflow.txt");
        tryOverflow("overflow.txt");
        System.out.println("running overflow2.txt");
        tryOverflow("overflow2.txt");
        System.out.println("running overflow3.txt");
        tryOverflow("overflow3.txt");
    }
    
    public void testInfo() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Trader Name    : Civilian jim bob\r\n");
        sb.append("Rank and Exp   : 2 points, Alignment=33 Tolerant\r\n");
        sb.append("Corp           # 1, jim\r\n");
        sb.append("Ship Name      : jso fso2\r\n");
        sb.append("Ship Info      : IonStream Merchant Cruiser Ported=0 Kills=0\r\n");
        sb.append("Date Built     : 02:31:53 PM Mon Feb 08, 2016\r\n");
        sb.append("Turns to Warp  : 3\r\n");
        sb.append("Current Sector : 82\r\n");
        sb.append("Turns left     : 65520\r\n");
        sb.append("Total Holds    : 75 - Colonists=2 Empty=73\r\n");
        sb.append("Fighters       : 2,500\r\n");
        sb.append("Credits        : 949,999\r\n");
        
        shipTypeDao.create("Merchant Cruiser");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        //PlayerDao pdao = (PlayerDao)factory.getBean("playerDao");
        Player p = session.getTrader();
        assertNotNull(p);
        assertTrue("name not correct:"+p.getName(), "jim bob".equals(p.getName()));
        assertTrue("exp not right:"+p.getExperience(), 2==p.getExperience());
        assertTrue("algn not right:"+p.getAlignment(), 33==p.getAlignment());
        assertTrue("credits not right:"+p.getCredits(), 949999==p.getCredits());
        assertTrue("turns not right:"+p.getTurns(), 65520==p.getTurns());
        
        Corporation c = p.getCorporation();
        assertNotNull(c);
        assertTrue("id not right:"+c.getId(), 1==c.getId());
        assertTrue("name not right:"+c.getName(), "jim".equals(c.getName()));
        
        Ship s = p.getCurShip();
        assertNotNull(s);
        assertTrue("name not correct:"+s.getName(), "jso fso2".equals(s.getName()));
        assertTrue("holds not correct:"+s.getHolds(), 75==s.getHolds());
        assertTrue("fuel not correct:"+s.getHoldContents(s.FUEL_ORE), 0==s.getHoldContents(s.FUEL_ORE));
        assertTrue("org not correct:"+s.getHoldContents(s.ORGANICS), 0==s.getHoldContents(s.ORGANICS));
        assertTrue("equipment not correct:"+s.getHoldContents(s.EQUIPMENT), 0==s.getHoldContents(s.EQUIPMENT));
        assertTrue("cols not correct:"+s.getHoldContents(s.COLONISTS), 2==s.getHoldContents(s.COLONISTS));
        assertTrue("figs not correct:"+s.getFighters(), 2500==s.getFighters());
        
        ShipType st = s.getShipType();
        assertNotNull(st);
        assertTrue("Wrong turns per warp, "+st.getTurnsPerWarp(), 3==st.getTurnsPerWarp());
        
        
    }
    
    public void testInfoWithHoldsFull() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Trader Name    : Civilian jim bob\r\n");
        sb.append("Rank and Exp   : 2 points, Alignment=33 Tolerant\r\n");
        sb.append("Corp           # 1, jim\r\n");
        sb.append("Ship Name      : jso fso2\r\n");
        sb.append("Ship Info      : IonStream Merchant Cruiser Ported=0 Kills=0\r\n");
        sb.append("Date Built     : 02:31:53 PM Mon Feb 08, 2016\r\n");
        sb.append("Turns to Warp  : 3\r\n");
        sb.append("Current Sector : 82\r\n");
        sb.append("Turns left     : 65520\r\n");
        sb.append("Total Holds    : 75 - Fuel Ore=69 Organics=2 Equipment=1 Colonists=3\r\n");
        sb.append("Fighters       : 2,500\r\n");
        sb.append("Credits        : 949,999\r\n");
        
        shipTypeDao.create("Merchant Cruiser");
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        //PlayerDao pdao = (PlayerDao)factory.getBean("playerDao");
        Player p = session.getTrader();
        assertNotNull(p);
        assertTrue("name not correct:"+p.getName(), "jim bob".equals(p.getName()));
        assertTrue("exp not right:"+p.getExperience(), 2==p.getExperience());
        assertTrue("algn not right:"+p.getAlignment(), 33==p.getAlignment());
        assertTrue("credits not right:"+p.getCredits(), 949999==p.getCredits());
        assertTrue("turns not right:"+p.getTurns(), 65520==p.getTurns());
        
        Corporation c = p.getCorporation();
        assertNotNull(c);
        assertTrue("id not right:"+c.getId(), 1==c.getId());
        assertTrue("name not right:"+c.getName(), "jim".equals(c.getName()));
        
        Ship s = p.getCurShip();
        assertNotNull(s);
        assertTrue("name not correct:"+s.getName(), "jso fso2".equals(s.getName()));
        assertTrue("holds not correct:"+s.getHolds(), 75==s.getHolds());
        assertTrue("fuel not correct:"+s.getHoldContents(s.FUEL_ORE), 69==s.getHoldContents(s.FUEL_ORE));
        assertTrue("org not correct:"+s.getHoldContents(s.ORGANICS), 2==s.getHoldContents(s.ORGANICS));
        assertTrue("equipment not correct:"+s.getHoldContents(s.EQUIPMENT), 1==s.getHoldContents(s.EQUIPMENT));
        assertTrue("cols not correct:"+s.getHoldContents(s.COLONISTS), 3==s.getHoldContents(s.COLONISTS));
        assertTrue("figs not correct:"+s.getFighters(), 2500==s.getFighters());
    } 
    
    public void testInfoWithEvil() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Trader Name    : Menace 2nd Class jim\r\n");
        sb.append("Rank and Exp   : 45 points, Alignment=-192 Crass\r\n");
        sb.append("Ship Name      : jso fso2\r\n");
        sb.append("Ship Info      : IonStream Merchant Cruiser Ported=0 Kills=0\r\n");
        sb.append("Date Built     : 02:31:53 PM Mon Feb 08, 2016\r\n");
        sb.append("Turns to Warp  : 3\r\n");
        sb.append("Current Sector : 82\r\n");
        sb.append("Turns left     : 65520\r\n");
        sb.append("Total Holds    : 75 - Fuel Ore=69 Organics=2 Equipment=1 Colonists=3\r\n");
        sb.append("Fighters       : 2,500\r\n");
        sb.append("Credits        : 949,999\r\n");
        
        shipTypeDao.create("Merchant Cruiser");
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        //PlayerDao pdao = (PlayerDao)factory.getBean("playerDao");
        Player p = session.getTrader();
        assertNotNull(p);
        assertTrue("name not correct:"+p.getName(), "jim".equals(p.getName()));
        assertTrue("exp not right:"+p.getExperience(), 45==p.getExperience());
        assertTrue("algn not right:"+p.getAlignment(), -192==p.getAlignment());
        assertTrue("credits not right:"+p.getCredits(), 949999==p.getCredits());
        assertTrue("turns not right:"+p.getTurns(), 65520==p.getTurns());
    }    
    
    public void testInfoNoCorp() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Trader Name    : Sergeant Major jim bob\r\n");
        sb.append("Rank and Exp   : 2 points, Alignment=33 Tolerant\r\n");
        sb.append("Ship Name      : jso fso2\r\n");
        sb.append("Ship Info      : IonStream Merchant Cruiser Ported=0 Kills=0\r\n");
        sb.append("Date Built     : 02:31:53 PM Mon Feb 08, 2016\r\n");
        sb.append("Turns to Warp  : 3\r\n");
        sb.append("Current Sector : 82\r\n");
        sb.append("Turns left     : 65520\r\n");
        sb.append("Total Holds    : 75 - Empty=75\r\n");
        sb.append("Fighters       : 2,500\r\n");
        sb.append("Credits        : 949,999\r\n");
        
        shipTypeDao.create("Merchant Cruiser");
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        //PlayerDao pdao = (PlayerDao)factory.getBean("playerDao");
        Player p = session.getTrader();
        assertNotNull(p);
        assertTrue("name not correct:"+p.getName(), "jim bob".equals(p.getName()));
        assertTrue("exp not right:"+p.getExperience(), 2==p.getExperience());
        assertTrue("algn not right:"+p.getAlignment(), 33==p.getAlignment());
        assertTrue("credits not right:"+p.getCredits(), 949999==p.getCredits());
        assertTrue("turns not right:"+p.getTurns(), 65520==p.getTurns());
        
        Corporation c = p.getCorporation();
        assertNull(c);
        
        Ship s = p.getCurShip();
        assertNotNull(s);
        assertTrue("name not correct:"+s.getName(), "jso fso2".equals(s.getName()));
        assertTrue("holds not correct:"+s.getHolds(), 75==s.getHolds());
        assertTrue("figs not correct:"+s.getFighters(), 2500==s.getFighters());
    }
    
    public void testCompactStatus() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append(" Sect 69�Turns 65,452�Creds 695,151�Figs 2,500�Shlds 400�Hlds 75�Ore 10�Org 12\r\n");
        sb.append(" Equ 15�Col 3�Phot 1�Armd 10�Lmpt 3�GTorp 2�TWarp No�Clks 4�Beacns 3�AtmDt 2\r\n");
        sb.append(" Crbo 100�EPrb 10�MDis 10�PsPrb No�PlScn Yes�LRS Holo�Aln 33�Exp 150\r\n");
        sb.append(" Corp 1�Ship 1 MerCru\r\n");
        sb.append("\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        session.setTrader(((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true));
        Logger.getLogger(TWLexer.class).setLevel(Level.DEBUG);
        lex.yylex();
        Logger.getLogger(TWLexer.class).setLevel(Level.WARN);
        assertTrue("Should have exited state", lex.getState() == lex.YYINITIAL);
        Sector sec = session.getSector();
        assertNotNull(sec);
        assertTrue("sector not right:"+sec.getId(), 69==sec.getId());
        
        Player p = session.getTrader();
        assertNotNull(p);
        assertTrue("exp not right:"+p.getExperience(), 150==p.getExperience());
        assertTrue("algn not right:"+p.getAlignment(), 33==p.getAlignment());
        assertTrue("credits not right:"+p.getCredits(), 695151==p.getCredits());
        assertTrue("turns not right:"+p.getTurns(), 65452==p.getTurns());
        assertTrue("Corp not right:"+p.getCorporation().getId(), 1==p.getCorporation().getId());
        
        Ship s = p.getCurShip();
        assertNotNull(s);
        assertTrue("Gtorps not correct:"+s.getGenesis(), 2==s.getGenesis());
        assertTrue("Cloaks not correct:"+s.getCloaks(), 4==s.getCloaks());
        assertTrue("Beacons not correct:"+s.getBeacons(), 3==s.getBeacons());
        assertTrue("Atomics not correct:"+s.getAtomics(), 2==s.getAtomics());
        assertTrue("Planet scanner not correct:"+s.getPlanetScanner(), s.getPlanetScanner());
        assertTrue("Long range scanner not correct:"+s.getLongRangeScanType(), s.HOLO_SCAN == s.getLongRangeScanType());
        assertTrue("id not correct:"+s.getTWId(), 1==s.getTWId());
        assertTrue("holds not correct:"+s.getHolds(), 75==s.getHolds());
        assertTrue("figs not correct:"+s.getFighters(), 2500==s.getFighters());
        assertTrue("shields not correct:"+s.getShields(), 400==s.getShields());
        assertTrue("fuel not correct:"+s.getHoldContents(s.FUEL_ORE), 10==s.getHoldContents(s.FUEL_ORE));
        assertTrue("org not correct:"+s.getHoldContents(s.ORGANICS), 12==s.getHoldContents(s.ORGANICS));
        assertTrue("equ not correct:"+s.getHoldContents(s.EQUIPMENT), 15==s.getHoldContents(s.EQUIPMENT));
        assertTrue("cols not correct:"+s.getHoldContents(s.COLONISTS), 3==s.getHoldContents(s.COLONISTS));
        assertTrue("photons not correct:"+s.getPhotonMissiles(), 1==s.getPhotonMissiles());
        assertTrue("Crombites not correct:"+s.getCrombiteLvl(), 100==s.getCrombiteLvl() );
        assertTrue("Eprobes not correct:"+s.getProbes(), 10==s.getProbes());
        assertTrue("Mine disrupts not correct:"+s.getMineDisrupters(), 10==s.getMineDisrupters());
        assertTrue("Armid mines not correct:"+s.getMines(s.ARMID), 10==s.getMines(s.ARMID) );
        assertTrue("Limpet mines not correct:"+s.getMines(s.LIMPET), 3==s.getMines(s.LIMPET));
        
    }
    
    public void testSectorDisplay() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Sector  : 1 in The Federation.\r\n");
        sb.append("Beacon  : FedSpace, FedLaw Enforced\r\n");
        sb.append("Ports   : Sol, Class 0 (Special)\r\n");
        sb.append("Planets : (M) Terra\r\n");
        sb.append("Traders : Rear Admiral the super man, w/ 2,500 ftrs,\r\n");
        sb.append("in jksdf asdflwe. (AldenShrike Merchant Cruiser)\r\n");
        sb.append("Warps to Sector(s) :  2 - (3) - (4) - (5) - (6) - (7)\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector sec = sectorDao.get(1);
        assertNotNull(sec);
        assertTrue("sector not right:"+sec.getId(), 1==sec.getId());
        assertTrue("beacon not right:"+sec.getBeacon(), "FedSpace, FedLaw Enforced".equals(sec.getBeacon()));
        
        
        Port p = sec.getPort();
        assertNotNull(p);
        assertTrue("port class not right:"+p.getPortClass(), 0==p.getPortClass());
        assertTrue("port name not right:"+p.getName(), "Sol".equals(p.getName()));
        
        Planet[] planets = sec.getPlanets();
        assertNotNull(planets);
        assertTrue("wrong number of planets:"+planets.length, 1==planets.length);
        Planet planet = planets[0];
        assertTrue("wrong planet name:"+planet.getName(), "Terra".equals(planet.getName()));
        
        Sector[] warps = sec.getWarps();
        assertNotNull(warps);
        assertTrue("wrong number of warps:"+warps.length, 6==warps.length);
    } 
    
    public void testDuplicatePlanetsInSectorDisplay() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Sector  : 1 in The Federation.\r\n");
        sb.append("Beacon  : FedSpace, FedLaw Enforced\r\n");
        sb.append("Ports   : Sol, Class 0 (Special)\r\n");
        sb.append("Planets : (M) Terra\r\n");
        sb.append("Traders : Rear Admiral the super man, w/ 2,500 ftrs,\r\n");
        sb.append("in jksdf asdflwe. (AldenShrike Merchant Cruiser)\r\n");
        sb.append("Warps to Sector(s) :  2 - (3) - (4) - (5) - (6) - (7)\r\n");
        sb.append("\n");
        sb.append("Command [TL=00:00:00]:[1] (?=Help)? :");
        sb.append("Sector  : 1 in The Federation.\r\n");
        sb.append("Beacon  : FedSpace, FedLaw Enforced\r\n");
        sb.append("Ports   : Sol, Class 0 (Special)\r\n");
        sb.append("Planets : (M) Terra\r\n");
        sb.append("Traders : Rear Admiral the super man, w/ 2,500 ftrs,\r\n");
        sb.append("in jksdf asdflwe. (AldenShrike Merchant Cruiser)\r\n");
        sb.append("Warps to Sector(s) :  2 - (3) - (4) - (5) - (6) - (7)\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector sec = sectorDao.get(1);       
        Planet[] planets = sec.getPlanets();
        assertNotNull(planets);
        assertTrue("wrong number of planets:"+planets.length, 1==planets.length);
        Planet planet = planets[0];
        assertTrue("wrong planet name:"+planet.getName(), "Terra".equals(planet.getName()));
        
    }
    
    public void testCommandPrompt() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("Command [TL=00:00:00]:[1] (?=Help)? :");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector sec = session.getSector();
        assertNotNull(sec);
        assertTrue("sec not right:"+sec.getId(), 1==sec.getId());
    }
    
    public void testPlanetPrompt() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Planet command (?=help) [D] ?");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("prompt not right:"+session.getPrompt(), session.PROMPT_PLANET == session.getPrompt());
    }
    
    public void testStardockPrompt() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("<StarDock> Where to? (?=Help)");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("prompt not right:"+session.getPrompt(), session.PROMPT_STARDOCK == session.getPrompt());
    }
    
    public void testGainExperience() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("For playing your turns today, you receive 1 experience point(s).");
       
        Player p = playerDao.get("test", Player.TRADER, true);
        session.setTrader(p);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("experience not right:"+p.getExperience(), 1 == p.getExperience());
        
    }
    public void testLoseExperience() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("and you LOSE 5 experience point(s).\n");
       
        Player p = playerDao.get("test", Player.TRADER, true);
        session.setTrader(p);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("experience not right:"+p.getExperience(), -5 == p.getExperience());
        
    }
    
    public void testAlignmentChange() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("For building this planet you receive 25 experience point(s).\r\n");
        sb.append("and your alignment went down by 10 point(s).\r\n");
        sb.append("and your alignment went up by 50 point(s).\r\n");
        sb.append("\r\n");
        Player p = playerDao.get("test", Player.TRADER, true);
        session.setTrader(p);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("alignment not right:"+p.getAlignment(), 40 == p.getAlignment());
        
    }
    
    public void testWarpMove() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Auto Warping to sector 333\r\n");
       
        Player p = playerDao.get("test", Player.TRADER, true);
        session.setTrader(p);
        ShipType st = shipTypeDao.get("shiptype", true);
        st.setTurnsPerWarp(3);
        Ship s = shipDao.create("foo");
        p.setTurns(100);
        p.setCurShip(s);
        s.setShipType(st);
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("wrong turns:"+session.getTrader().getTurns(), 97 == session.getTrader().getTurns());
        assertTrue("wrong sector:"+session.getSector().getId(), 333 == session.getSector().getId());
    }
    
    public void testTurnsLeft() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Docking...\r\n");
        sb.append("One turn deducted, 9968 turns left.\r\n");
       
        Player p = playerDao.get("test", Player.TRADER, true);
        session.setTrader(p);
        p.setTurns(100);
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        assertTrue("wrong turns:"+session.getTrader().getTurns(), 9968 == session.getTrader().getTurns());
    }
    
    public void tryOverflow(String file) throws Exception {
        Player p = ((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true);
        Ship s = shipDao.create("foo");
        p.setCurShip(s);
        shipTypeDao.create("Merchant Cruiser");
        shipTypeDao.create("Merchant Freighter");
        session.setTrader(p);
         
        PipedInputStream inStream = new PipedInputStream();
        PipedOutputStream outStream = new PipedOutputStream(inStream);
        LexerRunner runner = new LexerRunner();
        runner.setLexerClass("org.twdata.TW1606.tw.TWLexer");
        runner.setBeanFactory(factory);
        runner.init(inStream);
        ((TWLexer)runner.getLexer()).setFirstCommand(false);
        
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
        Thread lexThread = new Thread(runner);
        lexThread.start();
        
        BasicConfigurator.configure();
        InputStream in = getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = in.read(buffer)) > -1) {
            outStream.write(buffer, 0, len);
            //Thread.currentThread().sleep(50);
        }
        //outStream.close();
    }
    
    public void tryReaderOverflow(String file) throws Exception {
        Player p = ((PlayerDao)factory.getBean("playerDao")).get("bob", Player.TRADER, true);
        Ship s = shipDao.create("foo");
        p.setCurShip(s);
        shipTypeDao.create("Merchant Cruiser");
        shipTypeDao.create("Merchant Freighter");
        session.setTrader(p);
         
        PipedReader inStream = new PipedReader();
        PipedWriter outStream = new PipedWriter(inStream);
        LexerRunner runner = new LexerRunner();
        runner.setLexerClass("org.twdata.TW1606.tw.TWLexer");
        runner.setBeanFactory(factory);
        runner.init(inStream);
        ((TWLexer)runner.getLexer()).setFirstCommand(false);
        System.out.println("set first command to false");
        
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
        Thread lexThread = new Thread(runner);
        lexThread.start();
        
        BasicConfigurator.configure();
        Reader in = new InputStreamReader(getClass().getResourceAsStream(file));
        char[] buffer = new char[1024];
        int len = 0;
        while ((len = in.read(buffer)) > -1) {
            outStream.write(buffer, 0, len);
            //System.out.println("written:"+new String(buffer, 0, len));
            //Thread.currentThread().sleep(50);
        }
        //outStream.close();
    }
}    


