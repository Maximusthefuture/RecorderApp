package com.example.recorderapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnRecordedCallback {

    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String TAG = "MainActivity";
    private RecordsAdapter adapter;
    private RecyclerView recyclerView;
    private RecordingService recordingService;
    private MediaPlayerService mMediaPlayerService;
    private OnRecordClickListener mOnRecordClickListener = (this::startMediaPlayerService);
    private boolean permissionToRecordAccepted = false;
    private String[] permission = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ImageButton playButton;
    private boolean mBound = false;
    private boolean isBound = false;
    private File directoryFile;
    private Messenger mMessenger;
    private ServiceConnection mPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            isBound = false;
        }
    };
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RecordingService.RecordBinder binder = (RecordingService.RecordBinder) service;
            recordingService = binder.getService();
            recordingService.setOnRecordCallBack(MainActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permission, REQUEST_RECORD_AUDIO_PERMISSION);
//        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        mMediaPlayerService = new MediaPlayerService();
        findViewById(R.id.button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecordingService.class);
            Toast.makeText(this, getString(R.string.press_to_record), Toast.LENGTH_SHORT).show();
            bindService(intent, mConnection, BIND_AUTO_CREATE);
            startService(intent);
        });

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBound = false;
        isBound = false;
        unbindService(mConnection);
        unbindService(mPlayerServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isBound) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            bindService(intent, mPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public List<File> getFiles(File currectDirectory) {
        File[] files = currectDirectory.listFiles();
        if (files == null) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(files));
    }

    public void init() {
        directoryFile = new File(Environment.getExternalStorageDirectory() + "/Download");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordsAdapter(mOnRecordClickListener);
        recordingService = new RecordingService();
        adapter.setFiles(getFiles(directoryFile));
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            finish();
        }
    }

    public void startMediaPlayerService(File file) {
        Message message = Message.obtain(null, MediaPlayerService.MSG_PLAY);
        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, mPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MediaPlayerService.FILE, file);
        message.setData(bundle);
        try {
            mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        startService(intent);
    }

    @Override
    public void onRecordFinished() {
        adapter.setFiles(getFiles(directoryFile));
        adapter.notifyDataSetChanged();
    }
}
