package com.mockbird.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 异步配置，启用 @Async 注解支持。
 * 用于请求日志异步写入，不阻塞 Mock 响应线程。
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
