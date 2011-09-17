package org.twdata.TW1606.tw.graph;

import org.twdata.TW1606.tw.model.Sector;
import junit.framework.TestCase;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.data.*;
import java.util.*;
import java.io.*;
import org.twdata.TW1606.tw.LexerBase;

public class SectorGraphTest extends LexerBase {

    SectorGraph sg;
    boolean flag = false;
    public SectorGraphTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { SectorGraphTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void setUp() {
        super.setUp();
        sg = new SectorGraph();
        flag = false;
    }
    
    public void testShortestPath() throws Exception {
        Sector s = sectorDao.create(1);
        Sector w = sectorDao.create(2);
        Sector w1 = sectorDao.create(3);
        w.addWarp(w1);
        s.addWarp(w);
        
        sg.addAllSectors(sectorDao.getAll());
        
        List path = sg.shortestPath(s, w1);
        assertNotNull(path);
        assertTrue("Path size not 2, was "+path.size()+":"+path, 2==path.size());
        
        Warp warp = (Warp) path.get(0);
        Warp warp1 = (Warp) path.get(1);
        assertTrue("Step 1 not correct", s.equals(warp.getSourceSector()));
        assertTrue("Step 2a not correct", w.equals(warp.getTargetSector()));
        assertTrue("Step 2b not correct", w.equals(warp1.getSourceSector()));
        assertTrue("Step 3 not correct", w1.equals(warp1.getTargetSector()));
    }
    
    public void testGetSectorsWithinRange() throws Exception {
        Sector s = sectorDao.create(1);
        Sector w = sectorDao.create(2);
        Sector w1 = sectorDao.create(3);
        Sector w2 = sectorDao.create(4);
        w.addWarp(w1);
        w.addWarp(w2);
        s.addWarp(w);
        
        sg.addAllSectors(sectorDao.getAll());
        
        ClosestWithinRangeIterator i = sg.getSectorsWithinRange(s, 1);
        assertNotNull(i);
        assertTrue("Should be first sector", i.hasNext());
        
        Sector list0 = (Sector)i.next();
        assertNotNull(list0);
        assertTrue("First Sector not right ", list0.equals(s));
        assertTrue("First sector distance not 0, was "+i.getCurrentDistance(), 0 == i.getCurrentDistance());
        
        assertTrue("Should be second sector", i.hasNext());
        Sector list1 = (Sector)i.next();
        assertNotNull(list1);
        assertTrue("Second Sector not right", list1.equals(w));
        assertTrue("Second sector distance not 1, was "+i.getCurrentDistance(), 1 == i.getCurrentDistance());
        
        assertTrue("Shouldn't be any more sectors", !i.hasNext());
        //assertTrue("Warp not right target", warp.getTargetSector().equals(w));
        //assertTrue("First hop wrong distance, "+i.getCurrentDistance(), 1 == i.getCurrentDistance());
        //assertTrue("Shouldn't be any more hops", !i.hasNext());
    }
    
}    

