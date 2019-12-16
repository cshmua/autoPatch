package autopatch.helper;

import autopatch.constants.CommonConsts;
import autopatch.domain.PatchInfo;
import autopatch.domain.PatchPackage;
import autopatch.domain.PatchUrl;
import autopatch.utils.JARFileUtils;
import autopatch.utils.MavenUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Description: 解压jar包的方法
 * @author: CSH
 * @date: 2019年04月10日
 * @version: V1.0
 */
public class JARFileExtract {

    /**
     * 删除临时jars文件
     *
     * @param destPath     war包路径
     * @param fileJarNames jar包文件名集合
     */
    public static void deleteTmpJars(String destPath, List<String> fileJarNames) {
        for (String fileJarName : fileJarNames) {
            String dir = PatchUrl.getInstance().getTargetDir() + File.separator + PatchUrl.getInstance().getTargetFileName();
            //实际jar文件地址
            String jarPath = dir + "/" + "tmp-" + fileJarName;
            FileUtils.deleteQuietly(new File(jarPath));
        }
    }

    public static void extractJARsToTempFolder(String destPath, PatchPackage patchPackages) {
        if (patchPackages.isWar()) {
            try {
                JarFile jarFile = new JarFile(destPath);
                Enumeration<JarEntry> enums = jarFile.entries();
                //jar包文件
                List<String> fileJarNames = patchPackages.getJarFiles();
                //js和jsp文件
                List<String> fileJsAndJsp = new ArrayList<>();
                //war包名下的文件
                List<String> fileWebClasses = new ArrayList<>();
                for (PatchInfo patchInfo : patchPackages.getPatchInfos()) {
                    String file = patchInfo.getFliePath();
                    String fileName = patchInfo.getFileName();
                    if (file.contains(".js") || file.contains(".jsp") || file.contains(".css") || file.contains(".jpg") || file.contains(".png")) {
                        fileJsAndJsp.add(file);
                    }
                    if (fileName.contains(JARFileUtils.getFileNameNoEx(patchPackages.getName()))) {
                        fileWebClasses.add(file);
                    }
                }
                //String dir = JARFileUtils.getFileNameNoEx(destPath);
                String dir = PatchUrl.getInstance().getTargetDir() + File.separator + PatchUrl.getInstance().getTargetFileName();
                new File(dir).mkdirs();
                while (enums.hasMoreElements()) {
                    JarEntry entry = enums.nextElement();
                    //抽出临时的jar文件，供接下来抽取
                    for (String fileJarName : fileJarNames) {
                        if (entry.getName().contains(fileJarName) || entry.getName().contains(JARFileUtils.getFileNameNoEx(fileJarName))) {
                            String[] files = entry.getName().split("/");
                            String newFileName = files[files.length - 1];
                            File toWrite = new File(dir + "/" + "tmp-" + newFileName);
                            FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), toWrite);
                            System.out.println(entry.getName());
                        }
                    }
                    //在war包下的js文件直接抽取
                    for (String file : fileJsAndJsp) {
                        if (file.equals(entry.getName())) {
                            String toWrite = dir + "/" + entry.getName();
                            FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), new File(toWrite));
                            PatchUrl.getInstance().count++;
                            PatchUrl.getInstance().resultMessage.append(entry.getName()).append('\n');
                            System.out.println(entry.getName());
                        }
                    }
                    //本地打包的，包含WEB/classes/目录的，也抽取出来
                    for (String file : fileWebClasses) {
                        if ((CommonConsts.CLASSES_DIRECTORY + file).equals(entry.getName())
                                //内部类
                                || ((entry.getName().endsWith(".class")) && (JARFileUtils.getFileNameNoEx(entry.getName()).contains(JARFileUtils.getFileNameNoEx(file) + "$")))) {
                            String toWrite = dir + "/" + JARFileUtils.getFileNameNoEx(patchPackages.getName())+ CommonConsts.JAR + "/" + file;
                            FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), new File(toWrite));
                            PatchUrl.getInstance().count++;
                            PatchUrl.getInstance().resultMessage.append(entry.getName()).append('\n');
                            System.out.println(entry.getName());
                        }
                    }
                }
                jarFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("解压jia文件需要传入war包！");
        }
    }

    public static void extractResourcesToTempFolder(String destPath, PatchPackage patchPackage) {
        if (patchPackage.isWar()) {
            try {
                List<String> fileJarNames = patchPackage.getJarFiles();
                for (String fileJarName : fileJarNames) {
                    String dir = PatchUrl.getInstance().getTargetDir() + File.separator + PatchUrl.getInstance().getTargetFileName();
                    //实际jar文件地址
                    String jarPath = dir + "/" + "tmp-" + fileJarName;
                    JarFile jarFile;
                    try {
                        jarFile = new JarFile(jarPath);
                    } catch (Exception e) {
                        //war包不包含不报错，因为可能是本地打包，存在WEB/classes/下
                        if(!jarPath.contains(JARFileUtils.getFileNameNoEx(patchPackage.getName()))) {
                            PatchUrl.getInstance().resultMessage.append(jarPath).append(" 不存在;");
                            System.out.println(jarPath + " 不存在;");
                        }
                        continue;
                    }
                    //建立jar文件的目录
                    extractClassFileToFolder(patchPackage, dir, jarFile, fileJarName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String dir = PatchUrl.getInstance().getTargetDir() + File.separator + PatchUrl.getInstance().getTargetFileName();
            JarFile jarFile;
            try {
                jarFile = new JarFile(destPath);
                String fileJarName = JARFileUtils.getRealFileName(destPath);
                //建立jar文件的目录
                extractClassFileToFolder(patchPackage, dir, jarFile, fileJarName);
            } catch (Exception e) {
                PatchUrl.getInstance().resultMessage.append(destPath).append(" 不存在;");
                System.out.println(destPath + " 不存在;");
            }
        }
    }

    /**
     * 抽取内部class文件
     *
     * @param patchPackage 待抽取的实体类
     * @param dir          补丁输出目录
     * @param jarFile      jar文件
     * @param fileJarName  jar文件名称
     * @throws IOException io异常
     */
    private static void extractClassFileToFolder(PatchPackage patchPackage, String dir, JarFile jarFile, String fileJarName) throws IOException {
        Enumeration<JarEntry> enums = jarFile.entries();
        List<PatchInfo> patchInfos = patchPackage.getPatchInfos();
        while (enums.hasMoreElements()) {
            JarEntry entry = enums.nextElement();
            for (PatchInfo file : patchInfos) {
                if (entry.getName().equals(file.getFliePath())  //普通.class文件
                        //内部类
                        || ((entry.getName().endsWith(".class")) && (JARFileUtils.getFileNameNoEx(entry.getName()).contains(JARFileUtils.getFileNameNoEx(file.getFliePath()) + "$")))) {
                    String toWrite = dir + "/" + fileJarName + "/" + entry.getName();
                    FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), new File(toWrite));
                    PatchUrl.getInstance().count++;
                    PatchUrl.getInstance().resultMessage.append(entry.getName()).append('\n');
                    System.out.println(entry.getName());
                }
            }
        }
        jarFile.close();
    }
}
