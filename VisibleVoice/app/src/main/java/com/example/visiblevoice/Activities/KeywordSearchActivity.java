package com.example.visiblevoice.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visiblevoice.Adapter.KeywordSearchAdapter;
import com.example.visiblevoice.Data.Lyric;
import com.example.visiblevoice.R;
import com.example.visiblevoice.algorithm.KMP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class KeywordSearchActivity extends AppCompatActivity {
    private ArrayList<Lyric> currentLyricList;
    private ArrayList<Lyric> lyricList;
    private SearchView searchView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_keyword);
        Intent intent = getIntent();

        lyricList = new ArrayList<>();
        currentLyricList = new ArrayList<>();
        getDataFromFile(intent.getExtras().getString("filename"));
        Log.d("searchActivity-json", intent.getExtras().getString("filename"));
        try{
            searchView = findViewById(R.id.keywordSearchView);
            searchView.setOnQueryTextListener(new SearchRequestListener());
            listView = findViewById(R.id.sentenceListView);
            listView.setOnItemClickListener(new itemClickListener());
        }catch(NullPointerException ne) {
            ne.printStackTrace();
        }

    }

    private void getDataFromFile(String filename) {
        try {
            String json = readJsonFromFile(filename);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray sentences = jsonObject.getJSONArray("sentences");

            for(int i = 0; i < sentences.length(); i++) {
                JSONObject o = sentences.getJSONObject(i);
                lyricList.add(new Lyric(Float.parseFloat(o.getJSONArray("words").getJSONObject(0).getString("start_time")), o.getString("sentence")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }

    private String readJsonFromFile(String filename) throws NullPointerException {
        try {
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

    private void search(String keyword) {
        currentLyricList.clear();
        ArrayList<ArrayList<Integer>> posLists = new ArrayList<>();

        for(Lyric lyric : lyricList) {
            ArrayList<Integer> list = KMP.keywordSearch(lyric.getText(), keyword);
            if(list.size() != 0) {
                posLists.add(list);
                currentLyricList.add(lyric);
            }
        }

        listView.setAdapter(new KeywordSearchAdapter(currentLyricList, posLists, keyword));
    }

    private class SearchRequestListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String keyword) {
            search(keyword);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String keyword) {
            search(keyword);
            return true;
        }
    }

    private class itemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //  출처: https://liveonthekeyboard.tistory.com/entry/안드로이드-startActivityForResult-onActivityResult-사용법 [키위남]
           /* Intent resultIntent = new Intent();
            resultIntent.putExtra("timeResult", currentLyricList.get(i).getStartTime());
            listView.getItemAtPosition(i);
            setResult(RESULT_OK,resultIntent);*/
            ((MainActivity)MainActivity.mContext).move_music( currentLyricList.get(i));
            finish();
        }
    }
}


