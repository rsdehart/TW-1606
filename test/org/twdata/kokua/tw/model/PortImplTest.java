package org.twdata.TW1606.tw.model;

import junit.framework.TestCase;

public class PortImplTest extends TestCase {

    public PortImplTest(String name) {
        super(name);
    }

    public void testDetermineUntradeable() {
        PortImpl port1 = new PortImpl();
        port1.setPortClass(1);
        PortImpl port2 = new PortImpl();
        port2.setPortClass(3);
        
        assertEquals(1, port1.determineUntradeable(port2));
        assertEquals(1, port2.determineUntradeable(port1));
        
        assertEquals(-1, port2.determineUntradeable(port2));
    }
    
}
