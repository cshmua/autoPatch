package autopatch.utils;

import autopatch.constants.CommonConsts;
import autopatch.domain.PatchInfo;
import autopatch.domain.PatchPackage;
import autopatch.domain.PatchUrl;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

/**
 * @Description: 文件工具类
 * @author: CSH
 * @date: 2019年04月11日
 * @version: V1.0
 */
public class JARFileUtils {

    /**
     * 获取去掉文件后缀的文件名
     *
     * @param fileName 文件名
     * @return 去掉后缀的文件名
     */
    public static String getFileNameNoEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }

    public static String getFileNameEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                return fileName.substring(dot);
            }
        }
        return fileName;
    }

    public static String getRealFileName(String destPath) {
        if ((destPath != null) && (destPath.length() > 0)) {
            int dot = destPath.lastIndexOf(File.separator);
            if ((dot > -1) && (dot < (destPath.length()))) {
                return destPath.substring(dot + 1);
            }
        }
        return destPath;
    }

    public static String getFirstFileName(String destPath) {
        if ((destPath != null) && (destPath.length() > 0)) {
            int dot = destPath.indexOf('/');
            if ((dot > -1) && (dot < (destPath.length()))) {
                return destPath.substring(0, dot);
            }
        }
        return destPath;
    }

    public static PatchPackage getPatchPackage(String destPatch, List<String> fileNames) throws Exception {
        PatchPackage war = new PatchPackage();
        List<PatchInfo> patchInfos = new ArrayList<>(fileNames.size());
        war.setName(JARFileUtils.getRealFileName(destPatch));
        if (destPatch.endsWith(CommonConsts.WAR)) {
            war.setWar(true);
        } else {
            war.setWar(false);
        }
        List<String> jars = new ArrayList<>();
        Map<String, String> newJars = new HashMap<>();
        for (String fileName : fileNames) {
            PatchInfo patchInfo = new PatchInfo();
            String jar = fileName.split("/")[0];
            if (StringUtils.isBlank(newJars.get(jar))) {
                File pomFile = new File(PatchUrl.getInstance().getDirGit() + "/" + jar + "/pom.xml");
                String version = MavenUtils.getVersion(pomFile);
                newJars.put(jar, jar + "-" + version + CommonConsts.JAR);
            }
            patchInfo.setFileName(newJars.get(jar));
            patchInfo.setFliePath(fileName.substring(jar.length()+1));
            patchInfos.add(patchInfo);
            if (!jars.contains(newJars.get(jar))) {
                jars.add(newJars.get(jar));
            }

        }
        war.setJarFiles(jars);
        war.setPatchInfos(patchInfos);
        return war;
    }

}
