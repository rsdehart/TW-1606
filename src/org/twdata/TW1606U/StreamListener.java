package org.twdata.TW1606U;

import java.io.IOException;

/**
 *  The stream filter is the base interface for components that want to
 *  intercept the communication between front end and back end components.
 *  Filters and protocol handlers are a good example.
 *
 *@created    October 19, 2003
 */
public interface StreamListener {

    /**
     *  Receives the block of data read from the back end.
     *
     *@param  b                the data 
     *@return                  the amount of bytes actually read
     *@exception  IOException  Description of the Exception
     */
    public void hasRead(byte[] b, int len);
             
    /**
     *  Called when data has been written to the stream from a filter
     *
     * @param s The data
     */         
    public void hasWritten(byte[] b);        


}

