package com.mockbird.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口响应 VO（View Object），用于向前端返回接口数据。
 * 相比 ApiInterface 实体，额外包含了关联的 {@code projectName}，
 * 通过跨表查询 Project 填充，避免前端额外请求。
 */
@Data
public class ApiInterfaceVO {
    private Long id;
    /** 所属项目 ID */
    private Long projectId;
    /** 所属项目名称（跨表查询 Project 填充） */
    private String projectName;
    /** 接口名称 */
    private String name;
    /** 接口路径，如 /api/users */
    private String path;
    /** 请求方法：GET / POST / PUT / DELETE */
    private String method;
    /** 接口级上游地址，为空时使用项目级上游地址 */
    private String upstreamUrl;
    /** 请求参数示例，JSON 字符串 */
    private String requestParams;
    /** 响应示例，JSON 字符串 */
    private String responseExample;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
