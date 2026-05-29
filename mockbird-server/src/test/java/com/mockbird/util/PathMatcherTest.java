package com.mockbird.util;

import com.mockbird.entity.ApiInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PathMatcherTest {

    private List<ApiInterface> interfaces;

    @BeforeEach
    void setUp() {
        interfaces = new ArrayList<>();

        ApiInterface api1 = new ApiInterface();
        api1.setId(1L);
        api1.setPath("/api/user/login");
        api1.setMethod("POST");
        interfaces.add(api1);

        ApiInterface api2 = new ApiInterface();
        api2.setId(2L);
        api2.setPath("/api/user/{id}");
        api2.setMethod("GET");
        interfaces.add(api2);

        ApiInterface api3 = new ApiInterface();
        api3.setId(3L);
        api3.setPath("/api/user/{userId}/post/{postId}");
        api3.setMethod("GET");
        interfaces.add(api3);
    }

    @Test
    void shouldExactMatch() {
        MatchResult result = PathMatcher.match("/api/user/login", interfaces);
        assertNotNull(result);
        assertEquals(1L, result.getApiInterface().getId().longValue());
        assertTrue(result.getPathVars().isEmpty());
    }

    @Test
    void shouldMatchPathVariable() {
        MatchResult result = PathMatcher.match("/api/user/123", interfaces);
        assertNotNull(result);
        assertEquals(2L, result.getApiInterface().getId().longValue());
        assertEquals("123", result.getPathVars().get("id"));
    }

    @Test
    void shouldMatchMultiplePathVariables() {
        MatchResult result = PathMatcher.match("/api/user/100/post/42", interfaces);
        assertNotNull(result);
        assertEquals(3L, result.getApiInterface().getId().longValue());
        assertEquals("100", result.getPathVars().get("userId"));
        assertEquals("42", result.getPathVars().get("postId"));
    }

    @Test
    void shouldPreferExactOverPattern() {
        ApiInterface exact = new ApiInterface();
        exact.setId(10L);
        exact.setPath("/api/user/login");
        exact.setMethod("POST");
        interfaces.add(0, exact);

        MatchResult result = PathMatcher.match("/api/user/login", interfaces);
        assertNotNull(result);
        assertEquals(10L, result.getApiInterface().getId().longValue());
        assertTrue(result.getPathVars().isEmpty());
    }

    @Test
    void shouldReturnNullWhenNoMatch() {
        assertNull(PathMatcher.match("/api/unknown/path", interfaces));
    }

    @Test
    void shouldReturnNullForNullRequestPath() {
        assertNull(PathMatcher.match(null, interfaces));
    }

    @Test
    void shouldReturnNullForEmptyInterfaces() {
        assertNull(PathMatcher.match("/api/test", Collections.emptyList()));
    }
}
