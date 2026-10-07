package com.nepbrain.technology.communication.enums;

import lombok.Getter;

@Getter
public enum SettingDetailEnum {
    EMAIL_AWS_ACCESS_KEY_ID("AWS_ACCESS_KEY_ID", "Aws Access Key Id", "AWS"),
    EMAIL_AWS_SECRET_ACCESS_KEY("AWS_SECRET_ACCESS_KEY", "Aws Secret Access Key", "AWS"),
    EMAIL_AWS_REGION("AWS_REGION", "Aws Region", "AWS"),
    EMAIL_AWS_MAIL_FROM("AWS_MAIL_FROM", "Aws Mail From", "AWS"),

    EMAIL_DEFAULT("DEFAULT_EMAIL", "Default Email", "DEFAULT_EMAIL"),

    EMAIL_MAILGUN_HOST("MAILGUN_HOST", "Mailgun Host", "MAILGUN"),
    EMAIL_MAILGUN_PORT("MAILGUN_PORT", "Mailgun Port", "MAILGUN"),
    EMAIL_MAILGUN_USERNAME("MAILGUN_USERNAME", "Mailgun Username", "MAILGUN"),
    EMAIL_MAILGUN_PASSWORD("MAILGUN_PASSWORD", "Mailgun Password", "MAILGUN"),

    EMAIL_DEFAULT_ATTACHMENT_MAIL("DEFAULT_ATTACHMENT_MAIL", "Default Attachment Mail", "DEFAULT_EMAIL"),

    EMAIL_SUPPORT_EMAIL("SUPPORT_EMAIL", "Support Email", "SUPPORT"),
    EMAIL_SUPPORT_EMAIL_NAME("SUPPORT_EMAIL_NAME", "Support Email Name", "SUPPORT"),
    EMAIL_FROM_EMAIL("FROM_EMAIL", "From Email", "SUPPORT"),
    EMAIL_FROM_EMAIL_NAME("FROM_EMAIL_NAME", "From Email Name", "SUPPORT"),

    GENERAL_COMPANY_NAME("COMPANY_NAME", "Company Name", "COMPANY"),
    GENERAL_COMPANY_ADDRESS("COMPANY_ADDRESS", "Company Address", "COMPANY"),
    GENERAL_COMPANY_CONTACT_NUMBER("COMPANY_CONTACT_NUMBER", "Company Contact Number", "COMPANY"),
    GENERAL_COMPANY_PHONE_OFFICE("COMPANY_PHONE_OFFICE", "Company Phone Office", "COMPANY"),
    GENERAL_COMPANY_EMAIL("COMPANY_EMAIL", "Company Email", "COMPANY"),
    GENERAL_UPDATE_PAYMENT_URL("UPDATE_PAYMENT_URL", "Update Payment URL", "COMPANY"),
    GENERAL_TRIAL_PERIOD("TRIAL_PERIOD", "Trial Period", "COMPANY"),

    GENERAL_OTP_RESEND_TIME("OTP_RESEND_TIME", "Otp Resend Time", "OTP"),
    GENERAL_OTP_EXPIRY_TIME("OTP_EXPIRY_TIME", "Otp Expiry Time", "OTP"),
    GENERAL_OTP_LENGTH("OTP_LENGTH", "Otp Length", "OTP"),
    GENERAL_OTP_RESEND_LIMIT("OTP_RESEND_LIMIT", "Otp Resend Limit", "OTP");

    private final String code;
    private final String description;
    private final String categoryCode;

    SettingDetailEnum(String code, String description, String categoryCode) {
        this.code = code;
        this.description = description;
        this.categoryCode = categoryCode;
    }
}
