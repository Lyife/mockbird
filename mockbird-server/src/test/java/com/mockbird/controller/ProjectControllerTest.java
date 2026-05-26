package com.mockbird.controller;

import com.mockbird.entity.Project;
import com.mockbird.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController controller;

    private Project project;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        project = new Project();
        project.setId(1L);
        project.setName("测试项目");
        project.setDescription("项目描述");
    }

    @Test
    void shouldReturnProjectList() throws Exception {
        when(projectService.list()).thenReturn(Arrays.asList(project));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("测试项目"));
    }

    @Test
    void shouldReturnProjectById() throws Exception {
        when(projectService.getById(1L)).thenReturn(project);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("测试项目"));
    }

    @Test
    void shouldReturn404WhenProjectNotFound() throws Exception {
        when(projectService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProject() throws Exception {
        when(projectService.save(any(Project.class))).thenReturn(true);

        String json = "{\"name\":\"新项目\",\"description\":\"描述\"}";
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("新项目"));
    }

    @Test
    void shouldUpdateProject() throws Exception {
        when(projectService.getById(1L)).thenReturn(project);
        when(projectService.updateById(any(Project.class))).thenReturn(true);

        String json = "{\"name\":\"改名后的项目\"}";
        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("改名后的项目"));
    }

    @Test
    void shouldReturn404WhenUpdatingMissingProject() throws Exception {
        when(projectService.getById(999L)).thenReturn(null);

        String json = "{\"name\":\"改名\"}";
        mockMvc.perform(put("/api/projects/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteProject() throws Exception {
        when(projectService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
