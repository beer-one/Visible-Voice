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




        Log.d("song","music list size: "+musicList.size());
        //Log.d("song","music list 0 : "+musicList.get(0).file_name);

        ///////////////////////////////////////////
    }
    public static MusicListController getInstance(){
        if(instance==null){
            Log.d("song","instance is null");
            instance=new MusicListController();
        }
        return instance;
    }
    public void removeRecord(int idx){
        musicList.remove(idx);
    }
    public void addRecord(Record record){
        musicList.add(record);
    }
    public String getFilename(int idx){
        return musicList.get(idx).file_name;
    }
    public String getCurrentFilename(){
        return musicList.get(current).file_name;
    }
    public void addMusic(Record music){ musicList.add(music);}
    public String getCurrentMusicPath(){
        return musicList.get(current).music_file.getAbsolutePath();
    }
    public String getCurrentJsonPath(){
        return musicList.get(current).json_file.getAbsolutePath();
    }
    public String getCurrentPngPath(){//수정해야됨
        return musicList.get(current).png_file.getAbsolutePath();
    }
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
