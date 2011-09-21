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
public class TestEngine extends Engine {

    public void handleTyped(String txt) throws IOException {
        printer.print(txt+"\n");
    }
}

