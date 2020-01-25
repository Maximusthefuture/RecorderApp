package com.example.recorderapp;

import android.media.MediaMetadataRetriever;

import java.io.File;

public class Utils {

    public int getRecordLength(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getPath());
        String fileDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int seconds = (int) (( Long.parseLong(fileDuration) % (1000 * 60 * 60)) % 1000 * 60) / 1000;
        return seconds;
    }
}
