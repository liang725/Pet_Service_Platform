# 宠物之家 - 用户登录注册系统

这是一个完整的宠物平台用户登录注册系统，包含前端Vue.js应用和后端Spring Boot API。

## 项目结构

```
├── backend/                 # Spring Boot后端项目
│   ├── src/main/java/      # Java源码
│   │   └── com/ddliang/backend/
│   │       ├── controller/ # 控制器层
│   │       ├── service/    # 服务层
│   │       ├── mapper/     # 数据访问层
│   │       ├── entity/     # 实体类
│   │       ├── dto/        # 数据传输对象
│   │       ├── common/     # 通用类
│   │       └── util/       # 工具类
│   └── src/main/resources/ # 配置文件
├── my-pet-platform/        # Vue.js前端项目
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── stores/        # Pinia状态管理
│   │   ├── router/        # 路由配置
│   │   └── components/    # 通用组件
│   └── public/
└── database_schema.sql     # 数据库表结构
```

## 功能特性

### 后端功能
- ✅ 用户注册API
- ✅ 用户登录API
- ✅ JWT令牌生成和验证
- ✅ 密码加密存储
- ✅ 参数校验
- ✅ 统一返回结果格式
- ✅ 跨域支持

### 前端功能
- ✅ 用户登录页面
- ✅ 用户注册页面（独立页面）
- ✅ 用户状态管理（Pinia）
- ✅ 路由守卫
- ✅ 响应式设计
- ✅ 表单验证
- ✅ 登录状态持久化

## 技术栈

### 后端
- Spring Boot 4.0.1
- MyBatis
- MySQL
- JWT (jsonwebtoken)
- Spring Security Crypto
- Maven

### 前端
- Vue 3
- Vite
- Pinia (状态管理)
- Vue Router
- JavaScript ES6+

## 快速开始

### 1. 数据库准备

首先创建MySQL数据库并执行SQL脚本：

```sql
-- 执行 my-pet-platform/database_schema.sql 文件中的SQL语句
```

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 修改数据库配置
# 编辑 src/main/resources/application.properties
# 修改数据库连接信息：
# spring.datasource.url=jdbc:mysql://localhost:3306/pet_home?...
# spring.datasource.username=你的数据库用户名
# spring.datasource.password=你的数据库密码

# 编译并启动
mvn clean compile
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 3. 前端启动

```bash
# 进入前端目录
cd my-pet-platform

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:5173 启动

## API接口

### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com",
  "phone": "13800138000"
}
```

### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

## 页面路由

- `/auth` - 用户登录页面
- `/register` - 用户注册页面
- `/` - 首页（需要登录）
- `/profile` - 个人中心（需要登录）

## 测试账户

数据库初始化时会创建测试账户：
- 用户名: testuser1
- 密码: 需要注册新用户或查看数据库中的加密密码

## 开发说明

### 前端状态管理
使用Pinia管理用户状态，包括：
- 用户信息存储
- 登录状态管理
- Token管理
- 本地存储持久化

### 后端安全
- 密码使用BCrypt加密
- JWT令牌用于身份验证
- 参数校验防止恶意输入
- 跨域配置支持前端调用

### 路由守卫
- 未登录用户自动跳转到登录页
- 已登录用户访问登录页自动跳转到首页
- 基于用户状态的页面访问控制

## 注意事项

1. 确保MySQL服务正在运行
2. 修改后端配置文件中的数据库连接信息
3. 前端默认连接后端地址为 http://localhost:8080
4. 如需修改端口，请同时更新前端API配置

## 下一步开发

- [ ] 忘记密码功能
- [ ] 邮箱验证
- [ ] 用户头像上传
- [ ] 个人信息编辑
- [ ] 管理员功能
- [ ] 宠物档案管理

## 问题排查

如果遇到问题，请检查：
1. 数据库连接是否正常
2. 后端服务是否启动成功
3. 前端是否能正常访问后端API
4. 浏览器控制台是否有错误信息