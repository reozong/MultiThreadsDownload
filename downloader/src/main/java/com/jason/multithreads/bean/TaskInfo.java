package com.jason.multithreads.bean;

import com.jason.multithreads.DownloadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * By: zhenzong on 2018/4/6 20:55
 * Email: reozong@gmail.com
 * Reference:
 */
public class TaskInfo {

    public TaskInfo() {
        threadInfos = new ArrayList<>();
    }

    /** 初始url */
    public String url;
    /** 被重定向之后的地址 */
    public String redirUrl;
    /** 下载后保存文件夹 */
    public String directory;
    /** 保存文件名 */
    public String fileName;
    /** 保存文件 */
    public File file;
    public String mimeType;
    public int totalBytes;
    public int currBytes;
    /**
     * 实体标签，Etag是属于HTTP 1.1属性，它是由服务器生成返回给前端，
     * 当你第一次发起HTTP请求时，服务器会返回一个Etag，并在你第二次发起同一个请求时，
     * 客户端会同时发送一个If-None-Match，而它的值就是Etag的值（此处由发起请求的客户端来设置）。
     * 然后，服务器会比对这个客服端发送过来的Etag是否与服务器的相同，
     * 如果相同，就将If-None-Match的值设为false，返回状态为304，客户端继续使用本地缓存，不解析服务器返回的数据
     * 如果不相同，就将If-None-Match的值设为true，返回状态为200，客户端重新解析服务器返回的数据
     * 广泛用于断点下载
     * 说白了，
     * ETag 实体标签: 一般为资源实体的哈希值
     * 即ETag就是服务器生成的一个标记，用来标识返回值是否有变化。
     * 且Etag的优先级高于Last-Modified。
     **/
    public String eTag;
    /***
     * Content-Disposition就是当用户想把请求所得的内容存为一个文件的时候提供一个默认的文件名
     */
    public String disposition;
    /**
     * 用于重定向接收者到一个新URI地址
     */
    public String location;
    public DownloadCallback listener;
    /** 标识任务是否是从内存恢复的 */
    public boolean isResume;
    /** 标识任务是否应该暂停 */
    public boolean isStop;
    public List<ThreadInfo> threadInfos;
    public List<Header> headers;

    public void addThreadInfo(ThreadInfo threadInfo) {
        threadInfos.add(threadInfo);
    }

    public void removeThreadInfo(ThreadInfo threadInfo) {
        threadInfos.remove(threadInfo);
    }

    public void setDefaultHeaders() {
        if (headers == null || headers.isEmpty()) {
            headers = new ArrayList<>();
            headers.add(new Header("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg," +
                    "application/x-shockwave-flash, application/xaml+xml," +
                    "application/vnd.ms-xpsdocument, application/x-ms-xbap," +
                    "application/x-ms-application, application/vnd.ms-excel," +
                    "application/vnd.ms-powerpoint, application/msword, */*"));

            headers.add(new Header("Accept-Ranges", "bytes"));
            headers.add(new Header("Charset", "UTF-8"));
            headers.add(new Header("Connection", "Keep-Alive"));
            headers.add(new Header("Accept-Encoding", "identity"));
        }
    }

    /**
     * 自定义的请求头
     */
    public static class Header {
        public String key;
        public String value;

        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
