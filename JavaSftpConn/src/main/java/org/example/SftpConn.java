package org.example;

import com.jcraft.jsch.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SftpConn {
    private final String hostname;
    private final String username;
    private final String password;

    public ArrayList<String> readConfigFile() {
        Config config = ConfigFactory.load("configs/sftp_application.conf");
        String hostname = config.getString("sftp.credentials.hostname");
        String username = config.getString("sftp.credentials.username");
        String password = config.getString("sftp.credentials.password");
        return new ArrayList<String>(Arrays.asList(hostname, username, password));
    }

    public SftpConn() {
        ArrayList<String> credentials = this.readConfigFile();
        this.hostname = credentials.get(0);
        this.username = credentials.get(1);
        this.password = credentials.get(2);
    }

    public SftpConn(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }


    public ChannelSftp setupSftp() throws JSchException {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        JSch jschConn = new JSch();
        Session jschSession = jschConn.getSession(this.username, this.hostname);
        jschSession.setPassword(this.password);
        jschSession.setConfig(config);
        jschSession.connect();
        ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
        return channelSftp;
    }

    public void uploadSftpFile(String fromPath, String toPath) throws JSchException, SftpException {
        ChannelSftp channelSftp = this.setupSftp();
        channelSftp.connect();
        channelSftp.put(fromPath, toPath);
        channelSftp.exit();
    }

    public void downloadSftpFile(String fromPath, String toPath) throws JSchException, SftpException {
        ChannelSftp channelSftp = this.setupSftp();
        channelSftp.connect();
        channelSftp.get(fromPath, toPath);
        channelSftp.exit();
    }

    public void renameSftpFile(String oldPath, String newPath) throws JSchException, SftpException {
        ChannelSftp channelSftp = this.setupSftp();
        channelSftp.connect();
        channelSftp.rename(oldPath, newPath);
        channelSftp.exit();
    }

    public ArrayList<String> listSftpFiles(String path) throws JSchException, SftpException {
        ChannelSftp channelSftp = this.setupSftp();
        channelSftp.connect();
        Vector fileList = channelSftp.ls(path);
        ArrayList<String> files = new ArrayList<String>();

        for (int i = 0; i < fileList.size(); i++) {
            ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) fileList.get(i);
            files.add(lsEntry.getFilename());
        }
        channelSftp.exit();
        return files;
    }

    public static void main(String[] args) {
        Config config = ConfigFactory.load("configs/sftp_application.conf");
        System.out.println(config.getString("sftp.credentials.username"));

    }
}
