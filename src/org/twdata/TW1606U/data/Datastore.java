package org.twdata.TW1606U.data;

import jdbm.*;
import java.io.*;
import java.util.Map;

import org.apache.log4j.Logger;

import org.twdata.TW1606U.tw.model.*;

public interface Datastore {

    public void init();
    
    public void removeSession(String session);
    
    public Map getMap(String name, boolean sessionSpecific);
    
    public Map getMap(String name);
    
    public String getCurrentSession();
    
    public Object createModel(String name);
    
    
}
