package com.example.recorderapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.SystemClock;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.File;

public class Utils {
    Constants f = new Constants();

    public int getRecordLength(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getPath());
        String fileDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int seconds = (int) (( Long.parseLong(fileDuration) % (1000 * 60 * 60)) % 1000 * 60) / 1000;
        return seconds;
    }

    public void startTimer() {
        new Runnable() {
            @Override
            public void run() {
                long tStart = 0L;
                long tMilisSec = SystemClock.uptimeMillis() - tStart;

            }
        };
    }


//    public Notification testNotification(Context context, boolean isRecording, String channelId) {


//        Intent stopIntent = new Intent(context, RecordingService.class);
//
//        Intent pauseIntent = new Intent(context, RecordingService.class);
//        pauseIntent.setAction(ACTION_PAUSE);
//        Intent playIntent = new Intent(context, RecordingService.class);
//        playIntent.setAction(ACTION_PLAY);
//        stopIntent.setAction(ACTION_STOP);

//        PendingIntent playPending = PendingIntent.getService(context, 0, playIntent, 0);
//        PendingIntent pausePengingIntent = PendingIntent.getService(context, 0, pauseIntent, 0);
//        PendingIntent stopPedingIntent = PendingIntent.getService(context, 0, stopIntent, 0);
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
//        remoteViews.setOnClickPendingIntent(R.id.stop, stopPedingIntent);

//        if (isRecording) {
//            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_pause_black_24dp);
//            remoteViews.setOnClickPendingIntent(R.id.play, pausePengingIntent);
////            remoteViews.setTextViewText(R.id.duration_record_custom_notification, startTimer());
//            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Recording");
//            isRecording = false;
//        } else {
//            remoteViews.setOnClickPendingIntent(R.id.play, playPending);
//            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_play_arrow_black_24dp);
//            remoteViews.setTextViewText(R.id.recorder_state_text_view, "Pause");
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.RECORD_SERVICE_CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContent(remoteViews);
//        Notification notification = builder.build();
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notification);

//        return builder.build();

//    }
}
