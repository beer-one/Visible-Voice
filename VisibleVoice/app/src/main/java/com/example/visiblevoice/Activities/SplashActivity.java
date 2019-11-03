package com.example.visiblevoice.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.R;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences auto;
    private ImageView splashImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        Log.d("song","splash set contentview");
        splashImageView=(ImageView)findViewById(R.id.splashImageView);
        Animation slowly_appear;
        slowly_appear = AnimationUtils.loadAnimation(this,R.anim.fadein);
        splashImageView.setAnimation(slowly_appear);

        Log.d("song","splash set contentview");
        auto = getSharedPreferences(AppDataInfo.Login.key, AppCompatActivity.MODE_PRIVATE);
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 2000);

    }

    private class splashhandler implements Runnable{
        public void run(){
            if(!(auto.getBoolean(AppDataInfo.Login.checkbox,false))){
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            else{
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}
