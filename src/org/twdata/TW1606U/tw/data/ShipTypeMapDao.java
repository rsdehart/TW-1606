package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public class ShipTypeMapDao extends BaseMapDao implements ShipTypeDao {
    
    public static final String NAME = "shipType";
    
    public ShipType create(String name) {
        ShipType st = (ShipType) db.createModel(NAME);
        st.setName(name);
        update(st);
        return st;
    }
    
    public ShipType create(int id) {
        ShipType st = (ShipType) db.createModel(NAME);
        st.setId(id);
        update(st);
        return st;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }
    
    public void update(ShipType shipType) {
        Map m  = db.getMap(NAME);
        m.put(shipType.getName(), shipType);
    }
    
    public ShipType get(String name) {
        return get(name, false);
    }
    
    public ShipType get(String name, boolean create) {
        Map m = db.getMap(NAME);
        ShipType st = (ShipType) m.get(name);
        if (st == null && create) {
            st = create(name);
        }
        return st;
    }
    
    public ShipType getById(int id) {
        Map m = db.getMap(NAME);
        ShipType st;
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            st = (ShipType)i.next();
            if (st.getId() == id) {
                return st;
            }
        }
        return null;
    }
    
    public void remove(ShipType shipType) {
        Map m = db.getMap(NAME);
        m.remove(shipType.getName());
    }
    
}
