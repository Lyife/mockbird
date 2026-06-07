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

## Day 3 — 统一响应格式 + 全局异常处理 + 单元测试规范（2026-05-26）

**完成内容**：

- 定义 `Result<T>` 统一返回体（code/message/data），所有接口返回 JSON 信封
- 实现 `GlobalExceptionHandler`（@ControllerAdvice），统一处理 ResponseStatusException / MethodArgumentNotValidException / Exception
- Project 实体 name 字段加 `@NotBlank` 校验，Controller 加 `@Valid`
- Delete 返回 `Result<Void>` 替代裸字符串 "ok"
- 测试适配：加 `setControllerAdvice`，jsonPath 加 `$.data` 前缀，错误测试校验 code/message
- 按 springboot-tdd skill 规范审查并修复 Day 2 测试：补 JaCoCo 插件、补异常/参数校验/参数化测试/verify 校验，19 个测试全部通过

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| 返回包装 | `Result<T>` 直接返回 | 显式清晰，比 ResponseBodyAdvice 适合学习 |
| 异常处理 | @ControllerAdvice 三个 handler | 覆盖业务异常、校验异常、兜底异常 |
| 测试适配 | standaloneSetup + setControllerAdvice | 保持单测速度，不引入 Spring 上下文 |
| JaCoCo 版本 | 0.8.12 | JDK 8 兼容 |

## Day 4 — 接口管理 CRUD + 分页查询 + DTO/VO 分层 + 全项目注释（2026-05-27）

**完成内容**：

- ApiInterface 完整 CRUD（分页列表/详情/创建/更新/删除），保持原有 `/invoke` 代理接口不变
- 分页查询支持按名称模糊搜索、按方法类型筛选（GET/POST/PUT/DELETE）、按项目过滤
- DTO/VO 分层：请求用 DTO（ApiInterfaceCreateRequest/UpdateRequest/PageRequest），响应用 VO（含跨表 projectName）
- `PageResult<T>` 统一分页响应包装 + MyBatis-Plus 分页插件配置
- Bean Validation 参数校验（`@Valid` + `@NotNull`/`@NotBlank`）
- 全项目 27 个 Java 文件 Javadoc 注释补全
- InvokeRequest/InvokeResponse 统一使用 Lombok `@Data`
- ApiInterfaceControllerTest 25 个测试（9 invoke + 16 CRUD），34 个测试全部通过

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| 请求参数 | DTO 类替代裸字段 | 可加校验注解，与 Entity 解耦 |
| 响应数据 | VO 含 projectName | 避免前端二次查项目名 |
| projectId 不可改 | UpdateRequest 不含 projectId | 接口归属创建后不应变更 |
| 分页条件 | LambdaQueryWrapper | 编译期安全检查，避免字符串字段名 |
| method 统一 uppercase | `toUpperCase()` | 存储层统一，查询时不区分大小写 |
| 全项目注释 | 27 个文件全加 Javadoc | 学习项目，注释便利理解 |

**AI 使用心得**：

| 发现 | 教训 |
|------|------|
| AI 默认保守缩小改动范围 | 不是提示问题，AI 应先做全项目扫描确认边界，责任在 AI |
| 安装插件/hooks 不知道自己装了什么 | ECC 插件带了 232 skills + GateGuard 拦截，后续装插件前先让 AI 扫描 |
| Hooks 对 AI 透明 | PreToolUse hooks 在工具执行前拦截，AI 感知不到，表象是"改不动代码" |
| GateGuard 成本高 | 本次会话 $59+，GateGuard 每个 Edit 需要 2-3 次重复调用，已禁用 |

**当前状态**：后端 4 张表、34 测试、全 CRUD + 注释，Day 5 将进入 Mock 规则引擎。

## Day 5 — Mock 规则引擎：路径匹配 + 响应模板 + 单元测试（2026-05-28）

**完成内容**：

