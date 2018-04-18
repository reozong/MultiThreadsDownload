package com.jason.multithreads.bean;

/**
 * Description: 需要保存到数据库的下载线程信息类，便于实现断点下载
 *
 * By: zhenzong on 2018/4/6 21:03
 * Email: reozong@gmail.com
 * Reference:
 */
public class ThreadInfo {

    public String id;
    public String url;
    /** 下载起始点 */
    public int start;
    /** 下载结束点 */
    public int end;
    /** 下载暂停标记 */
    public boolean isStop;

    public ThreadInfo(String id, String url, int start, int end) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
    }
}
