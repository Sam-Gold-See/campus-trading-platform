# Campus Trading Platform - 校园跳蚤市场

一个专注校园内部、重信息撮合、轻图片展示的二手交易平台。通过"纯文本流+结构化标签+同校实名+严格信用计算"，实现极低门槛发布与快速、安全的线下撮合。

## 🚀 项目特点

- **校园实名认证**：必须通过 `.edu.cn` 教育邮箱注册
- **双轨信息流**：首页分为"求购"与"转让"两个独立Tab
- **克制化发布**：最多200字纯文本 + 1张实物图
- **结构化标签**：强制选择分类、价格区间、交接校区
- **信用分体系**：动态积分引擎，信用分低于60将被限制发布权限
- **赛博朋克UI**：霓虹发光效果、深色主题、科技感设计

---

## 📦 技术栈

### 后端
- **框架**: Spring Boot 4.0.6
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **ORM**: MyBatis
- **Java版本**: JDK 21

### 前端
- **框架**: Vue 3.5 + TypeScript
- **UI组件库**: Element Plus
- **构建工具**: Vite 8
- **状态管理**: Pinia
- **路由**: Vue Router 5

---

## 📋 环境要求

| 组件 | 版本要求 |
|-----|---------|
| JDK | 21+ |
| Node.js | 20.19.0+ 或 22.12.0+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Maven | 3.8+ |

---

## 📁 项目结构

```
campus-trading-platform/
├── campus-trading-platform-backend/     # 后端项目
│   ├── src/main/java/                   # Java源代码
│   │   └── samgoldsee/campus/trading/platform/
│   │       ├── controller/              # 控制器层
│   │       ├── service/                 # 服务层
│   │       ├── mapper/                  # 数据访问层
│   │       ├── entity/                  # 实体类
│   │       ├── dto/                     # 数据传输对象
│   │       ├── config/                  # 配置类
│   │       ├── security/                # 安全认证
│   │       ├── exception/               # 异常处理
│   │       ├── interceptor/             # 拦截器
│   │       └── task/                    # 定时任务
│   ├── src/main/resources/
│   │   ├── application.yaml             # 主配置文件
│   │   ├── application-dev.yaml         # 开发环境配置
│   │   └ sql/Init.sql                   # 数据库初始化脚本
│   └── pom.xml                          # Maven配置
│
├── campus-trading-platform-frontend/    # 前端项目
│   ├── src/
│   │   ├── api/                         # API接口
│   │   ├── components/                  # 组件
│   │   ├── layout/                      # 布局组件
│   │   ├── router/                      # 路由配置
│   │   ├── stores/                      # 状态管理
│   │   ├── styles/                      # 样式文件（赛博朋克主题）
│   │   ├── types/                       # TypeScript类型定义
│   │   ├── utils/                       # 工具函数
│   │   └── views/                       # 页面视图
│   ├── package.json                     # NPM配置
│   ├── vite.config.ts                   # Vite配置
│   └── .env.development                  # 开发环境变量
│   └── .env.production                   # 生产环境变量
│
└── md/                                   # 项目文档
    ├── 需求分析.md
    ├── 概要设计/
    └── 详细设计/
```

---

## 🗄️ 数据库配置

### 1. 创建数据库并执行初始化脚本

**方式一：MySQL命令行**

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本（会自动创建数据库和表）
source campus-trading-platform-backend/src/main/resources/sql/Init.sql
```

**方式二：MySQL Workbench / Navicat**

1. 打开 MySQL Workbench 或 Navicat
2. 连接到 MySQL 服务器
3. 打开 `campus-trading-platform-backend/src/main/resources/sql/Init.sql`
4. 执行整个脚本

**方式三：Docker MySQL容器**

```bash
# 启动MySQL容器
docker run -d --name mysql-ctp \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -p 3306:3306 \
  mysql:8.0

# 等待MySQL启动后，执行初始化脚本
docker exec -i mysql-ctp mysql -uroot -p123456 \
  < campus-trading-platform-backend/src/main/resources/sql/Init.sql
```

### 2. 初始化脚本内容

脚本会自动完成：
- 创建数据库 `campus_trading_platform`
- 创建表：`user`、`category`、`item`、`review`
- 插入8个分类数据
- 插入8个测试用户（密码均为 `password123`）
- 插入模拟物品和评价数据

---

## 🔧 后端运行

### 方式一：本地开发环境

#### 1. 配置环境变量

编辑 `application-dev.yaml`，修改以下配置：

```yaml
ctp:
  datasource:
    host: localhost          # MySQL地址
    port: 3306               # MySQL端口
    database: campus_trading_platform
    username: root           # MySQL用户名
    password: 123456         # MySQL密码
  redis:
    host: 127.0.0.1          # Redis地址
    port: 6379               # Redis端口
    database: 0
  jwt:
    secret: your-secret-key-must-be-at-least-32-characters-long
    expire: 86400            # Token过期时间（秒）
  root:
    edu-email: admin@scnu.edu.cn
    password: admin123
