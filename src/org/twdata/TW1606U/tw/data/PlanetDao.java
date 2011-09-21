package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface PlanetDao extends Dao {
    
    public Planet create(Sector sector, String name);
    
    public Planet create(Sector sector, int twid);
    
    public Collection getAll();
    
    public void update(Planet planet);
    
    public Planet get(int id);
    
    public Planet getByTWId(int id);
    
    public Planet getBySectorAndName(Sector sector, String name);
    
    public void remove(Planet planet);
    
}
