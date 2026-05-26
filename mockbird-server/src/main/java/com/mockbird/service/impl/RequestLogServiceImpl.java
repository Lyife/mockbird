package com.mockbird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockbird.entity.RequestLog;
import com.mockbird.mapper.RequestLogMapper;
import com.mockbird.service.RequestLogService;
import org.springframework.stereotype.Service;

@Service
public class RequestLogServiceImpl extends ServiceImpl<RequestLogMapper, RequestLog> implements RequestLogService {
}
