package org.twdata.TW1606U.util;

import java.awt.Component;
import java.awt.Container;

/**
 *  Recursively list files, and make a zip file.
 *
 */
public class SwingUtils {

    private SwingUtils(){}
    
    public static Component find(Container parent, String name) {
        Component[] kids = parent.getComponents();
        for (int x=0; x<kids.length; x++) {
            if (name.equals(kids[x].getName())) {
                return kids[x];
            } else if (kids[x] instanceof Container) {
                Component c = find((Container)kids[x], name);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }
    
    public static Component find(Container parent, String name, Class reqClass) {
        Component[] kids = parent.getComponents();
        for (int x=0; x<kids.length; x++) {
            if (name.equals(kids[x].getName()) && reqClass == kids[x].getClass()) {
                return kids[x];
            } else if (kids[x] instanceof Container) {
                Component c = find((Container)kids[x], name);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }
}

