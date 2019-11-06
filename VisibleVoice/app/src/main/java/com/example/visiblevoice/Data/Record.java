package com.example.visiblevoice.Data;


import java.io.File;


public class Record {

    public File music_file;
    public File json_file;
    public File png_file;
    public String full_path;
    public String file_name;

    public Record(String file_name, String full_path){
        this.file_name=file_name;
        this.full_path=full_path;
    }
    public Record(String file_name, File music_file){
        this.file_name = file_name;
        this.music_file = music_file;
    }
    public Record(String file_name, File music_file, File json_file, File png_file){
        this.file_name = file_name;
        this.music_file = music_file;
        this.json_file = json_file;
        this.png_file = png_file;
    }
    public Record(){}

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

    public File getPng_file() {
        return png_file;
    }

    public void setPng_file(File png_file) {
        this.png_file = png_file;
    }
}


