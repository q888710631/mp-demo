package com.honyee.app.utils.crypt;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author wu.dunhong
 */
public class BizMsgCryptUtil {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Logger log = LoggerFactory.getLogger(BizMsgCryptUtil.class);

    private BizMsgCryptUtil() {
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] toAesKey(String messageEncodeDecodeKey) {
        if (messageEncodeDecodeKey.length() != 43) {
            throw new RuntimeException("messageEncodeDecodeKey长度不正确");
        }
        return toBase64(messageEncodeDecodeKey + "=");
    }

    private static byte[] toBase64(String content) {
        return Base64Utils.decodeFromString(content);
    }

    private static Cipher toCipher(String messageEncodeDecodeKey) {
        // 设置解密模式为AES的CBC模式
        try {
            byte[] aesKey = toAesKey(messageEncodeDecodeKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 对密文进行解密，主要用于服务端推送消息解密，需要判定appid且加密内容存在偏移的情况
     *
     * @param appid                  需要匹配的小程序appid
     * @param encryptData            密文
     * @param messageEncodeDecodeKey 消息加密解密密钥
     * @param pos                    内容偏移量
     * @return 明文
     */
    public static String decrypt(String appid, String encryptData, String messageEncodeDecodeKey, int pos) {
        Cipher cipher = toCipher(messageEncodeDecodeKey);
        try {
            // 解密
            byte[] original = cipher.doFinal(toBase64(encryptData));
            // ==== 分离随机字符串,网络字节序和AppId
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);
            // 获取内容长度，byte数组的第pos 至 pos+4 个元素代表了消息体的真实字符个数，也就是长度
            int contentLength = recoverNetworkBytesOrder(Arrays.copyOfRange(bytes, pos, pos + 4));
            // 内容
            String content = new String(Arrays.copyOfRange(bytes, pos + 4, pos + 4 + contentLength), CHARSET);
            // 携带的appid
            String fromAppid = new String(Arrays.copyOfRange(bytes, pos + 4 + contentLength, bytes.length), CHARSET);
            // appid不相同的情况
            if (!fromAppid.equals(appid)) {
                log.error("appid不一致");
                return null;
            }
            return content;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 对密文进行解密
     *
     * @param messageEncodeDecodeKey 消息加密密钥
     * @param encryptData            解密的数据
     * @return 明文
     */
    public static String decrypt(String messageEncodeDecodeKey, String encryptData) {
        byte[] key = messageEncodeDecodeKey.getBytes(StandardCharsets.UTF_8);
        key = Arrays.copyOfRange(key, 0, 16);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec iv = new IvParameterSpec(key);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.getDecoder().decode(encryptData);
            // 解密
            byte[] original = cipher.doFinal(encrypted);
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);
            // 内容
            return new String(bytes, CHARSET);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 生成4个字节的网络字节序
    public static byte[] getNetworkBytesOrder(int sourceNumber) {
        ByteBuffer b = ByteBuffer.wrap(new byte[4]);
        b.asIntBuffer().put(sourceNumber);
        return b.array();
    }

    // 生成4个字节的网络字节序 - 只是换了种实现方式
    public static byte[] getNetworkBytesOrder2(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    // 还原4个字节的网络字节序
    public static int recoverNetworkBytesOrder(byte[] orderBytes) {
        ByteBuffer buf = ByteBuffer.wrap(orderBytes);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf.getInt();
    }

    // 还原4个字节的网络字节序 - 只是换了种实现方式
    public static int recoverNetworkBytesOrder2(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    // 随机生成16位字符串
    public static String getRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     */
    public static String encrypt(String appid, String messageEncodeDecodeKey, String randomStr, String text) {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = appid.getBytes(CHARSET);
        // randomStr + networkBytesOrder + text + appid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(appidBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            Cipher cipher = toCipher(messageEncodeDecodeKey);
            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 对密文进行解密，主要用于移动端数据解密
     *
     * @param encryptData 密文
     * @param iv          向量
     * @param sessionKey  密钥
     * @return 解密后的bytes，可能需要分离随机字符串后再转String
     */
    public static byte[] decrypt(String encryptData, String iv, String sessionKey) {
        byte[] aesKey = Base64Utils.decodeFromString(sessionKey);
        byte[] aesIV = Base64Utils.decodeFromString(iv);
        byte[] aesEncryptedData = Base64Utils.decodeFromString(encryptData);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
            params.init(new IvParameterSpec(aesIV));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, params);
            return cipher.doFinal(aesEncryptedData);
        } catch (
                NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | InvalidParameterSpecException
                | BadPaddingException e
        ) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 生成签名
     *
     * @param messageValidToken 消息验证token
     * @param timestamp         时间戳，对应URL参数的timestamp
     * @param nonce             随机串，对应URL参数的nonce
     * @param encryptData       加密的数据或者随机串
     * @return 签名
     */
    public static String getSignature(String messageValidToken, String timestamp, String nonce, String encryptData) {
        String[] array = {messageValidToken, timestamp, nonce, encryptData};
        // 字符串排序
        Arrays.sort(array);
        return DigestUtils.sha1Hex(String.join("", array));
    }

    /**
     * 验证
     *
     * @param messageValidToken 消息验证token
     * @param msgSignature      签名串，对应URL参数的msg_signature
     * @param timestamp         时间戳，对应URL参数的timestamp
     * @param nonce             随机串，对应URL参数的nonce
     * @param encryptData       加密的数据或者随机串
     */
    public static void verify(String messageValidToken, String msgSignature, String timestamp, String nonce, String encryptData) {
        if (!getSignature(messageValidToken, timestamp, nonce, encryptData).equals(msgSignature)) {
            throw new RuntimeException("签名验证错误");
        }
    }

}
