package com.jason.multithreads.thread;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 *
 * @By: zhenzong on 2018/4/8 20:37
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class Utils {

    public static synchronized boolean createFile(String path, String fileName) {
        boolean hasFile = false;
        try {
            File dir = new File(path);
            boolean hasDir = dir.exists() || dir.mkdirs();
            if (hasDir) {
                File file = new File(dir, fileName);
                hasFile = file.exists() || file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasFile;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return null != info && info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getMimeType(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }

        int index = content.indexOf(":");
        if (index != -1) {
            return content.substring(0, index).trim().toLowerCase();
        }

        return "";
    }


    /**
     * 打印请求头
     *
     * @param connection
     */
    public static void printHeader(HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if (headerFields != null && headerFields.size() > 0) {
            Iterator<Map.Entry<String, List<String>>> iterator = headerFields.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> next = iterator.next();
                Log.w("Utils", next.getKey() + " : " + next.getValue() + '\n');
            }
        }
    }

    public static int calculateProgress(int curr, int total) {
        return (int) (curr * 100.0f / total);
    }
}
