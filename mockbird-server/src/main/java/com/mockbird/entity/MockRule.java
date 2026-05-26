package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mock_rule")
public class MockRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long apiInterfaceId;

    private String name;

    private String matchType;

    private String matchRule;

    private Integer responseStatusCode;

    private String responseBody;

    private String responseHeaders;

    private Integer delayMs;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
