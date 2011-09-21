package org.twdata.TW1606U.tw.model;

import java.util.Collection;
import java.util.Date;

public interface Corporation extends Owner {

    public int getId();
    public void setId(int id);
    
    public String getName();
    public void setName(String name);
    
    public Player getCeo();
    public void setCeo(Player ceo);
    
    public Date getLastModified();
	 public void setLastModified(Date lastModified);
}

