package org.twdata.TW1606.data;
import java.io.Serializable;

import java.util.*;

/**  Description of the Class */
public class SessionImpl implements Session, Serializable {

    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
    private Map props;

    public SessionImpl() {
        props = new HashMap();
    }

    /**
     *  Returns the value of name.
     *
     *@return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Sets the value of name.
     *
     *@param  name  The value to assign name.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Returns the value of host.
     *
     *@return    The host value
     */
    public String getHost() {
        return host;
    }


    /**
     *  Sets the value of host.
     *
     *@param  host  The value to assign host.
     */
    public void setHost(String host) {
        this.host = host;
    }


    /**
     *  Returns the value of port.
     *
     *@return    The port value
     */
    public int getPort() {
        return port;
    }


    /**
     *  Sets the value of port.
     *
     *@param  port  The value to assign port.
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     *  Returns the value of username.
     *
     *@return    The username value
     */
    public String getUsername() {
        return username;
    }


    /**
     *  Sets the value of username.
     *
     *@param  username  The value to assign username.
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     *  Returns the value of password.
     *
     *@return    The password value
     */
    public String getPassword() {
        return password;
    }


    /**
     *  Sets the value of password.
     *
     *@param  password  The value to assign password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setProperty(String name, String val) {
        if (props == null) {
            props = new HashMap();
        }
        props.put(name, val);
    }
    
    public String getProperty(String name) {
        if (props == null) {
            props = new HashMap();
        }
        return (String)props.get(name);
    }


}

