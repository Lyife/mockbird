package com.mockbird.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "项目名称不能为空")
    private String name;

    private String description;

    private String upstreamUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
