package com.example.visiblevoice.Activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.R;

public class SplashActivity extends Activity {

    private SharedPreferences auto;
    private ImageView splashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("song","splash start");

        splashImageView=(ImageView)findViewById(R.id.splashImageView);
        Animation slowly_appear;
        slowly_appear = AnimationUtils.loadAnimation(this,R.anim.fadein);
        splashImageView.setAnimation(slowly_appear);


        Log.d("song","splash set contentview");
        auto = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("song","sleep");

        if(!(auto.getBoolean(AppDataInfo.Login.checkbox,false))){
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}
