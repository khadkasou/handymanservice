package com.nepbrain.technology.apigateway.helper;

import com.nepbrain.technology.apigateway.exception.ApiException;
import com.nepbrain.technology.apigateway.enums.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AesEncryption {

    private final AesProperties aesProperties;

    public String encrypt(String message) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesProperties.getSecret().getBytes(), aesProperties.getAlgorithm());
            Cipher cipher = Cipher.getInstance(aesProperties.getPadding());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedMessage);
        } catch (Exception ex) {
            throw new ApiException(ErrorCodeEnum.AES001.getCode());
        }
    }

    public String decrypt(String encryptedMessage) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesProperties.getSecret().getBytes(), aesProperties.getAlgorithm());
            Cipher cipher = Cipher.getInstance(aesProperties.getPadding());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
            return new String(decryptedMessage);
        } catch (Exception ex) {
            throw new ApiException(ErrorCodeEnum.AES002.getCode());
        }
    }
}
