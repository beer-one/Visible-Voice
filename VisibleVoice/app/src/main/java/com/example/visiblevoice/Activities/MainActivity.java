package com.example.visiblevoice.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.visiblevoice.Controller.MusicListController;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Lyrics;
import com.example.visiblevoice.R;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private ImageView fileMenuBtn;
    private ImageView playBtn;
    private Button speedBtn;
    private SeekBar seekBar;
    private TextView lyricsTextView;
    private TextView useridView;
    private View navigationInflater;
    private SharedPreferences auto;
    private SharedPreferences currentfile;
    private Intent intent;
    private String email;

    private int speed=3; // speed has 5 step 0.5, 0.75, 1, 1.5, 2
    private int state=0; // state 0 = stop  // state 1 = playing // state 2 = pause
    private MediaPlayer mediaPlayer;
    private boolean playing=true;

    public static final int GET_MUSIC_LIST = 3333;

    public MusicListController musicListController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;

    private Thread th=new Thread(
            new Runnable(){
                @Override
                public void run() { // 쓰레드가 시작되면 콜백되는 메서드
                    // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
                    while(true) {
                        if(playing && state == 1){
                            try {
                                int progress = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(progress);
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == GET_MUSIC_LIST){
            if(resultCode == GET_MUSIC_LIST) {
                play_music(musicListController.getCurrentMusicPath());
                Log.d("song","play new music...");
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListController = MusicListController.getInstance();

        fileMenuBtn = findViewById(R.id.file_menu);
        playBtn=findViewById(R.id.play);
        speedBtn=findViewById(R.id.speedBtn);
        seekBar=findViewById(R.id.seekbar);
        lyricsTextView=findViewById(R.id.lyricsTextView);


        auto = getSharedPreferences(AppDataInfo.Login.key, Activity.MODE_PRIVATE);



        initLayout();

        String userid = auto.getString(AppDataInfo.Login.userID, null);
        Toast.makeText(getApplicationContext(),"user id : "+userid,Toast.LENGTH_SHORT).show();
        useridView.setText("dongwook");
        fileMenuBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        speedBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser){
                    try{
                        if(mediaPlayer!=null){
                            Log.d("song","term is "+i);
                            mediaPlayer.seekTo(i);
                        }
                        if(!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            playBtn.setImageResource(R.drawable.pause);
                            state=1;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        try{
            if(currentfile.getString(AppDataInfo.CurrentFile.music,null)!=null){
                //play_music(musicListController.getCurrentMusicPath());
                play_music(currentfile.getString(AppDataInfo.CurrentFile.music,null));
                Log.d("song","play new music...");
            }
        }
        catch (NullPointerException ne){
            ne.printStackTrace();
        }

        try{
            // get user's email
            // TO-DO : you should get play list using email
            //       : musicController.addMusic() <- put record in music controller
            intent = getIntent();
            //email=(String) intent.getExtras().get("email");
            ////////////////////////////////////////////////
        }catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("song","get email >>>"+email);
    }
    private void initLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("소리가 보인다");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer_root);
        navigationView = (NavigationView) findViewById(R.id.nv_main_navigation_root);
        navigationInflater = getLayoutInflater().inflate(R.layout.nav_header_main, null, false);//다른 view의 객체 가져오기위해사용

        useridView = (TextView) navigationInflater.findViewById((R.id.userIdTextView));
        Log.d("useridview",useridView.getText().toString());
        useridView.setText(auto.getString(AppDataInfo.Login.userID, null));
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void play_music(String fileName){
        Log.d("song","play music... "+fileName);
        Uri fileUri = Uri.parse( fileName );

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Log.d("song","set mediaPlayer  "+mediaPlayer.toString());

        // 재생이 끝날때 이벤트 처리
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                musicListController.moveNextMusic();
                state=0;
            }
        });

        try {
            // 재생 시작
            mediaPlayer.setDataSource(MainActivity.this, fileUri);
            mediaPlayer.prepare(); mediaPlayer.start();

            state=1;

            playBtn.setImageResource(R.drawable.pause);

            seekBar.setMax(mediaPlayer.getDuration());
            // seekbar 이동을 위한 스레드 시작
            try{
                playing=true;
                seekBar.setProgress(0);

                th.start();
            }catch (Exception e){
            }
            Log.d("song","state is 1");
        } catch (IOException e) {
            Log.d("song","fail to play media");
            e.printStackTrace();
        }
    }
    private void restart_music(){
        if(mediaPlayer==null) return;
        mediaPlayer.start();
        state=1;
        playBtn.setImageResource(R.drawable.pause);
//        Log.d("song","state is 1");
    }
    private void pause_music(){
        if(mediaPlayer==null) return;
        mediaPlayer.pause();
        state=2;
        playBtn.setImageResource(R.drawable.play);
//        Log.d("song","state is 2");
    }
    private void setSpeed(){
        float s=1.0f;
        switch (speed){
            case 0:
                s=0.5f;speedBtn.setText("x 0.5");
                break;
            case 1:
                s=0.75f;speedBtn.setText("x 0.75");
                break;
            case 2:
                s=1.0f;speedBtn.setText("x 1.0");
                break;
            case 3:
                s=1.5f;speedBtn.setText("x 1.5");
                break;
            case 4:
                s=2.0f;speedBtn.setText("x 2.0");
                break;
        }

        speed=(speed + 1) % 5;
        if(mediaPlayer==null) return;
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(s));
        if(state != 1) mediaPlayer.pause();
/*
        state=1;
        playBtn.setImageResource(R.drawable.pause);
*/
    }

    private void move_music(Lyrics lyrics){
        // 리릭스에 입력된 시작 시간부터 미디어를 재생하는 메서드
        if(mediaPlayer == null) return;
        mediaPlayer.seekTo(lyrics.getStartTime());
    }
    private void logout() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

        SharedPreferences.Editor editor = auto.edit();

        editor.clear();
        editor.commit();
        Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
        editor = auto.edit();
        editor.putBoolean("checkbox",false);
        editor.commit();
        finish();
        //intent = new Intent(MainActivity.this, LoginActivity.class);
        //startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_menu :
                if(mediaPlayer!=null && mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                playing=false;
                //intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(new Intent(MainActivity.this, FileListActivity.class));
                //startActivityForResult(intent, GET_MUSIC_LIST);
                break;
            case R.id.speedBtn:
                setSpeed();
                break;
            case R.id.play:
                if(state==0) { // stop -> playing
                    Log.d("song","before playing >>music list size :" + musicListController.musicList.size());
                    Log.d("song","before playing >>music current :" + musicListController.current);
                    if(musicListController.musicList.size()!=0)
                        Log.d("song","before playing >>music 0  :" + musicListController.musicList.get(0).full_path);

                    play_music(musicListController.getCurrentMusicPath());
//                    play_music("");
                } else if (state ==1) { // playing -> pause
                    pause_music();
                } else if(state == 2) { // pause -> playing
                    restart_music();
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(menuItem.getItemId()) {
            case R.id.navigation_item_attachment:
                Toast.makeText(this, "logout clicked..", Toast.LENGTH_SHORT).show();
                logout();
                break;
            case R.id.FileDownloadActivitybtn:
                Toast.makeText(this, "최근파일다운로드 clicked..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, FileDownloadActivity.class));
                break;
        }

        //drawerLayout.closeDrawer(GravityCompat.START);
        return false;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
