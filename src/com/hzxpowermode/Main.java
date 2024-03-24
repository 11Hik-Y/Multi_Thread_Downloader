package com.hzxpowermode;

import java.util.Scanner;

/*
    主类
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
        System.out.println(url);
    }
}