- 实现 `PathMatcher` 路径匹配器：精确匹配 > `{param}` 路径变量匹配
- 实现 `TemplateEngine` 模板引擎：`${timestamp}`、`${randomInt}`、`${randomInt(min,max)}`、`${request.param.xxx}`、`${request.header.xxx}`、`${request.body}`
- `MockRuleController` 完整 CRUD，创建时校验关联接口是否存在
- `MockRequestController` Mock 请求入口（`/mock/**`），串联路径匹配→规则查找→模板渲染→日志记录
- Log 写入 `request_log` 表
- 7+8+11=26 个新增测试，全项目 60 测试通过
- curl 端到端验证：`/mock/actuator/health` 返回动态数据

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| Mock 入口 | `/mock/**` 独立映射 | 与 `/api/*` CRUD 分离 |
| 路径匹配 | {param} → `([^/]+)` regex | JDK 自带，无需引入第三方 |
| 模板引擎 | 自实现 regex 替换 | 简单够用 |
| MatchResult | 独立文件 | Java public class 规则 |

**当前状态**：Mock 引擎核心链路跑通，60 测试通过。Day 6-7 周末复盘，Day 8 进入 Swagger 导入或前端开发。

## Day 6 — 第一周复盘（2026-06-02）

**完成内容**：

- 全项目 60 测试通过确认
- 逐模块代码回顾（35 源文件 + 5 测试，9 个模块）
- Mock 引擎全链路口述验证
- 生成项目 README（快速启动、API 概览、占位符说明）
- 总结 3 个有效 Prompt 模式：精准 Spec、分层拆分、流程 Prompt
- 本周踩坑汇总（6 项）+ 认知收获（4 条）
- Obsidian 学习笔记 + Memory 更新

**Prompt 模式总结**：

| 模式 | 核心 | 代表案例 |
|------|------|---------|
| 精准 Spec → 零返工 | 先写清楚目标再动手 | Day 5 Mock 引擎一把过 |
| 分层拆分 | 复杂需求拆成独立可验证小块 | Day 4 分页+DTO/VO+校验 |
| 流程 Prompt > 代码 Prompt | 管理 AI 行为比写好代码更重要 | 三段式 TaskCreate 清单 |

**认知收获**：

1. 学习流程是踩坑踩出来的，不是设计出来的——三次错误才收敛到三段式清单
2. Memory 不重读 = 不存在——强制 Day 开始时读核心规则文件
3. 复杂项目需要更窄更精确的 Prompt——一次一个函数，给足上下文
4. 经验可编码为 Skill——双循环流程可封装为可复用技能

**当前状态**：第一周结束，后端核心功能完整（4 表 + 60 测试 + Mock 引擎）。Day 8 开始第二周：Swagger 导入或前端开发。

## Day 8 — Swagger/OpenAPI 文件导入（2026-06-03）

**完成内容**：

- 集成 MinIO 对象存储（minio:7.1.4, JDK 8 兼容），MinioConfig 自动创建 bucket
- 实现 `OpenApiParser` 解析器：支持 OpenAPI 3.0 JSON/YAML，遍历 paths 提取 GET/POST/PUT/DELETE/PATCH 等操作
- 创建 `FileController` 文件上传端点 `POST /api/files/upload`
- 全链路：上传文件 → 存 MinIO → 解析 OpenAPI → 批量创建 ApiInterface → 返回导入结果
- 异常处理：空文件、项目不存在、格式校验、JSON/YAML 解析错误、非 OpenAPI 文档
- 12 个新增测试（7 OpenApiParser + 5 FileController），全项目 72 测试通过

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| MinIO SDK 版本 | 7.1.4 | JDK 8 兼容，8.x 需要 Java 11+ |
| YAML 解析 | Jackson YAML → JsonNode 统一处理 | 与 JSON 解析复用同一套 extract 逻辑 |
| 解析范围 | 仅 paths 节点 | 第一期不做 schema/components 导入 |
| 名称生成 | summary 优先 → 降级为 METHOD path | 保证每条接口都有可读名称 |
| 构造器注入 | FileController 使用构造器注入 | MinioClient + String bucket + Service 混合注入 |

