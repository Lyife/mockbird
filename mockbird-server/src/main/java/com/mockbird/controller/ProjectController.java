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

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @GetMapping
    public Result<List<Project>> list() {
        return Result.success(projectService.list());
    }

    @GetMapping("/{id}")
    public Result<Project> getById(@PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        return Result.success(project);
    }

    @PostMapping
    public Result<Project> create(@Valid @RequestBody Project project) {
        projectService.save(project);
        return Result.success(project);
    }

    @PutMapping("/{id}")
    public Result<Project> update(@PathVariable Long id, @Valid @RequestBody Project project) {
        if (projectService.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        project.setId(id);
        projectService.updateById(project);
        return Result.success(project);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (projectService.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        projectService.removeById(id);
        return Result.success(null);
    }
}
