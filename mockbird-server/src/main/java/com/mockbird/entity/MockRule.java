package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Mock 规则实体，映射到 mock_rule 表。
 * 每条规则关联一个接口（apiInterfaceId），定义在什么条件下返回什么 Mock 数据。
 * 支持延迟响应（delayMs）模拟网络慢速场景。
 */
@Data
@TableName("mock_rule")
public class MockRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的接口 ID */
    private Long apiInterfaceId;

    /** 规则名称 */
    private String name;

    /** 匹配类型：exact(精确) / wildcard(通配) / regex(正则) */
    private String matchType;

    /** 匹配规则表达式 */
    private String matchRule;

    /** Mock 返回的 HTTP 状态码，默认 200 */
    private Integer responseStatusCode;

    /** Mock 返回的响应体，JSON 字符串 */
    private String responseBody;

    /** Mock 返回的响应头，JSON 字符串 */
    private String responseHeaders;

    /** 模拟延迟时间（毫秒） */
    private Integer delayMs;

    /** 是否启用：1 启用 / 0 停用 */
    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
