package com.example.visiblevoice.models;


import androidx.annotation.NonNull;
import androidx.room.*;

//table
@Entity(tableName = "record")
public class Record {
    @PrimaryKey
    @NonNull
    public String fileName;
    private String audioPath;
    private String wordCloudPath;
    private String JsonPath;

    @NonNull
    public String getFileName() {  return fileName;  }

    public void setFileName(@NonNull String fileName) {  this.fileName = fileName;  }

    public String getAudioPath() {  return audioPath; }

    public void setAudioPath(String audioPath) {   this.audioPath = audioPath;  }

    public String getWordCloudPath() {   return wordCloudPath;  }

    public void setWordCloudPath(String wordCloudPath) {  this.wordCloudPath = wordCloudPath; }

    public String getJsonPath() { return JsonPath; }

    public void setJsonPath(String jsonPath) {JsonPath = jsonPath; }

}
