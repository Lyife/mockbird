package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 请求日志实体，映射到 request_log 表。
 * 每次 Mock 调用都会记录一条日志，包含请求信息、匹配的 Mock 规则、
 * 返回的响应内容以及处理耗时，用于问题排查和调用链追踪。
 */
@Data
@TableName("request_log")
public class RequestLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属项目 ID */
    private Long projectId;

    /** 被调用的接口 ID */
    private Long apiInterfaceId;

    /** 命中的 Mock 规则 ID */
    private Long mockRuleId;

    /** 请求的 HTTP 方法：GET / POST / PUT / DELETE */
    private String requestMethod;

    /** 请求路径，如 /api/users */
    private String requestPath;

    /** 请求头，JSON 字符串 */
    private String requestHeaders;

    /** 请求体 */
    private String requestBody;

    /** Mock 返回的 HTTP 状态码 */
    private Integer responseStatusCode;

    /** Mock 返回的响应体 */
    private String responseBody;

    /** 处理耗时（毫秒） */
    private Long durationMs;

    /** 记录创建时间 */
    private LocalDateTime createdAt;
}
