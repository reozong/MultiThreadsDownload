package com.jason.multithreads;

import java.io.File;

/**
 * @Description: 下载任务总回调
 *
 * @By: zhenzong on 2018/4/7 10:12
 * @Email: reozong@gmail.com
 * @Reference:
 */
public interface DownloadCallback {
    void onPrepare();

    void onStart(String url, String name, long length);

    void onProgress(int progress);

    void onStop(int progress);

    void onFinish(File file);

    void onError(int status, String msg);
}
