package com.example.visiblevoice.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.visiblevoice.Activities.MainActivity;
import com.example.visiblevoice.Adapter.LyricAdapter;
import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LyricListViewFragment extends Fragment {
    private ListView listView;
    private LyricAdapter lyric_adapter;
    private ArrayList<Lyric> lyricArrayList;
    private SharedPreferences currentfile;
    Context lContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

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

                Log.d("가사","play : "+((MainActivity)MainActivity.mContext).getState());

                switch(((MainActivity)MainActivity.mContext).getState()) {
                    case 0:
                        ((MainActivity)MainActivity.mContext).play_music(currentfile.getString(AppDataInfo.CurrentFile.music,null));
                        break;
                    case 2:
                        ((MainActivity)MainActivity.mContext).restart_music();
                        break;
                }

                Log.d("가사","lyricArrayList.get(position)의 시간 : "+lyricArrayList.get(position).getStartTime());
                ((MainActivity)MainActivity.mContext).move_music(lyricArrayList.get(position));

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


        lyric_adapter.setCurrentTime(lyricArrayList.get(currentPosition).getStartTime());
        lyric_adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(currentPosition);
    }

    private void getDataFromFile(String filename) {
        try {
            if(filename==null)
                return;
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
        Log.d("filename", filename);
        try {
            if(filename==null)
                return null;
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
}
