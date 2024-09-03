package com.honyee.app.utils.crypt;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES工具
 *
 * @author wu.dunhong
 */
public class AESUtil {

    private static final String AES = "AES";

    /**
     * 生成随机密钥
     *
     * @param seed 种子
     */
    public static byte[] randomAesKey(String seed) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(AES); // 创建AES的Key生产者

        kgen.init(128, new SecureRandom(seed.getBytes())); // 利用用户密码作为随机数初始化出
        // 128位的key生产者
        //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行

        SecretKey secretKey = kgen.generateKey(); // 根据用户密码，生成一个密钥
        System.out.println(secretKey);
        return secretKey.getEncoded(); // 返回基本编码格式的密钥，如果此密钥不支持编码，则返
    }

    /**
     * 加密
     *
     * @param key     密钥，长度24位
     * @param content 加密的内容
     */
    public static String encode(String key, String content) throws Exception {
        byte[] keyDecode = Base64.getDecoder().decode(key);
        return encode(keyDecode, content);
    }

    /**
     * 加密
     *
     * @param keyDecode 密钥
     * @param content   加密的内容
     */
    public static String encode(byte[] keyDecode, String content) throws Exception {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        SecretKeySpec keySpec = new SecretKeySpec(keyDecode, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(1, keySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes()));
    }

    /**
     * 解密
     *
     * @param key     密钥，长度24位
     * @param content 解密内容
     */
    public static String decode(String key, String content) throws Exception {
        byte[] keyDecode = Base64.getDecoder().decode(key);
        return decode(keyDecode, content);
    }

    /**
     * 解密
     *
     * @param keyDecode     密钥
     * @param content 解密内容
     */
    public static String decode(byte[] keyDecode, String content) throws Exception {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        SecretKeySpec keySpec = new SecretKeySpec(keyDecode, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(2, keySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(content)));
    }
}
