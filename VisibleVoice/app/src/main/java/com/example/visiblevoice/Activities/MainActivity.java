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

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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


import com.example.visiblevoice.Controller.MusicListController;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;
import com.example.visiblevoice.db.AppDatabase;
import com.example.visiblevoice.db.RecordDAO;
import com.example.visiblevoice.models.Record;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private View navigationInflater;
    private SharedPreferences auto;
    private SharedPreferences currentfile;
    private ImageButton keywordSearchButton;

    private ViewPager viewPager;
    private PagerAdapter pageAdapter;

    private int speed=3; // speed has 5 step 0.5, 0.75, 1, 1.5, 2
    private int state=0; // state 0 = stop  // state 1 = playing // state 2 = pause
    private MediaPlayer mediaPlayer;
    private boolean playing=true;

    public static final int GET_MUSIC_LIST = 3333;

    public MusicListController musicListController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RecordDAO recordDAO;

    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    public static Context mContext;

    private MusicThread musicThread = new MusicThread();

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

        viewPager = (ViewPager) findViewById(R.id.pager); //
        pageAdapter = new PagerAdapter
                (getSupportFragmentManager(), 2);
        viewPager.setAdapter(pageAdapter);

        mContext = this;

        auto = getSharedPreferences(AppDataInfo.Login.key, Activity.MODE_PRIVATE);


        keywordSearchButton = findViewById(R.id.keywordSearchButton);
        keywordSearchButton.setOnClickListener(new SearchClickListener());

        initLayout();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


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

                            /*LyricListViewFragment fragment = ((LyricListViewFragment)getSupportFragmentManager().findFragmentById(R.id.lyric_listview));
                            Log.d("fragment",fragment+"");
                            fragment.moveListViewItem(i);*/

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

    public void play_music(String fileName){
        Log.d("song","play music... "+fileName);
        Uri fileUri = Uri.parse( fileName );

        Log.d("song","set mediaPlayer  "+mediaPlayer.toString());

        // 재생이 끝날때 이벤트 처리
        try {
            // 재생 시작
            mediaPlayer.reset();
            mediaPlayer.setDataSource(MainActivity.this, fileUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    musicListController.moveNextMusic();
                    state=0;
                }
            });

            state=1;

            playBtn.setImageResource(R.drawable.pause);

            seekBar.setMax(mediaPlayer.getDuration());
            // seekbar 이동을 위한 스레드 시작
            try{
                playing=true;
                seekBar.setProgress(0);

                if(musicThread.isAlive()) {
                    musicThread.interrupt();
                    Log.d("musicThread", "musicThread is interrupted");
                }

                while(musicThread.isAlive());
                Log.d("musicThread", "musicThread is dead");
                musicThread = null;
                musicThread = new MusicThread();

                musicThread.start();
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

    public void move_music(Lyric lyrics){
        // 리릭스에 입력된 시작 시간부터 미디어를 재생하는 메서드
        if(mediaPlayer == null) return;
        Log.d("가사","lyrics.getStartTime() : "+lyrics.getStartTime());
        mediaPlayer.seekTo((int)lyrics.getStartTime()*1000);
       // ((LyricListViewFragment)getSupportFragmentManager().findFragmentById(R.id.lyric_listview)).moveListViewItem((int)lyrics.getStartTime()*1000);

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
                /*if(mediaPlayer!=null && mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                playing=false;*/
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


                    try{
                        Log.d("file저장","실행할 음성파일 : "+currentfile.getString(AppDataInfo.CurrentFile.music,null));
                        play_music(currentfile.getString(AppDataInfo.CurrentFile.music,null));
                    }catch (NullPointerException ne){
                        ne.printStackTrace();
                    }

//                    play_music("");
                } else if (state ==1) { // playing -> pause
                    pause_music();
                } else if(state == 2) { // pause -> playing
                    restart_music();
                }
                break;
        }
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

    private class MusicThread extends Thread {
        int current = 0;

        public void run() { // 쓰레드가 시작되면 콜백되는 메서드
            // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    if(playing && state == 1){

                        int progress = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(progress);
                        Log.d("progress",progress+"");
                        final int pos = ((LyricListViewFragment)pageAdapter.getItem(1)).findListViewItem(progress);
                        Log.d("progress", "pos = " + pos + ", cur = " + current);
                        if(pos != current) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    ((LyricListViewFragment)pageAdapter.getItem(1)).moveListViewItem(pos);
                                    current = pos;
                                }
                            });
                        }
                        Thread.sleep(1000);

                    }
                }
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            playing = false;
        }
    }

    public boolean getPlaying() {
        return playing;
    }
    private void insertSftpKey(String str) {
        AssetManager asset = getResources().getAssets();
        InputStream is = null;

        try {
            is = asset.open("key/" + str);
            File dir = new File(AppDataInfo.Path.VisibleVoiceFolder);
            dir.mkdir();
            OutputStream os = new FileOutputStream(new File(AppDataInfo.Path.VisibleVoiceFolder+"/"+ str));

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

class WCFragment extends Fragment {

    private ImageView WordCloudImageView;
    private SharedPreferences currentfile;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_wc, container, false);
        WordCloudImageView=(ImageView) v.findViewById(R.id.wordcloudImg);

        mContext = getContext();
        currentfile= mContext.getSharedPreferences(AppDataInfo.CurrentFile.key, AppCompatActivity.MODE_PRIVATE);
        try {
            File WCfile = new File(currentfile.getString(AppDataInfo.CurrentFile.png,null));
            Log.d("fragment","file : "+WCfile.getName());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(WCfile));
            Log.d("fragment","file : "+b);
            WordCloudImageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(NullPointerException ne){
            ne.printStackTrace();
        }
        return v;
    }

}

