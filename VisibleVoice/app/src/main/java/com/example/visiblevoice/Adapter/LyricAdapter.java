package com.example.visiblevoice.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;

import java.util.ArrayList;

public class LyricAdapter extends BaseAdapter {
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
        TextView lyric_text = (TextView)convertView.findViewById(R.id.lyric_TextView);

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