package org.twdata.TW1606.data;

import org.apache.log4j.Logger;
import org.twdata.TW1606.signal.MessageBus;

public abstract class BaseMapDao implements Dao {
    
    protected Datastore db;
    protected Logger log;
    protected DaoManager dm;
    protected MessageBus bus;
    
    public BaseMapDao() {
        log = Logger.getLogger(this.getClass());
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setDaoManager(DaoManager dm) {
        this.dm = dm;
    }
    
    public void setDatastore(Datastore ds) {
        this.db = ds;
    }
    
}
