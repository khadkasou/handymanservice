package com.nepbrain.technology.communication.util;

/*
 * @Created At 21/05/2024
 * @Author suroj.awal
 */

import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleAuthenticator {

    private GoogleAuthenticator() {

    }

    public static String generateTotpCode(String secret){
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build();
        com.warrenstrange.googleauth.GoogleAuthenticator gAuth = new com.warrenstrange.googleauth.GoogleAuthenticator(config);
        int totp = gAuth.getTotpPassword(secret);
        return String.format("%06d", totp);

    }

}
