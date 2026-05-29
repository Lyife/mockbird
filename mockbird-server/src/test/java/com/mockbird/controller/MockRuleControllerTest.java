package com.mockbird.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mockbird.common.GlobalExceptionHandler;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.MockRule;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.MockRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MockRuleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MockRuleService mockRuleService;

    @Mock
    private ApiInterfaceService apiInterfaceService;

    @InjectMocks
    private MockRuleController controller;

    private MockRule rule;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        rule = new MockRule();
        rule.setId(1L);
        rule.setApiInterfaceId(1L);
        rule.setName("测试规则");
        rule.setResponseBody("{\"code\":200}");
        rule.setResponseStatusCode(200);
        rule.setEnabled(1);
        rule.setDelayMs(0);
    }

    @Test
    void shouldListMockRules() throws Exception {
        when(mockRuleService.page(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<MockRule>().setRecords(Arrays.asList(rule)));

        mockMvc.perform(get("/api/mock-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].name").value("测试规则"));
    }

    @Test
    void shouldFilterByApiInterfaceId() throws Exception {
        when(mockRuleService.page(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<MockRule>().setRecords(Arrays.asList(rule)));

        mockMvc.perform(get("/api/mock-rules?apiInterfaceId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldGetMockRuleDetail() throws Exception {
        when(mockRuleService.getById(1L)).thenReturn(rule);

        mockMvc.perform(get("/api/mock-rules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试规则"));
    }

    @Test
    void shouldReturn404ForMissingDetail() throws Exception {
        when(mockRuleService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/mock-rules/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateMockRule() throws Exception {
        ApiInterface api = new ApiInterface();
        api.setId(1L);
        when(apiInterfaceService.getById(1L)).thenReturn(api);
        when(mockRuleService.save(any(MockRule.class))).thenReturn(true);

        mockMvc.perform(post("/api/mock-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"apiInterfaceId\":1,\"name\":\"新规则\",\"responseBody\":\"{}\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.enabled").value(1))
                .andExpect(jsonPath("$.data.responseStatusCode").value(200));
    }

    @Test
    void shouldRejectCreateWithoutApiInterfaceId() throws Exception {
        mockMvc.perform(post("/api/mock-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"无关联\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithMissingApiInterface() throws Exception {
        when(apiInterfaceService.getById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/mock-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"apiInterfaceId\":999,\"name\":\"无效关联\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateMockRule() throws Exception {
        when(mockRuleService.getById(1L)).thenReturn(rule, rule);

        mockMvc.perform(put("/api/mock-rules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"更新后的规则\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldReturn404ForUpdateMissing() throws Exception {
        when(mockRuleService.getById(999L)).thenReturn(null);

        mockMvc.perform(put("/api/mock-rules/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMockRule() throws Exception {
        when(mockRuleService.getById(1L)).thenReturn(rule);
        when(mockRuleService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/mock-rules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(mockRuleService).removeById(1L);
    }

    @Test
    void shouldReturn404ForDeleteMissing() throws Exception {
        when(mockRuleService.getById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/mock-rules/999"))
                .andExpect(status().isNotFound());
    }
}
