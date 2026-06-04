package com.mockbird.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAPI 文件导入结果。
 */
@Data
public class OpenApiImportResult {

    /** 成功导入的接口数量 */
    private int importedCount;

    /** 导入过程中产生的警告/错误信息 */
    private List<String> errors = new ArrayList<>();

    /** 上传文件在 MinIO 中的 URL */
    private String fileUrl;
}
