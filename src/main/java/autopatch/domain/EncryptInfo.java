package autopatch.domain;

/**
 * 加密内容
 * @author CSH
 * @since 2022/6/1
 */
public class EncryptInfo {

    private volatile static EncryptInfo instance; //单例
    /**
     * 加密秘钥一般是票据编码
     */
    private String key;

    /**
     * 原文地址
     */
    private String srcFile;

    /**
     * 输出地址
     */
    private String dest;

    /**
     * 文件名
     */
    private String fileName;

    public volatile StringBuffer resultMessage;    //输出结果

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static EncryptInfo getInstance(){
        if(instance == null){
            synchronized(EncryptInfo.class){
                if(instance == null){
                    instance = new EncryptInfo();
                    instance.resultMessage = new StringBuffer();
                }
            }
        }
        return instance;
    }
}
