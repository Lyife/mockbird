-- MockBird 核心表 DDL
-- 数据库: mockbird

CREATE TABLE IF NOT EXISTS project (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(100) NOT NULL            COMMENT '项目名称',
    description VARCHAR(500)                     COMMENT '项目描述',
    upstream_url VARCHAR(500)                    COMMENT '默认上游地址，如 http://real-backend:8080',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

CREATE TABLE IF NOT EXISTS api_interface (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_id      BIGINT NOT NULL              COMMENT '所属项目',
    name            VARCHAR(200) NOT NULL        COMMENT '接口名称',
    path            VARCHAR(500) NOT NULL        COMMENT '请求路径',
    method          VARCHAR(10) NOT NULL         COMMENT '请求方法 GET/POST/PUT/DELETE',
    upstream_url    VARCHAR(500)                 COMMENT '上游地址，为空则用项目级',
    request_params  TEXT                         COMMENT '请求参数 JSON',
    response_example TEXT                        COMMENT '响应示例 JSON',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口定义表';

CREATE TABLE IF NOT EXISTS mock_rule (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    api_interface_id    BIGINT NOT NULL          COMMENT '关联接口',
    name                VARCHAR(200)             COMMENT '规则名称',
    match_type          VARCHAR(20) DEFAULT 'exact' COMMENT 'exact/wildcard/regex',
    match_rule          VARCHAR(500)             COMMENT '匹配表达式',
    response_status_code INT DEFAULT 200         COMMENT '响应状态码',
    response_body       TEXT                     COMMENT '响应体模板',
    response_headers    TEXT                     COMMENT '响应头 JSON',
    delay_ms            INT DEFAULT 0            COMMENT '模拟延迟毫秒',
    enabled             TINYINT DEFAULT 1        COMMENT '启用状态',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Mock规则表';

CREATE TABLE IF NOT EXISTS request_log (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_id          BIGINT                   COMMENT '所属项目',
    api_interface_id    BIGINT                   COMMENT '命中的接口',
    mock_rule_id        BIGINT                   COMMENT '命中的规则',
    request_method      VARCHAR(10)              COMMENT '请求方法',
    request_path        VARCHAR(500)             COMMENT '请求路径',
    request_headers     TEXT                     COMMENT '请求头 JSON',
    request_body        TEXT                     COMMENT '请求体',
    response_status_code INT                     COMMENT '响应状态码',
    response_body       TEXT                     COMMENT '响应体',
    duration_ms         BIGINT                   COMMENT '耗时毫秒',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请求日志表';
