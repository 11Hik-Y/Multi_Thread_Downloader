package com.hzxpowermode.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http相关工具类
 */
public class HttpUtils {

    public static long getFileContentLength(String url) throws IOException {
        long contentLength = 0;
        HttpURLConnection conn = null;
        //关闭资源
        try {
            conn = getHttpURLConnection(url);
            contentLength = conn.getContentLengthLong();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return contentLength;
    }

    public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws IOException {
        HttpURLConnection conn = getHttpURLConnection(url);
        LogUtils.info("下载的区间为：{} - {}", startPos, endPos);

        if (endPos != 0) {
            conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
        } else {
            conn.setRequestProperty("Range", "bytes=" + startPos + "-");
        }
        return conn;
    }

    /**
     * 获取HttpURLConnection连接对象
     *
     * @param url 文件地址
     * @return HttpURLConnection连接对象
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
        // 设置浏览器标识信息，模拟浏览器
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");

        return conn;
    }

    /**
     * 获取下载的文件的名字
     *
     * @param url 文件地址
     * @return 文件名
     */
    public static String getHttpFileName(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index + 1);
    }
}
