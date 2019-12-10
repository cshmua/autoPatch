package autopatch.helper;

import autopatch.domain.PatchInfo;
import autopatch.domain.PatchPackage;
import autopatch.domain.PatchUrl;
import autopatch.utils.JARFileUtils;
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
     * 导出临时jar文件
     * @param destPath 待解压的war包路径
     * @param patchPackages 补丁集合实体
     */
    public static void extractJARsToTempFolder(String destPath, Map<String,PatchPackage> patchPackages) {
        PatchPackage warPackage = patchPackages.get(JARFileUtils.getFileNameNoEx(JARFileUtils.getRealFileName(destPath)));
        if(warPackage.isWar()) {
            try {
                JarFile jarFile = new JarFile(destPath);
                Enumeration<JarEntry> enums = jarFile.entries();
                System.out.println("auth-patch begin:");
                //jar包文件
                List<String> fileJarNames = warPackage.getJarFiles();
                //js和jsp文件
                List<String> fileJsAndJsp = new ArrayList<>();
                for(String file : warPackage.getPatchFiles()){
                    if(file.contains(".js") || file.contains(".jsp") || file.contains(".css") || file.contains(".jpg") || file.contains(".png")){
                        //将名称替换为war包的名称
                        fileJsAndJsp.add(file.replaceAll(file.split("/")[0], ""));
                    }
                }
                //String dir = JARFileUtils.getFileNameNoEx(destPath);
                String dir = PatchUrl.getInstance().getTargetDir()+File.separator+PatchUrl.getInstance().getTargetFileName();
                new File(dir).mkdirs();
                warPackage.setJarFiles(fileJarNames);
                while (enums.hasMoreElements()) {
                    JarEntry entry = enums.nextElement();
                    for (int i = 0; i < fileJarNames.size(); i++) {
                        String fileJarName = fileJarNames.get(i);
                        if (entry.getName().contains(fileJarName) || entry.getName().contains(JARFileUtils.getFileNameNoEx(fileJarName))) {
                            String[] files = entry.getName().split("/");
                            String newFileName = files[files.length - 1];
                            File toWrite = new File(dir + "/" + "tmp-" + newFileName);
                            //更新-classes后缀的jar文件
                            if (!entry.getName().contains(fileJarName) && entry.getName().contains(JARFileUtils.getFileNameNoEx(fileJarName))) {
                                fileJarNames.set(i, newFileName);
                                PatchPackage tmpPatch = patchPackages.remove(fileJarName);
                                Set<String> tmpSet = tmpPatch.getPatchFiles();
                                Set<String> newSet = new HashSet<>();
                                for (String tmp : tmpSet) {
                                    newSet.add(tmp.replaceAll(fileJarName, newFileName));
                                }
                                tmpPatch.setName(newFileName);
                                tmpPatch.setPatchFiles(newSet);
                                patchPackages.put(newFileName, tmpPatch);
                            }
                            FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), toWrite);
                            System.out.println(entry.getName());
                        }
                    }
                    for (String fileName : fileJsAndJsp) {
                        if (fileName.equals("/" + entry.getName())) {
                            String toWrite = dir + "/" + entry.getName();
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

    /**
     * 导出补丁
     * @param destPath 待解压的war包路径
     * @param patchPackages 补丁集合实体
     */
    public static void extractResourcesToTempFolder(String destPath,Map<String,PatchPackage> patchPackages) {
        try {
            PatchPackage warPackage = patchPackages.get(JARFileUtils.getFileNameNoEx(JARFileUtils.getRealFileName(destPath)));
            List<String> fileJarNames = warPackage.getJarFiles();
            for (String fileJarName : fileJarNames) {
                String dir = PatchUrl.getInstance().getTargetDir()+File.separator+PatchUrl.getInstance().getTargetFileName();
                //实际jar文件地址
                String jarPath = dir + "/" + "tmp-" + fileJarName;
                JarFile jarFile;
                try {
                    jarFile = new JarFile(jarPath);
                }catch (Exception e){
                    PatchUrl.getInstance().resultMessage.append(jarPath).append(" may not exit;");
                    System.out.println(jarPath + " may not exit");
                    continue;
                }
                //建立jar文件的目录
                String dirPath = dir + "/" + fileJarName;
                /*File dirFile = new File(dirPath);
                dirFile.mkdirs();*/
                Enumeration<JarEntry> enums = jarFile.entries();
                //PatchUrl.getInstance().resultMessage.append("auth-patch JARs begin:").append(dirPath).append('\n');
                System.out.println("auth-patch JARs begin:" + dirPath);
                while (enums.hasMoreElements()) {
                    JarEntry entry = enums.nextElement();
                    for (String fileName : patchPackages.get(fileJarName).getPatchFiles()) {
                        if ((fileJarName+"/"+entry.getName()).equals(fileName)  //普通.class文件
                            //内部类
                            || ((entry.getName().endsWith(".class")) && ((fileJarName+"/"+ JARFileUtils.getFileNameNoEx(entry.getName())).contains(JARFileUtils.getFileNameNoEx(fileName)+ "$")))) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除临时jars文件
     * @param destPath war包路径
     * @param fileJarNames jar包文件名集合
     */
    public  static void deleteTmpJars(String destPath,List<String> fileJarNames) {
        for (String fileJarName : fileJarNames) {
            String dir = PatchUrl.getInstance().getTargetDir()+File.separator+PatchUrl.getInstance().getTargetFileName();
            //实际jar文件地址
            String jarPath = dir + "/" + "tmp-" + fileJarName;
            FileUtils.deleteQuietly(new File(jarPath));
        }
    }

    public static void extractJARsToTempFolder(String destPath, PatchPackage patchPackages) {
        if(patchPackages.isWar()) {
            try {
                JarFile jarFile = new JarFile(destPath);
                Enumeration<JarEntry> enums = jarFile.entries();
                System.out.println("auth-patch begin:");
                //jar包文件
                List<String> fileJarNames = patchPackages.getJarFiles();
                //js和jsp文件
                List<String> fileJsAndJsp = new ArrayList<>();
                for(PatchInfo patchInfo : patchPackages.getPatchInfos()){
                    String file = patchInfo.getFliePath();
                    if(file.contains(".js") || file.contains(".jsp") || file.contains(".css") || file.contains(".jpg") || file.contains(".png")){
                        //将名称替换为war包的名称
                        fileJsAndJsp.add(file.replaceAll(file.split("/")[0], ""));
                    }
                }
                //String dir = JARFileUtils.getFileNameNoEx(destPath);
                String dir = PatchUrl.getInstance().getTargetDir()+File.separator+PatchUrl.getInstance().getTargetFileName();
                new File(dir).mkdirs();
                while (enums.hasMoreElements()) {
                    JarEntry entry = enums.nextElement();
                    for (int i = 0; i < fileJarNames.size(); i++) {
                        String fileJarName = fileJarNames.get(i);
                        if (entry.getName().contains(fileJarName) || entry.getName().contains(JARFileUtils.getFileNameNoEx(fileJarName))) {
                            String[] files = entry.getName().split("/");
                            String newFileName = files[files.length - 1];
                            File toWrite = new File(dir + "/" + "tmp-" + newFileName);
                            FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), toWrite);
                            System.out.println(entry.getName());
                        }
                    }
                    for (String fileName : fileJsAndJsp) {
                        if (fileName.equals("/" + entry.getName())) {
                            String toWrite = dir + "/" + entry.getName();
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
        if(patchPackage.isWar()){
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
                        PatchUrl.getInstance().resultMessage.append(jarPath).append(" 不存在;");
                        System.out.println(jarPath + " 不存在;");
                        continue;
                    }
                    //建立jar文件的目录
                    String dirPath = dir + "/" + fileJarName;
                    Enumeration<JarEntry> enums = jarFile.entries();
                    System.out.println("war包抽取开始:" + dirPath);
                    List<PatchInfo> patchInfos = patchPackage.getPatchInfos();
                    while (enums.hasMoreElements()) {
                        JarEntry entry = enums.nextElement();
                        for (PatchInfo file : patchInfos) {
                            if ((fileJarName + "/" + entry.getName()).equals(file.getFliePath())  //普通.class文件
                                    //内部类
                                    || ((entry.getName().endsWith(".class")) && ((fileJarName + "/" + JARFileUtils.getFileNameNoEx(entry.getName())).contains(JARFileUtils.getFileNameNoEx(file.getFliePath()) + "$")))) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            String dir = PatchUrl.getInstance().getTargetDir() + File.separator + PatchUrl.getInstance().getTargetFileName();
            JarFile jarFile;
            try {
                jarFile = new JarFile(destPath);
                String fileJarName = JARFileUtils.getRealFileName(destPath);
                //建立jar文件的目录
                String dirPath = dir + "/" + fileJarName;
                Enumeration<JarEntry> enums = jarFile.entries();
                System.out.println("jar包抽取开始:" + dirPath);
                List<PatchInfo> patchInfos = patchPackage.getPatchInfos();
                while (enums.hasMoreElements()) {
                    JarEntry entry = enums.nextElement();
                    for (PatchInfo file : patchInfos) {
                        if ((fileJarName + "/" + entry.getName()).equals(file.getFliePath())  //普通.class文件
                                //内部类
                                || ((entry.getName().endsWith(".class")) && ((fileJarName + "/" + JARFileUtils.getFileNameNoEx(entry.getName())).contains(JARFileUtils.getFileNameNoEx(file.getFliePath()) + "$")))) {
                            String toWrite = dir + "/" + fileJarName + "/" + entry.getName();
                            FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), new File(toWrite));
                            PatchUrl.getInstance().count++;
                            PatchUrl.getInstance().resultMessage.append(entry.getName()).append('\n');
                            System.out.println(entry.getName());
                        }
                    }
                }
                jarFile.close();
            } catch (Exception e) {
                PatchUrl.getInstance().resultMessage.append(destPath).append(" 不存在;");
                System.out.println(destPath + " 不存在;");
            }
        }
    }
}
