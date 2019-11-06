package com.example.visiblevoice.Client;


import android.util.Log;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPClient {

    private Session session = null;
    private final int BUFSIZE = 4096;
    private final byte[] buffer = new byte[BUFSIZE];
    private ArrayList<Byte> byteArray;


    private InputStream in;
    private Channel channel = null;

    private ChannelSftp channelSftp = null;

    /**
     * 서버와 연결에 필요한 값들을 가져와 초기화 시킴
     *
     * @param host
     *            서버 주소
     * @param userName
     *            접속에 사용될 아이디
     * @param port
     *            포트번호
     * @param privateKey
     *            키 파일이 있는 경로
     */
    public void init(final String host, final String userName, final int port, String privateKey) {

        final JSch jSch = new JSch();
        try {
            Log.d("sftp", privateKey);
            File f = new File(privateKey);
            File pub = new File(privateKey+".pub");
            if(f.isFile() && pub.isFile()) {//키가 존재한다면

                jSch.addIdentity(privateKey, privateKey+".pub", null);
                Log.d("sftp","key가 있다");
            }
            else{
                Log.d("sftp","key가 없다");
            }
            Thread sftpConnectionThread = new Thread() {
                public void run() {
                    try{
                        session = jSch.getSession(userName, host, port);


                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);
                        session.connect();

                        channel = session.openChannel("sftp");
                        channel.connect();
                        channelSftp = (ChannelSftp) channel;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

            sftpConnectionThread.start();
            sftpConnectionThread.join();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }



    public boolean exist(final String dir, final String filename) {
        boolean ret = false;
        try {
            Log.d("sftp", channelSftp.pwd());
            channelSftp.cd(dir);
            Vector v = channelSftp.ls(filename);
            ret = (v != null);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 하나의 폴더를 생성한다.
     *
     * @param dir
     *            이동할 주소
     * @param mkdirName
     *            생상할 폴더명
     */

    public void mkdir(final String dir, final String mkdirName) {

        try {
            Thread mkdirThread = new Thread() {
                public void run() {
                    try{


                        channelSftp.cd(dir);
                        channelSftp.mkdir(mkdirName);
                        Log.d("MKDIRJOOHAN","ls : "+ channelSftp.ls("."));
                        channelSftp.chmod(0777,dir+"/"+mkdirName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            };
            mkdirThread.start();
            mkdirThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 하나의 파일을 업로드 한다.
     *
     * @param dir
     *            저장시킬 주소(서버)
     * @param file
     *            저장할 파일
     */
    public void upload(final String dir, final File file, final boolean temp) {


        Thread uploadThread = new Thread() {
            public void run() {
                FileInputStream[] in = {null};
                try{
                    Log.d("sftp", channelSftp.pwd());
                    in[0] = new FileInputStream(file);
                    String filename = file.getName();
                    if(temp) {
                        int extensionPoint = filename.lastIndexOf(".");
                        String extension = filename.substring(extensionPoint);
                        filename = filename.substring(0, extensionPoint);
                        filename += "__temp" + extension;
                    }



                    Log.d("dong","dir : "+dir);
                    Log.d("dong","file : "+file.getName());

                    if(dir != null) channelSftp.cd(dir);

                    channelSftp.put(in[0], filename);
                    channelSftp.chmod(0757,filename);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        in[0].close();
                    }
                    catch(IOException ie){
                        ie.printStackTrace();
                    }
                }
            }
        };
        try {
            uploadThread.start();
            uploadThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    /**
     * 하나의 파일을 다운로드 한다.
     *
     * @param dir
     *            저장할 경로(서버)
     * @param downloadFileName
     *            다운로드할 파일
     */
    public ArrayList<Byte> download(final String dir, final String downloadFileName) {
        try{
            Thread downloadThread = new Thread() {
                public void run() {
                    try {
                        BufferedInputStream bis;
                        channelSftp.cd(dir);
                        // Download file
                        Log.d("download log","ls : "+channelSftp.ls(dir));
                        in = channelSftp.get(dir+"/"+downloadFileName);
                        Log.d("download log","in : "+in);

                        byteArray = new ArrayList<Byte>();

                        int len;
                        try {
                            while ((len = in.read(buffer)) > 0) {
                                for(int i = 0; i < len; i++)
                                    byteArray.add(buffer[i]);
                            }
                        }
                        catch (IOException ie) {
                            ie.printStackTrace();
                        }

                    } catch (SftpException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            };
            //Log.d("download log","리턴전 in : "+in);
            try {
                downloadThread.start();
                downloadThread.join();
                Log.d("download log","리턴전 in : "+in);
                return byteArray;
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Log.d("download test","에러 출력  : "+ie);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 서버와의 연결을 끊는다.
     */
    public void disconnection() {
        channelSftp.quit();
        session.disconnect();

    }

}