package com.example.visiblevoice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener{
    Intent intent;
    Button fileUploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        fileUploadBtn = findViewById(R.id.fileUploadBtn);

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
