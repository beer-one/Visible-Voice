package com.example.visiblevoice.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.visiblevoice.Data.AppDataInfo;
import com.example.visiblevoice.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class WCFragment extends Fragment {

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