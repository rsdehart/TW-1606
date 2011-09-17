package org.twdata.TW1606.tw.gui;

import org.twdata.TW1606.tw.model.Sector;
import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.*;

public class SectorMapTest extends DaoBase {

    SectorMap map;
    
    public SectorMapTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { SectorMapTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void setUp() {
        super.setUp();
        map = new SectorMap();
        map.setDaoManager(dm);
    }
    
    public void testChangeAttribute_null() throws Exception {
        Sector s = sectorDao.get(1, true);
        map.changeAttribute("1", "color", "blue");
        assertTrue("Didn't set color:"+s.getNote(), "[color:blue]\n".equals(s.getNote()));
        
    }
    
    public void testChangeAttribute_other() throws Exception {
        Sector s = sectorDao.get(1, true);
        s.setNote("foo");
        map.changeAttribute("1", "color", "blue");
        assertTrue("Didn't set color:"+s.getNote(), "[color:blue]\nfoo".equals(s.getNote()));
    }
    
    public void testChangeAttribute_exists() throws Exception {
        Sector s = sectorDao.get(1, true);
        s.setNote("[color:red]\nfoo");
        map.changeAttribute("1", "color", "blue");
        assertTrue("Didn't set color:"+s.getNote(), "[color:blue]\nfoo".equals(s.getNote()));
    }
}    


