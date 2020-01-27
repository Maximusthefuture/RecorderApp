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
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordingService extends Service {

    private static final String CHANNEL_ID = "MyChannel";
    private static final String TAG = "RecordingService";
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_PAUSE = "RECORD_SERVICE_ACTION_PAUSE";
    private static final String ACTION_PLAY = "RECORD_SERVICE_ACTION_PLAY";
    private static final String ACTION_STOP = "RECORD_SERVICE_ACTION_STOP";
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private String mFileName;
    private RecordBinder mRecordBinder = new RecordBinder();
    private OnRecordedCallback mOnRecordedCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        checkPermission();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mRecordBinder;
    }


    public class RecordBinder extends Binder {
        RecordingService getService() {
            return RecordingService.this;
        }
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
            mOnRecordedCallback.onRecordFinished();
            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        }
        startForeground(NOTIFICATION_ID, testNotification());
        return START_NOT_STICKY;
    }

    public boolean isRecording() {
        return isRecording;
    }


    public void setOnRecordCallBack(OnRecordedCallback onRecordCallBack) {
        mOnRecordedCallback = onRecordCallBack;
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
            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Recording");
            remoteViews.setChronometer(R.id.chromomener, SystemClock.elapsedRealtime() , null, true);
//            remoteViews.setTextViewText(R.id.duration_record_custom_notification, String.format("%02d", 1));
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
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date currectDate = new Date();


            Log.d(TAG, "recordStart: " + mFileName);
            mFileName = Environment.getExternalStorageDirectory() + "/Download/";
            mFileName += simpleDateFormat.format(currectDate) + " audiotest.3gpp";
//            String file = ContextCompat.getDataDir(this).getPath() + mFileName;

            releaseRecorder();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(mFileName);
            isRecording = true;
            mMediaRecorder.prepare();
            if (isRecording) {
                mMediaRecorder.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
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

}
