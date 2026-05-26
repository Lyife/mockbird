package com.mockbird.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mockbird.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
