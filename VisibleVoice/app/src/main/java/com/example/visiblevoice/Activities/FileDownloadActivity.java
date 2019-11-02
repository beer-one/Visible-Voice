package com.example.visiblevoice.Activities;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.visiblevoice.Client.SFTPClient;
import com.example.visiblevoice.Client.ServerInfo;
import com.example.visiblevoice.CustomFirebaseMessagingService;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.R;
import com.example.visiblevoice.db.AppDatabase;
import com.example.visiblevoice.db.RecordDAO;
import com.example.visiblevoice.models.Record;

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
    private ImageButton fileDownloadBtn;
    private ProgressDialog progressDialog;
    private final int BUFSIZE = 4096;
    private byte[] buffer = new byte[BUFSIZE];
    private RecordDAO recordDAO;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("파일 다운로드").setMessage("변환된 파일을 다운로드 하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                        try{
                            new BackgroundTask().execute();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                break;

        }
    }
    //새로운 TASK정의 (AsyncTask)
    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형을 뜻한다.(내가 사용할 매개변수타입을 설정하면된다)
    class BackgroundTask extends AsyncTask<Void , Void , Void> {


        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
        protected void onPreExecute() {
            progressDialog.setMessage("다운로드중입니다.. 기다려 주세요...");
            progressDialog.show();
        }

        //스레드의 주작업 구현
        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("dong", "json 다운로드 : "+jsontextView.getText().toString());
            receiveData(jsontextView.getText().toString());

            Log.d("dong", "png 다운로드 : "+pngtextView.getText().toString());
            receiveData(pngtextView.getText().toString());
            //update


            try {

                Thread.sleep(4000); //5초 대기

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
            recordDAO = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"db-record" )
                    .allowMainThreadQueries()   //Allows room to do operation on main thread
                    .build()
                    .getRecordDAO();

            Record record = new Record();
            String jsonFileName = jsontextView.getText().toString();
            String fileName = jsonFileName.substring(0,jsonFileName.length()-5);
            Log.d("dong",fileName);
            //record.setAudioPath();
            Log.d("file저장","png 넣을때 : "+AppDataInfo.Path.VisibleVoiceFolder + "/"+pngtextView.getText().toString());
            recordDAO.updateWCPath(fileName,AppDataInfo.Path.VisibleVoiceFolder + "/"+pngtextView.getText().toString());
            recordDAO.updateJSONPath(fileName,AppDataInfo.Path.VisibleVoiceFolder + "/"+jsontextView.getText().toString());

            progressDialog.dismiss();
            Log.d("dong", "progressDialog 출력완료");
            //Toast.makeText(getApplicationContext(),"파일 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            Log.d("dong", "토스트 출력 완료");
            Intent intent = new Intent(FileDownloadActivity.this, FileListActivity.class);
            startActivity(intent);
            finish();
            return null;
        }

        //Task가 취소되었을때 호출
        protected void onCancelled() {
            Log.d("dong", "취소됨");
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
            //Toast.makeText(FileDownloadActivity.this, "파일다운로드 완료", Toast.LENGTH_SHORT).show();
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
