package com.example.recorderapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

public class MediaPlayerService extends Service implements  MediaPlayer.OnPreparedListener{
    private static final String TAG = "MediaPlayerService";

    public static final String FILE = "fileName";
    private File mFile;
    private MediaPlayer mMediaPlayer;
    String fileName ="";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


//        mFile = new File()
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFile = (File) intent.getExtras().get(MediaPlayerService.FILE);
//        mFile = new File(fileName);
        Log.d(TAG, "onStartCommand: " + mFile);
        playSelected(mFile.getPath());
        return super.onStartCommand(intent, flags, startId);
    }



        public void playSelected(String fileName) {

        Log.d(TAG, "playSelected: "+ fileName);
        mMediaPlayer  = new MediaPlayer();
//        String title = record.getTitle();
//        Log.d(TAG, "playSelected: " + title);
        try {
            mMediaPlayer.setDataSource(fileName);
//            mMediaPlayer.setOnPreparedListener(this);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