```

#### 2. 启动Redis

```bash
# Windows（需先安装Redis）
redis-server

# Linux/Mac
redis-server /usr/local/etc/redis.conf
```

#### 3. 编译并运行

```bash
cd campus-trading-platform-backend

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/campus-trading-platform-backend-0.0.1-SNAPSHOT.jar
```

后端启动后访问：`http://localhost:8080`

---

### 方式二：Docker部署

#### 1. 创建Dockerfile

在 `campus-trading-platform-backend/` 目录下创建 `Dockerfile`：

```dockerfile
# 使用多阶段构建
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 设置环境变量
ENV CTP_DATASOURCE_HOST=mysql \
    CTP_DATASOURCE_PORT=3306 \
    CTP_DATASOURCE_USERNAME=root \
    CTP_DATASOURCE_PASSWORD=123456 \
    CTP_REDIS_HOST=redis \
    CTP_REDIS_PORT=6379 \
    CTP_REDIS_DATABASE=0 \
    CTP_JWT_SECRET=samgoldsee.campus.trading.platform.jwt.secret.key \
    CTP_JWT_EXPIRE=86400 \
    CTP_ROOT_EDU_EMAIL=admin@scnu.edu.cn \
    CTP_ROOT_PASSWORD=admin123

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. 创建docker-compose.yml

在项目根目录创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mysql-ctp
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: campus_trading_platform
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./campus-trading-platform-backend/src/main/resources/sql/Init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - ctp-network

  redis:
    image: redis:7-alpine
    container_name: redis-ctp
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - ctp-network

  backend:
    build: ./campus-trading-platform-backend
    container_name: backend-ctp
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      CTP_DATASOURCE_HOST: mysql
      CTP_DATASOURCE_PORT: 3306
      CTP_DATASOURCE_USERNAME: root
      CTP_DATASOURCE_PASSWORD: 123456
      CTP_REDIS_HOST: redis
      CTP_REDIS_PORT: 6379
      CTP_REDIS_DATABASE: 0
      CTP_JWT_SECRET: samgoldsee.campus.trading.platform.jwt.secret.key
      CTP_JWT_EXPIRE: 86400
      CTP_ROOT_EDU_EMAIL: admin@scnu.edu.cn
      CTP_ROOT_PASSWORD: admin123
    networks:
      - ctp-network

volumes:
  mysql_data:
  redis_data:

networks:
  ctp-network:
    driver: bridge
```

#### 3. 启动Docker服务

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看后端日志
docker-compose logs -f backend

# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

---

## 🖥️ 前端运行

### 方式一：本地开发环境

#### 1. 安装依赖

```bash
cd campus-trading-platform-frontend

# 安装依赖
npm install

# 或使用pnpm（更快）
pnpm install
```

#### 2. 配置环境变量

`.env.development` 文件已配置好：

```env
VITE_API_BASE_URL=/api
```

#### 3. 启动开发服务器

```bash
# 启动开发服务器
npm run dev

# 或
pnpm dev
```

前端启动后访问：`http://localhost:5173`

> **注意**：前端通过 Vite 代理自动转发 `/api` 请求到 `http://localhost:8080`

#### 4. 构建生产版本

```bash
# 类型检查
npm run type-check

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

---

### 方式二：Docker部署前端

#### 1. 创建前端Dockerfile

在 `campus-trading-platform-frontend/` 目录下创建 `Dockerfile`：

```dockerfile
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 2. 创建nginx配置

在 `campus-trading-platform-frontend/` 目录下创建 `nginx.conf`：

```nginx
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API代理到后端
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
    gzip_min_length 1000;
}
```

#### 3. 更新docker-compose.yml

添加前端服务：

```yaml
services:
  frontend:
    build: ./campus-trading-platform-frontend
    container_name: frontend-ctp
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - ctp-network
```

---

## 👤 测试账号

数据库初始化后，以下账号可用于测试：

| 邮箱 | 密码 | 昵称 | 信用分 |
|-----|------|------|--------|
| admin@scnu.edu.cn | password123 | 管理员 | 100 |
| zhangsan@scnu.edu.cn | password123 | 张三 | 95 |
| lisi@scnu.edu.cn | password123 | 李四 | 88 |
| wangwu@scnu.edu.cn | password123 | 王五 | 75 |
| xiaoming@scnu.edu.cn | password123 | 小明 | 100 |
| xiaohong@scnu.edu.cn | password123 | 小红 | 82 |
| test001@scnu.edu.cn | password123 | 测试用户1 | 60 |
| test002@scnu.edu.cn | password123 | 测试用户2 | 55 |

> **提示**：`test002@scnu.edu.cn` 信用分低于60，可用于测试权限限制功能

---

## 📚 API接口文档

### 用户模块 `/api/user`

