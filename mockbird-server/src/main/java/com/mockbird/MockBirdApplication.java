package com.mockbird;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MockBird 启动类。
 * {@code @MapperScan} 扫描 Mapper 接口目录，免去每个 Mapper 单独加 {@code @Mapper} 注解。
 */
@SpringBootApplication
@MapperScan("com.mockbird.mapper")
public class MockBirdApplication {
    public static void main(String[] args) {
        SpringApplication.run(MockBirdApplication.class, args);
    }
}
