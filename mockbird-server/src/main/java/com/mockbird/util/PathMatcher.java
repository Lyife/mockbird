package com.mockbird.util;

import com.mockbird.entity.ApiInterface;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径匹配器，将接收到的 Mock 请求路径与已定义的接口路径进行匹配。
 * 匹配优先级：精确匹配 > {param} 路径变量匹配。
 */
public class PathMatcher {

    private static final Pattern PATH_VAR = Pattern.compile("\\{([^/]+)\\}");

    /**
     * 在接口列表中查找匹配的接口。
     *
     * @param requestPath 请求路径（去掉 /mock 前缀后的部分）
     * @param interfaces  所有已注册的接口定义
     * @return 匹配结果，未命中返回 null
     */
    public static MatchResult match(String requestPath, List<ApiInterface> interfaces) {
        if (requestPath == null || interfaces == null || interfaces.isEmpty()) {
            return null;
        }

        String normalizedPath = requestPath.endsWith("/") && requestPath.length() > 1
                ? requestPath.substring(0, requestPath.length() - 1) : requestPath;

        // 精确匹配优先
        for (ApiInterface api : interfaces) {
            String apiPath = api.getPath();
            if (apiPath != null && (apiPath.equals(normalizedPath) || apiPath.equals(requestPath))) {
                return new MatchResult(api, Collections.<String, String>emptyMap());
            }
        }

        // {param} 路径变量匹配
        for (ApiInterface api : interfaces) {
            String apiPath = api.getPath();
            if (apiPath == null || !apiPath.contains("{")) {
                continue;
            }

            Map<String, String> vars = extractPathVars(apiPath, normalizedPath);
            if (vars != null) {
                return new MatchResult(api, vars);
            }
        }

        return null;
    }

    /**
     * 用接口路径的模式匹配实际请求路径，同时提取路径变量。
     *
     * @param patternPath 接口定义的路径模式（如 /api/user/{id}）
     * @param actualPath  实际请求路径（如 /api/user/123）
     * @return 提取的变量 Map，不匹配返回 null
     */
    static Map<String, String> extractPathVars(String patternPath, String actualPath) {
        List<String> varNames = new ArrayList<>();
        Matcher varMatcher = PATH_VAR.matcher(patternPath);
        while (varMatcher.find()) {
            varNames.add(varMatcher.group(1));
        }

        String regex = PATH_VAR.matcher(patternPath).replaceAll("([^/]+)");
        Pattern compiled = Pattern.compile("^" + regex + "$");
        Matcher pathMatcher = compiled.matcher(actualPath);

        if (!pathMatcher.matches()) {
            return null;
        }

        Map<String, String> vars = new LinkedHashMap<>();
        for (int i = 0; i < varNames.size(); i++) {
            vars.put(varNames.get(i), pathMatcher.group(i + 1));
        }
        return vars;
    }
}
