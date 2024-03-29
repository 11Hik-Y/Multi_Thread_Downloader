package com.hzxpowermode.core;

import com.hzxpowermode.constant.Constant;
import com.hzxpowermode.utils.HttpUtils;
import com.hzxpowermode.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

public class DownloaderTask implements Callable<Boolean> {
    private String url;
    private long startPos;
    private long endPos;
    private int part;

    public DownloaderTask(String url, long startPos, long endPos, int part) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
    }

    @Override
    public Boolean call() throws Exception {
        // 获取文件名
        String httpFileName = HttpUtils.getHttpFileName(url);
        // 分块的文件名
        httpFileName = httpFileName + ".temp" + part;
        // 下载路径
        httpFileName = Constant.PATH + httpFileName;

        // 获取分块下载的连接
        HttpURLConnection conn = HttpUtils.getHttpURLConnection(url, startPos, endPos);

        try (
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                RandomAccessFile w = new RandomAccessFile(httpFileName, "rw");
        ) {
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            int len = -1;
            while ((len = bis.read(buffer)) != -1) {
                // 统计一秒内的下载大小，通过原子类进行
                DownloadInfoThread.downSize.add(len);
                w.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("下载文件不存在{}", url);
        } catch (Exception e) {
            LogUtils.error("下载文件失败{}", url);
        }

        return null;
    }
}
