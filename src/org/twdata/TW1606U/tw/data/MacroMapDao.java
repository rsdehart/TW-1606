package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public class MacroMapDao extends BaseMapDao implements MacroDao {

    public static final String NAME = "macro";

    public Macro create(int id) {
        Macro m = (Macro) db.createModel(NAME);
        m.setId(id);
        update(m);
        return m;
    }

    public Macro create(int id, String name) {
        Macro m = (Macro) db.createModel(NAME);
        m.setId(id);
        m.setName(name);
        update(m);
        return m;
    }

    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }

    public void update(Macro macro) {
        Map m = db.getMap(NAME);
        m.put(new Integer(macro.getId()), macro);
    }

    public Macro get(String name) {
        Map m = db.getMap(NAME);
        Macro c;
        for (Iterator i = m.values().iterator(); i.hasNext();) {
            c = (Macro) i.next();
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Macro get(int id) {
        return get(id, false);
    }

    public Macro get(int id, boolean create) {
        Map m = db.getMap(NAME);
        Macro c = (Macro) m.get(new Integer(id));
        if (c == null && create) {
            c = create(id);
        }
        return c;
    }

    public void remove(Macro macro) {
        Map m = db.getMap(NAME);
        m.remove(new Integer(macro.getId()));
    }
}
