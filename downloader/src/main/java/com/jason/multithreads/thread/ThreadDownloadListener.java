package com.jason.multithreads.thread;

import com.jason.multithreads.bean.ThreadInfo;

/**
 * @Description: 线程下载中的回调接口
 *
 * @By: zhenzong on 2018/4/7 17:19
 * @Email: reozong@gmail.com
 * @Reference:
 */
interface ThreadDownloadListener {
    void onProgress(int progress);

    /**
     * 下载暂停
     *
     * @param threadInfo
     */
    void onStop(ThreadInfo threadInfo);

    void onFinish(ThreadInfo threadInfo);
}
