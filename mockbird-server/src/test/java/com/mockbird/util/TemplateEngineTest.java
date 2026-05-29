package com.mockbird.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateEngineTest {

    @Test
    void shouldReplaceTimestamp() {
        String result = TemplateEngine.render("${timestamp}", null);
        assertNotNull(result);
        assertFalse(result.contains("${"));
        long ts = Long.parseLong(result);
        assertTrue(ts > 0);
    }

    @Test
    void shouldReplaceRandomInt() {
        String result = TemplateEngine.render("${randomInt}", null);
        int val = Integer.parseInt(result);
        assertTrue(val >= 0 && val < 10000);
    }

    @Test
    void shouldReplaceRandomIntWithRange() {
        for (int i = 0; i < 20; i++) {
            String result = TemplateEngine.render("${randomInt(10,99)}", null);
            int val = Integer.parseInt(result);
            assertTrue(val >= 10 && val <= 99, "Expected 10~99, got " + val);
        }
    }

    @Test
    void shouldReplaceContextVariable() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("request.param.name", "张三");

        String result = TemplateEngine.render("Hello, ${request.param.name}", ctx);
        assertEquals("Hello, 张三", result);
    }

    @Test
    void shouldReplaceMultiplePlaceholders() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("request.param.name", "test");

        String result = TemplateEngine.render("name=${request.param.name}, body=${request.body}", ctx);
        assertEquals("name=test, body=${request.body}", result);
    }

    @Test
    void shouldKeepUnrecognizedPlaceholder() {
        String result = TemplateEngine.render("${unknown.var}", null);
        assertEquals("${unknown.var}", result);
    }

    @Test
    void shouldHandleNullTemplate() {
        assertNull(TemplateEngine.render(null, null));
    }

    @Test
    void shouldHandleTemplateWithoutPlaceholders() {
        assertEquals("plain text", TemplateEngine.render("plain text", null));
    }
}
