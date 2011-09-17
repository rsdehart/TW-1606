package org.twdata.TW1606.util;

import java.io.*;

/**
 *  Recursively list files, and make a zip file.
 *
 */
public interface Backup {


    public void backupFiles(File rootDir, String[] fileNames, String backupName) 
        throws IOException;

    public void restore(File rootDir, InputStream in) throws IOException;

}

