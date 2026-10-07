package com.nepbrain.technology.core.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AesService {

    @Value("${aes.encryption.algorithm}")
    private String algorithm;
    @Value("${aes.encryption.padding}")
    private String padding;
    @Value("${aes.encryption.secretKey}")
    private String secretKey;

    public String encrypt(String message) {
        try {
            SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(padding);
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            return Base64.getEncoder()
                    .encodeToString(encryptedMessage);
        } catch (Exception e) {
            return "";
        }
    }

    public String decrypt(String encryptedMessage) {
        try {
            SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(padding);
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder()
                    .decode(encryptedMessage));
            return new String(decryptedMessage);
        } catch (Exception e) {
            return "";
        }
    }
}
