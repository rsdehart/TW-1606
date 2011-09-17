package org.twdata.TW1606.data;

public interface Session {

    public String getName();
    public void setName(String name);
    
    public String getHost();
    public void setHost(String host);
    
    public int getPort();
    public void setPort(int port);
    
    public String getUsername();
    public void setUsername(String username);
    
    public String getPassword();
    public void setPassword(String password);
    
    public String getProperty(String name);
    public void setProperty(String name, String value);
}

