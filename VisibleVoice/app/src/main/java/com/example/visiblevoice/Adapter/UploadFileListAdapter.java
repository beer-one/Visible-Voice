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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visiblevoice.Data.FileInfo;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;

import java.util.ArrayList;
import java.util.List;

public class UploadFileListAdapter extends BaseAdapter {

    private List<FileInfo> fileList;
    private LayoutInflater inflater = null;


    public UploadFileListAdapter(List<FileInfo> list) {
        this.fileList = list;
    }
    @Override
    public int getCount() {
        return fileList.size();
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
            convertView = inflater.inflate(R.layout.upload_list_item, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.file_icon);
        TextView filenameTextView = (TextView) convertView.findViewById(R.id.file_name);
        TextView capacityTextView = (TextView) convertView.findViewById(R.id.file_capacity);

        if(fileList.get(position).getisDir()) {
            iconImageView.setImageResource(R.drawable.vv_folder);
            capacityTextView.setText("("+fileList.get(position).getChildren()+")");
        }
        else {
            iconImageView.setImageResource(R.drawable.vv_musicfile);
            capacityTextView.setText(fileList.get(position).getCapacityAsString());
        }

        filenameTextView.setText(fileList.get(position).getFilename());

        return convertView;
    }
}