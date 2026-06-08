package com.mockbird.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mockbird.common.Constants;
import com.mockbird.common.PageResult;
import com.mockbird.common.Result;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.MockRule;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.MockRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Mock 规则管理 Controller。
 * 提供 Mock 规则的 CRUD 端点，规则关联到具体接口后配置响应模板。
 */
@RestController
@RequestMapping("/api/mock-rules")
public class MockRuleController {

    private final MockRuleService mockRuleService;
    private final ApiInterfaceService apiInterfaceService;

    public MockRuleController(MockRuleService mockRuleService, ApiInterfaceService apiInterfaceService) {
        this.mockRuleService = mockRuleService;
        this.apiInterfaceService = apiInterfaceService;
    }

    /**
     * 分页查询 Mock 规则列表，可按接口 ID 筛选。
     */
    @GetMapping
    public Result<PageResult<MockRule>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long apiInterfaceId) {

        LambdaQueryWrapper<MockRule> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(MockRule::getName, name);
        }
        if (apiInterfaceId != null) {
            wrapper.eq(MockRule::getApiInterfaceId, apiInterfaceId);
        }
        wrapper.orderByDesc(MockRule::getCreatedAt);

        Page<MockRule> mpPage = mockRuleService.page(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(mpPage, mpPage.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<MockRule> detail(@PathVariable Long id) {
        MockRule rule = mockRuleService.getById(id);
        if (rule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_MOCK_RULE_NOT_FOUND + id);
        }
        return Result.success(rule);
    }

    @PostMapping
    public Result<MockRule> create(@RequestBody MockRule rule) {
        if (rule.getApiInterfaceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "关联接口 ID 不能为空");
        }
        ApiInterface api = apiInterfaceService.getById(rule.getApiInterfaceId());
        if (api == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "关联接口不存在: " + rule.getApiInterfaceId());
        }
        if (rule.getEnabled() == null) {
            rule.setEnabled(Constants.DEFAULT_ENABLED);
        }
        if (rule.getResponseStatusCode() == null) {
            rule.setResponseStatusCode(Constants.DEFAULT_STATUS_CODE);
        }
        if (rule.getDelayMs() == null) {
            rule.setDelayMs(Constants.DEFAULT_DELAY_MS);
        }
        mockRuleService.save(rule);
        return Result.success(rule);
    }

    @PutMapping("/{id}")
    public Result<MockRule> update(@PathVariable Long id, @RequestBody MockRule rule) {
        MockRule existing = mockRuleService.getById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_MOCK_RULE_NOT_FOUND + id);
        }
        rule.setId(id);
        mockRuleService.updateById(rule);
        return Result.success(mockRuleService.getById(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        MockRule existing = mockRuleService.getById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_MOCK_RULE_NOT_FOUND + id);
        }
        mockRuleService.removeById(id);
        return Result.success(null);
    }
}
