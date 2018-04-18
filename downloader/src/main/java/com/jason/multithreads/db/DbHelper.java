package com.jason.multithreads.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jason.multithreads.constants.DBConstants;

/**
 * @Description:
 *
 * @By: zhenzong on 2018/4/7 11:22
 * @Email: reozong@gmail.com
 * @Reference:
 */
class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "downloads.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstants.SQL_CREATE_TASK);
        db.execSQL(DBConstants.SQL_CREATE_THREAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBConstants.SQL_DROP_TASK);
        db.execSQL(DBConstants.SQL_DROP_THREAD);
    }
}
