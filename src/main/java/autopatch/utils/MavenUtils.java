package autopatch.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

/**
 * maven提取工具类
 * @author CSH
 * @since 2019/9/3
 */
public class MavenUtils {

    private static DocumentBuilder builder;

    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getVersion(File pomFile) throws Exception {
        Document doc = builder.parse(pomFile);
        NodeList versionNodeList = doc.getElementsByTagName("version");
        if (versionNodeList == null || versionNodeList.getLength() == 0) {
            throw new RuntimeException("错误的pom.xml文件" + pomFile.getName() + "！未获取到版本号！");
        }
        for (int i = 0; i < versionNodeList.getLength(); i++) {
            Node versionNode = versionNodeList.item(i);
            //找到节点project 下的 version 节点
            if ("project".equals(versionNode.getParentNode().getNodeName())) {
                String tempVersion = versionNode.getFirstChild().getNodeValue().trim().replace(" ", "");
                //如果包含${project.parent.version}，则查找parent节点下的version
                if (tempVersion.contains("${project.parent.version}")) {
                    return tempVersion.replace("${project.parent.version}", getParentVersion(doc));
                } else {
                    return tempVersion;
                }
            }
        }
        //如果没有找到project节点下的version版本，则使用parent节点下的version
        return getParentVersion(doc);
    }

    /**
     * 获取parent节点的version
     *
     * @param doc
     * @return
     */
    private static String getParentVersion(Document doc) {
        NodeList parent = doc.getElementsByTagName("parent");
        if (parent == null || parent.getLength() == 0) {
            throw new RuntimeException("错误的pom.xml文件：未找到parent节点！");
        }
        NodeList parentInfo = parent.item(0).getChildNodes();
        for (int j = 0; j < parentInfo.getLength(); j++) {
            Node parentVersion = parentInfo.item(j);
            if ("version".equals(parentVersion.getNodeName())) {
                return parentVersion.getFirstChild().getNodeValue();
            }
        }
        throw new RuntimeException("错误的pom.xml文件：未找到parent.version！");
    }

}
