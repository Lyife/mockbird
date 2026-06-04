package com.mockbird.util;

import com.mockbird.entity.ApiInterface;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiParserTest {

    private static final String VALID_OPENAPI = "{\n" +
            "  \"openapi\": \"3.0.0\",\n" +
            "  \"info\": { \"title\": \"Pet Store\", \"version\": \"1.0\" },\n" +
            "  \"paths\": {\n" +
            "    \"/pets\": {\n" +
            "      \"get\": {\n" +
            "        \"summary\": \"List all pets\",\n" +
            "        \"parameters\": [{\"name\": \"limit\", \"in\": \"query\"}],\n" +
            "        \"responses\": {\n" +
            "          \"200\": {\n" +
            "            \"content\": {\n" +
            "              \"application/json\": {\n" +
            "                \"example\": [{\"id\": 1, \"name\": \"dog\"}]\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"post\": {\n" +
            "        \"summary\": \"Create a pet\",\n" +
            "        \"responses\": {\n" +
            "          \"201\": {}\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/pets/{id}\": {\n" +
            "      \"get\": {\n" +
            "        \"responses\": {\n" +
            "          \"200\": {\n" +
            "            \"content\": {\n" +
            "              \"application/json\": {\n" +
            "                \"schema\": { \"type\": \"object\" }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    void shouldParseValidOpenApiJson() {
        List<ApiInterface> list = OpenApiParser.parse(VALID_OPENAPI, 1L);
        assertEquals(3, list.size());

        ApiInterface first = list.get(0);
        assertEquals(1L, first.getProjectId().longValue());
        assertEquals("/pets", first.getPath());
        assertEquals("GET", first.getMethod());
        assertEquals("List all pets", first.getName());
        assertNotNull(first.getRequestParams());

        ApiInterface second = list.get(1);
        assertEquals("POST", second.getMethod());
        assertEquals("Create a pet", second.getName());

        ApiInterface third = list.get(2);
        assertEquals("/pets/{id}", third.getPath());
        assertEquals("GET", third.getMethod());
        assertNotNull(third.getResponseExample());
    }

    @Test
    void shouldFallbackNameWhenNoSummary() {
        String json = "{\"openapi\":\"3.0.0\",\"paths\":{\"/api/test\":{\"get\":{\"responses\":{}}}}}";
        List<ApiInterface> list = OpenApiParser.parse(json, 1L);
        assertEquals(1, list.size());
        assertEquals("GET /api/test", list.get(0).getName());
    }

    @Test
    void shouldThrowWhenMissingOpenApiField() {
        String json = "{\"info\":{},\"paths\":{}}";
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> OpenApiParser.parse(json, 1L));
        assertTrue(e.getMessage().contains("openapi"));
    }

    @Test
    void shouldThrowWhenMissingPaths() {
        String json = "{\"openapi\":\"3.0.0\"}";
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> OpenApiParser.parse(json, 1L));
        assertTrue(e.getMessage().contains("paths"));
    }

    @Test
    void shouldThrowWhenInvalidJson() {
        String invalidJson = "{{\"broken\": true}";
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> OpenApiParser.parse(invalidJson, 1L));
        assertTrue(e.getMessage().contains("格式"));
    }

    @Test
    void shouldReturnEmptyListForEmptyPaths() {
        String json = "{\"openapi\":\"3.0.0\",\"paths\":{}}";
        List<ApiInterface> list = OpenApiParser.parse(json, 1L);
        assertTrue(list.isEmpty());
    }

    @Test
    void shouldSkipNonHttpMethodFields() {
        String json = "{\"openapi\":\"3.0.0\",\"paths\":{\"/test\":{\"summary\":\"should be skipped\",\"get\":{\"responses\":{}}}}}";
        List<ApiInterface> list = OpenApiParser.parse(json, 1L);
        assertEquals(1, list.size());
        assertEquals("GET /test", list.get(0).getName());
    }
}
