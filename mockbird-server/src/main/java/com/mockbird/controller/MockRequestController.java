package com.mockbird.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mockbird.common.Constants;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.MockRule;
import com.mockbird.entity.RequestLog;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.MockRuleService;
import com.mockbird.service.RequestLogService;
import com.mockbird.util.MatchResult;
import com.mockbird.util.PathMatcher;
import com.mockbird.util.TemplateEngine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Mock 请求入口 Controller，拦截所有发往 /mock/** 的请求。
 * 串联路径匹配 → 规则查找 → 模板渲染 → 日志记录 的完整链路。
 */
@RestController
@RequestMapping("/mock")
public class MockRequestController {

    private final ApiInterfaceService apiInterfaceService;
    private final MockRuleService mockRuleService;
    private final RequestLogService requestLogService;

    public MockRequestController(ApiInterfaceService apiInterfaceService,
                                  MockRuleService mockRuleService,
                                  RequestLogService requestLogService) {
        this.apiInterfaceService = apiInterfaceService;
        this.mockRuleService = mockRuleService;
        this.requestLogService = requestLogService;
    }

    /**
     * 处理所有 /mock/** 请求，不限定 HTTP 方法。
     */
    @RequestMapping("/**")
    public void handleMockRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();

        String requestURI = request.getRequestURI();
        String targetPath = requestURI.substring(requestURI.indexOf(Constants.MOCK_PATH_PREFIX) + Constants.MOCK_PATH_PREFIX.length());
        if (targetPath.isEmpty()) {
            targetPath = "/";
        }

        // 构建模板上下文
        Map<String, Object> context = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            context.put(Constants.TEMPLATE_PREFIX_PARAM + name, request.getParameter(name));
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            context.put(Constants.TEMPLATE_PREFIX_HEADER + name, request.getHeader(name));
        }
        String requestBody = readBody(request);
        context.put(Constants.TEMPLATE_KEY_BODY, requestBody);

        // 路径匹配
        List<ApiInterface> allApis = apiInterfaceService.list();
        MatchResult matched = PathMatcher.match(targetPath, allApis);
        if (matched == null) {
            response.setStatus(404);
            response.setContentType(Constants.CONTENT_TYPE_JSON_UTF8);
            response.getWriter().write("{\"code\":404,\"message\":\"未找到匹配的 Mock 接口: " + targetPath + "\"}");
            return;
        }

        ApiInterface api = matched.getApiInterface();
        for (Map.Entry<String, String> entry : matched.getPathVars().entrySet()) {
            context.put(Constants.TEMPLATE_PREFIX_PATH + entry.getKey(), entry.getValue());
        }

        // 查找启用的 Mock 规则
        MockRule rule = findEnabledRule(api.getId());
        if (rule == null) {
            response.setStatus(404);
            response.setContentType(Constants.CONTENT_TYPE_JSON_UTF8);
            response.getWriter().write("{\"code\":404,\"message\":\"接口无启用的 Mock 规则\"}");
            logRequest(startTime, request, api, null, targetPath, requestBody, 404, null);
            return;
        }

        // 模拟延迟
        if (rule.getDelayMs() != null && rule.getDelayMs() > 0) {
            try {
                Thread.sleep(rule.getDelayMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 模板渲染
        String renderedBody = TemplateEngine.render(rule.getResponseBody(), context);

        // 写响应
        int statusCode = rule.getResponseStatusCode() != null ? rule.getResponseStatusCode() : Constants.DEFAULT_STATUS_CODE;
        response.setStatus(statusCode);
        response.setContentType(Constants.CONTENT_TYPE_JSON_UTF8);
        response.getWriter().write(renderedBody);

        // 记录日志
        logRequest(startTime, request, api, rule, targetPath, requestBody, statusCode, renderedBody);
    }

    private MockRule findEnabledRule(Long apiInterfaceId) {
        List<MockRule> rules = mockRuleService.list(
                new LambdaQueryWrapper<MockRule>()
                        .eq(MockRule::getApiInterfaceId, apiInterfaceId)
                        .eq(MockRule::getEnabled, Constants.DEFAULT_ENABLED)
                        .orderByDesc(MockRule::getCreatedAt));
        return rules.isEmpty() ? null : rules.get(0);
    }

    private String readBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private void logRequest(long startTime, HttpServletRequest request, ApiInterface api,
                             MockRule rule, String targetPath, String requestBody,
                             int statusCode, String responseBody) {
        RequestLog log = new RequestLog();
        log.setProjectId(api.getProjectId());
        log.setApiInterfaceId(api.getId());
        log.setMockRuleId(rule != null ? rule.getId() : null);
        log.setRequestMethod(request.getMethod());
        log.setRequestPath(targetPath);
        log.setRequestHeaders(headersJson(request));
        log.setRequestBody(requestBody);
        log.setResponseStatusCode(statusCode);
        log.setResponseBody(responseBody);
        log.setDurationMs(System.currentTimeMillis() - startTime);
        log.setCreatedAt(LocalDateTime.now());
        requestLogService.save(log);
    }

    private String headersJson(HttpServletRequest request) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (!first) json.append(",");
            json.append("\"").append(escape(name)).append("\":\"")
                    .append(escape(request.getHeader(name))).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
