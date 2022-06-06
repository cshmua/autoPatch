package autopatch.utils;

import java.io.File;

/**
 * 文件工具类
 * @author CSH
 * @since 2019/12/31
 */
public class FileUtils {

    /**
     * 获取去掉文件后缀的文件名
     *
     * @param fileName 文件名
     * @return 去掉后缀的文件名
     */
    public static String getFileNameNoEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }

    public static String getFileNameEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                return fileName.substring(dot);
            }
        }
        return fileName;
    }

    public static String getRealFileName(String destPath) {
        if ((destPath != null) && (destPath.length() > 0)) {
            int dot = destPath.lastIndexOf(File.separator);
            if ((dot > -1) && (dot < (destPath.length()))) {
                return destPath.substring(dot + 1);
            }
        }
        return destPath;
    }

    public static String getFirstFileName(String destPath) {
        if ((destPath != null) && (destPath.length() > 0)) {
            int dot = destPath.indexOf('/');
            if ((dot > -1) && (dot < (destPath.length()))) {
                return destPath.substring(0, dot);
            }
        }
        return destPath;
    }


}
