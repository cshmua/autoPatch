package autopatch.utils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

/**
 * DES加密工具
 * @author CSH
 * @since 2022/6/1
 */
public class DESUtils {
    /**
     * 根据参数生成KEY
     */
    public static Key getKey(String strKey) {
        Key key;
        try {
            KeyGenerator _generator = KeyGenerator.getInstance("DES");
            _generator.init(new SecureRandom(strKey.getBytes()));
            key = _generator.generateKey();
            _generator = null;
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
        return key;
    }

    /**
     * 文件file进行加密并保存目标文件destFile中
     *
     * @param file   要加密的文件 如c:/test/srcFile.txt
     * @param dest 加密后存放的文件名 如c:/加密后文件.txt
     */
    public static void encrypt(String file, String dest, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();
    }
    /**
     * 文件采用DES算法解密文件
     *
     * @param file 已加密的文件 如c:/加密后文件.txt
     *         * @param destFile
     *         解密后存放的文件名 如c:/ test/解密后文件.txt
     */
    public static void decrypt(String file, String dest, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = is.read(buffer)) >= 0) {
            //System.out.println();
            cos.write(buffer, 0, r);
        }
        cos.close();
        out.close();
        is.close();
    }
    public static void main(String[] args) throws Exception {
        Key key= getKey("520101");
        encrypt("E:\\works\\博思\\前置交换&模版\\数据规范管理\\电子票据规范标准520101.xls", "E:\\works\\博思\\前置交换&模版\\数据规范管理\\电子票据规范标准520101加密.xls",key); //加密
        decrypt("E:\\works\\博思\\前置交换&模版\\数据规范管理\\电子票据规范标准520101加密.xls", "E:\\works\\博思\\前置交换&模版\\数据规范管理\\电子票据规范标准520101解密.xls",key); //解密

    }
}