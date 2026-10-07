package com.nepbrain.technology.communication.util;

import org.springframework.util.StringUtils;

/** Strips HTML for in-app notification body display. */
public final class HtmlPlainText {

    private HtmlPlainText() {
    }

    public static String of(String htmlOrText) {
        if (!StringUtils.hasText(htmlOrText)) {
            return "";
        }
        return htmlOrText
                .replaceAll("(?is)<script[^>]*>.*?</script>", " ")
                .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                .replaceAll("(?is)<br\\s*/?>", "\n")
                .replaceAll("(?is)</p>", "\n")
                .replaceAll("(?is)<[^>]*>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll(" *\\n *", "\n")
                .trim();
    }
}
