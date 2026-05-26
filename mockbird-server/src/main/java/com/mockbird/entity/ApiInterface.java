package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_interface")
public class ApiInterface {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String name;

    private String path;

    private String method;

    private String upstreamUrl;

    private String requestParams;

    private String responseExample;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
