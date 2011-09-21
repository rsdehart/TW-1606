package org.twdata.TW1606U;

import java.io.*;
import org.twdata.TW1606U.signal.*;
import org.apache.log4j.Logger;
import java.net.SocketException;


/**
 *  Description of the Class
 *
 *@created    October 19, 2003
 */
public class StreamReader implements StreamFilter, Runnable {

    /**
     *  Description of the Field
     */
    protected StreamFilter source;
    private MessageBus bus;
    private Thread reader = null;
    private static final Logger log = Logger.getLogger(StreamReader.class);
    
    public StreamReader() {
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void channel(OnlineStatusSignal signal) {
        String command = signal.getCommand();
        if ("online".equals(command)) {
            if (reader == null) {
                reader = new Thread(this);
                reader.start();
            }
        } else {
            if (reader != null) {
                reader = null;
            }
        }
    }

    /**
     *  Sets the filterSource attribute of the StreamReader object
     *
     *@param  source  The new filterSource value
     */
    public void setFilterSource(StreamFilter source) {
        this.source = source;
    }


    /**
     *  Gets the filterSource attribute of the StreamReader object
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
        return source.read(b);
    }


    /**
     *  Description of the Method
     *
     *@param  b                Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void write(byte[] b) throws IOException {
        source.write(b);
    }
    
    public void write(String s) throws IOException {
        write(s.getBytes("Cp1252"));   
    }


    /**
     *  Continuously read from our back end and display the data on screen.
     */
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Reading thread started...");
        }
        byte[] b = new byte[256];
        int n = 0;
        while (n >= 0) {
            try {
                
                n = read(b);
                if (log.isDebugEnabled() && n > 0) {
                    log.debug("Read: "+new String(b, 0, n));
                }
                //if (n > 0) {
                //    System.err.println("Terminal: \"" + (new String(b, 0, n, encoding)) + "\"");
                //}
                //if (n > 0) {
                //    write(b);
                //}
            } catch (SocketException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Caught socket exception, probably disconnecting", e);
                }    
                reader = null;
                break;
            } catch (IOException e) {
                reader = null;
                break;
            } catch (Throwable t) {
                log.warn("Caught unknown exception reading stream", t);
                reader = null;
                break;
            }
        }
    }
}

