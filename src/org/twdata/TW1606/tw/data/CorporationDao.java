package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public interface CorporationDao extends Dao {
    
    public Corporation create(int id);
    
    public Corporation create(int id, String name);
    
    public Collection getAll();
    
    public void update(Corporation corporation);
    
    public Corporation get(String name);
    
    public Corporation get(int id);
    
    public Corporation get(int id, boolean create);
    
    public void remove(Corporation corporation);
    
}
