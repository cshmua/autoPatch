package autopatch.domain;

/**
 * 待抽取的补丁文件实体
 * @author CSH
 * @since 2019/12/5
 */
public class PatchInfo {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String fliePath;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFliePath() {
        return fliePath;
    }

    public void setFliePath(String fliePath) {
        this.fliePath = fliePath;
    }
}
