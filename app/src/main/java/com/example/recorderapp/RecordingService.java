package com.example.recorderapp;

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
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;

public class RecordingService extends Service {

    public static final String CHANNEL_ID = "MyChannel";
    private static final String TAG = "RecordingService";
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_PAUSE = "RECORD_SERVICE_ACTION_PAUSE";
    private static final String ACTION_PLAY = "RECORD_SERVICE_ACTION_PLAY";
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    String fileName;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fileName = Environment.getExternalStorageDirectory() + "/something.3gpp";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_PLAY.equals(intent.getAction())) {
            recordStart();
            Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
        } else {
        }
        startForeground(NOTIFICATION_ID, testNotification());
        return START_NOT_STICKY;
    }



    public Notification testNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        Intent pauseIntent = new Intent(getApplicationContext(), RecordingService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        Intent playIntent = new Intent(getApplicationContext(), RecordingService.class);
        playIntent.setAction(ACTION_PLAY);

//        pauseIntent.putExtra(NOTIFICATION_ID, )

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, playIntent, 0);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setOnClickPendingIntent(R.id.play, pendingIntent);


//        remoteViews.setIntent(R.id.play, recordStart(), intent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews);
        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        return notification;

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

    public void recordStart() {
        try {
            releaseRecorder();

            File mOutFile = new File(fileName);
            if (mOutFile.exists()) {
                mOutFile.delete();
            }

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(fileName);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
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
        }
    }

}
