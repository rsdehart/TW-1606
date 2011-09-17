package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public interface ShipDao extends Dao {
    
    public Ship create(String name);
    
    public Ship create(int twid);
    
    public Collection getAll();
    
    public void update(Ship ship);
    
    public Ship get(int id);
    
    public Collection getBySector(Sector sec);
    
    public Ship getByTWId(int twid);
    
    public Ship getByTWId(int twid, boolean create);
    
    public void remove(Ship ship);
    
}
