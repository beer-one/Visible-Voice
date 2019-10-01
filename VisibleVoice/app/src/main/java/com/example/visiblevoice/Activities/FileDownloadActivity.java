package com.example.visiblevoice.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.visiblevoice.Client.SFTPClient;
import com.example.visiblevoice.Client.ServerInfo;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.WriteAbortedException;
import java.util.ArrayList;

public class FileDownloadActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences userData;
    private SharedPreferences fileData;
    private TextView jsontextView;
    private TextView pngtextView;
    private Button fileDownloadBtn;
    private ProgressDialog progressDialog;
    private final int BUFSIZE = 4096;
    private byte[] buffer = new byte[BUFSIZE];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        userData = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);
        fileData = getSharedPreferences(AppDataInfo.File.key,AppCompatActivity.MODE_PRIVATE);
        jsontextView =findViewById(R.id.jsonTextView);
        jsontextView.setText(fileData.getString(AppDataInfo.File.json,null));
        pngtextView = findViewById(R.id.pngTextView);
        pngtextView.setText(fileData.getString(AppDataInfo.File.png,null));
        fileDownloadBtn = findViewById(R.id.file_download_btn);
        progressDialog = new ProgressDialog(this);

        fileDownloadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.file_download_btn:
                Log.d("dong", "json 다운로드 : "+jsontextView.getText().toString());
                receiveData(jsontextView.getText().toString());
                //Log.d("dong", "png 다운로드 : "+pngtextView.getText().toString());
                //receiveData(pngtextView.getText().toString());


                break;
        }
    }
    /** 웹 서버에서 데이터 다운로드 */
    private void receiveData(final String filename) {

        final SFTPClient sftpclient = new SFTPClient();

               Thread downloadThread =new Thread(new Runnable() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("파일다운로드 중 입니다.. 기다려 주세요...");
                                progressDialog.show();
                                boolean status = false;
                                Log.d("dong", "receive ");
                                String username = userData.getString(AppDataInfo.Login.userID, null);
                                //VVpath = rootPath + "/"+"VisibleVoice";
                                SFTPClient sftpClient = new SFTPClient();


                                sftpClient.init(ServerInfo.host,ServerInfo.username,ServerInfo.port,AppDataInfo.Path.VisibleVoiceFolder+"/"+ServerInfo.privatekey);

                                ArrayList<Byte> byteArray = sftpClient.download(ServerInfo.folderPath+"/"+username,filename,AppDataInfo.Path.VisibleVoiceFolder);
                                byte[] bytes = new byte[byteArray.size()];
                                for(int i = 0; i < bytes.length; i++)
                                    bytes[i] = byteArray.get(i);
                                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                                Log.d("download log","inputstream : "+in);
                                //httpConn.requestWebServer(username,file.getName(), callback);
                                File newFile = new File(AppDataInfo.Path.VisibleVoiceFolder + "/" + filename);

                                if (!newFile.exists()){
                                    try {
                                        newFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }


                                try {
                                    FileOutputStream os = null;
                                    os = new FileOutputStream(newFile);
                                    Log.d("download log","os : "+os);
                                    int readCount;
                                    while ((readCount = in.read(buffer)) > 0) {
                                        Log.d("download log","readCount : "+readCount);
                                        os.write(buffer, 0, readCount);
                                    }
                                    in.close();
                                    os.close();
                                } catch (OptionalDataException ode){
                                    ode.printStackTrace();
                                }
                                catch (StreamCorruptedException sce){
                                    sce.printStackTrace();
                                }
                                catch (WriteAbortedException wae){
                                    wae.printStackTrace();
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
            }
        });

        try {
            downloadThread.start();
            downloadThread.join();
            Log.d("join test","레드벨벳");
            if(progressDialog!=null){
                progressDialog.dismiss();
                finish();
            }

            Log.d("join test","아이린");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*ew Thread() {e
            public void run() {
                httpConn.requestWebServer(file, callback);
            }
        }.start();*/
    }
}
