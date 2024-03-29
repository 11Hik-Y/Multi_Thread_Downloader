package com.hzxpowermode.core;

import com.hzxpowermode.constant.Constant;
import com.hzxpowermode.utils.FileUtils;
import com.hzxpowermode.utils.HttpUtils;
import com.hzxpowermode.utils.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * 下载器
 */
public class Downloader {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private ThreadPoolExecutor pool =
            new ThreadPoolExecutor(
                    Constant.THREAD_NUM,
                    Constant.THREAD_NUM,
                    0L,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(Constant.THREAD_NUM));

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

            if (contentLength == 0) {
                LogUtils.info("{} 文件无法下载", fileName);
                return;
            }

            // 判断文件是否已经下载
            if (localFileLength >= contentLength) {
                LogUtils.info("{} 文件已经下载完成，无需重复下载", fileName);
                return;
            }

            // 创建获取下载信息的任务对象
            downloadInfoThread = new DownloadInfoThread(contentLength);

            // 将任务交给线程执行，每隔一秒执行一次
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread, 1, 1, java.util.concurrent.TimeUnit.SECONDS);

            // 切分任务
            ArrayList<Future> list = new ArrayList<>();
            split(url, list);

            list.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });

            // 文件下载完毕之后
            // 合并
            if (merge(fileName)) {
                // 删除临时文件
                clearTempFile(fileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.print("\r");
            LogUtils.info("文件下载完成");
            // 判断conn是否为空，进行conn连接的关闭操作
            if (conn != null) {
                conn.disconnect();
            }
            // 关闭线程池
            scheduledExecutorService.shutdownNow();

            try {
                // 关闭线程池
                pool.shutdown();
                if (pool.awaitTermination(2, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 分割下载
     *
     * @param url        链接
     * @param futureList 任务集合
     */
    public void split(String url, ArrayList<Future> futureList) {
        try {
            // 获取下载文件的大小
            long contentLength = HttpUtils.getFileContentLength(url);

            // 计算切分后的文件大小
            long partSize = contentLength / Constant.THREAD_NUM;

            // 根据线程数量计算分块个数
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                // 计算下载的起始位置
                long startPos = i * partSize;
                // 计算结束位置
                long endPos = (i == Constant.THREAD_NUM - 1) ? 0 : (startPos + partSize - 1);

                // 创建任务对象
                DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i);

                // 将任务交到线程池
                Future<Boolean> future = pool.submit(downloaderTask);
                futureList.add(future);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件合并
     *
     * @param fileName 文件名
     * @return 是否合并成功
     */
    public boolean merge(String fileName) {
        System.out.println();
        LogUtils.info("开始合并文件 {}", fileName);
        byte[] buffer = new byte[Constant.BYTE_SIZE];
        int len = -1;
        try (
                RandomAccessFile r = new RandomAccessFile(fileName, "rw")
        ) {
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                try (
                        BufferedInputStream bis =
                                new BufferedInputStream(new FileInputStream(fileName + ".temp" + i))
                ) {
                    while ((len = bis.read(buffer)) != -1) {
                        r.write(buffer, 0, len);
                    }
                }
            }
            LogUtils.info("文件 {} 合并完成", fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清除临时文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean clearTempFile(String fileName) {
        for (int i = 0; i < Constant.THREAD_NUM; i++) {
            File f = new File(fileName + ".temp" + i);
            if (f.exists()) {
                f.delete();
            }
        }

        return true;
    }

}
