package com.jason.multithreads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.bean.ThreadInfo;
import com.jason.multithreads.constants.Error;
import com.jason.multithreads.db.DbManager;
import com.jason.multithreads.thread.DownloadTask;
import com.jason.multithreads.thread.TaskPoolManager;

import java.io.File;

/**
 * @Description:
 *
 * @By: zhenzong on 2018/4/8 21:16
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class DownloadManager {
    private static final String TAG = "DownloadManager";
    private Context context;
    private DbManager dbManager;
    private TaskPoolManager taskPoolManager;

    private static transient DownloadManager instance;


    private DownloadManager(Context context) {
        this.context = context.getApplicationContext();
        dbManager = DbManager.getInstance(this.context);
        taskPoolManager = TaskPoolManager.getInstance(this.context);
    }

    public static DownloadManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 开启下载任务
     *
     * @param url       下载地址
     * @param directory 下载文件保存目录
     * @param fileName  下载文件保存名称
     * @param listener  回调
     */
    public void start(String url, String directory, String fileName, DownloadCallback listener) {
        boolean hasListener = listener != null;
        if (canStart(url, listener, hasListener)) {
            // 开启下载涉及：开启新任务，或者从内存、数据库恢复下载
            TaskInfo info = tryRecoveringTask(url);
            if (info == null) {
                // 没有数据可拿来恢复，需要新建任务
                info = createNewTask(url, directory, fileName);
            } else {
                // 恢复成功
                info.isResume = true;
                // 重置暂停标记，否则就算恢复下载也无法继续
                for (ThreadInfo thread : info.threadInfos) {
                    thread.isStop = false;
                }
            }
            info.listener = listener;

            if (taskPoolManager.isFull()) {
                taskPoolManager.addTaskWaiting(info);
            } else {
                // 准备开启任务了
                DownloadTask task = new DownloadTask(context, info, info.listener);
                taskPoolManager.executeTask(task, info);
            }
        }
    }

    /**
     * 检查是否满足开启开启下载任务的条件
     */
    private boolean canStart(String url, DownloadCallback downloadListener, boolean hasListener) {
        if (TextUtils.isEmpty(url)) {
            if (hasListener) {
                downloadListener.onError(Error.ERROR_INVALID_URL, Error.MSG_INVALID_URL);
            }
            return false;
        }

//        if (!Utils.isNetworkAvailable(context)) {
//            if (hasListener) {
//                downloadListener.onError(Error.ERROR_NOT_NETWORK, "Network is not available.");
//            }
//            return false;
//        }

        if (taskPoolManager.isRunning(url)) {
            if (hasListener) {
                downloadListener.onError(Error.ERROR_REPEAT_URL, Error.MSG_REPEAT_URL + " " + url);
            }
            return false;
        }
        return true;
    }

    @NonNull
    private TaskInfo createNewTask(String url, String directory, String fileName) {
        TaskInfo info = new TaskInfo();
        info.url = url;
        if (TextUtils.isEmpty(directory)) {
            directory = context.getExternalCacheDir().getAbsolutePath();
        }
        info.directory = directory;
        info.fileName = fileName;
        return info;
    }

    /**
     * 尝试从内存及数据库恢复下载任务
     *
     * @param url
     * @return
     */
    private TaskInfo tryRecoveringTask(String url) {
        TaskInfo info = null;
        if (taskPoolManager.isStopped(url)) {
            info = taskPoolManager.getStoppedTask(url);
        } else {
            info = tryRecoveringFromDb(url);
        }
        return info;
    }


    private TaskInfo tryRecoveringFromDb(String url) {
        TaskInfo info = dbManager.queryTask(url);
        if (info != null) {
            // 如果数据库中已存在该任务信息，需要全部恢复
            Log.w(TAG, "数据库中已存在该任务信息，可恢复断点下载");
            info.threadInfos.clear();
            info.threadInfos.addAll(dbManager.queryAllThreads(url));
        }
        return info;
    }

    /**
     * 根据Url取消一个下载任务。
     * 取消下载，需要先停止下载，再删除本地信息和数据库信息
     *
     * @param url
     */
    public void cancel(String url) {
        taskPoolManager.stopTask(url);

        TaskInfo taskInfo;
        if (taskPoolManager.isRunning(url)) {
            taskInfo = taskPoolManager.getDownloadingTask(url);
        } else {
            taskInfo = dbManager.queryTask(url);
        }
        if (taskInfo != null) {
            File file = new File(taskInfo.directory, taskInfo.fileName);
            if (file.exists()) file.delete();

            dbManager.deleteTask(url);
            dbManager.deleteAllThreads(url);
        }
    }

    /**
     * 根据Url暂停一个下载任务
     *
     * @param url
     */
    public void stop(String url) {
        // 只是暂停，不需要删除什么信息
        taskPoolManager.stopTask(url);
    }

}
