package org.twdata.TW1606.tw.gui.terminal;

import java.util.List;


import junit.framework.TestCase;

public class TerminalHoverListenerTest extends TestCase {

    TerminalHoverListener listener;
    
    public TerminalHoverListenerTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        listener = new TerminalHoverListener();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFindLinks() {
        String line = "Warps to Sector(s) :  2 - (3) - (4) - (5) - (6) - (7)\r\n";
        List<Link> links = listener.findLinks(line);
        assertNotNull(links);
        assertEquals(6, links.size());
        
        assertEquals("3", links.get(1).getText());
        assertEquals(26, links.get(1).getStart());
        
    }

}
