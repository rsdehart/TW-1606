package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public class CorporationMapDao extends BaseMapDao implements CorporationDao {
    
    public static final String NAME = "corporation";
    
    public Corporation create(int id) {
        Corporation c = (Corporation) db.createModel(NAME);
        c.setId(id);
        update(c);
        return c;
    }
    
    public Corporation create(int id, String name) {
        Corporation c = (Corporation) db.createModel(NAME);
        c.setId(id);
        c.setName(name);
        update(c);
        return c;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }
    
    public void update(Corporation corporation) {
        Map m = db.getMap(NAME);
        m.put(new Integer(corporation.getId()), corporation);
    }
    
    public Corporation get(String name) {
        Map m = db.getMap(NAME);
        Corporation c;
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            c = (Corporation)i.next();
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    public Corporation get(int id) {
        return get(id, false);
    }
    
    public Corporation get(int id, boolean create) {
        Map m = db.getMap(NAME);
        Corporation c = (Corporation) m.get(new Integer(id));
        if (c == null && create) {
            c = create(id);
        }
        return c;
    }
    
    public void remove(Corporation corporation) {
        Map m = db.getMap(NAME);
        m.remove(new Integer(corporation.getId()));   
    }
    
}
