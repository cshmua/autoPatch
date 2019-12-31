package autopatch;

import autopatch.domain.PatchPackage;
import autopatch.domain.PatchUrl;
import autopatch.helper.GitHelper;
import autopatch.helper.JARFileExtract;
import autopatch.helper.DiffFilesInCommit;
import autopatch.utils.JARFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import java.util.List;

/***
 * 打包调用的实际方法
 * @author CSH
 * @since 2019/12/31
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
        PatchPackage patchPackage = JARFileUtils.getPatchPackage(destPath,fileNames);
        System.out.println("******************************************打包开始");
        if(patchPackage.isWar()) {
            //先写入.jar文件
            JARFileExtract.extractJARsToTempFolder(destPath, patchPackage);
            //再循环写入其他文件
            JARFileExtract.extractResourcesToTempFolder(destPath, patchPackage);
            //删除tmp文件
            JARFileExtract.deleteTmpJars(PatchUrl.getInstance().getDestPath(), patchPackage.getJarFiles());
        }else{
            //jar包直接解压
            JARFileExtract.extractResourcesToTempFolder(destPath, patchPackage);
        }
        System.out.println("******************************************打包结束");
        String resultcnt = "本次打包补丁数:"+PatchUrl.getInstance().count+"\n";
        System.out.println(resultcnt);
        PatchUrl.getInstance().resultMessage.insert(0,resultcnt);
    }
}
