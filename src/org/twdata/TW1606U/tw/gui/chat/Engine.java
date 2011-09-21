package org.twdata.TW1606U.tw.gui.chat;

import org.swixml.SwingEngine;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.*;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.*;
import java.util.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.data.DaoManager;
import org.apache.log4j.Logger;
import java.io.IOException;

/**
 *@created    October 18, 2003
 */
public abstract class Engine {

    protected Printer printer;
    protected StreamFilter filter;
    protected Logger log = Logger.getLogger(getClass());
    
    public void setPrinter(Printer printer) {
        log.debug("setting printer");
        this.printer = printer;
    }
    
    public void setStreamFilter(StreamFilter filter) {
        this.filter = filter;
    }
    
    public void handleTyped(String txt) throws IOException {}
    
    public void setType(String type) {}
}

