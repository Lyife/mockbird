package com.mockbird.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类。
 * 配置 HTTP 连接超时和读取超时，用于代理转发接口请求到上游服务。
 */
@Configuration
public class RestTemplateConfig {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 30000;

    /**
     * 创建 RestTemplate Bean，配置 5 秒连接超时和 30 秒读取超时。
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return new RestTemplate(factory);
    }
}
