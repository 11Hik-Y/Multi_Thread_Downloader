package com.hzxpowermode;

import com.hzxpowermode.core.Downloader;

import java.util.Scanner;

/**
 * 主类
 */
public class Main {
    public static void main(String[] args) {
        String url = null;
        if (args == null || args.length == 0) {
            for (; ; ) {
                System.out.println("请输入下载地址");
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
        System.out.println("下载时间为：" + (double) ((end - start) / 1000));
    }
}
