package com.example.visiblevoice.Controller;

import android.os.Environment;
import android.util.Log;

import com.example.visiblevoice.Data.Record;

import java.util.ArrayList;

public class MusicListController {
    public ArrayList<Record> musicList;
    public int current=0;
    private static MusicListController instance;
    public MusicListController(){
        Log.d("song","music controller created");
        instance=this;
        musicList=new ArrayList<Record>();
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
