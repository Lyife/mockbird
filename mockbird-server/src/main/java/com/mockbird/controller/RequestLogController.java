package com.mockbird.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mockbird.common.Constants;
import com.mockbird.common.PageResult;
import com.mockbird.common.Result;
import com.mockbird.dto.RequestLogPageRequest;
import com.mockbird.entity.RequestLog;
import com.mockbird.service.RequestLogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/request-logs")
public class RequestLogController {

    @Resource
    private RequestLogService requestLogService;

    @GetMapping
    public Result<PageResult<RequestLog>> list(RequestLogPageRequest req) {
        LambdaQueryWrapper<RequestLog> wrapper = new LambdaQueryWrapper<>();
        if (req.getProjectId() != null) {
            wrapper.eq(RequestLog::getProjectId, req.getProjectId());
        }
        wrapper.orderByDesc(RequestLog::getCreatedAt);

        Page<RequestLog> page = requestLogService.page(
                new Page<>(req.getPage(), req.getSize()), wrapper);

        return Result.success(PageResult.of(page, page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<RequestLog> getById(@PathVariable Long id) {
        RequestLog log = requestLogService.getById(id);
        if (log == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, Constants.MSG_REQUEST_LOG_NOT_FOUND + id);
        }
        return Result.success(log);
    }
}
