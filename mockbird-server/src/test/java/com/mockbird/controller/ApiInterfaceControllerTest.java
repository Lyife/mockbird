package com.mockbird.controller;

import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.Project;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ApiInterfaceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApiInterfaceService apiInterfaceService;

    @Mock
    private ProjectService projectService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiInterfaceController controller;

    private ApiInterface apiInterface;
    private Project project;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        apiInterface = new ApiInterface();
        apiInterface.setId(1L);
        apiInterface.setProjectId(1L);
        apiInterface.setName("测试接口");
        apiInterface.setPath("/api/test");
        apiInterface.setMethod("GET");

        project = new Project();
        project.setId(1L);
        project.setName("测试项目");
        project.setUpstreamUrl("http://upstream:8080");
    }

    @Test
    void shouldInvokeSuccessfully() throws Exception {
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"result\":\"ok\"}", null, HttpStatus.OK);
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.body").value("{\"result\":\"ok\"}"));
    }

    @Test
    void shouldFailWhenInterfaceNotFound() throws Exception {
        when(apiInterfaceService.getById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/interfaces/999/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenProjectNotFound() throws Exception {
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(null);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenNoUpstreamUrl() throws Exception {
        project.setUpstreamUrl(null);
        apiInterface.setUpstreamUrl(null);
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUseInterfaceUpstreamUrlWhenSet() throws Exception {
        apiInterface.setUpstreamUrl("http://override:9090");
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", null, HttpStatus.OK);
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }
}
