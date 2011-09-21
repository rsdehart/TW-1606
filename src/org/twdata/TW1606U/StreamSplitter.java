/*
 *  This file is part of "The Java Telnet Application".
 *
 *  This is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  "The Java Telnet Application" is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this software; see the file COPYING.  If not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *  Boston, MA 02111-1307, USA.
 */
package org.twdata.TW1606U;

import de.mud.telnet.ScriptHandler;

import java.util.*;

import java.io.*;
import java.net.URL;

import javax.swing.*;
import java.awt.event.*;

import java.lang.reflect.Method;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.gui.*;
import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 *@created    October 25, 2003
 */
public class StreamSplitter implements StreamFilter {

    private static final Logger log = Logger.getLogger(StreamSplitter.class);

    private StreamListener[] rawStreamListeners = null;
    private StreamListener[] noAnsiStreamListeners = null;
    private StreamFilter source;
    
    /**
     *  Constructor for the StreamSplitter object
     *
     *@param  bus  Description of the Parameter
     *@param  id   Description of the Parameter
     */
    public StreamSplitter() {
    }
    
    /**
     *  Notifies stream listeners of write
     */
    public void notifyStreamListeners(byte[] buffer) {
        notifyStreamListeners(buffer, 0, true);
    }
    
    public void notifyStreamListeners(byte[] buffer, int len, boolean isWrite) {
        if (log.isDebugEnabled()) {
            log.debug("sending "+new String(buffer));
        }
        if (rawStreamListeners != null) {
            for (int x=0; x < rawStreamListeners.length; x++) {
                if (isWrite) {
                    rawStreamListeners[x].hasWritten(buffer);
                } else {
                    rawStreamListeners[x].hasRead(buffer, len);
                }
            }
        }
        
        if (noAnsiStreamListeners != null && noAnsiStreamListeners.length > 0) {
            byte c[] = new byte[buffer.length];
            int result = stripAnsi(buffer, c, len);
            for (int x=0; x < noAnsiStreamListeners.length; x++) {
                if (isWrite) {
                    noAnsiStreamListeners[x].hasWritten(c);
                } else {
                    noAnsiStreamListeners[x].hasRead(c, result);
                }
            }
        }
    }
    
    public void addStreamListener(StreamListener listener, boolean filtered) {
        List list = null;
        synchronized(this) {
            if (filtered) {
                if (noAnsiStreamListeners == null) {
                    list = new ArrayList();
                } else {
                    list = new ArrayList(Arrays.asList(noAnsiStreamListeners));
                }
                list.add(listener);
                noAnsiStreamListeners = (StreamListener[]) list.toArray(new StreamListener[]{});
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("adding raw listener");
                }
                if (rawStreamListeners == null) {
                    list = new ArrayList();
                } else {
                    list = new ArrayList(Arrays.asList(rawStreamListeners));
                }
                list.add(listener);
                rawStreamListeners = (StreamListener[]) list.toArray(new StreamListener[]{});
            }
        }
    }
    
    public void removeStreamListener(StreamListener listener, boolean filtered) {
        List list = null;
        synchronized(this) {
            if (filtered) {
                if (noAnsiStreamListeners == null) {
                    list = new ArrayList();
                } else {
                    list = new ArrayList(Arrays.asList(noAnsiStreamListeners));
                }
                list.remove(listener);
                if (list.size() == 0) {
                    noAnsiStreamListeners = null;
                } else {
                    noAnsiStreamListeners = (StreamListener[]) list.toArray(new StreamListener[]{});
                }
            } else {
                if (rawStreamListeners == null) {
                    list = new ArrayList();
                } else {
                    list = new ArrayList(Arrays.asList(rawStreamListeners));
                }
                list.remove(listener);
                if (list.size() == 0) {
                    rawStreamListeners = null;
                } else {
                    rawStreamListeners = (StreamListener[]) list.toArray(new StreamListener[]{});
                }
            }
        }
    }
    
    /**
     *  Sets the filterSource attribute of the StreamSplitter object
     *
     *@param  source  The new filterSource value
     */
    public void setFilterSource(StreamFilter source) {
        this.source = source;
    }
    
    /**
     *  Gets the filterSource attribute of the Terminal object
     *
     *@return    The filterSource value
     */
    public StreamFilter getFilterSource() {
        return source;
    }


    /**
     *  Description of the Method
     *
     *@param  b  Description of the Parameter
     *@return    Description of the Return Value
     */
    public int read(byte[] b) throws IOException {
        int result = -1;

        result = source.read(b);

        if (result > -1) {
            notifyStreamListeners(b, result, false);
        }

        return result;
    }


    int NORMAL = 0, ESCAPE = 1, ESCAPE2 = 2, ESCAPE_STRING = 3;
    int ansiState = NORMAL;

    //hand made lexer to strip out pesky ansi escape codes

    //we don't want the overhead of another lexer, and we can't do it in the full lex
    /**
     *  Description of the Method
     *
     *@param  b       Description of the Parameter
     *@param  c       Description of the Parameter
     *@param  amount  Description of the Parameter
     *@return         Description of the Return Value
     */
    public int stripAnsi(byte[] b, byte[] c, int amount) {
        int counter;
        int rCounter;
        int numbytes;

        numbytes = c.length;
        rCounter = 0;
        for (counter = 0; counter < amount; counter++) {
            char current = (char) b[counter];
            switch (ansiState) {
                case 0:
                    //NORMAL:
                    if (current != 27 && current != 0) {
                        //get rid of those pesky nulls

                        c[rCounter] = b[counter];
                        rCounter++;
                    } else if (current == 27) {
                        ansiState = ESCAPE;
                    }
                    break;
                case 1:
                    //ESCAPE:
                    if (current == '[' || Character.isDigit(current)) {
                        ansiState = ESCAPE2;
                    } else if (current == '\"') {
                        ansiState = ESCAPE_STRING;

                    }
                    break;
                case 2:
                    //ESCAPE2
                    if (Character.isLetter(current)) {
                        ansiState = NORMAL;
                    } else if (current == '[' || Character.isDigit(current)) {
                        ansiState = ESCAPE2;
                    } else {
                        ansiState = ESCAPE;
                    }
                    break;
                case 3:
                    //ESCAPE_STRING:
                    if (current == '\"') {
                        ansiState = ESCAPE;
                    }
                    break;
            }
        }

        return rCounter;
    }


    /**
     *  Description of the Method
     *
     *@param  b  Description of the Parameter
     */
    public void write(byte[] b) throws IOException {
        source.write(b);
        notifyStreamListeners(b);
    }
}
