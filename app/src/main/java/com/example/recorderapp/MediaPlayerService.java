package com.example.recorderapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;

public class MediaPlayerService extends Service  {
    private static final String TAG = "MediaPlayerService";
    private static final String MEDIA_CHANNEL_ID = "Media ChannelId";
    private static final int MEDIA_PLAYER_NOTIFICATION_ID = 2;
    private static final String ACTION_PAUSE = "MEDIA_SERVICE_ACTION_PAUSE";
    private static final String ACTION_PLAY = "MEDIA_SERVICE_ACTION_PLAY";
    private static final String ACTION_STOP = "MEDIA_SERVICE_ACTION_STOP";
    private boolean isPlaying = false;
    Messenger mMessenger;
    public static final String FILE = "fileName";
    private File mFile;
    private MediaPlayer mMediaPlayer;
    static final int MSG_PLAY = 1;
    String fileName;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(new PlayerHandler(this));
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

    }

    class PlayerHandler extends Handler {
        private Context mContext;

        public PlayerHandler(Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_PLAY:
                    mFile = (File) msg.getData().getSerializable(FILE);

//                    fileName = msg.getData().getString(FILE);
                    playSelected(mFile.getPath());
                    break;
                    default:
                        super.handleMessage(msg);

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {
            mFile = (File) intent.getSerializableExtra(FILE);
        }

        isPlaying = true;
        if (ACTION_PLAY.equals(intent.getAction())) {
//           mMediaPlayer.prepare();
            mMediaPlayer.start();
            Log.d(TAG, "onStartCommand: " + mFile.getPath());
            isPlaying = true;
        } else if (ACTION_PAUSE.equals(intent.getAction())) {
            Toast.makeText(this, "Player paused", Toast.LENGTH_SHORT).show();
            mMediaPlayer.pause();
            isPlaying = false;
        } else if (ACTION_STOP.equals(intent.getAction())) {
            mMediaPlayer.stop();
            stopSelf();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE);
            }
        }

        startForeground(2, createNotifications());


        return START_STICKY;
    }

    public Notification createNotifications() {


        Intent stopIntent = new Intent(getApplicationContext(), MediaPlayerService.class);

        Intent pauseIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        Intent playIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
        playIntent.setAction(ACTION_PLAY);
        stopIntent.setAction(ACTION_STOP);

        PendingIntent playPending = PendingIntent.getService(this, 0, playIntent, 0);
        PendingIntent pausePengingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        PendingIntent stopPedingIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setOnClickPendingIntent(R.id.stop, stopPedingIntent);

        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_pause_black_24dp);
            remoteViews.setOnClickPendingIntent(R.id.play, pausePengingIntent);
//            remoteViews.setTextViewText(R.id.duration_record_custom_notification, startTimer());
            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Playing");
            isPlaying = false;
        } else {
            remoteViews.setOnClickPendingIntent(R.id.play, playPending);
            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_play_arrow_black_24dp);
            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Pause");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MEDIA_CHANNEL_ID)
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
            NotificationChannel channel = new NotificationChannel(MEDIA_CHANNEL_ID, name, importance);
            channel.setDescription(desctription);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }


    public void playSelected(String fileName) {

        releaseMP();
        mMediaPlayer = new MediaPlayer();
        Toast.makeText(this, "" + fileName, Toast.LENGTH_SHORT).show();
        try {
            mMediaPlayer.setDataSource(fileName);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void releaseMP() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer = null;
    }

}
