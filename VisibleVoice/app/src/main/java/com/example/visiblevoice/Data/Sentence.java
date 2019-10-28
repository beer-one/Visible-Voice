package com.example.visiblevoice.Data;

public class Sentence {
    private String time;
    private String sentence;

    public Sentence(String time, String sentence) {
        this.time = time;
        this.sentence = sentence;
    }

    public void setTime(String time) { this.time = time; }
    public void setSentence(String sentence) { this.sentence = sentence; }
    public String getTime() { return time; }
    public String getSentence() { return sentence; }
}
