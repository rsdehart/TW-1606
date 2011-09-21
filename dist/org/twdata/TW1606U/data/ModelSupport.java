package org.twdata.TW1606U.data;

import com.thoughtworks.xstream.*;

public interface ModelSupport {
    
    public void registerAliases(XStream xs);
    
    public DaoAwareModel createModel(String name);
}

