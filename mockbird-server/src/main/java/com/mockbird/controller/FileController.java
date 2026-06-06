package com.mockbird.controller;

import com.mockbird.common.Constants;
import com.mockbird.common.Result;
import com.mockbird.dto.OpenApiImportResult;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.Project;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.ProjectService;
import com.mockbird.util.OpenApiParser;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 文件管理 Controller，提供文件上传和 OpenAPI 导入功能。
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final MinioClient minioClient;
    private final String bucket;
    private final ProjectService projectService;
    private final ApiInterfaceService apiInterfaceService;

    public FileController(MinioClient minioClient,
                          String minioBucket,
                          ProjectService projectService,
                          ApiInterfaceService apiInterfaceService) {
        this.minioClient = minioClient;
        this.bucket = minioBucket;
        this.projectService = projectService;
        this.apiInterfaceService = apiInterfaceService;
    }

    @PostMapping("/upload")
    public Result<OpenApiImportResult> upload(@RequestParam("file") MultipartFile file,
                                              @RequestParam("projectId") Long projectId) throws Exception {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.MSG_FILE_EMPTY);
        }
        Project project = projectService.getById(projectId);
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_PROJECT_NOT_FOUND + projectId);
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".json") && !filename.endsWith(".yaml") && !filename.endsWith(".yml"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.MSG_UNSUPPORTED_FORMAT);
        }

        String objectName = Constants.MINIO_OBJECT_PREFIX + UUID.randomUUID() + "-" + filename;
        byte[] bytes = file.getBytes();
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(stream, bytes.length, Constants.MINIO_PART_SIZE)
                    .contentType(Constants.MINIO_UPLOAD_CONTENT_TYPE)
                    .build());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "文件存储失败: " + e.getMessage());
        }

        String content = new String(bytes, Constants.CHARSET_UTF8);
        List<ApiInterface> interfaces;
        try {
            interfaces = OpenApiParser.parse(content, projectId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        OpenApiImportResult result = new OpenApiImportResult();
        if (!interfaces.isEmpty()) {
            apiInterfaceService.saveBatch(interfaces);
            result.setImportedCount(interfaces.size());
        }
        result.setFileUrl("/" + bucket + "/" + objectName);
        return Result.success(result);
    }
}
