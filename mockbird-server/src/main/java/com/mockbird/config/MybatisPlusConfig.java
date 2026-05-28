package com.mockbird.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类。
 * 主要作用是注册分页插件，使 {@code Page<T>} 查询能自动生成 SQL LIMIT 子句。
 * 不注册此插件时，分页对象不会拦截查询，导致全表查询。
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 核心拦截器，并添加 MySQL 分页内部拦截器。
     * PaginationInnerInterceptor 会拦截所有分页查询，
     * 根据数据库方言（MySQL）生成对应的 LIMIT 语句。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
