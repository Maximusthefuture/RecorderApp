package com.example.recorderapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permission = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ImageButton playButton;
    RecordsAdapter adapter;
    RecyclerView recyclerView;
    RecordingService recordingService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permission, REQUEST_RECORD_AUDIO_PERMISSION);
        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        findViewById(R.id.button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecordingService.class);
            startService(intent);

        });

        playButton = findViewById(R.id.button_play);
        playButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Play button clicked", Toast.LENGTH_SHORT).show();
        });

        init();
        Log.d(TAG, "recordStart: " + getDir("AudioFolder", MODE_PRIVATE));

    }

    public List<File> getFiles(File currectDirectory) {
        File[] files = currectDirectory.listFiles();
        if (files == null) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(files));
    }

    public void init() {
        File directoryFile = new File(Environment.getExternalStorageDirectory() + "/Download");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordsAdapter(mOnRecordClickListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recordingService = new RecordingService();
        adapter.setFiles(getFiles(directoryFile));
//        if (getIntent().getAction().equals("ACTION_STOP")) {
//            adapter.notifyDataSetChanged();
//        }
//        adapter.setRecordList(recordingService.getRecords());
//        adapter.notifyDataSetChanged();
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

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
    }

    OnRecordClickListener mOnRecordClickListener = (file -> {
//        recordingService.playSelected(file);
        startMediaPlayerService(file);

        Log.d(TAG, ": " + file);
    });

    public void startMediaPlayerService(File file) {
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra(MediaPlayerService.FILE, file);
        startService(intent);
    }


}
