package com.mockbird.util;

import com.mockbird.entity.ApiInterface;
import java.util.Map;

/**
 * 路径匹配结果，包含匹配到的接口定义和提取的路径变量。
 */
public class MatchResult {
    private final ApiInterface apiInterface;
    private final Map<String, String> pathVars;

    public MatchResult(ApiInterface apiInterface, Map<String, String> pathVars) {
        this.apiInterface = apiInterface;
        this.pathVars = pathVars;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

    public Map<String, String> getPathVars() {
        return pathVars;
    }
}
