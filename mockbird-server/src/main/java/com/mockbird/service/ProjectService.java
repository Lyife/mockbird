package com.mockbird.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mockbird.entity.Project;

/**
 * Project Service 接口。
 * 继承 IService 后自动获得批量操作方法（如 saveBatch、updateBatchById），
 * 具体业务方法可在此接口中添加声明。
 */
public interface ProjectService extends IService<Project> {
}
