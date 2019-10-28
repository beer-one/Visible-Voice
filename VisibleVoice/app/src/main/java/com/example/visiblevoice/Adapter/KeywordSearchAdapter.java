package com.example.visiblevoice.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.visiblevoice.Data.Sentence;
import com.example.visiblevoice.R;

import java.util.List;

public class KeywordSearchAdapter extends BaseAdapter {

    private List<Sentence> sentenceList;
    private LayoutInflater inflater = null;

    public KeywordSearchAdapter(List<Sentence> list) {
        this.sentenceList = list;
    }
    @Override
    public int getCount() {
        return sentenceList.size();
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

        timeTextView.setText(sentenceList.get(position).getTime());
        sentenceTextView.setText(sentenceList.get(position).getSentence());
        return convertView;
    }
}