package com.mockbird.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新接口的请求 DTO。
 * 与创建 DTO 不同，不包含 {@code projectId}，
 * 因为接口的归属项目在创建后不允许变更。
 */
@Data
public class ApiInterfaceUpdateRequest {
    /** 接口名称，必填 */
    @NotBlank(message = "接口名称不能为空")
    private String name;

    /** 接口路径，如 /api/users，必填 */
    @NotBlank(message = "接口路径不能为空")
    private String path;

    /** 请求方法：GET / POST / PUT / DELETE，必填 */
    @NotBlank(message = "请求方法不能为空")
    private String method;

    /** 接口级上游地址，可选 */
    private String upstreamUrl;

    /** 请求参数示例，JSON 字符串，可选 */
    private String requestParams;

    /** 响应示例，JSON 字符串，可选 */
    private String responseExample;
}
