package com.example.visiblevoice.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visiblevoice.Data.AppDataInfo;

import com.example.visiblevoice.Data.CurrentDownload;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CurrentDownloadAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<CurrentDownload> currentDownloads = null;
    private ArrayList<Boolean> isCheck;
    private int count = 0;

    public CurrentDownloadAdapter(Context context, ArrayList<CurrentDownload> currentDownloads) {
        this.currentDownloads = currentDownloads;
        this.count = currentDownloads.size();
        this.inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        isCheck = new ArrayList<Boolean>();
    }
    public boolean isChecked(int position){
        return isCheck.get(position);
    }
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return currentDownloads.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d("getView", "LyricAdapter getView() 호출");
        if(convertView == null) {
            //개별 리릭 xml 불러줘야함
            convertView = inflater.inflate(R.layout.item_current_download, parent, false);
        }

        CurrentDownload currentDownload = currentDownloads.get(position);


        //그 각각의 리릭 안에서 텍스트뷰 하나 뽑아옴
        TextView filenameTextView = (TextView)convertView.findViewById(R.id.item_filename);

        filenameTextView.setText(currentDownload.getFile_name());

        ImageView imageView = (ImageView)convertView.findViewById(R.id.item_image);
        File WCfile = new File(currentDownload.getPng_path());
        //Log.d("fragment","file : "+WCfile.getName());
        try{
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(WCfile));
            //Log.d("fragment","file : "+b);
            imageView.setImageBitmap(b);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
        Log.d("checkbox","checkbox"+position+":"+checkBox.isChecked());
        Log.d("checkbox","currentDownload cbx"+position+":"+currentDownload.ischecked());
        if(checkBox.isChecked()){
            currentDownload.setchecked(true);
            Log.d("checkbox","if currentDownload cbx"+position+":"+currentDownload.ischecked());
        }
        else{
            currentDownload.setchecked(false);
            Log.d("checkbox","else currentDownload cbx"+position+":"+currentDownload.ischecked());
        }

        /*if (checkBox != null) {
            checkBox.setChecked(false);
            //CheckBox cbox = (CheckBox)(convertView.findViewById(R.id.checkBox));
            checkBox.setChecked(isCheck.get(position));
            checkBox.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if (isCheck.get(position)) {
                        isCheck.set(position,false);
                    } else {
                        isCheck.set(position,true);
                    }
                }
            });
            checkBox.setChecked(isCheck.get(position));
        }*/

        return convertView;

    }
}
