package org.twdata.TW1606.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.apache.log4j.Logger;

/**
 *  Recursively list files, and make a zip file.
 *
 */
public class ZipBackup implements Backup{

    private static final Logger log = Logger.getLogger(ZipBackup.class);


    public void backupFiles(File rootDir, String[] fileNames, String zipName) 
        throws IOException {

        ArrayList files = new ArrayList();

        Stack pathStack = new Stack();
        File file;

        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        String path;

        for (int x = 0; x < fileNames.length; x++) {
            file = new File(rootDir, fileNames[x]);
            pathStack = new Stack();
            if (file.exists()) {
                pathStack.push(fileNames[x]);
                scanFile(file, pathStack, files);
            }
            else {
                log.info("File "+file.getPath()+" not found");
            }
        }

        if (files.size() > 0) {
            fos = new FileOutputStream(new File(rootDir, zipName));
            zos = new ZipOutputStream(fos);
    
            for (Iterator i = files.iterator(); i.hasNext(); ) {
                path = (String) i.next();
                file = new File(rootDir, path);
                addFile(path, file, zos);
            }
            zos.flush();
            zos.close();
            fos.close();
        }
    }


    public void restore(File rootDir, InputStream ins) throws IOException {
        InputStream in = new BufferedInputStream(ins);
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;

        File file;
        boolean skip = false;
        while ((e = zin.getNextEntry()) != null) {
            file = new File(rootDir, e.getName());
            skip = false;
            if (e.getName().endsWith("/")) {
                if (log.isDebugEnabled()) {
                    log.debug("trying to create:"+file.getPath());
                }
                file.mkdirs();
                skip = true;
            } else if (!file.getParentFile().exists()) {
                if (log.isDebugEnabled()) {
                    log.debug("trying to create parent:"+file.getParentFile().getPath());
                }
                file.getParentFile().mkdirs();
            }
            
            if (!skip) {
                unzip(zin, new File(rootDir, e.getName()));
            }
        }
        zin.close();
    }


    protected void scanFile(File file, Stack pathStack, List files)
        throws IOException {
        if (file.isDirectory()) {
            String[] pathList = file.list();
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                pathStack.push(pathList[i]);
                scanFile(fileList[i], pathStack, files);
            }
        }
        else {
            StringBuffer sb = new StringBuffer();
            String path;
            for (Iterator i = pathStack.iterator(); i.hasNext(); ) {
                path = (String) i.next();
                sb.append(path);
                if (i.hasNext()) {
                    sb.append("/");
                }
            }
            files.add(sb.toString().replace(File.separatorChar, '/'));
        }
        pathStack.pop();
    }


    protected void addFile(String path, File file, ZipOutputStream zos)
        throws IOException {

        // Create a file input stream and a buffered input stream.
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        // Create a Zip Entry and put it into the archive (no data yet).
        ZipEntry fileEntry = new ZipEntry(path);
        zos.putNextEntry(fileEntry);

        // Create a byte array object named data and declare byte count variable.
        byte[] data = new byte[1024];
        int byteCount;
        // Create a loop that reads from the buffered input stream and writes
        // to the zip output stream until the bis has been entirely read.
        while ((byteCount = bis.read(data, 0, 1024)) > -1) {
            zos.write(data, 0, byteCount);
        }
    }


    protected void unzip(ZipInputStream zin, File file)
        throws IOException {
        
        FileOutputStream out = new FileOutputStream(file);
        byte[] b = new byte[512];
        int len = 0;
        while ((len = zin.read(b)) != -1) {
            out.write(b, 0, len);
        }
        out.close();
    }

}

