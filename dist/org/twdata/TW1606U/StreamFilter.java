package org.twdata.TW1606U;

import java.io.IOException;

/**
 *  The stream filter is the base interface for components that want to
 *  intercept the communication between front end and back end components.
 *  Filters and protocol handlers are a good example.
 *
 *@created    October 19, 2003
 */
public interface StreamFilter {

    /**
     *  Set the source plugin where we get our data from and where the data sink
     *  (write) is. The actual data handling should be done in the read() and
     *  write() methods.
     *
     *@param  source                        the data source
     *@exception  IllegalArgumentException  Description of the Exception
     */
    public void setFilterSource(StreamFilter source)
             throws IllegalArgumentException;


    /**
     *  Gets the filterSource attribute of the StreamFilter object
     *
     *@return    The filterSource value
     */
    public StreamFilter getFilterSource();


    /**
     *  Read a block of data from the back end.
     *
     *@param  b                the buffer to read the data into
     *@return                  the amount of bytes actually read
     *@exception  IOException  Description of the Exception
     */
    public int read(byte[] b)
             throws IOException;


    /**
     *  Write a block of data to the back end.
     *
     *@param  b                the buffer to be sent
     *@exception  IOException  Description of the Exception
     */
    public void write(byte[] b)
             throws IOException;
             
}

