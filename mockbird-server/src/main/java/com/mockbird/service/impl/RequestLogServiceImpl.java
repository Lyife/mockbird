package com.mockbird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockbird.entity.RequestLog;
import com.mockbird.mapper.RequestLogMapper;
import com.mockbird.service.RequestLogService;
import org.springframework.stereotype.Service;

/**
 * RequestLog Service 实现类。
 * 继承 ServiceImpl 后自动获得 MyBatis-Plus 通用 CRUD 方法，
 * 复杂查询逻辑可在此类中添加。
 */
@Service
public class RequestLogServiceImpl extends ServiceImpl<RequestLogMapper, RequestLog> implements RequestLogService {
}
