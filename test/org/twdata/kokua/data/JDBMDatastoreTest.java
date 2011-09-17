package org.twdata.TW1606.data;

import org.twdata.TW1606.signal.MessageBus;
import org.twdata.TW1606.signal.SessionStatusSignal;
import junit.framework.TestCase;
import java.util.*;
import java.io.*;
import org.twdata.TW1606.signal.*;

public class JDBMDatastoreTest extends TestCase {

    JDBMDatastore db;
    public JDBMDatastoreTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { JDBMDatastoreTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void setUp() {
        db = new JDBMDatastore();
    }
    
    public void testKeyComparator() throws Exception {
        Comparator c = db.new KeyComparator();
        
        assertTrue(c.compare("bar", "foo") < 0);
        assertTrue(c.compare("foo", "bar") > 0);
        assertTrue(c.compare("foo", "foo") == 0);
        
        assertTrue(c.compare("foo/foo", "bar") > 0);
        assertTrue(c.compare("bar", "foo/foo") < 0);
        assertTrue(c.compare("foo/foo", "foo/foo") == 0);
        
    }
    
    public void testSaveAsXML() throws Exception {
        File tmp = File.createTempFile("prefix", ".txt");
        System.out.println("path:"+tmp.getParent());
        File dir = tmp.getParentFile();
        db.openRecordManager(tmp.getParent() + File.separator + "datastore");
        
        Map sessions = db.getMap(db.SESSION_MAP, false);
        Session s = new SessionImpl();
        s.setName("test");
        sessions.put("test", s);
        
        db.setMessageBus(new MessageBus());
        db.channel(new SessionStatusSignal(SessionStatusSignal.START_REQUEST, s));
        
        Map strings = db.getMap("strings");
        strings.put("foo", "bar");
        db.saveAsXMLFiles(dir);
        
        File sesXML = new File(dir, db.SESSION_MAP+".xml");
        assertTrue("Sessions xml doesn't exist", sesXML.exists());
        
        File strXML = new File(dir, "test.strings.xml");
        assertTrue("Strings xml doesn't exist", strXML.exists());
    }
    
    public void testRemoveSession() throws Exception {
        File tmp = File.createTempFile("prefix", ".txt");
        System.out.println("path:"+tmp.getParent());
        File dir = tmp.getParentFile();
        db.openRecordManager(tmp.getParent() + File.separator + "datastore");
        
        Map sessions = db.getMap(db.SESSION_MAP, false);
        Session s = new SessionImpl();
        s.setName("test");
        sessions.put("test", s);
        
        db.setMessageBus(new MessageBus());
        db.channel(new SessionStatusSignal(SessionStatusSignal.START_REQUEST, s));
        
        Map strings = db.getMap("strings");
        strings.put("foo", "bar");
        db.channel(new SessionStatusSignal(SessionStatusSignal.STOP, s));
        
        db.removeSession("test");
        db.channel(new SessionStatusSignal(SessionStatusSignal.START_REQUEST, s));
        strings = db.getMap("strings");
        assertNotNull(strings);
        assertTrue("Should be empty, has "+strings.size(), strings.size() == 0);
    }
    
    public void testLoadAsXML() throws Exception {
        File tmp = File.createTempFile("prefix", ".txt");
        File sesf = copy("sessions.xml", new File(tmp.getParentFile(), "sessions.xml"));
        File strf = copy("test.strings.xml", new File(tmp.getParentFile(), "test.strings.xml"));
        
        db.openRecordManager(tmp.getParent() + File.separator + "datastore");
        
        db.loadFromXMLFiles(tmp.getParentFile());
        
        Map sessions = db.getMap(db.SESSION_MAP, false);
        Session s = (Session)sessions.get("test");
        assertNotNull("Couldn't retrieve the session", s);
        assertTrue("Session name not right", "test".equals(s.getName()));
        
        db.setMessageBus(new MessageBus());
        db.channel(new SessionStatusSignal(SessionStatusSignal.START_REQUEST, s));
        Map strings = db.getMap("strings");
        String str = (String)strings.get("foo");
        assertNotNull("Couldn't retrieve string", str);
        assertTrue("String val not right", "bar".equals(str));
    }
    
    private File copy(String path, File out) throws Exception {
        InputStream in = getClass().getResourceAsStream(path);
        FileOutputStream fout = new FileOutputStream(out);
        byte[] b = new byte[64];
        int len = 0;
        while ((len = in.read(b)) > 0) {
            fout.write(b, 0, len);
        }
        in.close();
        fout.close();
        return out;
    }   
}    

