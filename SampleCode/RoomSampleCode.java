public class RoomSampleCode{

    private RecordDAO recordDAO;
    public void main (String args[]){

        //insert
        recordDAO = Room.databaseBuilder(this, AppDatabase.class,"db-record" )
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getRecordDAO();

        Record record = new Record();
        record.setAudioPath("audiopath...");
        record.setWordCloudPath(null);
        record.setJsonPath(null);
        recordDAO.insert(record);



        //update
        recordDAO = Room.databaseBuilder(this, AppDatabase.class,"db-record" )
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getRecordDAO();

        Record record = new Record();
        record.setAudioPath("audiopath...");
        record.setWordCloudPath("wordcloudpath...");
        record.setJsonPath("jsonpath...");
        recordDAO.update(record);

    }
}