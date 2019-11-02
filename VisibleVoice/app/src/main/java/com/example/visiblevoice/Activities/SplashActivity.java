package com.example.visiblevoice.Activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

        try{
            splashImageView=(ImageView)findViewById(R.id.splashImageView);
            Animation slowly_appear;
            slowly_appear = AnimationUtils.loadAnimation(this,R.anim.fadein);
            splashImageView.setAnimation(slowly_appear);
        }catch (Exception e) {
        }

        Log.d("song","splash set contentview");
        auto = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!(auto.getBoolean(AppDataInfo.Login.checkbox,false))){
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }
            }, 2000);
            //Thread.sleep(2000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("song","sleep");


        finish();
    }
}
