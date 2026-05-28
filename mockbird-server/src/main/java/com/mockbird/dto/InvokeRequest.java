package com.mockbird.dto;

import lombok.Data;

import java.util.Map;

/**
 * 真实请求调用（代理转发）的请求 DTO。
 * 用户在前端输入参数后，后端将请求代理转发到上游服务器。
 */
@Data
public class InvokeRequest {

    /** 查询参数，如 ?page=1&size=10 */
    private Map<String, String> queryParams;

    /** 自定义请求头 */
    private Map<String, String> headers;

    /** 请求体内容，JSON 字符串 */
    private String body;
}
