package com.example.visiblevoice.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.visiblevoice.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("song","splash start");
        setContentView(R.layout.activity_splash);
        Log.d("song","splash set contentview");

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("song","sleep");

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
