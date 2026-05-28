package com.mockbird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockbird.entity.Project;
import com.mockbird.mapper.ProjectMapper;
import com.mockbird.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * Project Service 实现类。
 * 继承 ServiceImpl 后自动获得 MyBatis-Plus 通用 CRUD 方法，
 * 复杂查询逻辑可在此类中添加。
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
}
