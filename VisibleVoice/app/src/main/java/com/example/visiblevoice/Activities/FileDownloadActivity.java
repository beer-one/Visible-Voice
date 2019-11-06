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
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.visiblevoice.Adapter.CurrentDownloadAdapter;
import com.example.visiblevoice.Client.SFTPClient;
import com.example.visiblevoice.Client.ServerInfo;
import com.example.visiblevoice.CustomFirebaseMessagingService;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.CurrentDownload;
import com.example.visiblevoice.R;
import com.example.visiblevoice.db.AppDatabase;
import com.example.visiblevoice.db.CurrentDownloadDAO;
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
import java.util.List;

public class FileDownloadActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences userData;
    private SharedPreferences fileData;
    private ImageButton fileDownloadBtn;
    private ProgressDialog progressDialog;
    private final int BUFSIZE = 4096;
    private byte[] buffer = new byte[BUFSIZE];
    private RecordDAO recordDAO;
    private CurrentDownloadDAO currentDownloadDAO;
    private String username;
    private ListView fileListView;
    private CurrentDownloadAdapter currentDownloadAdapter;
    private ArrayList<CurrentDownload> currentDownloadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        userData = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);
        fileData = getSharedPreferences(AppDataInfo.File.key,AppCompatActivity.MODE_PRIVATE);
        fileDownloadBtn = findViewById(R.id.file_download_btn);
        progressDialog = new ProgressDialog(this);
        fileListView = (ListView)findViewById(R.id.musicListListView);
        String jsonFileName = fileData.getString(AppDataInfo.File.json,null);
        String fileName = jsonFileName.substring(0,jsonFileName.length()-5);
        fileDownloadBtn.setOnClickListener(this);
        username = userData.getString(AppDataInfo.Login.userID, null);
        currentDownloadDAO = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"db-record" )
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getCurrentDownloadDAO();
        //recordDAO.clearRecordTable();

        //currentDownloadDAO.clearRecordTable();
        List<com.example.visiblevoice.models.CurrentDownload> recordList= currentDownloadDAO.getRecords();
        currentDownloadList = new ArrayList<CurrentDownload>();
        for(com.example.visiblevoice.models.CurrentDownload currentDownload : recordList){
            currentDownloadList.add(new CurrentDownload(currentDownload.getFileName(),currentDownload.getAudioPath(),currentDownload.getJsonPath(),currentDownload.getWordCloudPath()));
        }
        currentDownloadAdapter = new CurrentDownloadAdapter(this,currentDownloadList);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.item_filename);
                Log.d("CHKBOX","clicked");
                if(currentDownloadList.get(position).getChecked()){
                    Log.d("CHKBOX","if");
                    currentDownloadList.get(position).setchecked(false);
                    tv.setBackgroundColor(0xFFFFFFFF);
                }
                else{
                    Log.d("CHKBOX","else");
                    currentDownloadList.get(position).setchecked(true);
                    tv.setBackgroundColor(0x802196F3);
                }
                currentDownloadAdapter.notifyDataSetChanged();

            }
        });
        fileListView.setAdapter(currentDownloadAdapter);
    }


    //TODO 클릭시 리스트에 선택된 항목 다 다운바들수 있도록
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.file_download_btn:
               // Log.d("filedownloadtest", booleanArray.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("음성파일 분석 완료").setMessage("선택된 파일들의 분석된 정보를 적용하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                        try{

                            new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                        //Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
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

            Log.d("filedownloadtest", "doinbackground ");
            recordDAO = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"db-record" )
                    .allowMainThreadQueries()   //Allows room to do operation on main thread
                    .build()
                    .getRecordDAO();
           // Log.d("filedownloadtest", booleanArray.toString());
            for(int i=0;i<currentDownloadList.size();i++){
                Log.d("filedownloadtest", i+" : "+currentDownloadList.get(i).ischecked());
                if(currentDownloadList.get(i).ischecked()){
                    receiveData(currentDownloadList.get(i).getJson_path());
                    Log.d("filedownloadtest", i+" : "+currentDownloadList.get(i).getJson_path());
                    //Log.d("dong", "png 다운로드 : "+pngtextView.getText().toString());
                    receiveData(currentDownloadList.get(i).getPng_path());
                    Log.d("filedownloadtest", i+" : "+currentDownloadList.get(i).getPng_path());
                    receiveData(currentDownloadList.get(i).getMusic_path());
                    Log.d("filedownloadtest", i+" : "+currentDownloadList.get(i).getMusic_path());

                    Record record = new Record();
                    String jsonFileName = currentDownloadDAO.getRecordJsonFileName(currentDownloadList.get(i).getFile_name());
                    String pngFileName = currentDownloadDAO.getRecordPngFileName(currentDownloadList.get(i).getFile_name());
                    String fileName = currentDownloadList.get(i).getFile_name();
                    String musicFileName = currentDownloadDAO.getRecordMusicFileName(currentDownloadList.get(i).getFile_name());

                    Log.d("filedownloadlist",fileName+"삭제");
                    record.setFileName(fileName);
                    record.setAudioPath(musicFileName);
                    record.setWordCloudPath(pngFileName);
                    record.setJsonPath(jsonFileName);
                    if(recordDAO.findFileName(fileName) == null)
                        recordDAO.insert(record);


                    currentDownloadDAO.deleteRecord(currentDownloadList.get(i).getFile_name());
                }
            }

            //update


            progressDialog.dismiss();
            Log.d("dong", "progressDialog 출력완료");
            //Toast.makeText(getApplicationContext(),"파일 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            Log.d("dong", "토스트 출력 완료");
            //Intent intent = new Intent(FileDownloadActivity.this, FileListActivity.class);
            //startActivity(intent);
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
        final int pos = filename.lastIndexOf("/");


       // final SFTPClient sftpclient = new SFTPClient();

               Thread downloadThread =new Thread(new Runnable() {
                    @Override
                    public void run() {



                                boolean status = false;
                                Log.d("dong", "receive ");
                                //VVpath = rootPath + "/"+"VisibleVoice";
                                SFTPClient sftpClient = new SFTPClient();


                                sftpClient.init(ServerInfo.host,ServerInfo.username,ServerInfo.port,getFilesDir().getAbsolutePath() + "/"+ServerInfo.privatekey);

                                ArrayList<Byte> byteArray = sftpClient.download(ServerInfo.folderPath+"/"+username,filename.substring(pos+1));
                                Log.d("Download", ServerInfo.folderPath+"/"+username + "/" + filename.substring(pos+1));
                                byte[] bytes = new byte[byteArray.size()];
                                for(int i = 0; i < bytes.length; i++)
                                    bytes[i] = byteArray.get(i);
                                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                                Log.d("download log","inputstream : "+in);
                                //httpConn.requestWebServer(username,file.getName(), callback);
                                File newFile = new File(getFilesDir().getAbsolutePath() + "/" + username + "/" + filename.substring(pos+1));

                                Log.d("download", newFile.getAbsolutePath());
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
