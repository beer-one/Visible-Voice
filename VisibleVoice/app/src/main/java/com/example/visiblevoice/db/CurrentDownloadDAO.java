package com.example.visiblevoice.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visiblevoice.models.CurrentDownload;


import java.util.List;

@Dao
public interface CurrentDownloadDAO {
    @Insert
    public void insert(CurrentDownload... currentDownloads);
    @Update
    public void update(CurrentDownload... currentDownloads);
    @Delete
    public void delete(CurrentDownload... currentDownloads);
    @Query("SELECT * FROM currentdownload")
    public List<CurrentDownload> getRecords();
    @Query("SELECT audioPath FROM currentdownload WHERE fileName=:fileName")
    public String getRecordMusicFileName(String fileName);
    @Query("SELECT JsonPath FROM currentdownload WHERE fileName=:fileName")
    public String getRecordJsonFileName(String fileName);
    @Query("SELECT wordCloudPath FROM currentdownload WHERE fileName=:fileName")
    public String getRecordPngFileName(String fileName);
    @Query("DELETE FROM currentdownload")
    public void clearRecordTable();
    @Query("DELETE FROM currentdownload WHERE fileName=:fileName")
    public void deleteRecord(String fileName);
    @Query("UPDATE currentdownload SET wordCloudPath=:wordCloudPath WHERE fileName=:fileName")
    public void updateWCPath(String fileName,String wordCloudPath);
    @Query("UPDATE currentdownload SET JsonPath=:JsonPath WHERE fileName=:fileName")
    public void updateJSONPath(String fileName,String JsonPath);
    @Query("SELECT COUNT(fileName) FROM currentdownload")
    public int getNumberRecord();
}
