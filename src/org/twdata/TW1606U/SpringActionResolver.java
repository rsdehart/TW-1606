package org.twdata.TW1606U;

import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.gui.*;
import org.swixml.*;
import org.springframework.beans.factory.*;

/**
 *@created    October 18, 2003
 */
public class SpringActionResolver implements ActionResolver, BeanFactoryAware {
    
    private BeanFactory factory;
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
    }
    
    public Action resolve(Object target, String name) {
        return (Action)factory.getBean("action."+name);
    }
}

