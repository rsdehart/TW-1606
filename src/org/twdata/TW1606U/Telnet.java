/*
 *  This file is part of "The Java Telnet Application".
 *
 *  (c) Matthias L. Jugel, Marcus Meiner 1996-2002. All Rights Reserved.
 *
 *  Please visit http://javatelnet.org/ for updates and contact.
 *
 *  --LICENSE NOTICE--
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  --LICENSE NOTICE--
 *
 */
package org.twdata.TW1606U;

import de.mud.telnet.TelnetProtocolHandler;

import java.awt.Dimension;
import org.twdata.TW1606U.signal.*;
import org.apache.log4j.Logger;
import org.twdata.TW1606U.gui.Terminal;

import java.io.IOException;

/**
 *  The telnet plugin utilizes a telnet protocol handler to filter telnet
 *  negotiation requests from the data stream. <P>
 *
 *  <B>Maintainer:</B> Matthias L. Jugel
 *
 *@author     Matthias L. Jugel, Marcus Meiner
 *@created    October 19, 2003
 *@version    $Id: Telnet.java,v 1.6 2004/09/20 06:11:15 mrdon Exp $
 */
public class Telnet implements StreamFilter{

    private static final Logger log = Logger.getLogger(Telnet.class);
    
    /**
     *  Description of the Field
     */
    protected StreamFilter source;
    /**
     *  Description of the Field
     */
    protected TelnetProtocolHandler handler;
    
    protected Terminal terminal;

    protected MessageBus bus;

    /**
     *  Create a new telnet plugin.
     */
    public Telnet() {
        // create a new telnet protocol handler

        /*
         *  bus.registerPluginListener(new ConfigurationListener() {
         *  public void setConfiguration(PluginConfig config) {
         *  configure(config);
         *  }
         *  });
         */
         handler =  new TelnetProtocolHandler() {
                public String getTerminalType() {
                    return "ANSI";
                }


                public Dimension getWindowSize() {
                    return new Dimension(80,25);
                }


                /**
                 *  notify about local echo
                 *
                 *@param  echo  The new localEcho value
                 */
                public void setLocalEcho(boolean echo) {
                }


                /**
                 *  notify about EOR end of record
                 */
                public void notifyEndOfRecord() {}


                /**
                 *  write data to our back end
                 *
                 *@param  b                Description of the Parameter
                 *@exception  IOException  Description of the Exception
                 */
                public void write(byte[] b) throws IOException {
                    source.write(b);
                }
            };
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
        handler =
            new TelnetProtocolHandler() {
                /**
                 *  get the current terminal type
                 *
                 *@return    The terminalType value
                 */
                public String getTerminalType() {
                    return Telnet.this.terminal.getTerminalType();
                }


                /**
                 *  get the current window size
                 *
                 *@return    The windowSize value
                 */
                public Dimension getWindowSize() {
                    return Telnet.this.terminal.getWindowSize();
                }


                /**
                 *  notify about local echo
                 *
                 *@param  echo  The new localEcho value
                 */
                public void setLocalEcho(boolean echo) {
                    Telnet.this.terminal.setLocalEcho(echo);
                }


                /**
                 *  notify about EOR end of record
                 */
                public void notifyEndOfRecord() {
                    // bus.broadcast(new EndOfRecordRequest());
                }


                /**
                 *  write data to our back end
                 *
                 *@param  b                Description of the Parameter
                 *@exception  IOException  Description of the Exception
                 */
                public void write(byte[] b) throws IOException {
                    source.write(b);
                }
            };
    }


    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(OnlineStatusSignal signal) {
        String command = signal.getCommand();
        if (log.isDebugEnabled()) {
            log.debug("telnet caught: "+command);
        }
        if ("online".equals(command)) {
            handler.reset();
            try {
                handler.startup();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            handler.reset();
            // bus.broadcast(new LocalEchoRequest(true));
        }
        if (log.isDebugEnabled()) {
            log.debug("leaving channel");
        }
    }


    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(TelnetCommandSignal signal) {
        try {
            handler.sendTelnetControl(signal.getCommand());
        } catch (IOException ex) {
            // TODO: handle exception
        }
    }


    /*
     *  public void configure(PluginConfig cfg) {
     *  String crlf = cfg.getProperty("Telnet",id,"crlf");	// on \n
     *  if (crlf != null) handler.setCRLF(crlf);
     *  String cr = cfg.getProperty("Telnet",id,"cr");	// on \r
     *  if (cr != null) handler.setCR(cr);
     *  }
     */
    /**
     *  Sets the filterSource attribute of the Telnet object
     *
     *@param  source  The new filterSource value
     */
    public void setFilterSource(StreamFilter source) {
        this.source = source;
    }


    /**
     *  Gets the filterSource attribute of the Telnet object
     *
     *@return    The filterSource value
     */
    public StreamFilter getFilterSource() {
        return source;
    }


    /**
     *  Description of the Method
     *
     *@param  b                Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public int read(byte[] b) throws IOException {
        // We just don't pass read() down, since negotiate() might call other
        // functions and we need transaction points.
        int n;

        n = handler.negotiate(b);
        // we still have stuff buffered ...
        if (n > 0) {
            return n;
        }

        while (true) {
            n = source.read(b);
            if (n <= 0) {
                return n;
            }

            handler.inputfeed(b, n);
            n = 0;
            while (true) {
                n = handler.negotiate(b);
                if (n > 0) {
                    return n;
                }
                if (n == -1) {
                    // buffer empty.
                    break;
                }
            }
            return 0;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  b                Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void write(byte[] b) throws IOException {
        handler.transpose(b);
        // transpose 0xff or \n and send data
    }
}

