package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface ShipTypeDao extends Dao {
    
    public ShipType create(String name);
    
    public ShipType create(int id);
    
    public Collection getAll();
    
    public void update(ShipType shipType);
    
    public ShipType get(String name);
    
    public ShipType get(String name, boolean create);
    
    public ShipType getById(int id);
    
    public void remove(ShipType shipType);
    
}
