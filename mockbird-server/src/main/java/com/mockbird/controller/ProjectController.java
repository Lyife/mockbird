package com.mockbird.controller;

import com.mockbird.entity.Project;
import com.mockbird.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @GetMapping
    public List<Project> list() {
        return projectService.list();
    }

    @GetMapping("/{id}")
    public Project getById(@PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        return project;
    }

    @PostMapping
    public Project create(@RequestBody Project project) {
        projectService.save(project);
        return project;
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project project) {
        if (projectService.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在: " + id);
        }
        project.setId(id);
        projectService.updateById(project);
        return project;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        projectService.removeById(id);
        return "ok";
    }
}
