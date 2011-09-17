package org.twdata.TW1606.util;

import junit.framework.TestCase;
import java.io.*;
import java.util.*;

public class ZipBackupTest extends TestCase {

    public ZipBackupTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { ZipBackupTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void testFiller() {}
    
   
    /*
    public void testScanFile() throws Exception {
        ZipBackup zb = new ZipBackup();
        ArrayList files = new ArrayList();
        Stack pathStack;
        String[] fileNames = new String[] {"src", "build.xml"};
        File rootDir = new File("/home/mrdon/dev/tw1606");
        File file;
        for (int x=0; x<fileNames.length; x++) {
            file = new File(rootDir, fileNames[x]);
            pathStack = new Stack();
            if (file.exists()) {
                pathStack.push(fileNames[x]);
                zb.scanFile(file, pathStack, files);
            }
        }
        
        assertTrue(files.size() > 0);
        
        for (Iterator i = files.iterator(); i.hasNext(); ) {
            System.out.println("found file:"+i.next());
        }
        
        
    }
    
    public void testBackupFiles() throws Exception {
        ZipBackup zb = new ZipBackup();
        String[] fileNames = new String[] {"src", "build.xml"};
        File rootDir = new File("/home/mrdon/dev/tw1606");
        
        zb.backupFiles(rootDir, fileNames, "test.zip");
        
    }
    */
    
}    

