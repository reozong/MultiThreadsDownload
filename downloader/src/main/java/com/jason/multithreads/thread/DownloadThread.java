package com.jason.multithreads.thread;

import android.util.Log;

import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.bean.ThreadInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description: 下载线程，一个下载任务对应多个下载线程
 *
 * By: zhenzong on 2018/4/7 17:11
 * Email: reozong@gmail.com
 * Reference:
 */
class DownloadThread implements Runnable {

    public static final int CONNECT_TIMEOUT = 15000;
    private static String TAG = DownloadThread.class.getSimpleName();

    private TaskInfo taskInfo;
    private ThreadInfo threadInfo;
    private ThreadDownloadListener listener;

    public DownloadThread(ThreadInfo threadInfo, TaskInfo taskInfo, ThreadDownloadListener listener) {
        this.threadInfo = threadInfo;
        this.taskInfo = taskInfo;
        this.listener = listener;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream is = null;
        try {
            URL url = new URL(taskInfo.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(CONNECT_TIMEOUT);
            connection.setRequestMethod("GET");
            // 注意请求头
            addRequestHeaders(connection);

            /**多线程断点下载关键类 RandomAccessFile*/
            randomAccessFile = new RandomAccessFile(taskInfo.file, "rwd");
            randomAccessFile.seek(threadInfo.start);

            //开读
            byte[] buffer = new byte[4096];
            int length = -1;
            is = connection.getInputStream();
            // 增加手动暂停条件判断
            while (!threadInfo.isStop && (length = is.read(buffer)) > -1) {
                randomAccessFile.write(buffer, 0, length);
                threadInfo.start += length;
                listener.onProgress(length);
            }

            if (threadInfo.isStop) {
                Log.w(TAG, threadInfo.id + " 手动暂停");
                listener.onStop(threadInfo);
            } else {
                //下载完成
                listener.onFinish(threadInfo);
            }
        } catch (IOException e) {
            Log.w(TAG, "异常暂停");
            listener.onStop(threadInfo);
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) randomAccessFile.close();
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) connection.disconnect();
        }
    }

    private void addRequestHeaders(HttpURLConnection connection) {
        for (TaskInfo.Header header : taskInfo.headers) {
            connection.addRequestProperty(header.key, header.value);
        }
        /** 断点下载关键代码*/
        connection.setRequestProperty("Range", "bytes=" + threadInfo.start + "-" + threadInfo.end);
    }
}
