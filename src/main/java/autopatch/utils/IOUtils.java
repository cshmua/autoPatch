package autopatch.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * 读写辅助工具
 * @author CSH
 * @since 2019/9/5
 */
public class IOUtils {

    /**
     * 创建缓存文件
     * @param fileName 文件名
     * @return 成功true 失败false
     */
    public static boolean createCache(String fileName){
        try {
            File target = new File(fileName);
            if(!target.getParentFile().exists()){
                target.getParentFile().mkdirs();
                return true;
            }
            if(!target.exists()){
                target.createNewFile();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }
    /**
     * 读缓存
     * @param cacheFile 缓存文件
     * @return 缓存内容
     */
    public static String readCache(File cacheFile){
        if (cacheFile.exists()) {
            try (InputStream inputStream = new FileInputStream(cacheFile)) {
                byte[] bytes = new byte[(int) cacheFile.length()];
                //读缓存里的数据
                inputStream.read(bytes);
                //返回
                return (new String(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 写入缓存文件
     * @param cacheFile 缓存文件
     * @param targetFileName 目标文件名
     */
    public static void writeCache(File cacheFile,String targetFileName){
        if (!StringUtils.isBlank(targetFileName)) {
            //将缓存写入cache
            try (OutputStream outputStream = new FileOutputStream(cacheFile)) {
                //写入
                byte[] bytes = targetFileName.getBytes();
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
