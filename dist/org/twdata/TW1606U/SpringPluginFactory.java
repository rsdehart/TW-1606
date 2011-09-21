package org.twdata.TW1606U;

import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.gui.*;
import org.swixml.DefaultFactory;
import org.springframework.beans.factory.*;

/**
 *@created    October 18, 2003
 */
public class SpringPluginFactory extends DefaultFactory implements BeanFactoryAware {
    
    private BeanFactory factory;
    private String id;
    
    public SpringPluginFactory() {
        super(Object.class);
    }
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
    }
    
    public void setBeanId(String id) {
        this.id = id;
        template = factory.getBean(id).getClass();
        registerSetters();
    }
    
    public Object newInstance() {
        return factory.getBean(id);
    }
    
    public SpringPluginFactory cloneFactory(String id) {
        SpringPluginFactory clone = new SpringPluginFactory();
        clone.setBeanFactory(factory);
        clone.setBeanId(id);
        return clone;
    }
}

