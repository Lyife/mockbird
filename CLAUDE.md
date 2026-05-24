# MockBird

接口 Mock 服务平台。

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

## 环境信息

- **Java**: 1.8
- **Maven**: 3.9
- **Node**: v24
- **MySQL**: localhost:3306, root / 123456
- **MinIO**: localhost:9000 (API) / localhost:9001 (Console)
- **Nacos**: localhost:8848 (Console)
