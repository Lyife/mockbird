package com.mockbird.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mockbird.common.Constants;
import com.mockbird.common.PageResult;
import com.mockbird.common.Result;
import com.mockbird.dto.ApiInterfaceCreateRequest;
import com.mockbird.dto.ApiInterfacePageRequest;
import com.mockbird.dto.ApiInterfaceUpdateRequest;
import com.mockbird.dto.InvokeRequest;
import com.mockbird.dto.InvokeResponse;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.Project;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.ProjectService;
import com.mockbird.vo.ApiInterfaceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口管理 Controller，提供接口的完整 CRUD 以及代理调用功能。
 *
 * <h3>一对多关系处理</h3>
 * 接口（ApiInterface）通过 projectId 归属于项目（Project）。
 * 列表和详情接口在返回时会跨表查询 Project，填充 projectName 字段。
 *
 * <h3>DTO/VO 分层</h3>
 * - 请求用 DTO（ApiInterfaceCreateRequest / ApiInterfaceUpdateRequest），隔离前端 JSON 与 Entity
 * - 响应用 VO（ApiInterfaceVO），可携带跨表关联字段
 * - Controller 不直接返回 Entity，避免暴露数据库结构
 */
@RestController
@RequestMapping("/api/interfaces")
public class ApiInterfaceController {

    @Resource
    private ApiInterfaceService apiInterfaceService;

    @Resource
    private ProjectService projectService;

    @Resource
    private RestTemplate restTemplate;

    // ==================== CRUD 接口 ====================

