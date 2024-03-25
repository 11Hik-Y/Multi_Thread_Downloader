package com.hzxpowermode;

import com.hzxpowermode.core.Downloader;
import com.hzxpowermode.utils.LogUtils;

import java.util.Scanner;

/**
 * 主类
 */
public class Main {
    public static void main(String[] args) {
        String url = null;
        if (args == null || args.length == 0) {
            for (; ; ) {
                LogUtils.info("请输入下载地址：");
                Scanner sc = new Scanner(System.in);
                url = sc.nextLine();
                if (url != null && url.length() != 0) {
                    break;
                }
            }
        } else {
            url = args[0];
        }

        Downloader downloader = new Downloader();
        long start = System.currentTimeMillis();
        downloader.download(url);
        long end = System.currentTimeMillis();
        double time = (double) (end - start) / 1000;
        LogUtils.info("文件下载完成 总耗时为 {}s", time);
    }
}
