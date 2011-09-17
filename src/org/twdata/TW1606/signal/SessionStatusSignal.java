package org.twdata.TW1606.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606.data.Session;


/**  Description of the Class */
public class SessionStatusSignal extends BusSignal {


    /**  Description of the Field */
    public final static String START = "start";

    public final static String START_REQUEST = "startRequest";
    
    /**  Description of the Field */
    public final static String STOP = "stop";

    private String status;
    private Session session;

    /**
     *  Constructor for the SessionStatusSignal object
     *
     *@param  status  param
     *@param  name    param
     */
    public SessionStatusSignal(String status, Session session) {
        this.session = session;
        this.status = status;
    }

    /**
     *  Gets the name attribute of the SessionStatusSignal object
     *
     *@return    The name value
     */
    public String getName() {
        return session.getName();
    }
    
    public Session getSession() {
        return session;
    }

    /**
     *  Gets the status attribute of the SessionStatusSignal object
     *
     *@return    The status value
     */
    public String getStatus() {
        return status;
    }

}

