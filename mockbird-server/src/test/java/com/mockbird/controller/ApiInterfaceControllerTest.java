package com.mockbird.controller;

import com.mockbird.common.GlobalExceptionHandler;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.Project;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.statusCode").value(200))
                .andExpect(jsonPath("$.data.body").value("{\"result\":\"ok\"}"));
    }

    @Test
    void shouldFailWhenInterfaceNotFound() throws Exception {
        when(apiInterfaceService.getById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/interfaces/999/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("接口不存在: 999"));
    }

    @Test
    void shouldFailWhenProjectNotFound() throws Exception {
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(null);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("项目不存在: 1"));
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldUseInterfaceUpstreamUrlWhenSet() throws Exception {
        apiInterface.setUpstreamUrl("http://override:9090");
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", null, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(restTemplate).exchange(
                argThat(uri -> uri.toString().equals("http://override:9090/api/test")),
                eq(HttpMethod.GET),
                any(),
                eq(String.class));
    }

    @Test
    void shouldReturn500WhenUpstreamFails() throws Exception {
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE"})
    void shouldInvokeWithDifferentHttpMethods(String method) throws Exception {
        apiInterface.setMethod(method);
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", null, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.resolve(method)), any(), eq(String.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldForwardQueryParams() throws Exception {
        when(apiInterfaceService.getById(1L)).thenReturn(apiInterface);
        when(projectService.getById(1L)).thenReturn(project);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", null, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(responseEntity);

        String json = "{\"queryParams\": {\"page\": \"1\", \"size\": \"10\"}}";
        mockMvc.perform(post("/api/interfaces/1/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(restTemplate).exchange(
                argThat(uri -> uri.toString().contains("page=1") && uri.toString().contains("size=10")),
                eq(HttpMethod.GET),
                any(),
                eq(String.class));
    }
}
