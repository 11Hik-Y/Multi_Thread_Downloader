package com.hzxpowermode.utils;

import java.io.File;

/**
 * 文件工具类
 */
public class FileUtils {
    public static long getFileContentLength(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() ? file.length() : 0L;
    }
}
