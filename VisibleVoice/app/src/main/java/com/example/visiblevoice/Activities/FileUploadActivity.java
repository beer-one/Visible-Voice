package com.example.visiblevoice.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.visiblevoice.Adapter.UploadFileListAdapter;
import com.example.visiblevoice.Client.HttpConnection;
import com.example.visiblevoice.Controller.MusicListController;
import com.example.visiblevoice.Data.AppDataInfo;
//import com.example.visiblevoice.Data.Record;
import com.example.visiblevoice.Data.FileInfo;
import com.example.visiblevoice.R;
import com.example.visiblevoice.Client.SFTPClient;
import com.example.visiblevoice.Client.ServerInfo;
import com.example.visiblevoice.db.AppDatabase;
import com.example.visiblevoice.db.RecordDAO;
import com.example.visiblevoice.models.Record;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FileUploadActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    private String[] permissionedFormat={".*.mp3",".*.mp4",".*.m4a",".*.flac",".*.wav"};

    private HttpConnection httpConn = HttpConnection.getInstance();


    private ArrayList<String> Files;

    private ArrayList<FileInfo> fileItems;

    private String rootPath = "";
    private String nextPath = "";
    private String prevPath = "";
    private String currentPath = "";
    private String newFolderPath="";

    //private String VVpath = "";//Visible voice path
    //private TextView textView;
    private Button preDirBtn;
    private TextView curDirTextView;
    private ListView listView;

    private UploadFileListAdapter listAdapter;

    private SharedPreferences userData;
    private SharedPreferences fileData;
    private RecordDAO recordDAO;
    private String username;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        // get read external storage permission
        if (ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("song","Permission is not granted");
            ActivityCompat.requestPermissions(FileUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission has already been granted
            Log.d("song","Permission has already been granted");
        }

        //items = new ArrayList<>();
        fileItems = new ArrayList<>();

        listAdapter = new UploadFileListAdapter(fileItems);

        fileData= getSharedPreferences(AppDataInfo.File.key, AppCompatActivity.MODE_PRIVATE);
        userData = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);
        
        username = userData.getString(AppDataInfo.Login.userID, null);
        
    // check sd card is mounted
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("song","cannot use external storage");
            return;
        }

        // get external root directory path
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentPath = rootPath;

        preDirBtn = (Button) findViewById(R.id.preDirBtn);
        curDirTextView = (TextView) findViewById(R.id.curDirTextView);

        setButtonOptions();

        Log.d("song","root path : "+ rootPath);

        // set ListView by file list from root directory
        boolean result = setFileList(rootPath,"");
        if ( result == false ) { // if fail to get list , return
            return;
        }

        listView = (ListView) findViewById(R.id.uploadFileListView);

        // set ListView Adapter by file list
        listView.setAdapter(listAdapter);

        // set listview item's onClick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //currentPath = curDirTextView.getText().toString();
                String path = currentPath + "/" + fileItems.get(position).getFilename();
                Log.d("file", path);

                File fp = new File(path);
                Log.d("dir?", fp.isDirectory() ? "dir" : "not dir");
                if(fp.isDirectory()) {
                    // if selected file is directory
                    Log.d("song","you click directory");
                    nextPath(fileItems.get(position).getFilename()); // move directory
                } else {
                    // if selected file is not directory
                    Log.d("song","you click file");
                    setFileList(path, fileItems.get(position).getFilename());
                    // TO-DO : create code that upload selected file
                }

            }
        });

        preDirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPath();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("song","permission granted");
                } else {
                    Log.d("song","permission denied");
                }
                return;
            }
        }
    }

    public boolean setFileList(final String rootPath, final String fileName)    {
        // create file object
        final File fileRoot = new File(rootPath);
        final MusicListController musicListController = new MusicListController();
        // if rootPath is not directory
        if(fileRoot.isDirectory() == false) {
            Log.d("song",rootPath + " not directory");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("파일 변환").setMessage("선택하신 파일 "+fileName+"을 변환하시겠습니까?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    SendAsyncTask task = new SendAsyncTask();
                    Log.d("filename", "변환 대상 파일: " + rootPath);
                    Log.d("filename", fileRoot.exists() ? "exist" : "not exist");
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileRoot);
                    //Record record = new Record(fileName, fileRoot);
                    //musicListController.addMusic(record);
                    //insert
                    SharedPreferences.Editor file_data = fileData.edit();
                    file_data.putString(AppDataInfo.File.music_path, getFilesDir().getAbsolutePath() + "/" +
                            username + "/" + fileName);
                    file_data.commit();

                    // make file name string
                    String fname=rootPath.replace("/","+")+fileName.replace("\\.","+");
                    Log.d("song","fname : "+fname);


                    // create new folder for uploaded file
                    newFolderPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/visibleVoice/"+fname;
                    File dir = new File(newFolderPath);
                    dir.mkdir();
                    if (!dir.exists()) { // check
                        Log.d("song","fail to create new folder");
                    }else{
                        Log.d("song","success to create new folder");

                        // create new file and write file full path
                        try{
                            byte[] data=rootPath.getBytes();
                            FileOutputStream fos=new FileOutputStream(newFolderPath+"/path.txt");
                            for(int i=0;i<data.length;i++)
                                fos.write(data[i]);
                            fos.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) { }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return false;
        }

        // set root path TextView

        setButtonOptions();

        File[] fileList = fileRoot.listFiles();
        ArrayList<File> filterdFileList = new ArrayList<>();
        // clear item(file) list
        fileItems.clear();
        // set parents directory
        //items.add("..");

        if ( fileList == null ) { // if directory is empty
            Log.d("song","Could not find List");
        }  else { // if directory is not empty
            // set file list
            try {
                for (int i = 0; i < fileList.length; i++) {
                    if(fileList[i].isDirectory()) { // if file is directory
                        fileItems.add(new FileInfo(fileList[i].getName(), fileList[i].isDirectory(), (int) fileList[i].length())); // add file in list
                        filterdFileList.add(fileList[i]);
                    }
                    else {
                        Log.d("name",fileList[i].getName()+">>");

                        for(int j=0;j<permissionedFormat.length;j++){
                            if(fileList[i].getName().matches(permissionedFormat[j])) { // if file is permitted format
                                Log.d("name","add "+permissionedFormat[j]);
                                fileItems.add(new FileInfo(fileList[i].getName(), fileList[i].isDirectory(), (int)fileList[i].length())); // add file in list
                                filterdFileList.add(fileList[i]);
                                break;
                            }
                        }
                    }
                }

                for(int i = 0; i < filterdFileList.size(); i++) {
                    if(filterdFileList.get(i).isDirectory()) {
                        String[] children = filterdFileList.get(i).list();
                        int size = 0;
                        for(int j = 0; j < children.length; j++) {
                            for(int k = 0; k < permissionedFormat.length; k++){
                                if(children[j].matches(permissionedFormat[k])) { // if file is permitted format
                                    size++;
                                    break;
                                }
                            }
                        }
                        fileItems.get(i).setChildren(size);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // update ListView by new item(file) list
        listAdapter.notifyDataSetChanged();
        return true;
    }

    public void setButtonOptions() {
        int lastSlashPosition = currentPath.lastIndexOf("/");
        if(currentPath.equals(this.rootPath)) {
            curDirTextView.setText("파일");
            preDirBtn.setText("이전파일");
            preDirBtn.setClickable(false);
            preDirBtn.setEnabled(false);
            preDirBtn.setTextColor(0xFF555555);
        } else {
            curDirTextView.setText(currentPath.substring(lastSlashPosition+1));
            if(prevPath.equals(this.rootPath))
                preDirBtn.setText("파일");
            else {
                lastSlashPosition = prevPath.lastIndexOf("/");
                preDirBtn.setText(prevPath.substring(lastSlashPosition+1));
            }
            preDirBtn.setClickable(true);
            preDirBtn.setEnabled(true);
            preDirBtn.setTextColor(0xFF0022AA);
        }
    }

    public void nextPath(String str)    {
        // save current path
        prevPath = currentPath;

        // create next directory path
        currentPath = currentPath + "/" + str;
        // set ListView by next directory's files
        setFileList(currentPath,str);
    }

    public void prevPath() {

        int lastSlashPosition = currentPath.lastIndexOf("/");
        String filename = currentPath.substring(lastSlashPosition+1);
        currentPath = currentPath.substring(0, lastSlashPosition);

        lastSlashPosition = prevPath.lastIndexOf("/");
        prevPath = prevPath.substring(0, lastSlashPosition);

        //  set ListView by prev directory's files
        setFileList(currentPath, filename);
    }

    private class SendAsyncTask extends AsyncTask<File , Void , Void> {

        @Override
        protected Void doInBackground(File... files) {
            //VVpath = rootPath + "/"+"VisibleVoice";
            SFTPClient sftpClient = new SFTPClient();

            sftpClient.init(ServerInfo.host,ServerInfo.username,ServerInfo.port,getFilesDir().getAbsolutePath() +"/"+ServerInfo.privatekey);
            //Log.d("MKDIRJOOHAN",sftpClient.)
            sftpClient.mkdir(ServerInfo.folderPath,username); // /home/vvuser
            Log.d("MKDIRJOOHAN",ServerInfo.folderPath+"< >"+username);

            String filename = files[0].getName();

            if(sftpClient.exist(username, files[0].getName())) {
                Log.d("fileExist", "파일존재함");
                sftpClient.upload(null, files[0], true);
                int extensionPosition = filename.lastIndexOf(".");
                String extension = filename.substring(extensionPosition);
                filename = filename.substring(0, extensionPosition) + "__temp" + extension;
            }
            else {
                Log.d("fileExist", "파일존재하지않");
                sftpClient.upload(null, files[0], false);
            }

            httpConn.requestWebServer(username,filename, callback);

            try {
                byte[] buffer = new byte[1024];
                FileOutputStream out = new FileOutputStream(new File(getFilesDir().getAbsolutePath() + "/" +
                        username + "/" + files[0].getName()));
                Log.d("fileCopy", getFilesDir().getAbsolutePath() + "/" +
                        username + "/" + files[0].getName());
                FileInputStream in = new FileInputStream(files[0]);
                int cnt = 0;
                while((cnt = in.read(buffer)) != -1 )
                    out.write(buffer, 0, cnt);

                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("AsyncTask", "Termination");

        }
    }



    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Looper.prepare();
            Toast.makeText(FileUploadActivity.this, "네트워크 오류: 네트워킹이 원활하지 않습니다." , Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        @Override
        public void onResponse(Call call, Response response) {
            Log.d("dong","response");
            System.out.println("response");
            try {
                System.out.println("OK");
                String body = response.body().string();
                Log.d("dong", "서버에서 응답한 Body:"+body);


                Looper.prepare();
                if(body.equals("OK"))
                    Toast.makeText(FileUploadActivity.this, "요청 완료: 서버에 변환 요청 완료." , Toast.LENGTH_SHORT).show();
                else if(body.equals("FAIL"))
                    Toast.makeText(FileUploadActivity.this, "네트워크 오류: 네트워킹이 원활하지 않습니다." , Toast.LENGTH_SHORT).show();
                else if(body.equals("EXIST"))
                    Toast.makeText(FileUploadActivity.this, "서버에서 변환 완료된 기록 사용." , Toast.LENGTH_SHORT).show();
                Looper.loop();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };



}
