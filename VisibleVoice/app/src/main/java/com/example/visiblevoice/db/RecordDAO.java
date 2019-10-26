package com.example.visiblevoice.db;

import com.example.visiblevoice.models.Record;
import java.util.List;
import androidx.room.*;

//query
@Dao
public interface RecordDAO {
    @Insert
    public void insert(Record... records);
    @Update
    public void update(Record... records);
    @Delete
    public void delete(Record... record);
    @Query("SELECT * FROM record")
    public List<Record> getToDos();
    @Query("SELECT * FROM record WHERE id = :number")
    public Record getToDoWithId(int number);

}
