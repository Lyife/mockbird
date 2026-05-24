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
