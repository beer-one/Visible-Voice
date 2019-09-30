package com.example.visiblevoice.Controller;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.example.visiblevoice.Data.Record;

import java.util.ArrayList;

public class MusicListController {
    public ArrayList<Record> musicList;
    public int current=0;
    private static MusicListController instance;
    private SharedPreferences musicdata;
    public MusicListController(){
        Log.d("song","music controller created");
        instance=this;
        musicList=new ArrayList<Record>();

        //musicdata.getAll()
        // set sample (you should remove this code)

        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Over_the_Horizon_mp3/Over_the_Horizon.mp3";
        String fileName="Over_the_Horizon.mp3";
        Record record1=new Record(fileName,path);
        addMusic(record1);


        String path2= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/test2/test2.mp4";
        String fileName2="test2.mp4";
        Record record2=new Record(fileName2,path2);
        addMusic(record2);

        String path3= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/test3/test3.mp4";
        String fileName3="test3.mp4";
        Record record3=new Record(fileName3,path3);
        addMusic(record3);


        Log.d("song","music list size: "+musicList.size());
        Log.d("song","music list 0 : "+musicList.get(0).file_name);

        ///////////////////////////////////////////
    }
    public static MusicListController getInstance(){
        if(instance==null){
            Log.d("song","instance is null");
            instance=new MusicListController();
        }
        return instance;
    }
    public void addMusic(Record music){ musicList.add(music);}
    public String getCurrentMusicPath(){ return musicList.get(current).full_path; }
    public void setCurrent(int cur){
        current=cur;
        Log.d("song","current music number is "+current);
    }
    public void movePrevMusic(){
        if(musicList==null || musicList.size()==0) return;
        current=(current-1+musicList.size())%musicList.size();
        Log.d("song","current music number is "+current);
    }
    public void moveNextMusic(){
        if(musicList==null || musicList.size()==0) return;
        current=(current+1)%musicList.size();
        Log.d("song","current music number is "+current);
    }

}
