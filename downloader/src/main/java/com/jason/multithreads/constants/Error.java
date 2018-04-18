package com.jason.multithreads.constants;

public final class Error {
    private Error() {
    }

    /**
     * 没有网络
     */
    public static final int ERROR_NO_NETWORK = 0;
    /**
     * 创建文件失败
     */
    public static final int ERROR_CREATE_FILE = 1;
    /**
     * 无效Url，或者为空
     */
    public static final int ERROR_INVALID_URL = 2;
    /**
     * 服务器返回资源长度为0
     */
    public static final int ERROR_EMPTY_LENGTH = 3;
    /**
     * 重复的下载地址
     */
    public static final int ERROR_REPEAT_URL = 101;
    /**
     * 已下载完成的任务
     */
    public static final int ERROR_FINISHED_TASK = 102;
    /**
     * 建立连接出错
     */
    public static final int ERROR_OPEN_CONNECT = 138;
    /**
     * 无法获取真实下载地址
     */
    public static final int ERROR_CANNOT_GET_URL = 137;
    /**
     * 未能处理的重定向错误
     */
    public static final int ERROR_UNHANDLED_REDIRECT = 333;


    public static final String MSG_NOT_NETWORK = "没有网络";
    public static final String MSG_CREATE_FILE = "创建文件失败";
    public static final String MSG_INVALID_URL = "无效Url，或者为空";
    public static final String MSG_EMPTY_LENGTH = "服务器返回资源长度为0";
    public static final String MSG_REPEAT_URL = "重复的下载地址";
    public static final String MSG_FINISHED_TASK = "文件已存在，不需要再次下载";
}