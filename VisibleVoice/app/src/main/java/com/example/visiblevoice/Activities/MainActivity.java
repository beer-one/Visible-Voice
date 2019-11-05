package com.example.visiblevoice.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;


import com.example.visiblevoice.Adapter.LyricAdapter;
import com.example.visiblevoice.Adapter.PagerAdapter;
import com.example.visiblevoice.Controller.MusicListController;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.Fragment.LyricListViewFragment;
import com.example.visiblevoice.R;
import com.example.visiblevoice.db.AppDatabase;
import com.example.visiblevoice.db.RecordDAO;
import com.example.visiblevoice.models.Record;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private ImageView fileMenuBtn;
    private ImageView playBtn;
    private Button speedBtn;
    private SeekBar seekBar;
    private TextView useridView;
    private TextView titleTextView;
    private View navigationInflater;
    private SharedPreferences auto;
    private SharedPreferences currentfile;
    private ImageButton keywordSearchButton;
    private ImageButton menuButton;
    private TextView curentTimeTextView;
    private TextView musicTimeTextView;

    private ViewPager viewPager;
    private PagerAdapter pageAdapter;

    private int speed=3; // speed has 5 step 0.5, 0.75, 1, 1.5, 2
    private int state=0; // state 0 = stop  // state 1 = playing // state 2 = pause
    private MediaPlayer mediaPlayer;

    public MusicListController musicListController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RecordDAO recordDAO;

    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    public static Context mContext;

    private PlayMusicAsyncTask playMusicAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertSftpKey("id_rsa");
        insertSftpKey("id_rsa.pub");
        currentfile= getApplicationContext().getSharedPreferences(AppDataInfo.CurrentFile.key, AppCompatActivity.MODE_PRIVATE);
        Log.d("저장확인","실행할 음성파일 : "+currentfile.getString(AppDataInfo.CurrentFile.music,null));
        musicListController = MusicListController.getInstance();

        fileMenuBtn = findViewById(R.id.file_menu);
        playBtn=findViewById(R.id.play);
        speedBtn=findViewById(R.id.speedBtn);
        seekBar=findViewById(R.id.seekbar);

        titleTextView = findViewById(R.id.title);
        titleTextView.setText(currentfile.getString(AppDataInfo.CurrentFile.filename,null));
        viewPager = (ViewPager) findViewById(R.id.pager); //
        pageAdapter = new PagerAdapter
                (getSupportFragmentManager(), 2);
        viewPager.setAdapter(pageAdapter);


        curentTimeTextView = (findViewById(R.id.currentTimeText));
        musicTimeTextView = (findViewById(R.id.totalTimeText));

        playMusicAsyncTask = new PlayMusicAsyncTask();
        mContext = this;

        auto = getSharedPreferences(AppDataInfo.Login.key, Activity.MODE_PRIVATE);

        keywordSearchButton = findViewById(R.id.keywordSearchButton);
        keywordSearchButton.setOnClickListener(new SearchClickListener());

        menuButton=findViewById(R.id.menuButton);

        initLayout();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        String userid = auto.getString(AppDataInfo.Login.userID, null);
        Toast.makeText(getApplicationContext(),"user id : "+userid,Toast.LENGTH_SHORT).show();
        useridView.setText(auto.getString(AppDataInfo.Login.userID,null));
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
        updateMusicList();

        //Log.d("currentfile", currentfile.getString(AppDataInfo.CurrentFile.music, null));
        if(currentfile.getString(AppDataInfo.CurrentFile.music, null) != null) {
            try {
                setMediaPlayer(currentfile.getString(AppDataInfo.CurrentFile.music, null));
                musicTimeTextView.setText(timeToString(mediaPlayer.getDuration()/1000));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void updateMusicList(){

        currentfile = getSharedPreferences(AppDataInfo.CurrentFile.key,AppCompatActivity.MODE_PRIVATE);

        recordDAO = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"db-record" )
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getRecordDAO();
        //recordDAO.clearRecordTable();
        List<Record> recordList= recordDAO.getRecords();
        musicListController = new MusicListController();


        for(com.example.visiblevoice.models.Record record_model : recordList){
            int check =1;
            for(com.example.visiblevoice.Data.Record record_data:musicListController.musicList){
                Log.d("filepath", "record_model.getFileName : "+record_model.getFileName());
                Log.d("filepath", "record_data : "+record_data.file_name);
                if(record_model.getFileName() == record_data.file_name){
                    File json_file;
                    File png_file;
                    File audio_file;
                    if(record_model.getWordCloudPath()==null){
                        png_file = null;
                    }
                    else{
                        png_file = new File(record_model.getWordCloudPath());
                    }
                    if(record_model.getJsonPath()==null){
                        json_file = null;
                    }
                    else{
                        json_file = new File(record_model.getJsonPath());
                    }

                    if(record_model.getAudioPath() ==null){
                        audio_file = null;
                    }
                    else{
                        audio_file = new File(record_model.getAudioPath());
                    }

                    record_data.setMusic_file(audio_file);
                    record_data.setJson_file(json_file);
                    record_data.setPng_file(png_file);
                    check =0;
                    break;
                }
            }
            if(check==1){
                File json_file;
                File png_file;
                File audio_file;
                if(record_model.getWordCloudPath()==null){
                    png_file = null;
                }
                else{
                    png_file = new File(record_model.getWordCloudPath());
                }
                if(record_model.getJsonPath()==null){
                    json_file = null;
                }
                else{
                    json_file = new File(record_model.getJsonPath());
                }

                if(record_model.getAudioPath() ==null){
                    audio_file = null;
                }
                else{
                    audio_file = new File(record_model.getAudioPath());
                }

                com.example.visiblevoice.Data.Record record = new com.example.visiblevoice.Data.Record(record_model.getFileName(),audio_file,json_file,png_file);
                Log.d("filepath", "외않되 : "+record.getPng_file());
                //record.setPng_file(png_file);
                musicListController.addRecord(record);

            }
        }


    }
    public void refreshMediaPlayer(){
        viewPager = (ViewPager) findViewById(R.id.pager); //
        pageAdapter = new PagerAdapter
                (getSupportFragmentManager(), 2);
        viewPager.setAdapter(pageAdapter);
        currentfile= getSharedPreferences(AppDataInfo.CurrentFile.key, AppCompatActivity.MODE_PRIVATE);

        Log.d("title",currentfile.getString(AppDataInfo.CurrentFile.filename,null)+"");
        titleTextView.setText(currentfile.getString(AppDataInfo.CurrentFile.filename,null));

        try{
            if(currentfile.getString(AppDataInfo.CurrentFile.music,null)!=null){
                Log.d("song","play new music...");
                play_music(currentfile.getString(AppDataInfo.CurrentFile.music,null));

            }
        }
        catch (NullPointerException ne){
            ne.printStackTrace();
        }
    }

    private void initLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer_root);
        navigationView = (NavigationView) findViewById(R.id.nv_main_navigation_root);
        navigationInflater = getLayoutInflater().inflate(R.layout.nav_header_main, null, false);//다른 view의 객체 가져오기위해사용

        View nav_header_view = navigationView.getHeaderView(0);

        useridView = (TextView) nav_header_view.findViewById((R.id.userIdTextView));
        Log.d("useridview",useridView.getText().toString());;
        useridView.setText(auto.getString(AppDataInfo.Login.userID, null));
        Log.d("useridview2",useridView.getText().toString());;
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    public void setMediaPlayer(String fileName) throws IOException {
        // 재생 시작
        Uri fileUri = Uri.parse( fileName );

        mediaPlayer.reset();
        mediaPlayer.setDataSource(MainActivity.this, fileUri);
        mediaPlayer.prepare();

    }

    public void play_music(String fileName){
        Log.d("song","play music... "+fileName);
        Log.d("song","mediaPlayer.getDuration()  "+mediaPlayer.getDuration());

        // 재생이 끝날때 이벤트 처리
        try {
            // 재생 시작
            setMediaPlayer(fileName);
            state=1;
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    musicListController.moveNextMusic();
                    state=0;
                }
            });

            playBtn.setImageResource(R.drawable.pause);

            seekBar.setMax(mediaPlayer.getDuration());
            // seekbar 이동을 위한 스레드 시작
            try{
                state=1;
                seekBar.setProgress(0);

                Log.d("musicThread", "status = " + playMusicAsyncTask.getStatus());
                Log.d("musicThread", "Running : " + AsyncTask.Status.RUNNING);
                if(playMusicAsyncTask.getStatus()==AsyncTask.Status.RUNNING) {
                    playMusicAsyncTask.cancel(false);
                    //while (playMusicAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                    Log.d("musicThread", "status = " + playMusicAsyncTask.getStatus());
                }

                Log.d("musicThread", "musicThread is dead");
                playMusicAsyncTask = null;
                playMusicAsyncTask = new PlayMusicAsyncTask();

                musicTimeTextView.setText(timeToString(mediaPlayer.getDuration()/1000));
                playMusicAsyncTask.execute();
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d("song","state is 1");
        } catch (IOException e) {
            Log.d("song","fail to play media");
            e.printStackTrace();
        }
    }
    public void restart_music(){
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
    }

    public void move_music(Lyric lyrics){
        // 리릭스에 입력된 시작 시간부터 미디어를 재생하는 메서드
        if(mediaPlayer == null) return;
        Log.d("가사","lyrics.getStartTime() : "+lyrics.getStartTime());
        mediaPlayer.seekTo((int)lyrics.getStartTime()*1000);

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
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_menu :
                startActivity(new Intent(MainActivity.this, FileListActivity.class));
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

                    try {
                        Log.d("file저장","실행할 음성파일 : "+currentfile.getString(AppDataInfo.CurrentFile.music,null));
                        play_music(currentfile.getString(AppDataInfo.CurrentFile.music,null));
                    } catch (NullPointerException ne){
                        ne.printStackTrace();
                    }
                } else if (state ==1) { // playing -> pause
                    pause_music();
                } else if (state == 2) { // pause -> playing
                    restart_music();
                }
                break;
        }
    }
    public String timeToString(float time) {
        int hours = (int)time / 3600;
        int minutes = ((int)time % 3600) / 60;
        int seconds = ((int)time % 3600) % 60;
        String ret = hours == 0 ? "00:" : hours < 10 ? "0"+hours : hours+"";
        ret += (minutes == 0 ? "00:" : minutes < 10 ? "0"+minutes+":" : minutes+":");
        ret += (seconds == 0 ? "00" : seconds < 10 ? "0"+seconds : seconds+"");
        return ret;
    }
    private class SearchClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), // 현재 화면의 제어권자
                    KeywordSearchActivity.class);
            intent.putExtra("filename", currentfile.getString(AppDataInfo.CurrentFile.json,null));
            Log.d("search-jsonfile", currentfile.getString(AppDataInfo.CurrentFile.json,null));
            startActivity(intent);
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

    private class PlayMusicAsyncTask extends AsyncTask<Void , Integer , Void>{
        private int currentPosition = 0;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                while(!isCancelled()) {
                    Log.d("progress", "status: " + this.getStatus());
                    Log.d("progress","state : "+state);
                    if(state == 1){

                        int progress = mediaPlayer.getCurrentPosition();

                        Log.d("progress",progress+"");
                        int position = ((LyricListViewFragment)pageAdapter.getItem(1)).findListViewItem(progress);
                        publishProgress(progress, position);
                    }
                    Thread.sleep(1000);
                }
            }catch (InterruptedException ie){
                Log.d("musicThread", "musicThread is interrupted");
                ie.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("musicThread", "musicThread is interrupted2");
            state = 2;
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            Log.d("musicThead", "is cancelled");

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("progress","length : "+values[1]);
            if(values[1] != currentPosition) {
                ((LyricListViewFragment) pageAdapter.getItem(1)).moveListViewItem(values[1]);
                currentPosition = values[1];
            }
            Log.d("progress","length : "+values.length);
            Log.d("progress","value[0] : "+values[0]/1000);
            curentTimeTextView.setText(timeToString(values[0]/1000));
            seekBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

       /* @Override
        protected void onProgressUpdate(Integer... progress) {
            // 파일 다운로드 퍼센티지 표시 작업
        }
        */
        @Override
        protected void onPostExecute(Void aVoid) {
            // doInBackground 에서 받아온 total 값 사용 장소
            Log.d("musicThead", "onPostExecute");
        }
    }

    public int getState() {
        return state;
    }
    private void insertSftpKey(String str) {
        AssetManager asset = getResources().getAssets();
        InputStream is = null;

        try {
            is = asset.open("key/" + str);
            File dir = new File(getFilesDir().getAbsolutePath() + "/" + AppDataInfo.Login.userID);
            dir.mkdir();
            OutputStream os = new FileOutputStream(new File(getFilesDir().getAbsolutePath() + "/"+ str));

            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }
            os.close();
            is.close();

            Log.d("key", "insert success");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}










