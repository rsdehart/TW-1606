package org.twdata.TW1606U.tw.model;

import java.util.Collection;
import java.util.Date;

public interface Trigger {

    public int getId();
    public void setId(int id);
    
    public String getName();
    public void setName(String name);
    
    public String getInText();
    public void setInText(String text);

    public String getOutText();
    public void setOutText(String text);

    public Date getLastModified();
    public void setLastModified(Date lastModified);
}

