package com.example.recorderapp;

public class Record {

    private String mTitle;
    private String mDuration;

    public Record(String title, String mDuration) {
        this.mTitle = title;
        this.mDuration = mDuration;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDuration() {
        return mDuration;
    }
}
