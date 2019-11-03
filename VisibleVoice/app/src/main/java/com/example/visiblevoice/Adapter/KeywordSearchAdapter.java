package com.example.visiblevoice.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;
import com.example.visiblevoice.algorithm.KMP;

import java.util.ArrayList;
import java.util.List;

public class KeywordSearchAdapter extends BaseAdapter {

    private List<Lyric> lyricsList;
    private String keyword;
    private ArrayList<ArrayList<Integer>> posList;
    private LayoutInflater inflater = null;

    public KeywordSearchAdapter(List<Lyric> list, ArrayList<ArrayList<Integer>> posList, String keyword) {
        this.lyricsList = list;
        this.keyword = keyword;
        this.posList = posList;
    }
    @Override
    public int getCount() {
        return lyricsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 출처: https://mixup.tistory.com/46 [투믹스 작업장]
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.keyword_item, parent, false);
        }

        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        TextView sentenceTextView = (TextView) convertView.findViewById(R.id.sentenceTextView);
        SpannableStringBuilder ssb = new SpannableStringBuilder(lyricsList.get(position).getText());

        for(int i : posList.get(position))
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor(AppDataInfo.Color.gray_string)), i, i + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        timeTextView.setText(lyricsList.get(position).getStartTime() + "");
        sentenceTextView.setText(ssb);
        return convertView;
    }
}