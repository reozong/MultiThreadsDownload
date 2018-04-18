package com.jason.multithreads.thread;

import android.content.Context;
import android.util.Log;

import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.bean.ThreadInfo;
import com.jason.multithreads.db.DbManager;

/**
 * -Description: 主要负责为任务分配线程进行下载
 *
 * -By: zhenzong on 2018/4/12 21:03
 * -Email: reozong@gmail.com
 * -Reference:
 */
class ThreadDispatcher {
    private static final String TAG = "ThreadDispatcher";

    /** 默认每个线程负责下载10Mb=10485760数据 */
    private static final int DEFAULT_LENGTH_PER_THREAD = 10485760;
    /** 每个线程至少下载5Mb */
    private static final int MIN_LENGTH = 5242880;

    private Context context;
    private TaskInfo taskInfo;
    private ThreadDownloadListener listener;

    private ThreadDispatcher(Context context, TaskInfo taskInfo, ThreadDownloadListener listener) {
        this.context = context;
        this.taskInfo = taskInfo;
        this.listener = listener;
    }

    public static void dispatchThread(Context context, TaskInfo taskInfo, ThreadDownloadListener listener) {
        new ThreadDispatcher(context, taskInfo, listener).dispatchThread();
    }

    /**
     * 为各个任务分配线程开启下载
     * 规则如下：
     * <p>
     * 每个线程默认负责10Mb数据；
     * 10Mb及以下，只分配1个线程；
     */
    private void dispatchThread() {
        if (taskInfo.isResume) {
            Log.w(TAG, "恢复下载");
            reuseStoppedThread();
        } else {
            Log.w(TAG, "新任务下载，记入数据库");
            DbManager.getInstance(context).insertTask(taskInfo);
            dispatchNewThread();
        }
    }

    /**
     * 恢复被暂停的任务并重用其线程信息
     */
    private void reuseStoppedThread() {
        for (ThreadInfo threadInfo : taskInfo.threadInfos) {
            DownloadThread thread = new DownloadThread(threadInfo, taskInfo, listener);
            Log.w(TAG, "线程" + threadInfo.id + " 恢复下载，从 " + threadInfo.start + " - " + threadInfo.end);
            DownloadPoolManager.getInstance().addThread(thread);
        }
    }

    /**
     * 新任务分配新线程
     */
    private void dispatchNewThread() {
        int totalLength = taskInfo.totalBytes;
        int threadCount = getThreadCount(totalLength);
        Log.w(TAG, "当前任务分配的线程数：" + threadCount);
        int start, end;

        for (int i = 0; i < threadCount; i++) {
            start = i * DEFAULT_LENGTH_PER_THREAD;
            end = start + DEFAULT_LENGTH_PER_THREAD - 1;
            if (i == threadCount - 1) {
                end = totalLength;
            }
            String id = String.valueOf(System.currentTimeMillis() + i + 1);


            ThreadInfo threadInfo = new ThreadInfo(id, taskInfo.url, start, end);
            taskInfo.addThreadInfo(threadInfo);
            // 新线程要保存数据库
            DbManager.getInstance(context).insertThread(threadInfo);

            Log.w(TAG, "线程" + (i + 1) + "负责：" + start + "-" + end);
            DownloadThread thread = new DownloadThread(threadInfo, taskInfo, listener);
            DownloadPoolManager.getInstance().addThread(thread);
        }
    }

    /**
     * 规则如下：
     * 每个线程默认负责10Mb数据，最大不超过（10+5）Mb，最少不小于5Mb；
     * 文件大小如果小于默认大小（10Mb），只分配一个线程；
     * 如果总大小对默认大小求余，余数Y不到5Mb，则余数部分不另外分配线程，即最后一个线程负责10+Y大小数据；
     *
     * @param totalLength
     * @return
     */
    private int getThreadCount(int totalLength) {
        int threadCount;
        if (totalLength <= DEFAULT_LENGTH_PER_THREAD) {
            // 只需要单个线程下载即可
            threadCount = 1;
        } else {
            // 启用多线程下载
            threadCount = totalLength / DEFAULT_LENGTH_PER_THREAD;
            long remains = totalLength % DEFAULT_LENGTH_PER_THREAD;
            if (remains > MIN_LENGTH) {
                threadCount++;
            }
        }
        return threadCount;
    }
}
