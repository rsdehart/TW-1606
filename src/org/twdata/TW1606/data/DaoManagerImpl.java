package org.twdata.TW1606.data;

import org.springframework.beans.factory.*;


public class DaoManagerImpl implements DaoManager, BeanFactoryAware {

    private BeanFactory factory;
    
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
    }
    
    public Dao getDao(String name) {
        String id = name + "Dao";
        return (Dao) factory.getBean(id);
    }
    
}
