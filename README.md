# 风险地理信息系统 - 后端

## 项目简介

风险地理信息系统（Risk GIS）后端服务，基于 Spring Boot 3.2 + MyBatis-Plus + PostgreSQL + PostGIS 构建，提供 GIS 空间分析、风险评估、灾害预警等核心业务功能的 API 接口。

## 技术栈

- **框架**: Spring Boot 3.2
- **语言**: Java 17+
- **数据库**: PostgreSQL 15+ with PostGIS 3.3+
- **ORM**: MyBatis-Plus 3.5+
- **工具库**: Hutool 5.8+
- **GIS 工具**: GeoTools 28.2+
- **安全**: Spring Security + JWT
- **API 文档**: SpringDoc OpenAPI (Swagger UI)
- **构建工具**: Maven

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- PostgreSQL 15+ with PostGIS extension

### 数据库设置

1. 安装 PostgreSQL 和 PostGIS

```bash
# macOS
brew install postgresql postgis

# Ubuntu
sudo apt-get install postgresql postgresql-contrib postgis
```

2. 创建数据库

```sql
CREATE DATABASE risk_gis_dev;
\c risk_gis_dev
CREATE EXTENSION postgis;
```

3. 运行应用

```bash
# 使用开发环境配置
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 访问应用

- API 基础路径: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- 健康检查: http://localhost:8080/api/health

### 默认账号

- 用户名: admin
- 密码: admin123

## API 文档

启动应用后访问 Swagger UI: http://localhost:8080/api/swagger-ui.html

## 开发指南

### 构建项目

```bash
mvn clean package
```

### 运行测试

```bash
mvn test
```

### 代码规范

- 遵循 Java 编码规范
- 使用 Lombok 减少样板代码
- 编写单元测试和集成测试
- 提交前运行代码检查

## 项目结构

```
src/main/java/com/riskgis/
├── controller/        # REST 控制器
├── service/           # 业务逻辑层
├── mapper/            # 数据访问层（MyBatis-Plus）
├── model/             # 实体类
├── dto/               # 数据传输对象
├── config/            # 配置类
├── security/          # 安全组件
├── filter/            # 过滤器
└── util/              # 工具类
```

## 部署说明

### 生产环境

```bash
# 打包应用
mvn clean package -Pprod

# 运行应用
java -jar target/risk-gis-backend-1.0.0.jar \
  --spring.profiles.active=prod \
  --DB_URL=jdbc:postgresql://prod-db:5432/risk_gis \
  --DB_USERNAME=prod_user \
  --DB_PASSWORD=prod_password \
  --JWT_SECRET=prod-secret-key \
  --AMAP_KEY=prod-amap-key
```

## 更新日志

### 2026-05-11

- 初始化后端项目
- 实现 GIS、风险、认证模块
- 配置 Swagger API 文档
- 配置 JWT 认证
