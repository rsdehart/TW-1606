package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface SectorDao extends Dao {
    
    public Sector create(int id);
    
    public Collection getAll();
    
    public void update(Sector sector);
    
    public Sector get(int sector);
    
    public Sector get(int sector, boolean create);
    
    public void remove(Sector sector);
    
}
