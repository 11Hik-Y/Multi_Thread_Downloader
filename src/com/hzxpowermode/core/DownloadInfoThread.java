package com.hzxpowermode.core;

import com.hzxpowermode.constant.Constant;

public class DownloadInfoThread implements Runnable {
    // 下载文件的总大小
    private long httpFileContentLength;

    //已下载的文件的大小
    private double finishedSize;

    // 本次累计下载的文件大小
    private volatile double downSize;

    // 前一次下载的大小
    public double prevSize;

    public DownloadInfoThread(int httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }

    @Override
    public void run() {
        // 计算文件的总大小 单位：MB
        String fileSize = String.format("%.2f", httpFileContentLength / Constant.MB);

        // 计算每秒的下载速度 kb
        int speed = (int) ((downSize - prevSize) / Constant.KB);
        prevSize = downSize;

        // 剩余文件的大小
        double remainSize = httpFileContentLength - finishedSize - downSize;

        // 计算剩余时间
        String remainTime = String.format("%.1f", remainSize / Constant.KB / speed);

        if ("Infinity".equalsIgnoreCase(remainTime)) {
            remainTime = "-";
        }

        // 已下载的大小
        String currentFileSize = String.format("%.2f", (downSize - finishedSize) / Constant.MB);

        String downInfo = String.format("文件大小：%s MB，已下载：%s MB，速度：%s KB/s，剩余：%s MB，剩余时间：%s s",
                fileSize, currentFileSize, speed, remainSize / Constant.MB, remainTime);

        System.out.println("\r");
        System.out.println(downInfo);
    }
}
