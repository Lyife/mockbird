# CHANGELOG

## Day 1 — 项目脚手架搭建（2026-05-23）

**完成内容**：

- 搭建 Maven 父子工程结构，父 POM 管理 Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 版本
- 创建 `mockbird-server` 模块，引入 6 个 starter（Web、Actuator、MyBatis-Plus、MySQL、Lombok、Validation），启动类 `MockBirdApplication`
- 配置 `application.yml`：数据源连接 `mockbird` 库、MyBatis-Plus SQL 日志、Actuator health 端点
- 创建 `mockbird-web` 前端工程（Vue 3 + Vite + Element Plus + Pinia + Axios），配置代理解决开发跨域
- 编写 `docker-compose.yml`，编排 MySQL 9.7 + MinIO + Nacos 2.3.1

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| 后端框架 | Spring Boot 2.7 | Java 1.8 兼容，用户熟练 |
| ORM | MyBatis-Plus 3.5.5 | 用户常用，代码生成方便 |
| 前端框架 | Vue 3 + Element Plus | 社区活跃，AI 友好 |
| 基础设施 | Docker Compose | 一条命令启动全部，不污染本机 |
| 跨域方案 | Vite 开发代理 | 开发阶段最简洁，无需后端配 CORS |

**目录结构**：

```
mockbird/
├── pom.xml                          # Maven 父 POM
├── docker-compose.yml               # MySQL + MinIO + Nacos
├── mockbird-server/
│   ├── pom.xml                      # Spring Boot 依赖
│   └── src/main/
│       ├── java/com/mockbird/
│       │   └── MockBirdApplication.java
│       └── resources/
│           └── application.yml
└── mockbird-web/
    ├── package.json
    ├── vite.config.ts               # 含代理配置
    └── src/
        ├── App.vue
        └── main.ts
```

**代码仓库**：https://github.com/Lyife/mockbird

**当前状态**：基础设施 + 前后端骨架就绪，均可独立启动，尚未编写任何业务代码。

## Day 2 — 数据库设计 + MyBatis-Plus 代码生成（2026-05-25）

**完成内容**：

- 设计 4 张核心表，DDL 写入 `db/init.sql`
- 手动生成 Entity / Mapper / Service / Controller 分层代码
- Project 完整 RESTful CRUD（`/api/projects`）
- 真实请求调用代理接口（`POST /api/interfaces/{id}/invoke`），支持 upstream_url 两级解析
- 12 个 Controller 单元测试全部通过

**数据库表**：

| 表 | 用途 | 关键字段 |
|----|------|---------|
| `project` | 项目 | name, description, upstream_url |
| `api_interface` | 接口定义 | project_id, path, method, upstream_url |
| `mock_rule` | Mock 规则 | api_interface_id, match_type, response_body, delay_ms, enabled |
| `request_log` | 请求日志 | project_id, api_interface_id, mock_rule_id, 完整请求/响应/耗时 |

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| 代码生成 | 手写替代 FastAutoGenerator | JDK 8 兼容性问题 |
| upstream_url | 项目级 + 接口级两级 | 接口级覆盖，项目级兜底 |
| 上游调用 | RestTemplate 代理转发 | 避免浏览器 CORS |
| 测试策略 | 纯 Mockito Controller 单测 | Service/Mapper 无自定义逻辑，测框架无意义 |
