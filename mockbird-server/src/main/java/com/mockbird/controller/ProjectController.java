package com.mockbird.controller;

import com.mockbird.common.Result;
import com.mockbird.entity.Project;
import com.mockbird.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 项目管理 Controller，提供项目的完整 CRUD 操作。
 * 项目是顶层资源，接口（ApiInterface）归属于项目。
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    /**
     * 获取全部项目列表（不分页，项目数量通常较少）。
     *
     * @return 所有项目的列表
     */
    @GetMapping
    public Result<List<Project>> list() {
        return Result.success(projectService.list());
    }

    /**
     * 根据 ID 查看项目详情。
     *
     * @param id 项目 ID
     * @return 项目实体
     * @throws ResponseStatusException 404 当项目不存在时
     */
    @GetMapping("/{id}")
    public Result<Project> getById(@PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        return Result.success(project);
    }

    /**
     * 创建项目。请求体中的 name 通过 @Valid + @NotBlank 校验。
     *
     * @param project 项目实体（name 必填）
     * @return 创建成功的项目（含自动生成的 id）
     */
    @PostMapping
    public Result<Project> create(@Valid @RequestBody Project project) {
        projectService.save(project);
        return Result.success(project);
    }

    /**
     * 更新项目。先校验存在性，再设置 id 执行更新。
     *
     * @param id      项目 ID
     * @param project 更新内容
     * @return 更新后的项目
     * @throws ResponseStatusException 404 当项目不存在时
     */
    @PutMapping("/{id}")
    public Result<Project> update(@PathVariable Long id, @Valid @RequestBody Project project) {
        // 存在性校验：不存在则返回 404，防止更新无效记录
        if (projectService.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        project.setId(id);
        projectService.updateById(project);
        return Result.success(project);
    }

    /**
     * 删除项目。先校验存在性，再执行删除。
     *
     * @param id 项目 ID
     * @return 空数据，仅表示成功
     * @throws ResponseStatusException 404 当项目不存在时
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (projectService.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        projectService.removeById(id);
        return Result.success(null);
    }
}
