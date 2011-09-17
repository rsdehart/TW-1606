package org.twdata.TW1606.tw;

import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class LexerComputerTest extends LexerBase {

    public LexerComputerTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { LexerComputerTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void testShipListInfo() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("==-- Trade Wars 2002 --==\r\n");
        sb.append("<Computer>\r\n");
        sb.append("\r\n");
        sb.append("<Computer activated>\r\n");
        sb.append("\r\n");
        sb.append("Computer command [TL=00:00:00]:[1] (?=Help)? C\r\n");
        sb.append("<Examine Ship Stats>\r\n");
        sb.append("\r\n");
        sb.append("You call up the Ship Catalog and browse through Starship specs.\r\n");
        sb.append("\r\n");
        sb.append("Which ship are you interested in (?=List) ?\r\n");
        sb.append("\r\n");
        sb.append("<A> *** Escape Pod ***\r\n");
        sb.append("<B> Merchant Cruiser\r\n");
        sb.append("<C> Scout Marauder\r\n");
        sb.append("<D> Missile Frigate\r\n");
        sb.append("<E> BattleShip\r\n");
        sb.append("<F> Corporate FlagShip\r\n");
        sb.append("<G> Colonial Transport\r\n");
        sb.append("\r\n");
        sb.append("<Q> To Leave\r\n");
        sb.append("\r\n");
        sb.append("Which ship are you interested in (?=List) ?\r\n");
        
        TWLexer lex = createLexer(sb.toString());
        lex.yylex();
        
        Collection c = shipTypeDao.getAll();
        assertNotNull(c);
        assertTrue("Should have found 7 ship types, found "+c.size(), 7==c.size());
        assertNotNull("Scout should exist", shipTypeDao.get("Scout Marauder", false));
    }
}    


