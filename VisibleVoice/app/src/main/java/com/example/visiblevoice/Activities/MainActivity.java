package com.example.visiblevoice.Activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.visiblevoice.R;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView fileMenuBtn;
    private ImageView playBtn;
    private Button speedBtn;
    private SeekBar seekBar;

    private Intent intent;
    private String email;

    private int speed=3; // speed has 5 step 0.5, 0.75, 1, 1.5, 2
    private int state=0; // state 0 = stop  // state 1 = playing // state 2 = pause
    private MediaPlayer mediaPlayer;
    private boolean drag=false;

    private  Thread th=new Thread(
            new Runnable(){
                @Override
                public void run() { // 쓰레드가 시작되면 콜백되는 메서드
                    // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
                    while(mediaPlayer!=null){
                        if(!drag){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileMenuBtn = findViewById(R.id.file_menu);
        playBtn=findViewById(R.id.play);
        speedBtn=findViewById(R.id.speedBtn);
        seekBar=findViewById(R.id.seekbar);

        fileMenuBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        speedBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser){
                    if(mediaPlayer!=null){
                        Log.d("song","term is "+i);
                        mediaPlayer.seekTo(i);
                    }
                    if(!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        playBtn.setImageResource(R.drawable.pause);
                        state=1;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                drag=true;
                Log.d("song","drag is true");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                drag=false;
                Log.d("song","drag is false");
            }
        });

        try{
            // get user's email
            intent=getIntent();
            email=(String) intent.getExtras().get("email");
//            Log.d("song","get email >>>"+email);
        }catch (Exception e) {}
        Log.d("song","get email >>>"+email);
    }

    private void play_music(String fileName){
        fileName= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Over_the_Horizon_mp3/Over_the_Horizon.mp3";
        Log.d("song","start trans str to uri : "+ fileName);

        Uri fileUri = Uri.parse( fileName );
        String filePath = fileUri.getPath();
        Log.d("song","parsing... " + fileUri);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.d("song","set mediaPlayer  "+mediaPlayer.toString());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                mediaPlayer.release();
//                mediaPlayer=null;
                playBtn.setImageResource(R.drawable.stop);
                state=0;
                Log.d("song","state is 0");
            }
        });


        try {
            mediaPlayer.setDataSource(MainActivity.this, fileUri);
            mediaPlayer.prepare(); mediaPlayer.start();

            state=1;
            seekBar.setMax(mediaPlayer.getDuration());

            th.start();
            Log.d("song","state is 1");
        } catch (IOException e) {
            Log.d("song","fail to play media");
            e.printStackTrace();
        }
    }

    private void restart_music(){
        if(mediaPlayer!=null)
            mediaPlayer.start();
        state=1;
        Log.d("song","state is 1");
    }
    private void pause_music(){
        mediaPlayer.pause();
        state=2;
        Log.d("song","state is 2");
    }
    private void setSpeed(){
        float s=1.0f;
        switch (speed){
            case 0:
                s=0.5f;
                speedBtn.setText("x 0.5");
                break;
            case 1:
                s=0.75f;
                speedBtn.setText("x 0.75");
                break;
            case 2:
                s=1.0f;
                speedBtn.setText("x 1.0");
                break;
            case 3:
                s=1.5f;
                speedBtn.setText("x 1.5");
                break;
            case 4:
                s=2.0f;
                speedBtn.setText("x 2.0");
                break;
        }
        Log.d("song","speed = "+ speed +" __ "+s);
        speed=(speed + 1) % 5;
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(s));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_menu :
                intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
                break;
            case R.id.speedBtn:
                setSpeed();
                break;
            case R.id.play:
                if(state==0) { // stop -> playing
                    play_music("");
                    playBtn.setImageResource(R.drawable.pause);
                } else if (state ==1) { // playing -> pause
                    pause_music();
                    playBtn.setImageResource(R.drawable.play);
                } else if(state == 2) { // pause -> playing
                    restart_music();
                    playBtn.setImageResource(R.drawable.pause);
                }
                break;
        }
    }
}
