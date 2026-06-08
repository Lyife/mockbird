# MockBird

接口 Mock 服务平台——前端/客户端团队在等待真实后端接口开发期间，用 MockBird 先伪造出接口的响应数据来联调，不被后端进度卡住。

## 产品概述

核心使用链路：**创建项目 → 定义接口 → 配置 Mock 规则 → 前端调 Mock URL 拿假数据 → 查请求日志**。

| 功能 | 说明 |
|------|------|
| 项目管理 | 多项目隔离，项目级 upstream_url 默认上游地址 |
| 接口定义管理 | 手动定义或 Swagger 导入，接口级 upstream_url 可覆盖项目级 |
| Mock 规则配置 | 精确/通配/正则匹配，响应模板支持 `${timestamp}` 占位符，可配延迟 |
| 真实请求调用 | 接口详情页弹窗 → 输入参数 → 后端代理转发到上游 → 展示响应 |
| 在线测试 | 平台内输入参数调 Mock 接口，即时看响应 |
| 请求日志 | 记录每次 Mock 调用的完整请求/响应/耗时 |

### 页面

| 页面 | 内容 |
|------|------|
| 后台管理布局 | 左侧菜单 + 顶部导航 + 内容区 |
| 项目列表页 | 分页表格，新建/编辑弹窗，删除确认 |
| 接口列表页 | 分页表格 + 按名称搜索 + 按方法筛选 |
| Mock 规则配置页 | 选择接口 → 配置匹配规则 → 填写响应内容 → 启停开关 |
| 在线测试页 | 输入请求参数 → 点击发送 → JSON 高亮响应 |
| 请求日志页 | 按项目筛选、按时间排序，详情看完整请求/响应 |
| 产品首页 | 产品介绍 + 快速开始引导 |

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Spring Boot 2.7 + MyBatis-Plus |
| 前端 | Vue 3 + Element Plus + Vite |
| 数据库 | MySQL 9.7 |
| 存储 | MinIO |
| 注册配置 | Nacos 2.3.1 |

## 项目结构

```
mockbird/                  # Maven 父工程
├── mockbird-server/       # Spring Boot 后端（端口 8080）
├── mockbird-web/          # Vue 3 前端（端口 5173）
└── docker-compose.yml     # MySQL + MinIO + Nacos
```

## 快速启动

```bash
# 启动基础设施
docker-compose up -d

# 启动后端
cd mockbird-server && mvn spring-boot:run

# 启动前端
cd mockbird-web && npm run dev
```

## 开发流程

开始任何新 Day 的工作前（用户说"开始dayN"/"继续"/"第N天"等），先读 `memory/MEMORY.md` 获取项目记忆，按 `feedback_day_checklist.md` 逐项执行学习循环启动四步：

1. 回顾上一 Day —— 列出实际产出，不是一句话概括
2. 确认今天目标 —— 让用户确认或调整
3. 开放问题 —— 让用户提问，不是 AI 自问自答
4. 等用户说"开始项目循环"

**自检铁律**：每次准备说出"确认后开始项目循环"之前，回头逐项核对以上四步——每步是执行了还是只提了一嘴。任何一步是"提了一嘴"就必须补。全部确认后才轮到计划模式 → 用户批准 → 项目循环。

## 环境信息

- **Java**: 1.8
- **Maven**: 3.9
- **Node**: v24
- **MySQL**: localhost:3306, root / 123456
- **MinIO**: localhost:9000 (API) / localhost:9001 (Console)
- **Nacos**: localhost:8848 (Console)
