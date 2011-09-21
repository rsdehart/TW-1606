package org.twdata.TW1606U.data;

import jdbm.*;
import java.io.*;
import java.util.*;

public interface SessionDao extends Dao {
    
    public Session create(String name);
    
    public Collection getAll();
    
    public void update(Session game);
    
    public Session get(String name);
    
    public Session get(String name, boolean create);
    
    public void remove(Session game);
    
}
