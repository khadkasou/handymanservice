package com.nepbrain.technology.apigateway.helper;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonMaskerUtilTest {

    @Test
    void maskSensitiveFields_masksPasswordInJson() throws IOException {
        String json = "{\"username\":\"alice\",\"password\":\"secret123\"}";
        String masked = JsonMaskerUtil.maskSensitiveFields(json, "password");

        assertTrue(masked.contains("\"password\":\"*****\""));
        assertFalse(masked.contains("secret123"));
        assertTrue(masked.contains("alice"));
    }

    @Test
    void maskSensitiveFields_masksNestedPassword() throws IOException {
        String json = "{\"user\":{\"password\":\"nested-secret\",\"email\":\"a@b.com\"}}";
        String masked = JsonMaskerUtil.maskSensitiveFields(json, "password");

        assertTrue(masked.contains("\"password\":\"*****\""));
        assertFalse(masked.contains("nested-secret"));
        assertTrue(masked.contains("a@b.com"));
    }

    @Test
    void maskSensitiveFields_masksPasswordInArray() throws IOException {
        String json = "[{\"password\":\"one\"},{\"password\":\"two\"}]";
        String masked = JsonMaskerUtil.maskSensitiveFields(json, "password");

        assertTrue(masked.contains("*****"));
        assertFalse(masked.contains("one"));
        assertFalse(masked.contains("two"));
    }

    @Test
    void maskSensitiveFields_handlesFormUrlEncoded() throws IOException {
        String form = "username=bob&password=plain";
        String masked = JsonMaskerUtil.maskSensitiveFields(form, "password");

        assertTrue(masked.contains("\"password\":\"*****\""));
        assertFalse(masked.contains("plain"));
        assertTrue(masked.contains("bob"));
    }

    @Test
    void maskSensitiveFields_emptyInput_returnsAsIs() throws IOException {
        assertNull(JsonMaskerUtil.maskSensitiveFields(null, "password"));
        assertEquals("", JsonMaskerUtil.maskSensitiveFields("", "password"));
    }

    @Test
    void maskSensitiveFields_leavesUnrelatedFieldsIntact() throws IOException {
        String json = "{\"token\":\"abc\",\"name\":\"test\"}";
        String masked = JsonMaskerUtil.maskSensitiveFields(json, "password");

        assertTrue(masked.contains("\"token\":\"abc\""));
        assertTrue(masked.contains("\"name\":\"test\""));
    }
}
