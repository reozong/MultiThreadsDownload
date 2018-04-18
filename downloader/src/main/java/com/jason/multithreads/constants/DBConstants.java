package com.jason.multithreads.constants;

/**
 * @Description:
 *
 * @By: zhenzong on 2018/4/7 11:25
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class DBConstants {
    public static final String _ID = "_id";

    // 任务相关
    public static final String TB_TASK = "task_info";
    public static final String TB_TASK_URL = "base_url";
    public static final String TB_TASK_URL_REAL = "real_url";
    public static final String TB_TASK_DIR_PATH = "file_path";
    public static final String TB_TASK_CURRENT_BYTES = "currentBytes";
    public static final String TB_TASK_TOTAL_BYTES = "totalBytes";
    public static final String TB_TASK_FILE_NAME = "file_name";
    public static final String TB_TASK_MIME_TYPE = "mime_type";
    public static final String TB_TASK_ETAG = "e_tag";
    public static final String TB_TASK_DISPOSITION = "disposition";
    public static final String TB_TASK_LOCATION = "location";

    // 线程相关
    public static final String TB_THREAD = "thread_info";
    public static final String TB_THREAD_ID = "id";
    public static final String TB_THREAD_URL = "url";
    public static final String TB_THREAD_START = "start";
    public static final String TB_THREAD_END = "end";

    /** 建任务表 */
    public static final String SQL_CREATE_TASK = "CREATE TABLE " +
            TB_TASK + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TB_TASK_URL + " CHAR, " +
            TB_TASK_URL_REAL + " CHAR, " +
            TB_TASK_DIR_PATH + " CHAR, " +
            TB_TASK_FILE_NAME + " CHAR, " +
            TB_TASK_MIME_TYPE + " CHAR, " +
            TB_TASK_ETAG + " CHAR, " +
            TB_TASK_DISPOSITION + " CHAR, " +
            TB_TASK_LOCATION + " CHAR, " +
            TB_TASK_CURRENT_BYTES + " INTEGER, " +
            TB_TASK_TOTAL_BYTES + " INTEGER)";
    /** 建线程表 */
    public static final String SQL_CREATE_THREAD = "CREATE TABLE " +
            TB_THREAD + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TB_THREAD_URL + " CHAR, " +
            TB_THREAD_START + " INTEGER, " +
            TB_THREAD_END + " INTEGER, " +
            TB_THREAD_ID + " CHAR)";

    public static final String SQL_DROP_TASK = "DROP TABLE IF EXISTS " + TB_TASK;
    public static final String SQL_DROP_THREAD = "DROP TABLE IF EXISTS " + TB_THREAD;
}
