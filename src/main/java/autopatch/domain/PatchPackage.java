package autopatch.domain;

import java.util.List;
import java.util.Set;

/**
 * 待打包的补丁实体类
 * @author CSH
 * @since 2019/4/11
 */
public class PatchPackage {

    /**
     * 文件名称(war包包含.jar、.js和.jsp文件)
     */
    private String name;

    /**
     * 是否是war包
     */
    private boolean isWar;

    /**
     * 补丁文件集合2.0
     */
    private List<PatchInfo> patchInfos;

    /**
     * war包时单独保存jar文件补丁包，jar包时为空
     */
    private List<String> jarFiles;
    /**
     * 版本号
     */
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWar() {
        return isWar;
    }

    public void setWar(boolean war) {
        isWar = war;
    }

    public List<String> getJarFiles() {
        return jarFiles;
    }

    public void setJarFiles(List<String> jarFiles) {
        this.jarFiles = jarFiles;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<PatchInfo> getPatchInfos() {
        return patchInfos;
    }

    public void setPatchInfos(List<PatchInfo> patchInfos) {
        this.patchInfos = patchInfos;
    }
}
