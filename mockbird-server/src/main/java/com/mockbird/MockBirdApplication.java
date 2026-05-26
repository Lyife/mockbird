package com.mockbird;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mockbird.mapper")
public class MockBirdApplication {
    public static void main(String[] args) {
        SpringApplication.run(MockBirdApplication.class, args);
    }
}
