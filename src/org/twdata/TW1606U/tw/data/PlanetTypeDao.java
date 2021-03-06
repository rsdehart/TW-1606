package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface PlanetTypeDao extends Dao {
    
    public PlanetType create(String type);
    
    public Collection getAll();
    
    public void update(PlanetType planetType);
    
    public PlanetType get(String type);
    
    public PlanetType get(String type, boolean create);
    
    public void remove(PlanetType planetType);
    
}
