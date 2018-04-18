package com.jason.multithreads.thread;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.bean.ThreadInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jason.multithreads.constants.Constants.POOL_SIZE;
import static com.jason.multithreads.constants.Constants.POOL_SIZE_MAX;

/**
 * Description: 任务池管理类
 * <p>
 * By: zhenzong on 2018/4/14 17:15<p>
 * Email: reozong@gmail.com<p>
 * Reference:
 */
public class TaskPoolManager {
    private static String TAG = "TaskPoolManager";
    /** 待执行的任务列表 */
    private static final List<TaskInfo> TASK_WAITING = Collections.synchronizedList(new ArrayList<TaskInfo>());
    /** 正在执行下载的任务列表 */
    private static final ConcurrentHashMap<String, TaskInfo> TASK_DOWNLOADING = new ConcurrentHashMap<>();
    /** 被暂停的任务列表 */
    private static final ConcurrentHashMap<String, TaskInfo> TASK_STOPPED = new ConcurrentHashMap<>();

    private static final BlockingQueue<Runnable> POOL_QUEUE_TASK = new LinkedBlockingQueue<>(56);


    /** 任务池 */
    private ThreadPoolExecutor TaskPool;
    /** 最大并发任务数 */
    private int maxTask = 5;

    private Context context;
    private static transient TaskPoolManager instance;

    private TaskPoolManager(Context context) {
        this.context = context.getApplicationContext();

        initPool();
    }

    private void initPool() {
        TaskPool = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE_MAX, 5, TimeUnit.SECONDS, POOL_QUEUE_TASK,
                new ThreadFactory() {
                    AtomicInteger COUNT = new AtomicInteger(1);

                    @Override
                    public Thread newThread(@NonNull Runnable runnable) {
                        return new Thread(runnable, "TaskInfo #" + COUNT.getAndIncrement());
                    }
                });
        TaskPool.allowCoreThreadTimeOut(true);
    }

    public void setMaxTask(int maxTask) {
        this.maxTask = maxTask;
    }

    public static TaskPoolManager getInstance(Context context) {
        if (instance == null) {
            synchronized (TaskPoolManager.class) {
                if (instance == null) {
                    instance = new TaskPoolManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 执行任务，如果任务池已满，则放入等待队列
     *
     * @param task
     */
    public void executeTask(DownloadTask task, TaskInfo taskInfo) {
        if (taskInfo.listener != null) {
            taskInfo.listener.onPrepare();
        }
        TASK_DOWNLOADING.put(taskInfo.url, taskInfo);
        TaskPool.execute(task);
    }

    public void addTaskWaiting(TaskInfo taskInfo) {
        TASK_WAITING.add(taskInfo);
    }

    /**
     * 根据url暂停一个下载任务
     *
     * @param url
     */
    public void stopTask(String url) {
        if (TASK_DOWNLOADING.containsKey(url)) {
            TaskInfo taskInfo = TASK_DOWNLOADING.get(url);
            taskInfo.isStop = true;
            for (ThreadInfo thread : taskInfo.threadInfos) {
                thread.isStop = true;
            }
        }
    }

    public boolean isFull() {
        return TASK_DOWNLOADING.size() >= maxTask;
    }

    public boolean isRunning(String url) {
        return TASK_DOWNLOADING.containsKey(url);
    }

    public boolean isStopped(String url) {
        return TASK_STOPPED.containsKey(url);
    }

    public TaskInfo getStoppedTask(String url) {
        return TASK_STOPPED.get(url);
    }

    public TaskInfo getDownloadingTask(String url) {
        return TASK_DOWNLOADING.get(url);
    }

    /**
     * 从等待队列中取出一个新任务去执行
     */
    public synchronized void nextTask() {
        if (!TASK_WAITING.isEmpty()) {
            TaskInfo taskInfo = TASK_WAITING.remove(0);
            DownloadTask task = new DownloadTask(context, taskInfo, taskInfo.listener);
            executeTask(task, taskInfo);
        }
    }

    /**
     * 任务结束，移除掉
     *
     * @param url
     */
    public synchronized void removeTask(String url) {
        TASK_DOWNLOADING.remove(url);
    }

    /**
     * 任务暂停，加入到暂停队列便于恢复下载，且从正在下载队列中移出
     *
     * @param taskInfo
     */
    public synchronized void taskStop(TaskInfo taskInfo) {
        TASK_STOPPED.put(taskInfo.url, taskInfo);
        TASK_DOWNLOADING.remove(taskInfo.url);
    }

}
