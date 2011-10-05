package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public class TriggerMapDao extends BaseMapDao implements TriggerDao {

    public static final String NAME = "trigger";

    public Trigger create(int id) {
        Trigger m = (Trigger) db.createModel(NAME);
        m.setId(id);
        update(m);
        return m;
    }

    public Trigger create(int id, String name) {
        Trigger m = (Trigger) db.createModel(NAME);
        m.setId(id);
        m.setName(name);
        update(m);
        return m;
    }

    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }

    public void update(Trigger trigger) {
        Map m = db.getMap(NAME);
        m.put(new Integer(trigger.getId()), trigger);
    }

    public Trigger get(String name) {
        Map m = db.getMap(NAME);
        Trigger c;
        for (Iterator i = m.values().iterator(); i.hasNext();) {
            c = (Trigger) i.next();
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Trigger get(int id) {
        return get(id, false);
    }

    public Trigger get(int id, boolean create) {
        Map m = db.getMap(NAME);
        Trigger t = (Trigger) m.get(new Integer(id));
        if (t == null && create) {
            t = create(id);
        }
        return t;
    }

    public void remove(Trigger trigger) {
        Map m = db.getMap(NAME);
        m.remove(new Integer(trigger.getId()));
    }
}
