package com.jason.multithreads.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jason.multithreads.bean.TaskInfo;
import com.jason.multithreads.constants.DBConstants;

/**
 * @Description:
 *
 * @By: zhenzong on 2018/4/7 11:20
 * @Email: reozong@gmail.com
 * @Reference:
 */
class TaskInfoDaoImpl implements TaskInfoDAO {
    private DbHelper dbHelper;

    public TaskInfoDaoImpl(Context context) {
        dbHelper = new DbHelper(context);
    }

    @Override
    public void insertTask(TaskInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = new StringBuilder()
                .append("insert into ")
                .append(DBConstants.TB_TASK).append("(")
                .append(DBConstants.TB_TASK_URL).append(", ")
                .append(DBConstants.TB_TASK_URL_REAL).append(", ")
                .append(DBConstants.TB_TASK_DIR_PATH).append(", ")
                .append(DBConstants.TB_TASK_FILE_NAME).append(", ")
                .append(DBConstants.TB_TASK_MIME_TYPE).append(", ")
                .append(DBConstants.TB_TASK_DISPOSITION).append(", ")
                .append(DBConstants.TB_TASK_LOCATION).append(", ")
                .append(DBConstants.TB_TASK_ETAG).append(", ")
                .append(DBConstants.TB_TASK_CURRENT_BYTES).append(", ")
                .append(DBConstants.TB_TASK_TOTAL_BYTES).append(") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .toString();
        db.execSQL(sql, new Object[]{info.url, info.redirUrl, info.directory, info.fileName, info.mimeType,
                info.disposition, info.location, info.eTag, info.currBytes, info.totalBytes
        });
        db.close();
    }

    @Override
    public void deleteTask(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = new StringBuilder()
                .append("delete from ")
                .append(DBConstants.TB_TASK)
                .append(" where ")
                .append(DBConstants.TB_TASK_URL).append("=?")
                .toString();
        db.execSQL(sql, new String[]{url});
        db.close();
    }

    @Override
    public void updateTask(TaskInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = new StringBuilder()
                .append("update ")
                .append(DBConstants.TB_TASK).append(" set ")
                .append(DBConstants.TB_TASK_DISPOSITION).append("=?, ")
                .append(DBConstants.TB_TASK_LOCATION).append("=?, ")
                .append(DBConstants.TB_TASK_MIME_TYPE).append("=?, ")
                .append(DBConstants.TB_TASK_TOTAL_BYTES).append("=?, ")
                .append(DBConstants.TB_TASK_FILE_NAME).append("=?, ")
                .append(DBConstants.TB_TASK_CURRENT_BYTES).append("=? where ")
                .append(DBConstants.TB_TASK_URL).append("=?")
                .toString();
        db.execSQL(sql, new Object[]{info.disposition, info.location, info.mimeType, info.totalBytes, info.fileName,
                info.currBytes, info.url
        });
        db.close();
    }

    @Override
    public TaskInfo queryTask(String url) {
        TaskInfo taskInfo = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = new StringBuilder()
                .append("select ")
                .append(DBConstants.TB_TASK_URL_REAL).append(", ")
                .append(DBConstants.TB_TASK_DIR_PATH).append(", ")
                .append(DBConstants.TB_TASK_FILE_NAME).append(", ")
                .append(DBConstants.TB_TASK_MIME_TYPE).append(", ")
                .append(DBConstants.TB_TASK_DISPOSITION).append(", ")
                .append(DBConstants.TB_TASK_LOCATION).append(", ")
                .append(DBConstants.TB_TASK_ETAG).append(", ")
                .append(DBConstants.TB_TASK_CURRENT_BYTES).append(", ")
                .append(DBConstants.TB_TASK_TOTAL_BYTES).append(" from ")
                .append(DBConstants.TB_TASK).append(" where ")
                .append(DBConstants.TB_TASK_URL).append("=?")
                .toString();
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        if (cursor.moveToFirst()) {
            taskInfo = new TaskInfo();
            taskInfo.url = url;
            taskInfo.redirUrl = cursor.getString(0);
            taskInfo.directory = cursor.getString(1);
            taskInfo.fileName = cursor.getString(2);
            taskInfo.mimeType = cursor.getString(3);
            taskInfo.disposition = cursor.getString(4);
            taskInfo.location = cursor.getString(5);
            taskInfo.eTag = cursor.getString(6);
            taskInfo.currBytes = cursor.getInt(7);
            taskInfo.totalBytes = cursor.getInt(8);
        }

        cursor.close();
        db.close();
        return taskInfo;
    }
}
