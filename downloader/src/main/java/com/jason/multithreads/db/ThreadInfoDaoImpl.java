package com.jason.multithreads.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jason.multithreads.bean.ThreadInfo;
import com.jason.multithreads.constants.DBConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 数据库线程信息表查询实现类
 *
 * @By: zhenzong on 2018/4/7 11:21
 * @Email: reozong@gmail.com
 * @Reference:
 */
class ThreadInfoDaoImpl implements ThreadInfoDAO {
    private DbHelper dbHelper;

    public ThreadInfoDaoImpl(Context context) {
        dbHelper = new DbHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ")
                .append(DBConstants.TB_THREAD).append("(")
                .append(DBConstants.TB_THREAD_ID).append(", ")
                .append(DBConstants.TB_THREAD_URL).append(", ")
                .append(DBConstants.TB_THREAD_START).append(", ")
                .append(DBConstants.TB_THREAD_END).append(") values (?,?,?,?)");
        db.execSQL(sqlBuilder.toString(),
                new Object[]{threadInfo.id, threadInfo.url, threadInfo.start, threadInfo.end});
        db.close();
    }

    @Override
    public void deleteThread(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = new StringBuilder()
                .append("delete from ")
                .append(DBConstants.TB_THREAD).append(" where ")
                .append(DBConstants.TB_THREAD_ID).append("=?")
                .toString();
        db.execSQL(sql, new String[]{id});
        db.close();
    }

    @Override
    public void deleteAllThreads(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = new StringBuilder()
                .append("delete from ")
                .append(DBConstants.TB_THREAD).append(" where ")
                .append(DBConstants.TB_THREAD_URL).append("=?")
                .toString();
        db.execSQL(sql, new String[]{url});
    }

    @Override
    public void updateThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = new StringBuilder()
                .append("update ")
                .append(DBConstants.TB_THREAD).append(" set ")
                .append(DBConstants.TB_THREAD_START).append("=? where ")
                .append(DBConstants.TB_THREAD_URL).append("=? and ")
                .append(DBConstants.TB_THREAD_ID).append("=?")
                .toString();
        db.execSQL(sql, new Object[]{threadInfo.start, threadInfo.url, threadInfo.id});
    }

    @Override
    public ThreadInfo queryThread(String id) {
        ThreadInfo info = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = new StringBuilder()
                .append("select ")
                .append(DBConstants.TB_THREAD_URL).append(", ")
                .append(DBConstants.TB_THREAD_START).append(", ")
                .append(DBConstants.TB_THREAD_END).append(" from ")
                .append(DBConstants.TB_THREAD).append(" where ")
                .append(DBConstants.TB_THREAD_ID).append("=?")
                .toString();
        Cursor cursor = db.rawQuery(sql, new String[]{id});
        if (cursor.moveToFirst()) {
            info = new ThreadInfo(id, cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
        }
        cursor.close();
        db.close();
        return info;
    }

    @Override
    public List<ThreadInfo> queryAllThreads(String url) {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        String sql = new StringBuilder()
                .append("select ")
                .append(DBConstants.TB_THREAD_ID).append(", ")
                .append(DBConstants.TB_THREAD_START).append(",")
                .append(DBConstants.TB_THREAD_END).append(" from ")
                .append(DBConstants.TB_THREAD).append(" where ")
                .append(DBConstants.TB_THREAD_URL).append("=?")
                .toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        while (cursor.moveToNext()) {
            ThreadInfo info = new ThreadInfo(cursor.getString(0), url, cursor.getInt(1), cursor.getInt(2));
            threadInfos.add(info);
        }
        cursor.close();
        db.close();
        return threadInfos;
    }
}
