package sample.consts;

/**
 * GUI用到的常量
 */
public interface GUIconsts {

    String cacheDir = "C:/autoPatch/";
    /**
     * git项目缓存，保存上次读取的git项目的上级
     */
    String gitCache = "C:/autoPatch/gitCache.txt";

    /**
     * war文件缓存，保存上次读取的war文件的上级
     */
    String warCache = "C:/autoPatch/warCache.txt";

    /**
     * 补丁输出目录缓存
     */
    String targetDirCache = "C:/autoPatch/targetDirCache.txt";
}
