package autopatch.domain;

/**
 * 补丁打包的地址
 * @author CSH
 * @since 2019年04月10日
 * <pre>
 *     修改为界面获取，而不再读取配置文件
 * </pre>
 * @version  V2.0
 */
public class PatchUrl {

    private volatile static PatchUrl instance; //单例

    private String destPath;    //war包目录

    private String dirGit;      //项目的目录

    private String targetDir;   //补丁文件输出地址

    private String targetFileName;  //补丁文件输出名称

    private String oldCommit;   //上一次的commit

    private String newCommit;   //本次的commit

    private String version;     //版本号字段，非必填，供某些需要手动输入版本号的情况

    public volatile int count;   //打包的数量

    public volatile StringBuffer resultMessage;    //输出结果

    public String getDestPath() {
        return destPath;
    }

    private PatchUrl(){}

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getDirGit() {
        return dirGit;
    }

    public void setDirGit(String dirGit) {
        this.dirGit = dirGit;
    }

    public String getOldCommit() {
        return oldCommit;
    }

    public void setOldCommit(String oldCommit) {
        this.oldCommit = oldCommit;
    }

    public String getNewCommit() {
        return newCommit;
    }

    public void setNewCommit(String newCommit) {
        this.newCommit = newCommit;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static PatchUrl getInstance(){
        if(instance == null){
            synchronized(PatchUrl.class){
                if(instance == null){
                    instance = new PatchUrl();
                    instance.resultMessage = new StringBuffer();
                    /*InputStream is = PatchUrl.class.getResourceAsStream("/application.properties");
                    Properties prop = new Properties();
                    try {
                        prop.load(is);
                        instance.setDestPath(prop.getProperty("destPath"));
                        instance.setDirGit(prop.getProperty("git"));
                        instance.setSuffix(prop.getProperty("version"));
                        instance.setOldCommit(prop.getProperty("oldCommit"));
                        instance.setNewCommit(prop.getProperty("newCommit"));
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }
        }
        return instance;
    }
}
