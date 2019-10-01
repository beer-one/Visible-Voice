package com.example.visiblevoice.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.visiblevoice.Client.HttpConnection;
import com.example.visiblevoice.Controller.MusicListController;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Record;
import com.example.visiblevoice.R;
import com.example.visiblevoice.Client.SFTPClient;
import com.example.visiblevoice.Client.ServerInfo;

import java.io.File;
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
    private ArrayList<String> items;

    private String rootPath = "";
    private String nextPath = "";
    private String prevPath = "";
    private String currentPath = "";
    private String newFolderPath="";

    //private String VVpath = "";//Visible voice path
    private TextView textView;
    private ListView listView;

    private ArrayAdapter<String> listAdapter;

    private SharedPreferences userData;
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


        // init
        Files=new ArrayList<String>();
        textView = (TextView)findViewById(R.id.fileDownloadTextView);
        listView = (ListView)findViewById(R.id.uploadFileListView);
        items = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(FileUploadActivity.this, android.R.layout.simple_list_item_1, items);

        userData = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);

        // check sd card is mounted
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("song","cannot use external storage");
            return;
        }

        // get external root directory path
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("song","root path : "+ rootPath);

        // set ListView by file list from root directory
        boolean result = setFileList(rootPath,"");
        if ( result == false ) { // if fail to get list , return
            return;
        }

        // set ListView Adapter by file list
        listView.setAdapter(listAdapter);

        // set listview item's onClick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("song", position + " : " + items.get(position).toString());
                currentPath = textView.getText().toString();
                String path = items.get(position).toString();
                if (path.equals("..")) {
                    prevPath(path);
                } else {
                    File fp = new File(path);
                    if(fp.isDirectory()==false) {
                        // if selected file is directory
                        Log.d("song","you click directory");
                        nextPath(path); // move directory
                    } else {
                        // if selected file is not directory
                        Log.d("song","you click file");
                        // TO-DO : create code that upload selected file
                    }
                }
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
            Toast.makeText(FileUploadActivity.this, "Not Directory " + fileName , Toast.LENGTH_SHORT).show();
            Log.d("song","not directory");
            textView.setText(rootPath);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("파일 변환").setMessage("선택하신 파일 "+fileName+"을 변환하시겠습니까?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                    sendData(fileRoot);
                    Record record = new Record(fileName, fileRoot);
                    musicListController.addMusic(record);

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
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                    // find last '/'
                    String addr = textView.getText().toString();
                    int lastSlashPosition = addr.lastIndexOf("/");

                    // get string before '/'
                    addr = addr.substring(0, lastSlashPosition);
                    textView.setText(addr);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return false;
        }

        // set root path TextView
        textView.setText(rootPath);

        // get file list from current directory
        File[] fileList = fileRoot.listFiles();
        // clear item(file) list
        items.clear();
        // set parents directory
        items.add("..");

        if ( fileList == null ) { // if directory is empty
            Log.d("song","Could not find List");
        }  else { // if directory is not empty
            // set file list
            try {
                for (int i = 0; i < fileList.length; i++) {
                    if(fileList[i].isDirectory()) // if file is directory
                        items.add(fileList[i].getName()); // add file in list
                    else {
                        Log.d("name",fileList[i].getName()+">>");

                        for(int j=0;j<permissionedFormat.length;j++){
                            if(fileList[i].getName().matches(permissionedFormat[j])) { // if file is permitted format
                                Log.d("name","add "+permissionedFormat[j]);
                                items.add(fileList[i].getName()); // add file in list
                                break;
                            }
                        }
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

    public void nextPath(String str)    {
        // save current path
        prevPath = currentPath;

        // create next directory path
        nextPath = currentPath + "/" + str;
        // set ListView by next directory's files
        setFileList(nextPath,str);
    }

    public void prevPath(String str) {
        // save current path
        nextPath = currentPath;
        prevPath = currentPath;


        // find last '/'
        int lastSlashPosition = prevPath.lastIndexOf("/");

        // get string before '/'
        prevPath = prevPath.substring(0, lastSlashPosition);

        //  set ListView by prev directory's files
        setFileList(prevPath,prevPath.substring(1,lastSlashPosition));
    }


    /** 웹 서버로 데이터 전송 */
    private void sendData(final File file) {
        final SFTPClient sftpclient = new SFTPClient();
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean status = false;

                        String username = userData.getString(AppDataInfo.Login.userID, null);
                        //VVpath = rootPath + "/"+"VisibleVoice";
                        SFTPClient sftpClient = new SFTPClient();

                        sftpClient.init(ServerInfo.host,ServerInfo.username,ServerInfo.port,AppDataInfo.Path.VisibleVoiceFolder+"/"+ServerInfo.privatekey);
                        sftpClient.mkdir(ServerInfo.folderPath,username); // /home/vvuser
                        //Log.d()
                        sftpClient.upload(username,file);
                        httpConn.requestWebServer(username,file.getName(), callback);
                    }
                });
            }
        }).start();
        /*ew Thread() {e
            public void run() {
                httpConn.requestWebServer(file, callback);
            }
        }.start();*/
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("dong", "콜백오류:"+e.getMessage());
            Looper.prepare();
            Toast.makeText(FileUploadActivity.this, "콜백오류" , Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d("dong","response");
            System.out.println("response");
            try {
                System.out.println("OK");
                String body = response.body().string();
                Log.d("dong", "서버에서 응답한 Body:"+body);
                Looper.prepare();
                Toast.makeText(FileUploadActivity.this, "서버에서 응답한 Body:"+body , Toast.LENGTH_SHORT).show();
                Looper.loop();



                /*File AppDir = new File(getApplicationContext().getFilesDir(),"VisibleVoice");
                if(!AppDir.exists())
                    AppDir.mkdirs();


                byte[] result = response.body().bytes();
                File savefile = new File(AppDir.getPath()+"a.json");
                FileOutputStream os = new FileOutputStream(savefile);
                os.write(result);
                os.close();*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



}
