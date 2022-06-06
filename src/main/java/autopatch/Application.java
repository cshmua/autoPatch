package autopatch;

import autopatch.domain.EncryptInfo;
import autopatch.utils.DESUtils;

import java.security.Key;
import java.util.List;

/***
 * 打包调用的实际方法
 * @author CSH
 * @since 2019/12/31
 */
public class Application {
    public static void main(String[] args) throws Exception {
        //doGetPatch();
        //System.out.println("******************************************打包结束");
        //System.out.println("本次打包补丁数:" + PatchUrl.getInstance().count);
    }

    public static void doEncrypt() throws Exception {
        Key key= DESUtils.getKey(EncryptInfo.getInstance().getKey());
        DESUtils.encrypt(EncryptInfo.getInstance().getSrcFile(), EncryptInfo.getInstance().getDest()+"/"+EncryptInfo.getInstance().getFileName(),key); //加密
        String resultcnt = "文件加密成功！";
        EncryptInfo.getInstance().resultMessage.insert(0,resultcnt);
    }
}
