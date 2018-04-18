package com.jason.multithreads.db;

import com.jason.multithreads.bean.TaskInfo;

/**
 * @Description: 定义访问保存有下载任务信息的数据库接口
 *
 * @By: zhenzong on 2018/4/7 10:55
 * @Email: reozong@gmail.com
 * @Reference:
 */
interface TaskInfoDAO {
    /**
     * 插入一条下载信息
     */
    void insertTask(TaskInfo taskInfo);

    /**
     * 删除一条下载信息
     *
     * @param url 下载地址
     */
    void deleteTask(String url);

    /**
     * 更新已存在下载信息
     */
    void updateTask(TaskInfo threadInfo);

    /**
     * 查询某条信息
     *
     * @param url 下载地址
     */
    TaskInfo queryTask(String url);
}
