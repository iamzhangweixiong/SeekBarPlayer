package com.zhangwx.seekbarplayer;

import java.io.File;

/**
 * Created by zhangweixiong on 2017/9/1.
 */

public class Utils {
    public static boolean isFileExist(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return (file.exists() && file.isFile() && file.length()>0);
    }
}
