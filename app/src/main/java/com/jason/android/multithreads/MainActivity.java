package com.jason.android.multithreads;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jason.multithreads.DownloadCallback;
import com.jason.multithreads.DownloadManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownloadCallback {
    public static final String TAG = "MainActivity";
    private DownloadManager downloadManager;

//     String url = "http://dlsw.baidu.com/sw-search-sp/soft/7b/33461/freeime.1406862029.exe";
//     String name = "freeime.1406862029.exe";
//     String url = "http://wq.yeshitou.com/002.wmv";
//     String name = "teach002.wmv";

    String url = "http://wq.yeshitou.com/weiqin.zip";
    String name = "weiqin.zip";
    String dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        downloadManager = DownloadManager.getInstance(this);
        dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                downloadManager.start(url, dir, name, this);
                break;
            case R.id.button2:
                downloadManager.stop(url);
                break;
        }
    }

    @Override
    public void onPrepare() {
        Log.w(TAG, "onPrepare");
    }

    @Override
    public void onStart(String url, String name, long length) {
        Log.w(TAG, "onStart " + length);

    }

    @Override
    public void onProgress(int progress) {
        Log.w(TAG, "onProgress " + progress);

    }

    @Override
    public void onStop(int progress) {
        Log.w(TAG, "onStop " + progress);

    }

    @Override
    public void onFinish(File file) {
        Log.w(TAG, "onFinish " + file.getAbsolutePath());

    }

    @Override
    public void onError(int status, String msg) {
        Log.w(TAG, "onError " + msg);

    }
}
