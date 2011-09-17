package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public class PortMapDao extends BaseMapDao implements PortDao {
    
    public static final String NAME = "port";
    
    public Port create(int sector) {
        Port p = (Port) db.createModel(NAME);
        p.setId(sector);
        update(p);
        return p;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }
    
    public void update(Port port) {
        Map m = db.getMap(NAME);
        m.put(new Integer(port.getId()), port);
    }
    
    public Port get(int sector) {
        return get(sector, false);
    }
    
    public Port get(int sector, boolean create) {
        Map m = db.getMap(NAME);
        Port p = (Port)m.get(new Integer(sector));
        if (p == null && create) {
            p = create(sector);
        }
        return p;
    }   
    
    public void remove(Port port) {
        Map m = db.getMap(NAME);
        m.remove(new Integer(port.getId()));
    }
    
}
