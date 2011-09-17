package org.twdata.TW1606.tw;

import org.twdata.TW1606.tw.model.Sector;
import org.twdata.TW1606.tw.model.Port;
import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class LexerCIMTest extends LexerBase {

    public LexerCIMTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { LexerCIMTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void testComputerPlot() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("The shortest path (6 hops, 18 turns) from sector 300 to sector 600 is:\r\n");
        sb.append("300 > (248) > (753) > 359 > 892 > (598) > (600)\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        int[] chain = new int[] {248, 753, 359, 892, 598, 600};
        Sector s = sectorDao.get(300);
        assertNotNull(s);
        Sector[] warps = s.getWarps();
        assertTrue("size not right:"+warps.length, 1==warps.length);
        
        int pos = 0;
        while (s != null && pos < chain.length) {
            warps = s.getWarps();
            assertTrue(s.getId()+" size not right:"+warps.length, 1==warps.length);
            s = warps[0];
            if (chain[pos++] != s.getId()) {
                fail("Wrong sector :"+s.getId()+" pos:"+pos);
            }
        }
    }
    
    public void testCIMPlot() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append(": \r\n");
        sb.append("FM > 300\r\n");
        sb.append("  TO > 600\r\n");
        sb.append("300 > (248) > (753) > 359 > 892 > (598) > (600)\r\n");
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        int[] chain = new int[] {248, 753, 359, 892, 598, 600};
        Sector s = sectorDao.get(300);
        assertNotNull(s);
        Sector[] warps = s.getWarps();
        assertTrue("size not right:"+warps.length, 1==warps.length);
        
        int pos = 0;
        while (s != null && pos < chain.length) {
            warps = s.getWarps();
            assertTrue(s.getId()+" size not right:"+warps.length, 1==warps.length);
            s = warps[0];
            if (chain[pos++] != s.getId()) {
                fail("Wrong sector :"+s.getId()+" pos:"+pos);
            }
        }
    }
    
    public void testCIMWarps() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append(": \r\n");
        sb.append("   1    2    3    4    5    6    7\r\n");
        sb.append("   2    1    3    7    8    9   10\r\n");
        sb.append("   3    1    2    4   91\r\n");
        sb.append("   4    1    3    5\r\n");
        sb.append(" 999  324  476  687  823\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s;
        Sector[] c;
        
        s = sectorDao.get(1);
        assertNotNull(s);
        c = s.getWarps();
        assertTrue("wrong warps:"+c.length, 6==c.length);
        
        s = sectorDao.get(999);
        assertNotNull(s);
        c = s.getWarps();
        assertTrue("wrong warps:"+c.length, 4==c.length);
        
        List warps = Arrays.asList(new String[] {"324",  "476",  "687",  "823"});
        
        int pos = 0, count = 0;
        for (int x=0; x<c.length; x++) {
            s = c[x];
            if (warps.contains(String.valueOf(s.getId()))) {
                count++;
            }
        }
        assertTrue("not all warps found:"+count, 4==count);
    }
    
    public void testCIMPorts() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append(": \r\n");
        sb.append("   3 - 2910 100% - 2840 100%   2500 100% \r\n");
        sb.append("  27   2650 100% - 1820 100% - 1960 100% \r\n");
        sb.append("  59   2580 100%   2080 100% - 1580 100% \r\n");
        sb.append(" 205   1370  87%    940  44%   2850  72% \r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Sector s;
        Port p;
        
        s = sectorDao.get(3);
        assertNotNull(s);
        p = s.getPort();
        assertNotNull(p);
        assertTrue("wrong class:"+p.getPortClass(), p.CLASS_BBS==p.getPortClass());
        assertTrue("wrong cur fuel:"+p.getCurProduct(p.FUEL_ORE), 2910==p.getCurProduct(p.FUEL_ORE));
        assertTrue("wrong cur org:"+p.getCurProduct(p.ORGANICS), 2840==p.getCurProduct(p.ORGANICS));
        assertTrue("wrong cur equip:"+p.getCurProduct(p.EQUIPMENT), 2500==p.getCurProduct(p.EQUIPMENT));
        assertTrue("wrong max fuel:"+p.getMaxProduct(p.FUEL_ORE), 2910==p.getMaxProduct(p.FUEL_ORE));
        assertTrue("wrong max org:"+p.getMaxProduct(p.ORGANICS), 2840==p.getMaxProduct(p.ORGANICS));
        assertTrue("wrong max equip:"+p.getMaxProduct(p.EQUIPMENT), 2500==p.getMaxProduct(p.EQUIPMENT));
        
        s = sectorDao.get(205);
        assertNotNull(s);
        p = s.getPort();
        assertNotNull(p);
        assertTrue("wrong class:"+p.getPortClass(), p.CLASS_SSS==p.getPortClass());
        assertTrue("wrong cur fuel:"+p.getCurProduct(p.FUEL_ORE), 1370==p.getCurProduct(p.FUEL_ORE));
        assertTrue("wrong cur org:"+p.getCurProduct(p.ORGANICS), 940==p.getCurProduct(p.ORGANICS));
        assertTrue("wrong cur equip:"+p.getCurProduct(p.EQUIPMENT), 2850==p.getCurProduct(p.EQUIPMENT));
        assertTrue("wrong max fuel:"+p.getMaxProduct(p.FUEL_ORE), 1574==p.getMaxProduct(p.FUEL_ORE));
        assertTrue("wrong max org:"+p.getMaxProduct(p.ORGANICS), 2136==p.getMaxProduct(p.ORGANICS));
        assertTrue("wrong max equip:"+p.getMaxProduct(p.EQUIPMENT), 3958==p.getMaxProduct(p.EQUIPMENT));
        
    }    
    
}    


