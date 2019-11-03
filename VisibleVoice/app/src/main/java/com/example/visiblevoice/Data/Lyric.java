package com.example.visiblevoice.Data;

public class Lyric {
    private float startTime; // 시작 밀리세컨드
    //private int finishTime; // 끝나는 밀리세컨드
    private String text; // 해당 구간 가사

    public Lyric(){ }
    public Lyric(float startTime, String text){
        this.startTime=startTime;
        this.text=text;
    }
 /*   public Lyric(int startTime, int finishTime, String text){
        this.startTime=startTime;
        this.text=text;
        this.finishTime=finishTime;
    }*/

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    //public int getFinishTime() { return finishTime; }
    //public void setFinishTime(int finishTime) { this.finishTime = finishTime; }
    public float getStartTime() { return startTime; }
    public void setStartTime(float startTime) { this.startTime = startTime; }
    public String timeToString() {
        int hours = (int)startTime / 3600;
        int minutes = ((int)startTime % 3600) / 60;
        int seconds = ((int)startTime % 3600) % 60;
        String ret = hours == 0 ? "00:" : hours < 10 ? "0"+hours : hours+"";
        ret += (minutes == 0 ? "00:" : minutes < 10 ? "0"+minutes+":" : minutes+":");
        ret += (seconds == 0 ? "00" : seconds < 10 ? "0"+seconds : seconds+"");
        return ret;
    }
}
