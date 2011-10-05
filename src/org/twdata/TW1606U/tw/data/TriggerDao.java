package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface TriggerDao extends Dao {

    public Trigger create(int id);

    public Trigger create(int id, String name);

    public Collection getAll();

    public void update(Trigger trigger);

    public Trigger get(String name);

    public Trigger get(int id);

    public Trigger get(int id, boolean create);

    public void remove(Trigger trigger);
}
