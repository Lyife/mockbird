package com.mockbird.dto;

import lombok.Data;

/**
 * 接口列表分页查询的请求参数 DTO。
 * 不是 @RequestBody，而是通过 URL Query String 绑定：
 * {@code ?page=1&size=10&name=用户&method=GET&projectId=1}
 * Spring MVC 会自动将同名 query 参数映射到字段上。
 */
@Data
public class ApiInterfacePageRequest {
    /** 当前页码，默认 1 */
    private Integer page = 1;
    /** 每页条数，默认 10 */
    private Integer size = 10;
    /** 按接口名称模糊搜索，可选 */
    private String name;
    /** 按请求方法精确筛选，可选 */
    private String method;
    /** 按项目 ID 筛选，可选 */
    private Long projectId;
}
