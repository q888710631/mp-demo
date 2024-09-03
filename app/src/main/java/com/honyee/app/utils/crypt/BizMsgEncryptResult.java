package com.honyee.app.utils.crypt;

/**
 * 加密结果
 *
 * @author honyee
 */
public class BizMsgEncryptResult {
    
    private String timestamp;
    private String nonce;
    private String signature;
    private String randomStr;
    private String encryptData;
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getNonce() {
        return nonce;
    }
    
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public String getRandomStr() {
        return randomStr;
    }
    
    public void setRandomStr(String randomStr) {
        this.randomStr = randomStr;
    }
    
    public String getEncryptData() {
        return encryptData;
    }
    
    public void setEncryptData(String encryptData) {
        this.encryptData = encryptData;
    }
    
    public BizMsgEncryptResult timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public BizMsgEncryptResult nonce(String nonce) {
        this.nonce = nonce;
        return this;
    }
    
    public BizMsgEncryptResult signature(String signature) {
        this.signature = signature;
        return this;
    }
    
    public BizMsgEncryptResult randomStr(String randomStr) {
        this.randomStr = randomStr;
        return this;
    }
    
    public BizMsgEncryptResult encryptData(String encryptData) {
        this.encryptData = encryptData;
        return this;
    }
}
