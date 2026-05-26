package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("request_log")
public class RequestLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long apiInterfaceId;

    private Long mockRuleId;

    private String requestMethod;

    private String requestPath;

    private String requestHeaders;

    private String requestBody;

    private Integer responseStatusCode;

    private String responseBody;

    private Long durationMs;

    private LocalDateTime createdAt;
}
