package com.example.visiblevoice.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.visiblevoice.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView fileMenuBtn;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileMenuBtn = findViewById(R.id.file_menu);

        fileMenuBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.file_menu :
                intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
                break;

        }
    }
}
