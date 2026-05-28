package com.mockbird.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 真实请求调用（代理转发）的响应 DTO。
 * 封装了上游服务器返回的完整信息，包括状态码、响应头和耗时。
 */
@Data
public class InvokeResponse {

    /** 上游返回的 HTTP 状态码 */
    private int statusCode;

    /** 上游返回的响应头（一个头可能有多个值） */
    private Map<String, List<String>> headers;

    /** 上游返回的响应体 */
    private String body;

    /** 从发起请求到收到响应的耗时（毫秒） */
    private long durationMs;
}
