# MockBird

接口 Mock 服务平台——前端/客户端团队在等待真实后端接口开发期间，用 MockBird 先伪造出接口的响应数据来联调，不被后端进度卡住。

## 核心链路

**创建项目 → 定义接口 → 配置 Mock 规则 → 前端调 Mock URL 拿假数据 → 查请求日志**

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + JDK 8 |
| 前端 | Vue 3 + Element Plus + Vite |
| 数据库 | MySQL 9.7 |
| 存储 | MinIO |
| 注册配置 | Nacos 2.3.1 |

## 快速启动

```bash
# 1. 启动基础设施
docker-compose up -d

# 2. 初始化数据库（首次）
mysql -h localhost -u root -p123456 < db/init.sql

# 3. 启动后端（端口 8080）
cd mockbird-server && mvn spring-boot:run

# 4. 启动前端（端口 5173）
cd mockbird-web && npm install && npm run dev
```

## 项目结构

```
mockbird/
├── pom.xml                          # Maven 父 POM
├── docker-compose.yml               # MySQL + MinIO + Nacos
├── db/init.sql                      # 建表 DDL
├── mockbird-server/                 # Spring Boot 后端
│   └── src/main/java/com/mockbird/
│       ├── common/                  # Result、PageResult、GlobalExceptionHandler
│       ├── config/                  # MybatisPlusConfig、RestTemplateConfig
│       ├── controller/              # 5 个 Controller
│       ├── dto/                     # 请求 DTO
│       ├── entity/                  # 4 张表实体
│       ├── mapper/                  # MyBatis-Plus Mapper
│       ├── service/                 # 业务逻辑
│       ├── util/                    # PathMatcher、TemplateEngine
│       └── vo/                      # 响应 VO
└── mockbird-web/                    # Vue 3 前端
```

## API 概览

| 路由前缀 | 说明 |
|---------|------|
| `GET /mock/**` | Mock 请求入口（支持所有 HTTP 方法）|
| `GET /api/projects` | 项目 CRUD |
| `GET /api/interfaces` | 接口定义 CRUD |
| `POST /api/interfaces/{id}/invoke` | 代理转发到上游 |
| `GET /api/mock-rules` | Mock 规则 CRUD |
| `GET /api/request-logs` | 请求日志查询 |

### Mock 响应模板占位符

| 占位符 | 说明 |
|--------|------|
| `${timestamp}` | 当前时间戳（毫秒）|
| `${randomInt}` | 随机整数 |
| `${randomInt(min,max)}` | 指定范围随机整数 |
| `${request.param.xxx}` | 请求 Query 参数 |
| `${request.header.xxx}` | 请求 Header |
| `${request.body}` | 请求体原文 |

### 路径匹配

支持精确匹配和 `{param}` 路径变量匹配，精确匹配优先。

```
/api/users/123      → 精确匹配 /api/users/123
/api/users/456      → 模式匹配 /api/users/{id} → pathVars: {id: "456"}
```

## 环境要求

- **Java**: 1.8+
- **Maven**: 3.9+
- **Node**: 24+
- **MySQL**: 8.0+
- **端口**: 8080（后端）、5173（前端）、3306（MySQL）、9000/9001（MinIO）、8848（Nacos）

## 开发进度

| Day | 内容 | 日期 |
|-----|------|------|
| 1 | 项目脚手架搭建 | 05-23 |
| 2 | 数据库设计 + CRUD 生成 | 05-25 |
| 3 | 统一响应 + 全局异常 + 测试规范 | 05-26 |
| 4 | 接口管理 CRUD + DTO/VO 分层 | 05-27 |
| 5 | Mock 规则引擎（路径匹配 + 模板引擎）| 05-28 |
| 6-7 | 第一周复盘 | 06-02 |

## 单元测试

```bash
mvn -pl mockbird-server test
```

60 个测试，覆盖 Controller + Util 层。
