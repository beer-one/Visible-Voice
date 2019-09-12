package com.example.visiblevoice.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.visiblevoice.R;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener{
    private Intent intent;
    private Button fileUploadBtn;
    private ListView musicListListView;

    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        fileUploadBtn = findViewById(R.id.fileUploadBtn);
        musicListListView=findViewById(R.id.musicListListView);

        items=new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(FileListActivity.this, android.R.layout.simple_list_item_1, items);
        musicListListView.setAdapter(listAdapter);
        musicListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FileListActivity.this,MainActivity.class);
                Log.d("song","put extras >> path is  "+ items.get(position));
                intent.putExtra("path",items.get(position));
                setResult(3333,intent);
                finish();
            }
        });
        items.add("music 1");
        items.add("music 2");
        items.add("music 3");

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
