package com.example.visiblevoice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FileUploadActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    private String[] permissionedFormat={".*.mp3",".*.mp4",".*.m4a",".*.flac",".*.wav"};

    private ArrayList<String> Files;
    private ArrayList<String> items;

    private String rootPath = "";
    private String nextPath = "";
    private String prevPath = "";
    private String currentPath = "";

    private TextView textView;
    private ListView listView;

    private ArrayAdapter<String> listAdapter;

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
        textView = (TextView)findViewById(R.id.dirPathTextView);
        listView = (ListView)findViewById(R.id.uploadFileListView);
        items = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(FileUploadActivity.this, android.R.layout.simple_list_item_1, items);


        // check sd card is mounted
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("song","cannot use external storage");
            return;
        }

        // get external root directory path
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("song","root path : "+ rootPath);

        // set ListView by file list from root directory
        boolean result = setFileList(rootPath);
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


    public boolean setFileList(String rootPath)    {
        // create file object
        File fileRoot = new File(rootPath);
        // if rootPath is not directory
        if(fileRoot.isDirectory() == false)        {
            Toast.makeText(FileUploadActivity.this, "Not Directory" , Toast.LENGTH_SHORT).show();
            Log.d("song","not directory");
            textView.setText("cannot find directory "+rootPath);
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
        setFileList(nextPath);
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
        setFileList(prevPath);
    }

}