package org.twdata.TW1606;

import org.twdata.TW1606.TextEventGenerator;
import org.twdata.TW1606.StreamFilter;
import junit.framework.TestCase;
import java.io.*;

public class TextEventGeneratorTest extends TestCase {

    TextEventGenerator text;
    boolean flag = false;
    String result = null;
    public TextEventGeneratorTest(String name) {
        super(name);
    } 
    
    public static void main(String[] args) {
        String[] testCaseName = { TextEventGeneratorTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public void setUp() {
        text = new TextEventGenerator();
        text.online = true;
        flag = false;
    }
    
    public void testWaitFor() throws Exception {
        assertTrue(text.matchNumber == -1);
        feed("soobarfoo", 0);
        Thread t = new Thread() {
            public void run() {
                try {
                flag = (text.waitForString("bar", 5, false) != null);
                } catch (InterruptedException ex) {}
            }
        };
        t.start();
        Thread.currentThread().sleep(300);
        assertTrue(!flag);
        
        feed("foobafoo", 0);
        Thread.currentThread().sleep(500);
        assertTrue("Shouldn't have matched partial", !flag);
        
        feed("foobarfoo", 0);
        Thread.currentThread().sleep(500);
        
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread should have timed out");
        }
        assertTrue("string not found", flag);
        
        assertTrue("wait for not correctly registered", text.watchString.length==1 && text.currentIndex.length == 1);
        assertTrue("should not be waiting", !text.waitingFor);
        assertTrue("Should have found text", text.matchNumber == 0);
        
    }
    
    public void testWaitForFullLine() throws Exception {
        Thread t = new Thread() {
            public void run() {
                try {
                result = text.waitForString("bar", 5, true);
                } catch (InterruptedException ex) {}
            }
        };
        t.start();
        Thread.currentThread().sleep(300);
        assertTrue(!flag);
        
        feed("foobarsoo\n", 0);
        Thread.currentThread().sleep(500);
        
        assertTrue("full string not found", result.indexOf("soo") > -1);
    }
    
    public void testWaitMux() throws Exception {
        assertTrue(text.matchNumber == -1);
        final int[] flags = new int[]{-1};
        
        Thread t = new Thread() {
            public void run() {
                try {
                flags[0] = text.waitMux(new String[]{"bar", "jim"}, 1, false);
                } catch (InterruptedException ex) {}
            }
        };
        t.start();
        feed("foojimfoo", 200);
        Thread.currentThread().sleep(500);
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread should have timed out");
        }
        assertTrue("String not found: "+flags[0], flags[0] == 1);
        
        assertTrue("wait for not correctly registered", text.watchString.length==2 && text.currentIndex.length == 2);
        assertTrue("should not be waiting", !text.waitingFor);
        assertTrue("Should have found text", text.matchNumber == 1);
        
    }
    
    public void testWaitMuxAndFullLine() throws Exception {
        Thread t = new Thread() {
            public void run() {
                try {
                int x = text.waitMux(new String[]{"bar", "jim"}, 1, true);
                result = text.getLastLine();
                } catch (InterruptedException ex) {}
            }
        };
        t.start();
        feed("foojimsoo\n", 200);
        Thread.currentThread().sleep(500);
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread should have timed out");
        }
        assertTrue("Full string not found: "+result, result.indexOf("soo") > -1);
    }
    
    public void testWaitForMultiple() throws Exception {
        assertTrue(text.matchNumber == -1);
        Thread t = new Thread() {
            public void run() {
                try {
                flag = (text.waitForString("foo", 1, false) != null);
                } catch (InterruptedException ex) {}
            }
        };
        t.start();
        Thread t2 = new Thread() {
            public void run() {
                try {
                text.waitForString("bar", 1, false);
                } catch (InterruptedException ex) {}
            }
        };
        t2.start();
        feed("soobarsoo", 200);
        Thread.currentThread().sleep(1000);
        if (t.isAlive()) {
            t.interrupt();
            fail("Should have timed out");
        } 
        if (t2.isAlive()) {
            fail("Shouldn't have to interrupt bar thread");
            t2.interrupt();
        }
        assertTrue("Should have timed out", !flag);
        
        assertTrue("wait for not correctly registered", text.watchString.length==2 && text.currentIndex.length == 2);
        assertTrue("should be waiting", text.waitingFor);
        assertTrue("Should have found text", text.matchNumber == 1);
        
    }
    
    public void testWaitAndRespond() throws Exception {
        assertTrue(text.matchNumber == -1);
        Thread t = new Thread() {
            public void run() {
                try {
                flag = (text.waitForString("bar", 1, false) != null);
                } catch (InterruptedException ex) {}
            }
        };
        t.start();
        text.waitAndRespond("foo", "gotcha");
        StreamFilter filter = new StreamFilter() {
            public void setFilterSource(StreamFilter source) {}
            public StreamFilter getFilterSource() {return null;}
        
            public int read(byte[] b) throws IOException {return 0;}
        
            public void write(byte[] b) throws IOException {flag = true;}
        };
        text.filter = filter;
        feed("foobarfoo", 200);
        Thread.currentThread().sleep(1000);
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread should have timed out");
        }
        assertTrue("response not sent", flag);
        
        assertTrue("wait for not correctly registered", text.watchString.length==1 && text.currentIndex.length == 1);
        assertTrue("should not be waiting", !text.waitingFor);
        assertTrue("Should have found text", text.matchNumber == 0);
        
    }
    
    private void feed(final String txt, final long delay) throws Exception {
        Thread.currentThread().sleep(delay);
        byte[] b = txt.getBytes();
        text.hasRead(b, b.length);
    }

}    

