package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 接口定义实体，映射到 api_interface 表。
 * 每个接口归属于一个 Project（通过 projectId 关联），
 * 接口级 upstreamUrl 可覆盖项目级默认上游地址。
 */
@Data
@TableName("api_interface")
public class ApiInterface {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属项目 ID，关联 project 表的 id */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** 接口名称，如"获取用户列表" */
    @NotBlank(message = "接口名称不能为空")
    private String name;

    /** 接口路径，如 /api/users */
    @NotBlank(message = "接口路径不能为空")
    private String path;

    /** HTTP 请求方法：GET / POST / PUT / DELETE */
    @NotBlank(message = "请求方法不能为空")
    private String method;

    /** 接口级上游地址，可为空（为空时走项目级上游） */
    private String upstreamUrl;

    /** 请求参数示例，JSON 格式字符串 */
    private String requestParams;

    /** 响应结果示例，JSON 格式字符串 */
    private String responseExample;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
