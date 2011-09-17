package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public interface GameDao extends Dao {
    
    public Game create(String name);
    
    public Collection getAll();
    
    public void update(Game game);
    
    public Game get(String name);
    
    public Game get(String name, boolean create);
    
    public void remove(Game game);
    
}