**新增文件**：MinioConfig、OpenApiParser、OpenApiImportResult、FileController、OpenApiParserTest、FileControllerTest
**修改文件**：pom.xml（2 新依赖）、application.yml（MinIO + multipart 配置）

**当前状态**：后端 72 测试通过，MinIO 集成就绪，OpenAPI 导入功能完成。

## Day 9 — 魔法值清理 + 前端起步（2026-06-05）

**Part 1: 魔法值清理**

- 新建 `Constants.java` 集中管理 22 个共享常量（状态码、错误消息、Content-Type、默认值、模板前缀、MinIO 配置）
- 重构 11 个 Java 文件：common 层（Result、GlobalExceptionHandler）、5 个 Controller、2 个 Util、1 个 Config
- 72 测试通过

**Part 2: 前端后台管理布局**

- 新建 6 个文件：router/index.ts、api/index.ts、api/project.ts、layout/MainLayout.vue、views/Home.vue、views/ProjectList.vue
- 改造 3 个文件：main.ts（注册 router）、App.vue（router-view）、style.css（全局样式）
- Element Plus 后台管理布局：侧边栏菜单 + 顶部导航 + 内容区
- axios 封装：响应拦截器统一 ElMessage 错误提示
- 项目列表页：el-table + el-pagination，调 `/api/projects`
- 修复 3 个运行问题：MinIO 凭据、MySQL JDBC `allowPublicKeyRetrieval`、前端 API 异常未捕获

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| 常量范围 | 只在逻辑代码中用，注解属性不处理 | @TableName、@RequestMapping 属于配置元数据 |
| 前端路由 | `/` 独立首页 + `/projects` 在 MainLayout 内 | 首页不需要侧边栏 |
| Pinia | 暂不引入 | 项目列表页无需全局状态 |
| 错误处理 | axios 拦截器弹提示 + 组件 catch 静默 | 双重防护，不崩溃 |

**当前状态**：后端 72 测试通过，前端布局 + 项目列表页就绪，前后端联调通过。

## Day 10 — 前端项目管理 CRUD 完善 + 接口列表页（2026-06-06）

**完成内容**：

- 项目管理页新增新建/编辑弹窗（el-dialog + el-form）和删除确认（ElMessageBox），操作列含编辑/删除按钮
- 新增接口列表页：分页表格 + 名称搜索 + 方法筛选（el-tag 着色）+ 新建/编辑弹窗（含项目下拉选择）+ 删除确认
- 侧边栏新增"接口管理"菜单项，路由新增 `/interfaces`
- 封装 `api/interface.ts`（5 个类型 + 4 个 API 函数）
- `api/project.ts` 新增 createProject / updateProject / deleteProject

**技术决策**：

| 决策 | 选择 | 原因 |
|------|------|------|
| 项目列表分页 | 前端 slice 假分页 | 项目数量少，后端 GET /api/projects 返回全量 |
| 接口列表分页 | 后端真分页 | 接口可能多，GET /api/interfaces 支持 page/size |
| 方法着色 | el-tag 按方法类型设色 | GET 绿/POST 蓝/PUT 橙/DELETE 红，视觉区分 |
| editingId | null 表示新建，有值表示编辑 | 复用同一弹窗，简单直观 |
| 接口新建编辑 | 同一弹窗，editingId 区分 | 与 ProjectList 保持一致模式 |

**新增文件**：api/interface.ts、views/InterfaceList.vue
**修改文件**：api/project.ts、views/ProjectList.vue、layout/MainLayout.vue、router/index.ts

**当前状态**：前端 2 页面 CRUD 完整，前后端联调通过，准备进入 Mock 规则配置页或在线测试页。