| 接口 | 方法 | 描述 |
|-----|------|------|
| `/login` | POST | 用户登录 |
| `/sendRegisterCode` | POST | 发送注册验证码 |
| `/register` | POST | 用户注册 |
| `/profile` | GET | 获取当前用户资料 |
| `/profile/{userId}` | GET | 获取他人公开资料 |
| `/editNickname` | PUT | 修改昵称 |
| `/editPassword` | PUT | 修改密码 |
| `/logout` | POST | 登出 |

### 物品模块 `/api/item`

| 接口 | 方法 | 描述 |
|-----|------|------|
| `/publish` | POST | 发布物品（需信用分≥60） |
| `/feed` | GET | 获取需求墙列表 |
| `/search` | GET | 搜索筛选 |
| `/my-list` | GET | 我的发布列表 |
| `/{id}` | GET | 物品详情 |
| `/{id}/bump` | PUT | 擦亮物品 |
| `/{id}/offline` | PUT | 下架物品 |
| `/{id}/sold` | PUT | 标记已成交 |

### 分类模块 `/api/config`

| 接口 | 方法 | 描述 |
|-----|------|------|
| `/categories` | GET | 获取分类列表 |
| `/dict` | GET | 获取配置字典 |

### 评价模块 `/api/evaluate`

| 接口 | 方法 | 描述 |
|-----|------|------|
| `/submit` | POST | 提交评价 |
| `/user/{revieweeId}` | GET | 用户评价列表 |

---

## 🔒 信用分机制

### 信用分计算规则

| 行为 | 分数变化 |
|-----|---------|
| 新用户注册 | +100（基础分） |
| 交易成交 | +1 |
| 收到好评 | +2 |
| 收到差评 | -2 |

### 信用分等级

| 分数范围 | 徽章颜色 | 状态 |
|---------|---------|------|
| ≥90 | 金色（success） | 优秀 |
| 75-89 | 蓝色（primary） | 良好 |
| 60-74 | 黄色（warning） | 一般 |
| <60 | 红色（danger） | **限制发布权限** |

> 当信用分低于60分时，用户将被禁止发布物品和主动私信

---

## ⏰ 定时任务

系统包含以下定时任务：

| 任务 | 执行周期 | 功能 |
|-----|---------|------|
| 物品过期下架 | 每小时 | 自动下架超过14天未成交的物品 |

---

## 🎨 赛博朋克UI主题

本项目采用赛博朋克深色主题设计，特点包括：

- **霓虹发光效果**：按钮、卡片、输入框悬浮时发光
- **深色背景**：多层次深色背景色系
- **科技感字体**：Orbitron、Rajdhani、Share Tech Mono
- **网格背景**：登录/注册页面网格纹理

### 自定义主题颜色

修改 `src/styles/cyberpunk.css`：

```css
:root {
  /* 主色调 - 霓虹青色 */
  --cyber-primary: #00f0ff;
  
  /* 辅助色 - 霓虹粉色 */
  --cyber-secondary: #ff00aa;
  
  /* 强调色 - 霓虹紫色 */
  --cyber-accent: #9d00ff;
}
```

---

## 🚢 生产部署清单

### 后端部署

1. ✅ 修改 `application-prod.yaml` 配置生产环境参数
2. ✅ 使用环境变量覆盖敏感配置
3. ✅ 启用 HTTPS（配置 SSL 证书）
4. ✅ 配置日志输出路径
5. ✅ 设置 JVM 参数：`-Xms512m -Xmx1024m`

### 前端部署

1. ✅ 修改 `.env.production` 配置生产API地址
2. ✅ 执行 `npm run build` 构建
3. ✅ 配置 Nginx 反向代理
4. ✅ 启用 gzip 压缩
5. ✅ 配置 CDN 加速静态资源

### 数据库部署

1. ✅ 定期备份数据库
2. ✅ 配置主从复制（可选）
3. ✅ 设置合理的连接池大小

---

## 📝 开发指南

### 分支管理

```
main        # 生产分支
├── develop # 开发分支
│   ├── feature/xxx  # 功能分支
│   └── bugfix/xxx   # 修复分支
```

### 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 样式修改
refactor: 代码重构
test: 测试相关
chore: 构建/工具相关
```

---

## 🐛 常见问题

### 1. 后端启动失败 - 数据库连接错误

**解决方案**：
- 确认 MySQL 已启动
- 检查 `application-dev.yaml` 中数据库配置
- 确认数据库 `campus_trading_platform` 已创建

### 2. 后端启动失败 - Redis连接错误

**解决方案**：
- 确认 Redis 已启动：`redis-cli ping` 应返回 `PONG`
- 检查 Redis 端口是否正确（默认6379）

### 3. 前端登录后跳转失败

**解决方案**：
- 确认后端已启动且返回正确 Token
- 检查浏览器 localStorage 中是否保存了 token
- 清除浏览器缓存重新登录

### 4. 前端API请求401错误

**解决方案**：
- 确认请求头携带 `Authorization: Bearer {token}`
- 检查 Token 是否过期
- 重新登录获取新 Token

---

## 📄 License

本项目仅供学习和研究使用。

---

## 📞 联系方式

如有问题，请提交 Issue 或联系项目维护者。