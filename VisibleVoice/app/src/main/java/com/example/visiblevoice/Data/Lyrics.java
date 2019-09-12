package com.example.visiblevoice.Data;

public class Lyrics {
    private int startTime;
    private int finishTime;
    private String text;

    public Lyrics(){ }
    public Lyrics(int startTime,String text){
        this.startTime=startTime;
        this.text=text;
    }
    public Lyrics(int startTime,int finishTime,String text){
        this.startTime=startTime;
        this.text=text;
        this.finishTime=finishTime;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getFinishTime() { return finishTime; }
    public void setFinishTime(int finishTime) { this.finishTime = finishTime; }
    public int getStartTime() { return startTime; }
    public void setStartTime(int startTime) { this.startTime = startTime; }
}
