package org.twdata.TW1606.tw.gui.chat;

import org.swixml.SwingEngine;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.*;
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
import org.twdata.TW1606.tw.signal.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.*;
import org.twdata.TW1606.data.DaoManager;
import org.apache.log4j.Logger;
import java.io.IOException;

/**
 *@created    October 18, 2003
 */
public class TWEngine extends Engine {
    
    private int type;
    
    public TWEngine(int type, MessageBus bus) {
        this.type = type;
        bus.plug(this);
    }
    
    public void handleTyped(String txt) throws IOException {
        StringBuffer sb = new StringBuffer();
        if (type == ChatSignal.FEDCOM) {
            sb.append("`");
        } else if (type == ChatSignal.SUBSPACE) {
            sb.append("'");
        }
        sb.append(txt);
        sb.append("\n");
        filter.write(sb.toString().getBytes("Cp1252"));
    }
    
    public void channel(ChatSignal signal) {
        if (signal.getType() == type) {
            String name = signal.getSender().getName();
            StringBuffer sb = new StringBuffer();
            sb.append(name);
            sb.append("> ");
            sb.append(signal.getMessage());
            sb.append("\n");
            printer.print(sb.toString());
        }
    }
}

