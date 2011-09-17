package org.twdata.TW1606;

import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.gui.*;
import org.swixml.*;
import org.springframework.beans.factory.*;
import org.apache.log4j.Logger;

public class DefaultActionResolver implements ActionResolver {
    
    private ActionManager manager = null;
    private static final Logger log = Logger.getLogger(DefaultActionResolver.class);
    
    public void setActionManager(ActionManager manager) {
        this.manager = manager;
    }
    
    public Action resolve(Object target, String name) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving action:"+name);
        }
        return manager.getSwingAction(name);
    }
}

