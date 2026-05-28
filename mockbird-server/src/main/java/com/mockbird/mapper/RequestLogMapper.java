package com.mockbird.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mockbird.entity.RequestLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * RequestLog MyBatis-Plus Mapper 接口，映射到 request_log 表。
 * 继承 BaseMapper 后自动获得 CRUD 方法，无需手写 SQL。
 */
@Mapper
public interface RequestLogMapper extends BaseMapper<RequestLog> {
}
