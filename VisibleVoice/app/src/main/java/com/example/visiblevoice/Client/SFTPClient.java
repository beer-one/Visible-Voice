package com.example.visiblevoice.Client;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPClient {

    private Session session = null;

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
            if(privateKey!=null) {//키가 존재한다면
                jSch.addIdentity(privateKey);
                Log.d("sftp","key가 있다");
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
                        Log.d("dong","dir : "+ dir);
                        //channelSftp.chmod(757,dir);
                        channelSftp.cd(dir);
                        channelSftp.mkdir(mkdirName);
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
    public void upload(final String dir, final File file) {


        Thread uploadThread = new Thread() {
            public void run() {
                FileInputStream[] in = {null};
                try{
                    in[0] = new FileInputStream(file);
                    Log.d("dong","dir : "+dir);
                    Log.d("dong","file : "+file.getName());

                    channelSftp.cd(dir);
                    channelSftp.put(in[0], file.getName());
                    channelSftp.chmod(0757,file.getName());
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
     * @param path
     *            저장될 공간
     */
    public void download(String dir, String downloadFileName, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftp.cd(dir);
            in = channelSftp.get(downloadFileName);
        } catch (SftpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            out = new FileOutputStream(new File(path));
            int i;

            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 서버와의 연결을 끊는다.
     */
    public void disconnection() {
        channelSftp.quit();
        session.disconnect();

    }

}