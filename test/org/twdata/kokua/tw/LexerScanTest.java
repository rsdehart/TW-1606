package org.twdata.TW1606.tw;

import org.twdata.TW1606.tw.model.Sector;
import org.twdata.TW1606.tw.model.Player;
import org.twdata.TW1606.tw.model.Corporation;
import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class LexerScanTest extends LexerBase {

    public LexerScanTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { LexerScanTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void testDensityScan() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Long Range Scan\r\n");
        sb.append("\r\n");
        sb.append("                          Relative Density Scan\r\n");
        sb.append("-----------------------------------------------------------------------------\r\n");
        sb.append("Sector    26  ==>              0  Warps : 6    NavHaz :     0%    Anom : No\r\n");
        sb.append("Sector ( 143) ==>              0  Warps : 3    NavHaz :     0%    Anom : No\r\n");
        sb.append("Sector ( 154) ==>            462  Warps : 2    NavHaz :     0%    Anom : No\r\n");
        sb.append("Sector ( 309) ==>            100  Warps : 2    NavHaz :     0%    Anom : No\r\n");
        sb.append("Sector   406  ==>            100  Warps : 4    NavHaz :     0%    Anom : No\r\n");
        sb.append("Sector   725  ==>            100  Warps : 6    NavHaz :    44%    Anom : Yes\r\n");

        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s = sectorDao.get(154);
        assertNotNull(s);
        assertTrue("density not right:"+s.getDensity(), 462==s.getDensity());
        assertTrue("navhaz not right:"+s.getNavHaz(), 0==s.getNavHaz());
        assertTrue("anomaly not right:"+s.hasAnomaly(), !s.hasAnomaly());
        
        s = sectorDao.get(725);
        assertNotNull(s);
        assertTrue("density not right:"+s.getDensity(), 100==s.getDensity());
        assertTrue("navhaz not right:"+s.getNavHaz(), 44==s.getNavHaz());
        assertTrue("anomaly not right:"+s.hasAnomaly(), s.hasAnomaly());
    }

    public void testHoloScan() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("Command [TL=00:00:00]:[31] (?=Help)? : ");
        sb.append("\r\n");
        sb.append("Long Range Scan\r\n");
        sb.append("Select (H)olo Scan or (D)ensity Scan or (Q)uit? [D] \r\n");
        sb.append("One turn deducted, 9781 turns left.\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 317 in uncharted space.\r\n");
        sb.append("Ports   : Greyhawk, Class 2 (BSB)\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 792 in Polyxena (unexplored).\r\n");
        sb.append("Ports   : Ambartsumian, Class 1 (BBS)\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 894 in uncharted space (unexplored).\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 31 in Polyxena.\r\n");
        sb.append("Warps to Sector(s) :  317 - 792 - 894\r\n");
        org.apache.log4j.BasicConfigurator.configure();
        Player p = playerDao.create("test", Player.TRADER);
        session.setTrader(p);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();

        Sector s = sectorDao.get(317, false);
        assertNotNull(s);
        assertTrue("Should have port", s.getPort() != null);
        assertTrue("Should be visited", s.isVisited());

        s = sectorDao.get(792, false);
        assertNotNull(s);
        assertTrue("Should have port", s.getPort() != null);
        assertTrue("Should not be visited", !s.isVisited());
    }
    
    public void testSectorWarps() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Sector 516 has warps to sector(s) :  26 - 143 - 154 - 309 - 406 - 725\r\n");
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s = sectorDao.get(516);
        assertNotNull(s);
        Sector[] warps = s.getWarps();
        assertNotNull(warps);
        assertTrue("warps num not right:"+warps.length, 6==warps.length);
        for (int x=0; x<warps.length; x++) {
            assertNotNull("warp should exist:"+x, warps[x]);
        }    
        s = sectorDao.get(154);
        assertNotNull(s);
        assertNotNull(warps);
        assertTrue("warp not found", Arrays.asList(warps).contains(s));
    }
    
    public void testDeployedFigs() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("                     Deployed  Fighter  Scan\r\n");
        sb.append("\r\n");
        sb.append(" Sector    Fighters    Personal/Corp    Mode        Tolls\r\n");
        sb.append("===========================================================\r\n");
        sb.append("    69           3       Personal    Defensive            N/A\r\n");
        sb.append("   731          30       Personal    Offensive            N/A\r\n");
        sb.append("   983          1T         Corp      Toll                 0\r\n");
        sb.append("    84           2         Corp      Defensive            N/A\r\n");
        sb.append("                1T Total                                  0 Total\r\n");
        
        Player p = playerDao.get("jim", Player.TRADER, true);
        Corporation c = corpDao.get(1, true);
        p.setCorporation(c);
        session.setTrader(p);
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s = sectorDao.get(69);
        assertNotNull(s);
        assertTrue("figs not right:"+s.getFighters(), 3==s.getFighters());
        assertTrue("fig owner not right:"+s.getFighterOwner(), p==s.getFighterOwner());
        assertTrue("fig type not right:"+s.getFighterType(), s.DEFENSIVE==s.getFighterType());
        
        s = sectorDao.get(983);
        assertNotNull(s);
        assertTrue("figs not right:"+s.getFighters(), s.getFighters()>=1000);
        assertTrue("fig owner not right:"+s.getFighterOwner(), c==s.getFighterOwner());
        assertTrue("fig type not right:"+s.getFighterType(), s.TOLL==s.getFighterType());
        
    }
    
    public void testEtherProbe() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("Probe entering sector : 447\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 447 in uncharted space (unexplored).\r\n");
        sb.append("\r\n");
        sb.append("Probe entering sector : 245\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 245 in uncharted space (unexplored).\r\n");
        sb.append("\r\n");
        sb.append("Probe entering sector : 294\r\n");
        sb.append("\r\n");
        sb.append("Sector  : 294 in Lough Corrib.\r\n");
        sb.append("Ports   : Abae II, Class 4 (SSB)\r\n");
        sb.append("\r\n");
        sb.append("Probe Self Destructs\r\n");

        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s = sectorDao.get(245);
        assertNotNull(s);
        assertTrue("should only have 1 warp:"+s.getWarps().length, 1==s.getWarps().length);
        assertTrue("warp should be 294:"+s.getWarps()[0].getId(), 294==s.getWarps()[0].getId());
        assertTrue("294 should have port", s.getWarps()[0].getPort() != null);
    }
}    


