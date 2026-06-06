package com.mockbird.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mockbird.entity.ApiInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * OpenAPI 3.0 规范文件解析器。
 * 支持 JSON 和 YAML 格式，解析 paths 节点为 ApiInterface 列表。
 */
public class OpenApiParser {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final List<String> HTTP_METHODS = Arrays.asList(
            "get", "post", "put", "delete", "patch", "options", "head", "trace");

    private static final String KEY_OPENAPI = "openapi";
    private static final String KEY_PATHS = "paths";
    private static final String KEY_SUMMARY = "summary";
    private static final String KEY_PARAMETERS = "parameters";
    private static final String KEY_RESPONSES = "responses";
    private static final String KEY_200 = "200";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_APP_JSON = "application/json";
    private static final String KEY_EXAMPLE = "example";
    private static final String KEY_SCHEMA = "schema";

    private OpenApiParser() {
    }

    /**
     * 解析 OpenAPI 内容，每对 (path, method) 生成一条 ApiInterface。
     *
     * @param content   文件内容（JSON 或 YAML 格式）
     * @param projectId 接口归属项目 ID
     * @return ApiInterface 列表，不含 id/createdAt/updatedAt
     * @throws IllegalArgumentException 格式错误或非 OpenAPI 文档
     */
    public static List<ApiInterface> parse(String content, Long projectId) {
        JsonNode root = parseContent(content);
        validateOpenApi(root);
        return extractInterfaces(root, projectId);
    }

    private static JsonNode parseContent(String content) {
        String trimmed = content.trim();
        boolean isJson = trimmed.startsWith("{");
        boolean isYaml = !isJson;
        try {
            if (isJson) {
                return JSON_MAPPER.readTree(trimmed);
            }
            return YAML_MAPPER.readTree(trimmed);
        } catch (JsonProcessingException e) {
            String format = isJson ? "JSON" : "YAML";
            throw new IllegalArgumentException("文件不是合法的 " + format + " 格式：" + e.getOriginalMessage());
        }
    }

    private static void validateOpenApi(JsonNode root) {
        if (!root.has(KEY_OPENAPI)) {
            throw new IllegalArgumentException("不是有效的 OpenAPI 3.0 文档（缺少 openapi 字段）");
        }
        if (!root.has(KEY_PATHS)) {
            throw new IllegalArgumentException("OpenAPI 文档中没有 paths 定义");
        }
    }

    private static List<ApiInterface> extractInterfaces(JsonNode root, Long projectId) {
        List<ApiInterface> list = new ArrayList<>();
        JsonNode paths = root.get(KEY_PATHS);
        Iterator<String> pathNames = paths.fieldNames();
        while (pathNames.hasNext()) {
            String path = pathNames.next();
            JsonNode pathNode = paths.get(path);
            Iterator<String> fieldNames = pathNode.fieldNames();
            while (fieldNames.hasNext()) {
                String method = fieldNames.next().toLowerCase();
                if (!HTTP_METHODS.contains(method)) {
                    continue;
                }
                JsonNode operation = pathNode.get(method);
                ApiInterface api = new ApiInterface();
                api.setProjectId(projectId);
                api.setPath(path);
                api.setMethod(method.toUpperCase());
                api.setName(extractName(operation, path, method));
                api.setRequestParams(extractParams(operation));
                api.setResponseExample(extractResponseExample(operation));
                list.add(api);
            }
        }
        return list;
    }

    private static String extractName(JsonNode operation, String path, String method) {
        if (operation.has(KEY_SUMMARY) && !operation.get(KEY_SUMMARY).asText().isEmpty()) {
            return operation.get(KEY_SUMMARY).asText();
        }
        return method.toUpperCase() + " " + path;
    }

    private static String extractParams(JsonNode operation) {
        if (!operation.has(KEY_PARAMETERS)) {
            return null;
        }
        try {
            return JSON_MAPPER.writeValueAsString(operation.get(KEY_PARAMETERS));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static String extractResponseExample(JsonNode operation) {
        JsonNode responses = operation.get(KEY_RESPONSES);
        if (responses == null || !responses.has(KEY_200)) {
            return null;
        }
        JsonNode ok = responses.get(KEY_200);
        JsonNode content = ok.get(KEY_CONTENT);
        if (content == null || !content.has(KEY_APP_JSON)) {
            return null;
        }
        JsonNode jsonContent = content.get(KEY_APP_JSON);
        if (jsonContent.has(KEY_EXAMPLE)) {
            try {
                return JSON_MAPPER.writeValueAsString(jsonContent.get(KEY_EXAMPLE));
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        if (jsonContent.has(KEY_SCHEMA)) {
            try {
                return JSON_MAPPER.writeValueAsString(jsonContent.get(KEY_SCHEMA));
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return null;
    }
}
