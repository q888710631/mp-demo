package com.honyee.app.utils.crypt;

import java.nio.charset.StandardCharsets;

/**
 * 抖音
 */
public class BytedanceBizMsgCrypt {

    // 解密时使用，这个值一般从各个平台提供的解密demo中获取获取
    private static final int RANDOM_BYTES_POS = 32;

    /**
     * 对明文进行加密.
     * <p>
     * 加密后无法解密，加密pos=32，解密需要16，没找到哪里改
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     */
    public BizMsgEncryptResult encrypt(String appid, String messageValidToken, String messageEncodeDecodeKey,
                                       String text, String timestamp, String nonce) {
        String randomStr = BizMsgCryptUtil.getRandomStr();
        // 加密
        String encryptData = BizMsgCryptUtil.encrypt(appid, messageEncodeDecodeKey, randomStr, text);

        // 生成安全签名
        if ("".equals(timestamp)) {
            timestamp = Long.toString(System.currentTimeMillis());
        }
        String signature = BizMsgCryptUtil.getSignature(messageValidToken, timestamp, nonce, encryptData);

        return new BizMsgEncryptResult()
                .timestamp(timestamp)
                .nonce(nonce)
                .signature(signature)
                .randomStr(randomStr)
                .encryptData(encryptData);
    }

    /**
     * 对密文进行解密 用于移动端数据解密
     *
     * @param encryptedData 密文
     * @param iv            向量
     * @param sessionKey    密钥
     * @return 明文
     */
    public static String decrypt(String sessionKey, String iv, String encryptedData) {
        byte[] original = BizMsgCryptUtil.decrypt(encryptedData, iv, sessionKey);
        if (null != original && original.length > 0) {
            return new String(original, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文. 用于服务端推送消息解密
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timestamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param encryptData     密文，对应POST请求的数据
     * @return 解密后的原文
     */
    public String decrypt(String appid, String messageValidToken, String messageEncodeDecodeKey,
                             String msgSignature, String timestamp, String nonce, String encryptData) {
        // 验证安全签名
        BizMsgCryptUtil.verify(messageValidToken, msgSignature, timestamp, nonce, encryptData);
        // 解密
        return BizMsgCryptUtil.decrypt(appid, encryptData, messageEncodeDecodeKey, RANDOM_BYTES_POS);
    }
}
