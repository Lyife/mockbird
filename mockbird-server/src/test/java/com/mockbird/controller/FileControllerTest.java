package com.mockbird.controller;

import com.mockbird.common.GlobalExceptionHandler;
import com.mockbird.entity.Project;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.ProjectService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MinioClient minioClient;

    @Mock
    private ProjectService projectService;

    @Mock
    private ApiInterfaceService apiInterfaceService;

    private FileController controller;

    private static final String VALID_OPENAPI = "{\n" +
            "  \"openapi\": \"3.0.0\",\n" +
            "  \"info\": { \"title\": \"Test\", \"version\": \"1.0\" },\n" +
            "  \"paths\": {\n" +
            "    \"/users\": {\n" +
            "      \"get\": { \"summary\": \"List users\", \"responses\": {} },\n" +
            "      \"post\": { \"summary\": \"Create user\", \"responses\": {} }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @BeforeEach
    void setUp() {
        controller = new FileController(minioClient, "mockbird", projectService, apiInterfaceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldUploadAndImportSuccessfully() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test");
        when(projectService.getById(1L)).thenReturn(project);

        MockMultipartFile file = new MockMultipartFile("file", "api.json",
                MediaType.APPLICATION_JSON_VALUE, VALID_OPENAPI.getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.importedCount").value(2));

        verify(minioClient).putObject(any(PutObjectArgs.class));
        verify(apiInterfaceService).saveBatch(anyList());
    }

    @Test
    void shouldRejectEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "api.json",
                MediaType.APPLICATION_JSON_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("projectId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("文件不能为空"));
    }

    @Test
    void shouldRejectMissingProject() throws Exception {
        when(projectService.getById(99L)).thenReturn(null);

        MockMultipartFile file = new MockMultipartFile("file", "api.json",
                MediaType.APPLICATION_JSON_VALUE, VALID_OPENAPI.getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("projectId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("项目不存在: 99"));
    }

    @Test
    void shouldRejectNonJsonFile() throws Exception {
        Project project = new Project();
        project.setId(1L);
        when(projectService.getById(1L)).thenReturn(project);

        MockMultipartFile file = new MockMultipartFile("file", "readme.txt",
                MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("projectId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("仅支持 JSON/YAML 格式的文件"));
    }

    @Test
    void shouldRejectInvalidOpenApiContent() throws Exception {
        Project project = new Project();
        project.setId(1L);
        when(projectService.getById(1L)).thenReturn(project);

        MockMultipartFile file = new MockMultipartFile("file", "api.json",
                MediaType.APPLICATION_JSON_VALUE, "{\"not\": \"openapi\"}".getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("projectId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("不是有效的 OpenAPI 3.0 文档（缺少 openapi 字段）"));
    }
}
