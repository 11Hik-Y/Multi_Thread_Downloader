package com.hzxpowermode.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志工具类
 */
public class LogUtils {

    /**
     * 打印常规日志信息
     *
     * @param msg  信息
     * @param args 参数
     */
    public static void info(String msg, Object... args) {
        print(msg, "-info-", args);
    }

    /**
     * 打印错误日志信息
     *
     * @param msg  信息
     * @param args 参数
     */
    public static void error(String msg, Object... args) {
        print(msg, "-error-", args);
    }

    /**
     * 打印日志信息的私有方法，供内部方法调用
     *
     * @param msg  信息
     * @param args 参数
     */
    private static void print(String msg, String level, Object... args) {
        // 格式化信息，将信息拼接完成
        msg = String.format(msg.replace("{}", "%s"), args);
        // 获取当前线程名称
        String name = Thread.currentThread().getName();
        // 获取当前的时间
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 打印信息： 时间 线程名 日志级别 信息
        System.out.println(time + " " + name + level + msg);
    }
}
