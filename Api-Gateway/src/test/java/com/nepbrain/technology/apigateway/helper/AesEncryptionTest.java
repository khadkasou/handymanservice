package com.nepbrain.technology.apigateway.helper;

import com.nepbrain.technology.apigateway.enums.ErrorCodeEnum;
import com.nepbrain.technology.apigateway.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AesEncryptionTest {

    private static final String TEST_SECRET = "1234567890123456";
    private static final String ALGORITHM = "AES";
    private static final String PADDING = "AES/ECB/PKCS5Padding";

    private AesEncryption aesEncryption;

    @BeforeEach
    void setUp() {
        AesProperties properties = new AesProperties();
        properties.setSecret(TEST_SECRET);
        properties.setAlgorithm(ALGORITHM);
        properties.setPadding(PADDING);
        aesEncryption = new AesEncryption(properties);
    }

    @Test
    void encryptDecrypt_roundtrip() {
        String plain = "hello-gateway";
        String encrypted = aesEncryption.encrypt(plain);

        assertNotEquals(plain, encrypted);
        assertEquals(plain, aesEncryption.decrypt(encrypted));
    }

    @Test
    void encrypt_emptyString_roundtrip() {
        String encrypted = aesEncryption.encrypt("");
        assertEquals("", aesEncryption.decrypt(encrypted));
    }

    @Test
    void encrypt_sameInput_producesConsistentCiphertext() {
        String a = aesEncryption.encrypt("same");
        String b = aesEncryption.encrypt("same");
        assertEquals(a, b);
    }

    @Test
    void decrypt_invalidBase64_throwsApiException() {
        ApiException ex = assertThrows(ApiException.class, () -> aesEncryption.decrypt("!!!not-base64!!!"));
        assertEquals(ErrorCodeEnum.AES002.getCode(), ex.getCode());
    }

    @Test
    void encrypt_withInvalidSecret_throwsApiException() {
        AesProperties bad = new AesProperties();
        bad.setSecret("short");
        bad.setAlgorithm(ALGORITHM);
        bad.setPadding(PADDING);
        AesEncryption broken = new AesEncryption(bad);

        ApiException ex = assertThrows(ApiException.class, () -> broken.encrypt("payload"));
        assertEquals(ErrorCodeEnum.AES001.getCode(), ex.getCode());
    }

    @Test
    void encrypt_outputIsBase64() {
        String encrypted = aesEncryption.encrypt("payload");
        assertTrue(encrypted.matches("^[A-Za-z0-9+/=]+$"));
    }
}
