package autopatch.helper;

import autopatch.domain.ComboBoxItem;
import autopatch.domain.PatchUrl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 获取两次git提交差异文件，即待提取的补丁文件
 * @author: CSH
 * @date: 2019年04月10日
 * @version: V1.0
 */
public class GitHelper {

    public static Repository openJGitCookbookRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.setGitDir(new File(PatchUrl.getInstance().getDirGit()+"/.git"));
        return builder.build();
    }

    public static List<ComboBoxItem> getRecentCommit() throws Exception{
        File file = new File(PatchUrl.getInstance().getDirGit()+"/.git");
        if (!file.exists()) {
            PatchUrl.getInstance().resultMessage.append("git项目路径错误;");
            throw new IllegalArgumentException("git项目路径错误：");
        }
        Git git = Git.open(file);
        int i = 0;
        List<ComboBoxItem> comboBoxItemList = new ArrayList<>();
        for (RevCommit revCommit : git.log().call()) {
            comboBoxItemList.add(new ComboBoxItem(revCommit.getId().getName(), revCommit.getShortMessage()));
            if (++i == 20) {//获取十次提交记录
                return comboBoxItemList;
            }
        }
        return comboBoxItemList;
    }
}
