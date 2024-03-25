package com.hzxpowermode.core;

import com.hzxpowermode.constant.Constant;
import com.hzxpowermode.utils.HttpUtils;
import com.hzxpowermode.utils.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * 下载器
 */
public class Downloader {
    public void download(String url) {
        //获取文件名
        String fileName = HttpUtils.getHttpFileName(url);
        //文件下载路径
        fileName = Constant.PATH + fileName;

        //获取连接对象
        HttpURLConnection conn = null;
        try {
            conn = HttpUtils.getHttpURLConnection(url);

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
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
        } catch (FileNotFoundException f) {
            LogUtils.info("所下载的文件不存在 {}", url);
        } catch (Exception e) {
            LogUtils.error("文件下载失败");
        } finally {
            // 判断conn是否为空，进行conn连接的关闭操作
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
