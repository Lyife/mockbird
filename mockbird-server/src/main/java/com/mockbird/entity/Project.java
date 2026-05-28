package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 项目实体，映射到 project 表。
 * 项目是顶层资源，每个项目下可挂多个接口（ApiInterface）。
 * 项目的 upstreamUrl 作为该项目的默认上游地址，接口级 upstreamUrl 可覆盖它。
 */
@Data
@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目名称，必填 */
    @NotBlank(message = "项目名称不能为空")
    private String name;

    /** 项目描述，可选 */
    private String description;

    /** 项目级默认上游地址，接口级 upstreamUrl 可覆盖 */
    private String upstreamUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