    /**
     * 分页查询接口列表，支持按名称模糊搜索、按方法筛选、按项目筛选。
     * 返回的每个接口 VO 中包含 projectName（通过 projectId 跨表查询 Project 获取）。
     *
     * @param request 查询参数（page/size/name/method/projectId），由 URL Query String 自动绑定
     * @return 分页结果，包含当前页数据列表和总记录数
     */
    @GetMapping
    public Result<PageResult<ApiInterfaceVO>> list(ApiInterfacePageRequest request) {
        // 构建动态查询条件：有值才加，无值则忽略
        LambdaQueryWrapper<ApiInterface> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getName())) {
            wrapper.like(ApiInterface::getName, request.getName());
        }
        if (StringUtils.hasText(request.getMethod())) {
            // 统一转大写存储，避免 "get" 和 "GET" 不匹配
            wrapper.eq(ApiInterface::getMethod, request.getMethod().toUpperCase());
        }
        if (request.getProjectId() != null) {
            wrapper.eq(ApiInterface::getProjectId, request.getProjectId());
        }
        wrapper.orderByDesc(ApiInterface::getCreatedAt);

        // MyBatis-Plus 分页查询（需配合 MybatisPlusConfig 中的分页插件使用）
        Page<ApiInterface> page = apiInterfaceService.page(
                new Page<>(request.getPage(), request.getSize()), wrapper);

        // Entity → VO 转换，同时填充 projectName
        List<ApiInterfaceVO> vos = page.getRecords().stream().map(api -> {
            ApiInterfaceVO vo = new ApiInterfaceVO();
            BeanUtils.copyProperties(api, vo);
            // 跨表查询：每个接口关联的项目名称
            Project project = projectService.getById(api.getProjectId());
            if (project != null) {
                vo.setProjectName(project.getName());
            }
            return vo;
        }).collect(Collectors.toList());

        return Result.success(PageResult.of(page, vos));
    }

    /**
     * 查看接口详情，返回带有项目名称的 VO。
     *
     * @param id 接口 ID
     * @return 接口详情 VO，包含 projectName
     * @throws ResponseStatusException 404 当接口不存在时
     */
    @GetMapping("/{id}")
    public Result<ApiInterfaceVO> getById(@PathVariable Long id) {
        ApiInterface api = apiInterfaceService.getById(id);
        if (api == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_INTERFACE_NOT_FOUND + id);
        }
        // Entity → VO，并查关联项目名称
        ApiInterfaceVO vo = new ApiInterfaceVO();
        BeanUtils.copyProperties(api, vo);
        Project project = projectService.getById(api.getProjectId());
        if (project != null) {
            vo.setProjectName(project.getName());
        }
        return Result.success(vo);
    }

    /**
     * 创建接口。请求体使用 DTO 而非 Entity，前端无需关心数据库字段。
     * method 字段统一转大写以确保存储一致性。
     *
     * @param request 创建请求 DTO（参数通过 @Valid 校验）
     * @return 创建成功的接口 VO
     */
    @PostMapping
    public Result<ApiInterfaceVO> create(@Valid @RequestBody ApiInterfaceCreateRequest request) {
        // DTO → Entity
        ApiInterface api = new ApiInterface();
        BeanUtils.copyProperties(request, api);
        api.setMethod(request.getMethod().toUpperCase());
        apiInterfaceService.save(api);

        // 构建响应用的 VO
        ApiInterfaceVO vo = new ApiInterfaceVO();
        BeanUtils.copyProperties(api, vo);
        Project project = projectService.getById(api.getProjectId());
        if (project != null) {
            vo.setProjectName(project.getName());
        }
        return Result.success(vo);
    }

    /**
     * 更新接口。先校验接口是否存在（404），再执行更新。
     * projectId 不允许修改，因此更新 DTO 中不包含该字段。
     *
     * @param id      接口 ID
     * @param request 更新请求 DTO
     * @return 更新后的接口 VO
     * @throws ResponseStatusException 404 当接口不存在时
     */
    @PutMapping("/{id}")
    public Result<ApiInterfaceVO> update(@PathVariable Long id, @Valid @RequestBody ApiInterfaceUpdateRequest request) {
        // 存在性校验：不存在则返回 404
        ApiInterface api = apiInterfaceService.getById(id);
        if (api == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_INTERFACE_NOT_FOUND + id);
        }
        // DTO 拷贝到已有 Entity（保留 id 和 projectId）
        BeanUtils.copyProperties(request, api);
        api.setMethod(request.getMethod().toUpperCase());
        api.setId(id);
        apiInterfaceService.updateById(api);

        ApiInterfaceVO vo = new ApiInterfaceVO();
        BeanUtils.copyProperties(api, vo);
        Project project = projectService.getById(api.getProjectId());
        if (project != null) {
            vo.setProjectName(project.getName());
        }
        return Result.success(vo);
    }

    /**
     * 删除接口。先校验存在性，再执行删除。
     * 与 ProjectController.delete 保持一致的防护逻辑。
     *
     * @param id 接口 ID
     * @return 空数据，仅表示成功
     * @throws ResponseStatusException 404 当接口不存在时
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (apiInterfaceService.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.MSG_INTERFACE_NOT_FOUND + id);
        }
        apiInterfaceService.removeById(id);
        return Result.success(null);
    }

    // ==================== 代理调用接口 ====================

    /**
     * 将前端请求代理转发到上游服务器，实现"真实请求调用"功能。
     *
     * <h3>上游地址优先级</h3>
     * 接口级 upstreamUrl > 项目级 upstreamUrl > 报错。
     *
     * <h3>请求构建</h3>
     * 使用 RestTemplate 将用户的 query 参数和请求头代理转发给上游，返回上游的完整响应。
     *
     * @param id      接口 ID
     * @param request 代理调用请求（queryParams / headers / body）
     * @return 上游服务器返回的状态码、响应头、响应体和耗时
     */
    @PostMapping("/{id}/invoke")
    public Result<InvokeResponse> invoke(@PathVariable Long id, @RequestBody InvokeRequest request) {
        // 1. 查接口定义
        ApiInterface api = apiInterfaceService.getById(id);
        if (api == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.MSG_INTERFACE_NOT_FOUND + id);
        }

        // 2. 查所属项目
        Project project = projectService.getById(api.getProjectId());
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.MSG_PROJECT_NOT_FOUND + api.getProjectId());
        }

        // 3. 确定上游地址：接口级优先，其次项目级
        String upstreamUrl = api.getUpstreamUrl();
        if (upstreamUrl == null || upstreamUrl.isEmpty()) {
            upstreamUrl = project.getUpstreamUrl();
        }
        if (upstreamUrl == null || upstreamUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请先配置上游地址（项目级或接口级）");
        }

        // 4. 拼接完整 URL：上游地址 + 接口路径
        String url = upstreamUrl + api.getPath();

        // 5. 拼接 query 参数
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (request.getQueryParams() != null) {
            request.getQueryParams().forEach(uriBuilder::queryParam);
        }
        URI uri = uriBuilder.build().toUri();

        // 6. 设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(httpHeaders::add);
        }
        HttpEntity<String> entity = new HttpEntity<>(request.getBody(), httpHeaders);

        // 7. 发起代理请求并计时
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.resolve(api.getMethod().toUpperCase()),
                entity,
                String.class
        );
        long duration = System.currentTimeMillis() - start;

        // 8. 封装上游响应
        InvokeResponse result = new InvokeResponse();
        result.setStatusCode(response.getStatusCodeValue());
        result.setHeaders(response.getHeaders());
        result.setBody(response.getBody());
        result.setDurationMs(duration);
        return Result.success(result);
    }
}
