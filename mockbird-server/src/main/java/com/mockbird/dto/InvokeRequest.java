package com.mockbird.dto;

import java.util.Map;

public class InvokeRequest {

    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;

    public Map<String, String> getQueryParams() { return queryParams; }
    public void setQueryParams(Map<String, String> queryParams) { this.queryParams = queryParams; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
