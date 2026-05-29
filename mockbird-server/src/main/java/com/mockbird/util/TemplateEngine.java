package com.mockbird.util;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mock 响应模板引擎，替换响应体中的占位符为动态数据。
 */
public class TemplateEngine {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Pattern RANDOM_INT_RANGE = Pattern.compile("randomInt\\((\\d+),\\s*(\\d+)\\)");
    private static final Random RANDOM = new Random();

    /**
     * 处理模板字符串，替换所有 ${...} 占位符。
     *
     * @param template 包含占位符的模板字符串
     * @param context  上下文数据（请求参数、请求头、路径变量等）
     * @return 替换后的字符串
     */
    public static String render(String template, Map<String, Object> context) {
        if (template == null || !template.contains("${")) {
            return template;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER.matcher(template);

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = resolve(placeholder, context);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement != null ? replacement : matcher.group(0)));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String resolve(String key, Map<String, Object> context) {
        if (key == null) {
            return null;
        }

        if ("timestamp".equals(key)) {
            return String.valueOf(System.currentTimeMillis());
        }

        if ("randomInt".equals(key)) {
            return String.valueOf(RANDOM.nextInt(10000));
        }

        Matcher rangeMatcher = RANDOM_INT_RANGE.matcher(key);
        if (rangeMatcher.matches()) {
            int min = Integer.parseInt(rangeMatcher.group(1));
            int max = Integer.parseInt(rangeMatcher.group(2));
            return String.valueOf(min + RANDOM.nextInt(max - min + 1));
        }

        if (context != null && context.containsKey(key)) {
            Object value = context.get(key);
            return value != null ? value.toString() : "";
        }

        return null;
    }
}