class LyricListViewFragment extends Fragment {
    private ListView listView;
    private LyricAdapter lyric_adapter;
    private ArrayList<Lyric> lyricArrayList;
    private SharedPreferences currentfile;
    private int currentPosition;
    Context lContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        currentPosition=0;

        View v = inflater.inflate(R.layout.fragment_lyriclist, container, false);
        currentfile= getContext().getSharedPreferences(AppDataInfo.CurrentFile.key, AppCompatActivity.MODE_PRIVATE);
        listView = (ListView)v.findViewById(R.id.lyric_listview);
        lyricArrayList = new ArrayList<Lyric>();
        getDataFromFile(currentfile.getString(AppDataInfo.CurrentFile.json,null));

        listView.setDivider(null);
        lContext = getContext();
        lyric_adapter = new LyricAdapter(getContext(),lyricArrayList);
       /* mContext = getContext();
        currentfile= mContext.getSharedPreferences(AppDataInfo.CurrentFile.key, AppCompatActivity.MODE_PRIVATE);*/
        listView.setAdapter(lyric_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // Log.d("아이템 객체",lyric_adapter.getView(position,,listView)+"");
                //if(((MainActivity)MainActivity.mContext))
                if(!((MainActivity)MainActivity.mContext).getPlaying())
                    ((MainActivity)MainActivity.mContext).play_music(currentfile.getString(AppDataInfo.CurrentFile.music,null));
                Log.d("가사","lyricArrayList.get(position)의 시간 : "+lyricArrayList.get(position).getStartTime());
                ((MainActivity)MainActivity.mContext).move_music(lyricArrayList.get(position));
                //listView.setSelection(position);//가사
                //moveListViewItem((int)lyricArrayList.get(position).getStartTime()*1000);
                //listView.smoothScrollToPosition(position);

            }
        });

        return v;
    }
    public int findListViewItem(int currentPosition){
        for(int cnt = 1; cnt < lyricArrayList.size(); cnt++){
            if(lyricArrayList.get(cnt).getStartTime()*1000 > currentPosition){
                Log.d("progress","cnt : "+cnt);
                return cnt-1;
            }
        }
        return lyricArrayList.size()-1;
    }

    public void moveListViewItem(int currentPosition){

        Log.d("position", "currentPosition : "+currentPosition);


        //setTextColor(prevPosition,false, lyricArrayList.get(currentPosition).getStartTime());
        lyric_adapter.setCurrentTime(lyricArrayList.get(currentPosition).getStartTime());
        lyric_adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(currentPosition);
       // lyric_adapter.notifyDataSetChanged();
        //setTextColor(currentPosition,true, lyricArrayList.get(currentPosition).getStartTime());

    }

    private void getDataFromFile(String filename) {
        try {
            String json = readJsonFromFile(filename);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray lyrics = jsonObject.getJSONArray("sentences");

            for(int i = 0; i < lyrics.length(); i++) {
                JSONObject o = lyrics.getJSONObject(i);
                lyricArrayList.add(new Lyric(Float.parseFloat(o.getJSONArray("words").getJSONObject(0).getString("start_time")), o.getString("sentence")));
            }
            Log.d("가사","lyricArrayList.size() ; "+lyricArrayList.size());
            Log.d("가사","lyricArrayList ; "+lyricArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }

    private String readJsonFromFile(String filename) throws NullPointerException {
        String result = null;
        try {
            InputStream is = new FileInputStream(new File(filename));
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new NullPointerException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException();
        }
    }

    private void setTextColor(int pos, boolean on, float currentTime) {

        int firstPos = listView.getFirstVisiblePosition();

        int wantedPos = pos - firstPos;

        if (wantedPos < 0 || wantedPos >= listView.getChildCount()) {

            return;

        }

        TextView childView = listView.getChildAt(wantedPos).findViewById(R.id.lyric_TextView);

        if (childView == null) {

            return;

        }

        if (on && currentTime == lyricArrayList.get(pos).getStartTime()) {

            childView.setTextColor(0xFF0000FF);

        } else {

            childView.setTextColor(0xFF000000);

        }

    }

}

class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private WCFragment wcFragment;
    private LyricListViewFragment lyricListViewFragment;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        wcFragment = new WCFragment();
        lyricListViewFragment = new LyricListViewFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return wcFragment;
            case 1:
                return lyricListViewFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


class LyricAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Lyric> lyrics = null;
    private int count = 0;
    private float currentTime;

    public LyricAdapter(Context context, ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
        this.count = lyrics.size();
        this.inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return lyrics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("getView", "LyricAdapter getView() 호출");
        if(convertView == null) {
            //개별 리릭 xml 불러줘야함
            convertView = inflater.inflate(R.layout.lyric_item, parent, false);
        }

        Lyric lyric = lyrics.get(position);


        //그 각각의 리릭 안에서 텍스트뷰 하나 뽑아옴
        //TextView time_text = (TextView)convertView.findViewById(R.id.time_TextView);
        TextView lyric_text = (TextView)convertView.findViewById(R.id.lyric_TextView);

        //time_text.setText(Float.toString(lyric.getStartTime()));
        lyric_text.setText(lyric.getText());
        if(currentTime == lyrics.get(position).getStartTime())
            lyric_text.setTextColor(AppDataInfo.Color.selected_lyric);
        else
            lyric_text.setTextColor(AppDataInfo.Color.lyric);


        return convertView;

    }

    public float getCurrentTime() {
        return currentTime;
    }
    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }
}

