package com.hzxpowermode.core;

import com.hzxpowermode.constant.Constant;
import com.hzxpowermode.utils.FileUtils;
import com.hzxpowermode.utils.HttpUtils;
import com.hzxpowermode.utils.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 下载器
 */
public class Downloader {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void download(String url) {
        //获取文件名
        String fileName = HttpUtils.getHttpFileName(url);
        //文件下载路径
        fileName = Constant.PATH + fileName;

        // 获取本地文件的大小
        long localFileLength = FileUtils.getFileContentLength(fileName);

        //获取连接对象
        HttpURLConnection conn = null;
        DownloadInfoThread downloadInfoThread = null;
        try {
            conn = HttpUtils.getHttpURLConnection(url);
            // 获取下载文件的大小
            long contentLength = conn.getContentLengthLong();

            // 判断文件是否已经下载
            if (localFileLength >= contentLength) {
                LogUtils.info("{} 文件已经下载完成，无需重复下载", fileName);
                return;
            }

            // 创建获取下载信息的任务对象
            downloadInfoThread = new DownloadInfoThread(contentLength);
            // 将任务交给线程执行，每隔一秒执行一次
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread, 1, 1, java.util.concurrent.TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // try-with-resource自动关闭io资源
        try (
                // 文件输入流
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                // 文件输出流
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))
        ) {
            // 定义一个1MB大小的缓冲数组，用于存储每次读取到的数据
            int len = -1;
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            while ((len = bis.read(buffer)) != -1) {
                // 计算已经下载的文件大小
                downloadInfoThread.cumulation(len);
                // 将读取到的数据写入到文件输出流中
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException f) {
            LogUtils.info("所下载的文件不存在 {}", url);
        } catch (Exception e) {
            LogUtils.error("文件下载失败");
        } finally {
            System.out.print("\r");
            LogUtils.info("文件下载完成");
            // 判断conn是否为空，进行conn连接的关闭操作
            if (conn != null) {
                conn.disconnect();
            }

            // 关闭线程池
            scheduledExecutorService.shutdownNow();
        }
    }
}
