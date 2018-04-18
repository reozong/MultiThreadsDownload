package com.jason.multithreads.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jason.multithreads.constants.Constants.POOL_SIZE;
import static com.jason.multithreads.constants.Constants.POOL_SIZE_MAX;

/**
 * Description: 线程池管理类
 *
 * By: zhenzong on 2018/4/14 17:18
 * Email: reozong@gmail.com
 * Reference:
 */
public class DownloadPoolManager {

    private static final BlockingQueue<Runnable> POOL_QUEUE_THREAD = new LinkedBlockingQueue<>(128);

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger COUNT = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "DownloadThread #" + COUNT.getAndIncrement());
        }
    };

    /** 下载线程池 */
    private static ThreadPoolExecutor ThreadPool;
    private static transient DownloadPoolManager instance;

    private DownloadPoolManager() {
        ThreadPool = new ThreadPoolExecutor(
                POOL_SIZE, POOL_SIZE_MAX, 5, TimeUnit.SECONDS, POOL_QUEUE_THREAD, THREAD_FACTORY);
        ThreadPool.allowCoreThreadTimeOut(true);
    }

    public static DownloadPoolManager getInstance() {
        if (instance == null) {
            synchronized (DownloadPoolManager.class) {
                if (instance == null) {
                    instance = new DownloadPoolManager();
                }
            }
        }
        return instance;
    }

    public void addThread(DownloadThread thread) {
        ThreadPool.execute(thread);
    }
}
