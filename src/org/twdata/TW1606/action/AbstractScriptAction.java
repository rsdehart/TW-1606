package org.twdata.TW1606.action;

import javax.swing.JFrame;
import org.apache.log4j.Logger;
import bsh.*;
import org.twdata.TW1606.script.BeanShellHandler;
import org.twdata.TW1606.signal.*;

/**
 *  Description of the Class
 *
 */
public abstract class AbstractScriptAction extends AbstractAction implements ScriptAction {

    /**  Description of the Field */
    protected Logger log;

    protected static final int ENABLE = 1;
    protected static final int DISABLE = 0;
    protected static final int NULL = -1;
    
    protected String script = null;
    protected String globalScript = null;
    protected String path = null;
    protected String onConnect = null;
    protected String onDisconnect = null;
    protected String onScriptStart = null;
    protected String onScriptStop = null;
    protected boolean eventsEnabled = false;
    protected MessageBus bus;


    public AbstractScriptAction() {
        super();
        log = Logger.getLogger(getClass());
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     *  Constructor for the BSHAction object
     *
     *@param  id      Description of the Parameter
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     *  Sets the script attribute of the BSHAction object
     *
     *@param  script  The new script value
     */
    public void setScript(String global, String script) {
        this.script = script;
        this.globalScript = global;
    }

    
    public void setOnConnect(String onConnect) {
        this.onConnect = onConnect;
        enableEvents();
    }
    
    public void setOnDisconnect(String onDisconnect) {
        this.onDisconnect = onDisconnect;
        enableEvents();
    }
    
    public void setOnScriptStart(String onScriptStart) {
        this.onScriptStart = onScriptStart;
        enableEvents();
    }
    
    public void setOnScriptStop(String onScriptStop) {
        this.onScriptStop = onScriptStop;
        enableEvents();
    }
    
    private void enableEvents() {
        if (!eventsEnabled) {
            bus.plug(this);
            eventsEnabled = true;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  frame  Description of the Parameter
     */
    public abstract void invoke();
    
}

