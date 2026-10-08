package com.nepbrain.technology.core.util;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ValidationKeyHolder {

    private static final ThreadLocal<Deque<String>> TL = ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(String template) { // template is like "{CMS001}"
        if (template != null) {
            TL.get().addLast(template);
        }
    }

    public static String poll() {
        String key = TL.get().pollFirst();
        if (key == null) {
            return null;
        }
        return key.replace("{", "").replace("}", "");
    }

    public static void clear() {
        TL.remove();
    }
}
