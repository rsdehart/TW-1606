package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public interface PortDao extends Dao {
    
    public Port create(int sector);
    
    public Collection getAll();
    
    public void update(Port port);
    
    public Port get(int sector);
    
    public Port get(int sector, boolean create);
    
    public void remove(Port port);
    
}
