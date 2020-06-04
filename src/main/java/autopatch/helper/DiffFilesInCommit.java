package autopatch.helper;

import autopatch.constants.CommonConsts;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 提取两次提交文件的新增或修改差异
 * @author CSH
 * @since 2019/12/31
 * <pre>
 * Simple snippet which shows how to retrieve the diffs
 * between two commits
 * </pre>
 */ 
public class DiffFilesInCommit {

    public static List<String> listDiff(Git git, String newCommit) throws GitAPIException, IOException {
        String oldCommit = null;
        for (RevCommit revCommit : git.log().call()) {
            if (revCommit.getId().getName().equals(newCommit)) {
                oldCommit = revCommit.getParent(0).getName();
                break;
            }
        }
        if(StringUtils.isBlank(oldCommit))
            throw new IOException("未找到上次提交！");
        return listDiff(git,oldCommit,newCommit);
    }

    public static List<String> listDiff(Git git, String oldCommit, String newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(git.getRepository(), oldCommit))
                .setNewTree(prepareTreeParser(git.getRepository(), newCommit))
                .call();

        System.out.println("Found: " + diffs.size() + " differences");
        List<String> fileNames = new ArrayList<>(diffs.size());
        int i = 0;
        for (DiffEntry diff : diffs) {
            switch (diff.getChangeType()) {
                case ADD:
                case MODIFY:
                    fileNames.add(getZIPAddress(diff.getNewPath()));
                    System.out.println(fileNames.get(i));
                    i++;
                    break;
                default:
                    break;
            }
        }
        return fileNames;
    }


    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }


    private static String getZIPAddress(String path){
        String [] tmp = path.split("/");
        //首项加上后缀
        //tmp[0] = tmp[0]+"-"+suffix+".jar";
        //尾项如果是.java文件转换成.class文件
        if(tmp[tmp.length-1].contains(CommonConsts.JAVA)){
            tmp[tmp.length-1] = tmp[tmp.length-1].replaceAll(CommonConsts.JAVA,CommonConsts.CLASS);
        }
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        sb.append(tmp[0]).append('/');
        for(int i = 1; i < tmp.length; i++){
            //过滤一些前置的多余的文件目录
            if(tmp[i].equals(CommonConsts.DIR_JAVA) || tmp[i].equals(CommonConsts.DIR_RESOURCES) || tmp[i].equals(CommonConsts.DIR_WEBAPP)) {
                flag = true;
                continue;
            }
            if(!flag){
                continue;
            }
            sb.append(tmp[i]).append('/');
        }
        if(!sb.toString().isEmpty())
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

/*    private static String getZIPAddress(String path){
        String [] tmp = path.split("/");
        //首项加上后缀
        //tmp[0] = tmp[0]+"-"+suffix+".jar";
        //尾项如果是.java文件转换成.class文件
        if(tmp[tmp.length-1].contains(CommonConsts.JAVA)){
            tmp[tmp.length-1] = tmp[tmp.length-1].replaceAll(CommonConsts.JAVA,CommonConsts.CLASS);
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < tmp.length; i++){
            if(i==1 || i==2 || i==3 ) {
                continue;
            }
            sb.append(tmp[i]).append('/');
        }
        if(!sb.toString().isEmpty())
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }*/
}
