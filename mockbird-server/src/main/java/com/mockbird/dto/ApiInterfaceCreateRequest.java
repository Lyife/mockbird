package com.mockbird.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建接口的请求 DTO。
 * 将前端传入的 JSON 与 Entity 隔离，避免直接暴露数据库实体。
 * {@code projectId} 为必填，确保每个接口归属于一个项目。
 */
@Data
public class ApiInterfaceCreateRequest {
    /** 所属项目 ID，必填 */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** 接口名称，必填 */
    @NotBlank(message = "接口名称不能为空")
    private String name;

    /** 接口路径，如 /api/users，必填 */
    @NotBlank(message = "接口路径不能为空")
    private String path;

    /** 请求方法：GET / POST / PUT / DELETE，必填 */
    @NotBlank(message = "请求方法不能为空")
    private String method;

    /** 接口级上游地址，可选，为空时走项目级默认上游 */
    private String upstreamUrl;

    /** 请求参数示例，JSON 字符串，可选 */
    private String requestParams;

    /** 响应示例，JSON 字符串，可选 */
    private String responseExample;
}
