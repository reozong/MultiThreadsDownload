package com.jason.multithreads.db;

import android.content.Context;

import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.bean.ThreadInfo;

import java.util.List;

/**
 * @Description: 数据库操作集中管理类
 *
 * @By: zhenzong on 2018/4/7 16:48
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class DbManager implements TaskInfoDAO, ThreadInfoDAO {
    private static transient DbManager dbManager;
    private ThreadInfoDAO threadInfoDAO;
    private TaskInfoDAO taskInfoDAO;

    private DbManager(Context context) {
        threadInfoDAO = new ThreadInfoDaoImpl(context.getApplicationContext());
        taskInfoDAO = new TaskInfoDaoImpl(context.getApplicationContext());
    }

    public static DbManager getInstance(Context context) {
        if (dbManager == null) {
            synchronized (DbManager.class) {
                if (dbManager == null) {
                    dbManager = new DbManager(context);
                }
            }
        }
        return dbManager;
    }

    @Override
    public void insertTask(TaskInfo taskInfo) {
        taskInfoDAO.insertTask(taskInfo);
    }

    @Override
    public void deleteTask(String url) {
        taskInfoDAO.deleteTask(url);
    }

    @Override
    public void updateTask(TaskInfo threadInfo) {
        taskInfoDAO.updateTask(threadInfo);
    }

    @Override
    public TaskInfo queryTask(String url) {
        return taskInfoDAO.queryTask(url);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        threadInfoDAO.insertThread(threadInfo);
    }

    @Override
    public void deleteThread(String id) {
        threadInfoDAO.deleteThread(id);
    }

    @Override
    public void deleteAllThreads(String url) {
        threadInfoDAO.deleteAllThreads(url);
    }

    @Override
    public void updateThread(ThreadInfo threadInfo) {
        threadInfoDAO.updateThread(threadInfo);
    }

    @Override
    public ThreadInfo queryThread(String id) {
        return threadInfoDAO.queryThread(id);
    }

    @Override
    public List<ThreadInfo> queryAllThreads(String url) {
        return threadInfoDAO.queryAllThreads(url);
    }

    public void stopTask(String url) {
        deleteTask(url);
        deleteAllThreads(url);
    }
}
