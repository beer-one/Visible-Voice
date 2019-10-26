package com.example.visiblevoice.Activities;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visiblevoice.Controller.MusicListController;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Record;
import com.example.visiblevoice.R;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener{
    private Intent intent;
    private ImageButton fileUploadBtn;
    private ListView musicListListView;

    private ArrayAdapter<String> listAdapter;
    private MusicListController musicListController=MusicListController.getInstance();
    private ArrayList<String> nameList;
    private SharedPreferences currentfile;

    @Override
    protected void onStart() {
        super.onStart();

        nameList=new ArrayList<String>();
        for(Record record:musicListController.musicList)
            nameList.add(record.file_name);

        listAdapter = new ArrayAdapter<String>(FileListActivity.this, android.R.layout.simple_list_item_1, nameList);
        musicListListView.setAdapter(listAdapter);

        musicListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicListController.setCurrent(position);

                SharedPreferences.Editor setCurrentmusic = currentfile.edit();
                //setCurrentmusic.putString(AppDataInfo.CurrentFile.filename, musicListController.get);
                //setCurrentmusic.putString(AppDataInfo.CurrentFile.json , musicListController.getCurrentJsonPath());
                setCurrentmusic.putString(AppDataInfo.CurrentFile.music, musicListController.getCurrentMusicPath());
                //setCurrentmusic.putString(AppDataInfo.CurrentFile.png, false);
                setCurrentmusic.commit();

                startActivity(new Intent(FileListActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        fileUploadBtn = findViewById(R.id.fileUploadBtn);
        musicListListView=findViewById(R.id.musicListListView);

        currentfile = getSharedPreferences(AppDataInfo.CurrentFile.key,AppCompatActivity.MODE_PRIVATE);
        fileUploadBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fileUploadBtn :
                intent = new Intent(FileListActivity.this, FileUploadActivity.class);
                startActivity(intent);
                break;
        }
    }
}
