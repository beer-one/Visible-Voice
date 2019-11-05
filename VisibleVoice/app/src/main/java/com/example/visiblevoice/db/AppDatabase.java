package com.example.visiblevoice.db;

import com.example.visiblevoice.db.RecordDAO;
import com.example.visiblevoice.models.Record;

import androidx.room.Database;
import androidx.room.RoomDatabase;
//tables
@Database(entities = {Record.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordDAO getRecordDAO(); //query
}
