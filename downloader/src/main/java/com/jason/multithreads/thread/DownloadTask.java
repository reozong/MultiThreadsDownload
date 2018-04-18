package com.jason.multithreads.thread;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.jason.multithreads.DownloadCallback;
import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.bean.ThreadInfo;
import com.jason.multithreads.constants.Error;
import com.jason.multithreads.db.DbManager;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Description: 下载任务，定义开启下载任务时需要传入的参数等，一个文件对应一个下载任务，一个任务对应多个线程
 * <p>
 * By: zhenzong on 2018/4/7 17:11
 * Email: reozong@gmail.com
 */
public class DownloadTask implements Runnable, ThreadDownloadListener {
    private static String TAG = DownloadTask.class.getSimpleName();
    private static final int READ_TIMEOUT = 15000;
    /** 任务进度通知时间间隔 */
    private static final int UPDATE_PROGRESS_INTERVAL = 1000;

    private Context context;
    private DbManager dbManager;
    private TaskPoolManager taskPoolManager;


    private long lastTime;
    private int threadCount;
    private TaskInfo taskInfo;
    private DownloadCallback downloadListener;

    public DownloadTask(Context context, TaskInfo taskInfo, DownloadCallback listener) {
        this.context = context;
        this.taskInfo = taskInfo;
        this.downloadListener = listener;

        dbManager = DbManager.getInstance(context);
        taskPoolManager = TaskPoolManager.getInstance(context);
        // 初始化默认参数
        initTaskInfo(taskInfo);
    }

