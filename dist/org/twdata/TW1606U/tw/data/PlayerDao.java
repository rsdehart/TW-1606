package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public interface PlayerDao extends Dao {
    
    public Player create(String name, int type);
    
    public Collection getAll();
    
    public Collection getAllByType(int type);
    
    public void update(Player player);
    
    public Player get(String name, int type);
    
    public Player get(String name, int type, boolean create);
    
    public void remove(Player player);
    
}
