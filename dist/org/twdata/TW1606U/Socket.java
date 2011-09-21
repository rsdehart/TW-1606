package org.twdata.TW1606U;

import org.twdata.TW1606U.signal.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

/**
 *  The socket plugin acts as the data source for networked operations. <P>
 *
 *  <B>Maintainer:</B> Matthias L. Jugel
 *
 * @author     Matthias L. Jugel, Marcus Meiner
 * @version    $Id: Socket.java,v 1.8 2004/08/14 19:38:59 mrdon Exp $
 */
public class Socket implements StreamFilter {

    /**  Description of the Field */
    protected java.net.Socket socket;
    /**  Description of the Field */
    protected InputStream in;
    /**  Description of the Field */
    protected OutputStream out;

    /**  Description of the Field */
    protected String relay = null;
    /**  Description of the Field */
    protected int relayPort = 31415;
    
    private MessageBus bus;

    private static final Logger log = Logger.getLogger(Socket.class);

    /**
     *  Create a new socket plugin.
     */
    public Socket() {
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }

    /**
     *  Connect to the host and port passed. If the multi relayd (mrelayd) is
     *  used to allow connections to any host and the Socket.relay property is
     *  configured this method will connect to the relay first, send off the
     *  string "relay host port\n" and then the real connection will be
     *  published to be online.
     *
     * @param  host             Description of the Parameter
     * @param  port             Description of the Parameter
     * @exception  IOException  If something goes wrong
     */
    public void connect(final String host, final int port) throws IOException {
        if (host == null) {
            return;
        }
        
        socket = new java.net.Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        in = socket.getInputStream();
        //out = new BufferedOutputStream(socket.getOutputStream());
        out = socket.getOutputStream();
        bus.broadcast(new OnlineStatusSignal(OnlineStatusSignal.ONLINE));
    }
    
    public void channel(ShutdownSignal signal) {
        try {
            disconnect(true);
        } catch (IOException ex) {
            log.warn("Unable to disconnect", ex);
        }
    }

    /**
     *  Disconnect the socket and close the connection.
     *
     * @exception  IOException  If something goes wrong
     */
    public void disconnect() throws IOException {
        disconnect(false);
    }
    
    /**
     *  Disconnect the socket and close the connection.
     *
     * @exception  IOException  If something goes wrong
     */
    public void disconnect(boolean inShutdown) throws IOException {
        if (socket != null) {
            socket.close();
            in = null;
            out = null;
            if (!inShutdown) {
                bus.broadcast(new OnlineStatusSignal("offline"));
            }
        }
    }

    /**
     *  Sets the filterSource attribute of the Socket object
     *
     * @param  plugin  The new filterSource value
     */
    public void setFilterSource(StreamFilter plugin) {
        // we do not have a source other than our socket
    }

    /**
     *  Gets the filterSource attribute of the Socket object
     *
     * @return    The filterSource value
     */
    public StreamFilter getFilterSource() {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  b                Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  If something goes wrong
     */
    public int read(byte[] b) throws IOException {

        if (in == null) {
            disconnect();
            return -1;
        }

        int n = in.read(b);
        if (n < 0) {
            disconnect();
        }
        if (n > 0) {
            //log.info("reading:"+n+" bytes");
        }
        return n;
    }

    /**
     *  Description of the Method
     *
     * @param  b                Description of the Parameter
     * @exception  IOException  If something goes wrong
     */
    public void write(byte[] b) throws IOException {
        if (out == null) {
            return;
        }
        try {
            out.write(b);
        }
        catch (IOException e) {
            disconnect();
        }
        //log.info("writing:"+new String(b));
    }
}

