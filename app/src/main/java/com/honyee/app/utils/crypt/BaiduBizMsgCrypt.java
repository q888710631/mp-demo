package com.honyee.app.utils.crypt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 百度
 */
public class BaiduBizMsgCrypt {

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
            try {
                // ==== 分离
                int pos = RANDOM_BYTES_POS;
                // 去除补位字符
                byte[] bytes = PKCS7Encoder.decode(original);
                // 分离16位随机字符串,网络字节序和ClientId
                byte[] networkOrder = Arrays.copyOfRange(bytes, pos, pos + 4);
                int contentLength = BizMsgCryptUtil.recoverNetworkBytesOrder(networkOrder);
                return new String(Arrays.copyOfRange(bytes, pos + 4, pos + 4 + contentLength), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文. 用于服务端推送消息解密
     * <ol>
     * 	<li>利用收到的密文生成安全签名，进行签名验证</li>
     * 	<li>若验证通过，则提取xml中的加密消息</li>
     * 	<li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timestamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param encryptData     密文，对应postData
     * @return 解密后的原文
     */
    public String decrypt(String appid, String messageValidToken, String messageEncodeDecodeKey,
                             String msgSignature, String timestamp, String nonce, String encryptData) {
        // 验证安全签名
        BizMsgCryptUtil.verify(messageValidToken, msgSignature, timestamp, nonce, encryptData);
        // 解密
        return BizMsgCryptUtil.decrypt(appid, encryptData, messageEncodeDecodeKey, RANDOM_BYTES_POS);
    }

    /**
     * 验证
     */
    public void verify(String messageValidToken, String msgSignature, String timestamp, String nonce, String encryptData) {
        BizMsgCryptUtil.verify(messageValidToken, msgSignature, timestamp, nonce, encryptData);
    }
}
