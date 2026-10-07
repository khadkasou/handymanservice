package com.nepbrain.technology.employee.util;

/**
 * Utility class for converting between English and Nepali digits.
 */
public final class NepaliDigits {

    private static final char[] NEPALI_DIGITS = {
        '०', '१', '२', '३', '४', '५', '६', '७', '८', '९'
    };

    private static final char[] ENGLISH_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private NepaliDigits() {
        // Utility class - prevent instantiation
    }

    /**
     * Convert English digits to Nepali digits in a string.
     * 
     * @param text the text containing English digits
     * @return the text with English digits replaced by Nepali digits
     */
    public static String toNepaliDigits(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                result.append(NEPALI_DIGITS[c - '0']);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Convert Nepali digits to English digits in a string.
     * 
     * @param text the text containing Nepali digits
     * @return the text with Nepali digits replaced by English digits
     */
    public static String toEnglishDigits(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            boolean found = false;
            for (int i = 0; i < NEPALI_DIGITS.length; i++) {
                if (c == NEPALI_DIGITS[i]) {
                    result.append(ENGLISH_DIGITS[i]);
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Convert a number to Nepali digits string.
     * 
     * @param number the number to convert
     * @return the number as a string with Nepali digits
     */
    public static String toNepaliDigits(int number) {
        return toNepaliDigits(String.valueOf(number));
    }

    /**
     * Convert a number to Nepali digits string.
     * 
     * @param number the number to convert
     * @return the number as a string with Nepali digits
     */
    public static String toNepaliDigits(long number) {
        return toNepaliDigits(String.valueOf(number));
    }

    /**
     * Parse a string with Nepali digits to an integer.
     * 
     * @param nepaliNumber the string containing Nepali digits
     * @return the parsed integer value
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static int parseNepaliDigits(String nepaliNumber) {
        String englishNumber = toEnglishDigits(nepaliNumber);
        return Integer.parseInt(englishNumber);
    }

    /**
     * Parse a string with Nepali digits to a long.
     * 
     * @param nepaliNumber the string containing Nepali digits
     * @return the parsed long value
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static long parseNepaliDigitsLong(String nepaliNumber) {
        String englishNumber = toEnglishDigits(nepaliNumber);
        return Long.parseLong(englishNumber);
    }
}
