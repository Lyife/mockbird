package com.mockbird.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mockbird.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * Project MyBatis-Plus Mapper 接口，映射到 project 表。
 * 继承 BaseMapper 后自动获得 CRUD 方法，无需手写 SQL。
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
