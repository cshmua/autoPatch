package sample.consts;

/**
 * GUI用到的常量
 */
public interface GUIconsts {

    String cacheDir = "C:/encrypt/";

    /**
     * war文件缓存，保存上次读取的war文件的上级
     */
    String srcCache = "C:/encrypt/fileCache.txt";

    /**
     * 补丁输出目录缓存
     */
    String targetDirCache = "C:/encrypt/targetDirCache.txt";
}
