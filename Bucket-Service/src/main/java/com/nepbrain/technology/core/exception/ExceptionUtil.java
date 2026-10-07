package com.nepbrain.technology.core.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

    public static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(2048);
        Throwable current = throwable;
        while (current != null) {
            if (current != throwable) {
                builder.append("Caused by: ");
            }
            appendThrowable(builder, current);
            Throwable next = current.getCause();
            if (next == current) {
                break;
            }
            current = next;
        }
        return builder.toString();
    }

    private static void appendThrowable(StringBuilder builder, Throwable throwable) {
        builder.append(throwable).append(System.lineSeparator());
        for (StackTraceElement element : throwable.getStackTrace()) {
            builder.append("\tat ").append(element).append(System.lineSeparator());
        }
    }
}
