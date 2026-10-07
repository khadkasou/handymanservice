package com.nepbrain.technology.communication.enums;

import lombok.Getter;

@Getter
public enum LanguageEnum {
    EN("English"),
    FR("French"),
    ES("Spanish"),
    DE("German"),
    IT("Italian"),
    PT("Portuguese"),
    ZH("Chinese"),
    JA("Japanese"),
    HI("Hindi"),
    RU("Russian"),
    AR("Arabic"),
    BN("Bengali"),
    PA("Punjabi"),
    UR("Urdu"),
    VI("Vietnamese"),
    KO("Korean"),
    FA("Persian"),
    TR("Turkish"),
    NL("Dutch"),
    PL("Polish"),
    UK("Ukrainian"),
    RO("Romanian"),
    EL("Greek"),
    SV("Swedish"),
    DA("Danish"),
    NO("Norwegian"),
    FI("Finnish"),
    CS("Czech"),
    HU("Hungarian"),
    SK("Slovak"),
    HE("Hebrew"),
    TH("Thai"),
    MS("Malay"),
    ID("Indonesian"),
    TA("Tamil"),
    TE("Telugu"),
    MR("Marathi"),
    KN("Kannada"),
    GU("Gujarati"),
    ML("Malayalam"),
    OR("Odia"),
    SI("Sinhala"),
    KM("Khmer"),
    MY("Burmese"),
    AM("Amharic"),
    SW("Swahili"),
    YI("Yiddish");

    private final String value;


    LanguageEnum(String value) {
        this.value = value;
    }
}
