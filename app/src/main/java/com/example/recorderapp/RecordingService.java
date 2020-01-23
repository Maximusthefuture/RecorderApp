package com.example.recorderapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordingService extends Service {

    public static final String CHANNEL_ID = "MyChannel";
    private static final String TAG = "RecordingService";
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_PAUSE = "RECORD_SERVICE_ACTION_PAUSE";
    private static final String ACTION_PLAY = "RECORD_SERVICE_ACTION_PLAY";
    private static final String ACTION_STOP = "RECORD_SERVICE_ACTION_STOP";
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    boolean isRecording = false;
    private List<Record> mRecordList;
    int seconds;

    String fileName;
    String time;
    OnRecordClickListener mOnRecordClickListener;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        checkPermission();
        mRecordList = new ArrayList<>();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_PLAY.equals(intent.getAction())) {
            recordStart();
            isRecording = true;
            Toast.makeText(this, "Record start", Toast.LENGTH_SHORT).show();
        }
        else if (ACTION_PAUSE.equals(intent.getAction()) && !isRecording) {
            recordPause();
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            isRecording = false;

        } else if (ACTION_STOP.equals(intent.getAction())) {
            recordStop();
            stopSelf();
            stopForeground(STOP_FOREGROUND_REMOVE);
            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        }
        startForeground(NOTIFICATION_ID, testNotification());
        return START_NOT_STICKY;
    }



    public void checkPermission() {
        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
    }

    public Notification testNotification() {


        Intent stopIntent = new Intent(getApplicationContext(), RecordingService.class);

        Intent pauseIntent = new Intent(getApplicationContext(), RecordingService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        Intent playIntent = new Intent(getApplicationContext(), RecordingService.class);
        playIntent.setAction(ACTION_PLAY);
        stopIntent.setAction(ACTION_STOP);

        PendingIntent playPending = PendingIntent.getService(this, 0, playIntent, 0);
        PendingIntent pausePengingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        PendingIntent stopPedingIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setOnClickPendingIntent(R.id.stop, stopPedingIntent);



        if (isRecording) {
            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_pause_black_24dp);
            remoteViews.setOnClickPendingIntent(R.id.play, pausePengingIntent);
//            remoteViews.setTextViewText(R.id.duration_record_custom_notification, startTimer());
            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Recording");
            isRecording = false;
        } else {
            remoteViews.setOnClickPendingIntent(R.id.play, playPending);
            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_play_arrow_black_24dp);
            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Pause");

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews);
//        Notification notification = builder.build();
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notification);

        return builder.build();

    }

    public List<Record> getRecords() {
        List<Record> recordList = new ArrayList<>();
        String name = Environment.getExternalStorageDirectory().toString() + "/Download";

        Log.d(TAG, "getRecords: " + name);

        File file = new File(name);

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            Record record = new Record(files[i].getName(), "");
            recordList.add(record);
//            Collections.sort(recordList, Collections.reverseOrder());

        }

        return recordList;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String desctription = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(desctription);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void updateNotification(Notification notification) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordStart() {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date currectDate = new Date();

        fileName = getExternalCacheDir().getAbsolutePath();

//        Log.d(TAG, "recordStart: " + fileName);
        fileName += "/" +  simpleDateFormat.format(currectDate) + " audiotest.3gp";


        releaseRecorder();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(fileName);
        isRecording = true;

        try {
            mMediaRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isRecording) {
            mMediaRecorder.start();
        }

    }

    public void releaseRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void recordStop() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            releaseRecorder();

            isRecording = false;
        }
    }

    public void recordPause() {
        if (mMediaRecorder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mMediaRecorder.pause();
            }
        }
    }

    public void playSelected() {
//        Toast.makeText(getApplicationContext(), "Plaing", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "playSelected: "+ "playing");
//        mMediaPlayer  = new MediaPlayer();
//        mMediaPlayer.selectTrack();
//        mMediaPlayer.start();
//        mOnRecordClickListener.onRecordClick();
    }
}
