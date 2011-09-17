package org.twdata.TW1606.tw.data;

import org.twdata.TW1606.tw.model.Sector;
import org.twdata.TW1606.tw.model.Port;
import junit.framework.TestCase;
import java.util.*;
import java.io.*;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.*;
import org.twdata.TW1606.tw.*;

public class TWXProxyImporterTest  extends DaoBase {

    TWXProxyImporter importer;
    public TWXProxyImporterTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { TWXProxyImporterTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void setUp() {
        super.setUp();
        importer = new TWXProxyImporter();
        importer.setSession(session);
        importer.setDaoManager(dm);
    }
    
    public void testImportData() throws Exception {
        session.setGame(gameDao.create("game"));
        importer.importData(null, getClass().getResource("local.twx"));
        
        Collection c = sectorDao.getAll();
        
        Sector s = sectorDao.get(552, false);
        assertNotNull(s);
        Port p = s.getPort();
        assertNotNull(p);
        assertTrue("name not right, "+p.getName(), "Stardock".equals(p.getName()));
        assertTrue("class not right, "+p.getPortClass(), 9==p.getPortClass());
        
        int size = c.size();
        assertTrue("size should be 179, was "+size, size==179);
    }
    
}    

