/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.flume.source;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import org.apache.flume.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

import java.io.IOException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.flume.instrumentation.SourceCounter;




/**
 *
 * @author luis lazaro 
 */


public class FTPSourceUtils {
    private FTPClient ftpClient;
    private String server,user,password;
    private Integer port;
    private int runDiscoverDelay;
    private static final Logger log = LoggerFactory.getLogger(FTPSourceUtils.class);
    
    
    public FTPSourceUtils(Context context){
        ftpClient = new FTPClient();
        server = context.getString("name.server");
        user = context.getString("user");
        password = context.getString("password");
        runDiscoverDelay = context.getInteger("run.discover.delay");
        port = context.getInteger("port");
    }
    
    /*
    @return boolean, Opens a Socket connected to a server
    and login to return True if successfully completed, false if not.
    */
    public boolean connectToserver(){
        boolean success = false;
       try {
            ftpClient.connect(server,port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.error("Connect Failed");
            }
            success = ftpClient.login(user, password);
            if (!success) {
                log.error("Could not login to the server");
            }
            } catch (IOException ex) {
                log.error("Exception thrown in connectToserver", ex);
            }
       return success;
    }
    
    /*
    @return FTPClient
    */
    public FTPClient getFtpClient(){
        return ftpClient;
    }
    
    /*
    @return String, name of host to ftp
    */
    public String getServer(){
        return server;
    }
    
    /*
    @return FTPFile[] list of directories in current directory
    */
    public FTPFile[] getDirectories() throws IOException {
            return ftpClient.listDirectories();
    }
    
    /*
    @return FTPFile[] list of files in current directory
    */
    public FTPFile[] getFiles() throws IOException {
            return ftpClient.listFiles();
    }
    
    /*
    @return int delay for thread
    */
    public int getRunDiscoverDelay(){
        return runDiscoverDelay;
    }
    
}

