package com.honyee.app.utils.crypt;

import java.nio.charset.StandardCharsets;

/**
 * 微信
 */
public class WxBizMsgCrypt {

    // 解密时使用，这个值一般从各个平台提供的解密demo中获取获取
    private static final int RANDOM_BYTES_POS = 16;

    /**
     * 对密文进行解密 用于移动端数据解密
     *
     * @param sessionKey    密钥
     * @param iv            向量
     * @param encryptedData 密文
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
     * 将公众平台回复用户的消息加密打包. 用于服务端推送消息加密
     *
     * @param text      公众平台待回复用户的消息，xml格式的字符串
     * @param timestamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce     随机串，可以自己生成，也可以用URL参数的nonce
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     */
    public String encryptMsg(String appid, String messageValidToken, String messageEncodeDecodeKey,
                             String text, String timestamp, String nonce) {
        String randomStr = BizMsgCryptUtil.getRandomStr();
        // 加密
        String encryptData = BizMsgCryptUtil.encrypt(appid, messageEncodeDecodeKey, randomStr, text);

        // 生成安全签名
        if ("".equals(timestamp)) {
            timestamp = Long.toString(System.currentTimeMillis());
        }
        String signature = BizMsgCryptUtil.getSignature(messageValidToken, timestamp, nonce, encryptData);

        // 生成发送的xml
        return XMLParse.generate(encryptData, signature, timestamp, nonce);
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文. 用于服务端推送消息解密
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timestamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param encryptData     密文，对应postData
     * @return 解密后的原文
     */
    public String decryptMsg(String appid, String messageValidToken, String messageEncodeDecodeKey,
                             String msgSignature, String timestamp, String nonce, String encryptData) {
        // 提取密文
        Object[] encrypt = XMLParse.extract(encryptData);
        // 验证安全签名
        BizMsgCryptUtil.verify(messageValidToken, msgSignature, timestamp, nonce, encrypt[1].toString());
        // 解密
        return BizMsgCryptUtil.decrypt(appid, encrypt[1].toString(), messageEncodeDecodeKey, RANDOM_BYTES_POS);
    }
}
