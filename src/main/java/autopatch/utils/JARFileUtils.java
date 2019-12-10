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

    public static String WEB_INF = "/WEB-INF/";

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

    public static Map<String, PatchPackage> getPatchPackages(String destPatch, List<String> fileNames) throws Exception {
        PatchPackage war = new PatchPackage();
        war.setName(JARFileUtils.getRealFileName(destPatch));
        if (destPatch.endsWith(CommonConsts.WAR)) {
            war.setWar(true);
        } else {
            war.setWar(false);
        }
        List<String> jars = new ArrayList<String>();
        List<String> newFileNames = new ArrayList<>();
        Map<String, String> newJars = new HashMap<>();
        Map<String, PatchPackage> patchs = new HashMap<>();
        patchs.put(getFileNameNoEx(war.getName()), war);
        for (String fileName : fileNames) {
            String jar = fileName.split("/")[0];
            if (StringUtils.isBlank(newJars.get(jar))) {
                File pomFile = new File(PatchUrl.getInstance().getDirGit() + "/" + jar + "/pom.xml");
                newJars.put(jar, jar + "-" + MavenUtils.getVersion(pomFile) + CommonConsts.JAR);
            }
            if (!jars.contains(newJars.get(jar))) {
                jars.add(newJars.get(jar));
            }
            String newFileName = fileName.replaceAll(jar, newJars.get(jar));
            if (patchs.get(newJars.get(jar)) == null) {
                PatchPackage patchPackage = new PatchPackage();
                Set<String> fileSet = new HashSet<>();
                fileSet.add(newFileName);
                patchPackage.setPatchFiles(fileSet);
                patchs.put(newJars.get(jar), patchPackage);
            } else {
                patchs.get(newJars.get(jar)).getPatchFiles().add(newFileName);
            }
            newFileNames.add(newFileName);

        }
        war.setJarFiles(jars);
        war.setPatchFiles(new HashSet<>(newFileNames));
        return patchs;
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
        List<String> jars = new ArrayList<String>();
        List<String> newFileNames = new ArrayList<>();
        Map<String, String> newJars = new HashMap<>();
        Map<String, PatchPackage> patchs = new HashMap<>();
        patchs.put(getFileNameNoEx(war.getName()), war);
        for (String fileName : fileNames) {
            PatchInfo patchInfo = new PatchInfo();
            String jar = fileName.split("/")[0];
            if (StringUtils.isBlank(newJars.get(jar))) {
                File pomFile = new File(PatchUrl.getInstance().getDirGit() + "/" + jar + "/pom.xml");
                newJars.put(jar, jar + "-" + MavenUtils.getVersion(pomFile) + CommonConsts.JAR);
            }
            patchInfo.setFileName(newJars.get(jar));
            patchInfo.setFliePath(fileName.replaceAll(jar, newJars.get(jar)));
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
