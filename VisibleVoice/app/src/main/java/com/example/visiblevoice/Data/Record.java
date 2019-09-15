package com.example.visiblevoice.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import com.google.firebase.database.annotations.NotNull;

import java.io.File;

@Entity(tableName = "record")
public class Record {
    @PrimaryKey
    @NotNull
    public int id;
    public File music_file;
    public File json_file;
    public Bitmap wordcloud_img;
    public String full_path;
    public String file_name;

    public Record(String file_name, String full_path){
        this.file_name=file_name;
        this.full_path=full_path;
    }
    public Record(){}
    public Record(int id, File music_file, File json_file, Bitmap wordcloud_img) {
        this.id = id;
        this.music_file = music_file;
        this.json_file = json_file;
        this.wordcloud_img = wordcloud_img;
    }

    public File getMusic_file() {
        return music_file;
    }

    public void setMusic_file(File music_file) {
        this.music_file = music_file;
    }

    public File getJson_file() {
        return json_file;
    }

    public void setJson_file(File json_file) {
        this.json_file = json_file;
    }

    public Bitmap getWordcloud_img() {
        return wordcloud_img;
    }

    public void setWordcloud_img(Bitmap wordcloud_img) {
        this.wordcloud_img = wordcloud_img;
    }
}


