package com.mockbird.dto;

import lombok.Data;

public class RequestLogPageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Long projectId;

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}
