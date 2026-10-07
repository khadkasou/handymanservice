package com.nepbrain.technology.communication.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeneralUtil {

    public static Long get4DigitOTPCode() {
        return new SecureRandom().nextLong(1000, 9999);
    }

    public static Long get5DigitOTPCode() {
        return new SecureRandom().nextLong(10000, 99999);
    }

    public static Long get6DigitOTPCode() {
        return new SecureRandom().nextLong(100000, 999999);
    }

    public Long generateOTP(Integer otpLength) {
        Long otp;
        switch (otpLength) {
            case 5 -> otp = get5DigitOTPCode();
            case 6 -> otp = get6DigitOTPCode();
            default -> otp = get4DigitOTPCode();
        }
        return otp;
    }
}
