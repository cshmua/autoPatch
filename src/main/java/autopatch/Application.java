package autopatch;

import autopatch.domain.PatchPackage;
import autopatch.domain.PatchUrl;
import autopatch.helper.GitHelper;
import autopatch.helper.JARFileExtract;
import autopatch.porcelain.DiffFilesInCommit;
import autopatch.utils.JARFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description: Application$
 * @author: CSH
 * @date: 2019年04月10日
 * @version: V1.0
 */
public class Application {
    public static void main(String[] args) throws Exception {
        doGetPatch();
        System.out.println("******************************************打包结束");
        System.out.println("本次打包补丁数:"+ PatchUrl.getInstance().count);
    }

    public static void doGetPatch() throws Exception{
        List<String> fileNames;
        String destPath = PatchUrl.getInstance().getDestPath();
        //获取提交的补丁文件
        try (Repository repository = GitHelper.openJGitCookbookRepository()) {
            try (Git git = new Git(repository)) {
                if(StringUtils.isBlank(PatchUrl.getInstance().getOldCommit())){
                    fileNames = DiffFilesInCommit.listDiff(git,PatchUrl.getInstance().getNewCommit());
                } else {
                    fileNames = DiffFilesInCommit.listDiff(git, PatchUrl.getInstance().getOldCommit(), PatchUrl.getInstance().getNewCommit());
                }
            }
        }
        //获取待取出的补丁文件
        Map<String, PatchPackage> patchs = JARFileUtils.getPatchPackages(destPath,fileNames);
        //先写入.jar文件
        JARFileExtract.extractJARsToTempFolder(destPath,patchs);
        //再循环写入其他文件
        JARFileExtract.extractResourcesToTempFolder(destPath,patchs);
        //删除tmp文件
        JARFileExtract.deleteTmpJars(PatchUrl.getInstance().getDestPath(),patchs.get(JARFileUtils.getFileNameNoEx(JARFileUtils.getRealFileName(destPath))).getJarFiles());
        System.out.println("******************************************打包结束");
        System.out.println("本次打包补丁数:"+PatchUrl.getInstance().count);
        PatchUrl.getInstance().resultMessage.append("本次打包补丁数:").append(PatchUrl.getInstance().count);
    }
}