    /**
     * 初始化一些必要的信息
     *
     * @param taskInfo
     */
    private void initTaskInfo(TaskInfo taskInfo) {
        if (taskInfo.headers == null || taskInfo.headers.isEmpty()) {
            // 默认请求头参数
            this.taskInfo.setDefaultHeaders();
        }

        if (TextUtils.isEmpty(taskInfo.directory)) {
            taskInfo.directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        if (TextUtils.isEmpty(taskInfo.fileName)) {
            int indexOf = taskInfo.url.lastIndexOf("/");
            if (indexOf != -1) {
                taskInfo.fileName = taskInfo.url.substring(indexOf + 1);
            } else {
                taskInfo.fileName = UUID.randomUUID().toString();
            }
            Log.w(TAG, "fileName;" + taskInfo.fileName);
        }

        // 创建保存文件
        Utils.createFile(taskInfo.directory, taskInfo.fileName);
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            connection = buildConnection();
            addRequestHeaders(connection);
//            Utils.printHeader(connection);
            handleConnection(connection, connection.getResponseCode());
        } catch (IOException e) {
            onError(Error.ERROR_OPEN_CONNECT, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @NonNull
    private HttpURLConnection buildConnection() throws IOException {
        URL url = new URL(taskInfo.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(READ_TIMEOUT);
        return connection;
    }

    /**
     * 设置请求头参数
     *
     * @param connection
     */
    private void addRequestHeaders(HttpURLConnection connection) {
        for (TaskInfo.Header header : taskInfo.headers) {
            connection.addRequestProperty(header.key, header.value);
        }
    }

    private void handleConnection(HttpURLConnection connection, int code) throws IOException {
        Log.w(TAG, "responseCode:" + code);
        switch (code) {
            case HttpURLConnection.HTTP_OK:
                // 连接成功
                handleHttpOk(connection, code);
                break;
            case HttpURLConnection.HTTP_PARTIAL:
                Log.w(TAG, "连接成功 分段先不处理");
                onError(code, "分段先不处理");
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
            case HttpURLConnection.HTTP_NOT_MODIFIED:
            case 307:
                // 发生重定向
                Log.e(TAG, "重定向 暂不支持");
                onError(code, "重定向问题");
                break;
            default:
                // 连接失败
                Log.e(TAG, "连接失败");
                onError(code, connection.getResponseMessage());
                break;
        }
    }


    private void handleHttpOk(HttpURLConnection connection, int code) throws IOException {
        readResponseHeader(taskInfo, connection);

        if (taskInfo.totalBytes < 0 && downloadListener != null) {
            downloadListener.onError(Error.ERROR_EMPTY_LENGTH, Error.MSG_EMPTY_LENGTH);
            return;
        }

        taskInfo.file = new File(taskInfo.directory, taskInfo.fileName);
        if (taskInfo.file.exists() && taskInfo.file.length() == taskInfo.totalBytes) {
            onError(Error.ERROR_FINISHED_TASK, Error.MSG_FINISHED_TASK);
            return;
        }

        if (downloadListener != null)
            downloadListener.onStart(taskInfo.url, taskInfo.fileName, taskInfo.totalBytes);

        //分配线程去开启下载
        ThreadDispatcher.dispatchThread(context, taskInfo, this);
    }


    /**
     * 读取响应头信息，其中会获取资源总大小
     *
     * @param connection
     */
    private void readResponseHeader(TaskInfo taskInfo, HttpURLConnection connection) {
        Log.w(TAG, "连接成功 先读响应头获取取资源大小");

        taskInfo.disposition = connection.getHeaderField("Content-Disposition");
        taskInfo.location = connection.getHeaderField("Content-Location");
        taskInfo.mimeType = Utils.getMimeType(connection.getContentType());

        String length = connection.getHeaderField("Content-Length");
        try {
            taskInfo.totalBytes = Integer.parseInt(length);
        } catch (Exception e) {
            taskInfo.totalBytes = -1;
        }
    }

    private void onError(int code, String msg) {
        notifyError(code, msg);
        taskPoolManager.removeTask(taskInfo.url);
    }

    private void notifyError(int code, String msg) {
        if (downloadListener != null) {
            downloadListener.onError(code, msg);
        }
    }


    /**
     * 线程下载进度通知，由于可能是多线程下载，故加了一把锁，其他的同理
     *
     * @param progress
     */
    @Override
    public synchronized void onProgress(int progress) {
        taskInfo.currBytes += progress;
        long currTime = System.currentTimeMillis();
        if (currTime - lastTime >= UPDATE_PROGRESS_INTERVAL || lastTime <= 0L) {
            // 计算总进度
            int totalProgress = Utils.calculateProgress(taskInfo.currBytes, taskInfo.totalBytes);
            Log.w(TAG, "总进度:" + totalProgress + " 总大小：" + taskInfo.currBytes);
            if (downloadListener != null) {
                downloadListener.onProgress(totalProgress);
            }
            lastTime = currTime;
        }
    }

    /**
     * 下载暂停
     * 注意：只是分配给该任务的某个线程暂停了，并不一定代表整个任务暂停
     *
     * @param threadInfo
     */
    @Override
    public synchronized void onStop(ThreadInfo threadInfo) {
        Log.w(TAG, threadInfo.id + " 暂停下载，共下载了 " + threadInfo.start);
        // 更新本地线程进度信息
        dbManager.updateThread(threadInfo);

        // 由于一个任务可能对应多个线程，所以必须确保所有的线程都暂停了才能说明整个任务暂停了
        threadCount++;
        if (threadCount >= taskInfo.threadInfos.size()) {
            onTaskStop(taskInfo);
            threadCount = 0;
            if (downloadListener != null) {
                downloadListener.onStop(Utils.calculateProgress(taskInfo.currBytes, taskInfo.totalBytes));
            }
        }
    }

    /**
     * 任务暂停，需要保存当前进度
     */
    private void onTaskStop(TaskInfo taskInfo) {
        Log.w(TAG, "整个任务暂停");
        taskPoolManager.taskStop(taskInfo);

        dbManager.updateTask(taskInfo);
    }

    /**
     * 下载完成
     * 注意：只是分配给任务的某个线程下载完成，并不一定代表整个任务完成
     *
     * @param threadInfo
     */
    @Override
    public synchronized void onFinish(ThreadInfo threadInfo) {
        Log.w(TAG, threadInfo.id + " 片段下载完成");
        taskInfo.removeThreadInfo(threadInfo);
        dbManager.deleteThread(threadInfo.id);
        if (taskInfo.threadInfos.isEmpty()) {
            onTaskFinish(taskInfo);
            if (downloadListener != null) {
                downloadListener.onProgress(100);
                downloadListener.onFinish(taskInfo.file);
            }
            // 任务完成后，可能有其他正在等待执行的任务
            taskPoolManager.nextTask();
        }
    }

    private void onTaskFinish(TaskInfo taskInfo) {
        Log.w(TAG, "整个任务下载完成");
        taskPoolManager.removeTask(taskInfo.url);

        dbManager.deleteTask(taskInfo.url);
    }
}
