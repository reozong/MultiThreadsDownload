package com.jason.multithreads.db;

import com.jason.multithreads.bean.ThreadInfo;

import java.util.List;

/**
 * @Description: 定义访问保存有下载线程信息的数据库接口
 *
 * @By: zhenzong on 2018/4/7 10:54
 * @Email: reozong@gmail.com
 * @Reference:
 */
interface ThreadInfoDAO {

    /**
     * 插入一条线程信息
     */
    void insertThread(ThreadInfo threadInfo);

    /**
     * 删除一条线程信息
     */
    void deleteThread(String id);

    /**
     * 删除所有相关线程信息
     */
    void deleteAllThreads(String url);

    /**
     * 更新已存在线程信息
     */
    void updateThread(ThreadInfo threadInfo);

    /**
     * 通过id查询某条信息
     */
    ThreadInfo queryThread(String id);

    /**
     * 通过url可以查询到所有相关线程信息
     */
    List<ThreadInfo> queryAllThreads(String url);
}
