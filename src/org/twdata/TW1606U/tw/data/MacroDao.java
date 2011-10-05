package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface MacroDao extends Dao {

    public Macro create(int id);

    public Macro create(int id, String name);

    public Collection getAll();

    public void update(Macro macro);

    public Macro get(String name);

    public Macro get(int id);

    public Macro get(int id, boolean create);

    public void remove(Macro macro);
}
