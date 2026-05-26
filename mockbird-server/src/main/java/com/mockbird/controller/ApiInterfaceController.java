package com.mockbird.controller;

import com.mockbird.dto.InvokeRequest;
import com.mockbird.dto.InvokeResponse;
import com.mockbird.entity.ApiInterface;
import com.mockbird.entity.Project;
import com.mockbird.service.ApiInterfaceService;
import com.mockbird.service.ProjectService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/interfaces")
public class ApiInterfaceController {

    @Resource
    private ApiInterfaceService apiInterfaceService;

    @Resource
    private ProjectService projectService;

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/{id}/invoke")
    public InvokeResponse invoke(@PathVariable Long id, @RequestBody InvokeRequest request) {
        ApiInterface api = apiInterfaceService.getById(id);
        if (api == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "接口不存在: " + id);
        }

        Project project = projectService.getById(api.getProjectId());
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "项目不存在: " + api.getProjectId());
        }

        String upstreamUrl = api.getUpstreamUrl();
        if (upstreamUrl == null || upstreamUrl.isEmpty()) {
            upstreamUrl = project.getUpstreamUrl();
        }
        if (upstreamUrl == null || upstreamUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请先配置上游地址（项目级或接口级）");
        }

        String url = upstreamUrl + api.getPath();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (request.getQueryParams() != null) {
            request.getQueryParams().forEach(uriBuilder::queryParam);
        }
        URI uri = uriBuilder.build().toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(httpHeaders::add);
        }
        HttpEntity<String> entity = new HttpEntity<>(request.getBody(), httpHeaders);

        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.resolve(api.getMethod().toUpperCase()),
                entity,
                String.class
        );
        long duration = System.currentTimeMillis() - start;

        InvokeResponse result = new InvokeResponse();
        result.setStatusCode(response.getStatusCodeValue());
        result.setHeaders(response.getHeaders());
        result.setBody(response.getBody());
        result.setDurationMs(duration);
        return result;
    }
}
