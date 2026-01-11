# 🐾 宠物之家 (Pet Home)

一个功能完整的宠物电商平台，提供宠物商品购买、宠物信息管理、AI智能咨询、服务预约等综合服务。

## 项目概述

宠物之家是一个基于Spring Boot + Vue 3的前后端分离项目，为宠物爱好者提供一站式的宠物服务平台。项目采用现代化的技术栈，具备完整的电商功能、智能AI咨询、用户管理和后台管理系统。

### 核心功能

- **电商购物系统** - 商品浏览、购物车、订单管理、多种支付方式
- **AI智能咨询** - 基于DeepSeek API的宠物问题智能解答
- **用户中心** - 个人信息管理、订单查询、收货地址、收藏夹
- **宠物管理** - 宠物信息录入、健康档案
- **服务预约** - 宠物美容等服务在线预约可下载打印预约单
- **管理后台** - 商品管理、用户管理、订单处理、宠物管理

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **安全**: Spring Security + JWT
- **构建工具**: Maven

### 前端技术
- **框架**: Vue 3
- **构建工具**: Vite
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **图标**: Iconify

### 第三方服务
- **AI服务**: DeepSeek API
- **支付**: 支付宝、微信支付（模拟）
- **文件存储**: 本地存储/阿里云OSS

## 📁 项目结构

```
pet-home/
├── backend/                                    # 后端Spring Boot项目
│   ├── .mvn/wrapper/                          # Maven包装器
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ddliang/backend/
│   │   │   │   ├── controller/                # 控制器层
│   │   │   │   │   ├── AdminAppointmentController.java
│   │   │   │   │   ├── AdminPetController.java
│   │   │   │   │   ├── AdminProductController.java
│   │   │   │   │   ├── AdminUserController.java
│   │   │   │   │   ├── AddressController.java
│   │   │   │   │   ├── AiController.java
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── CartController.java
│   │   │   │   │   ├── FavoriteController.java
│   │   │   │   │   ├── PetController.java
│   │   │   │   │   ├── ProductController.java
│   │   │   │   │   ├── ServiceCareItemsController.java
│   │   │   │   │   └── UserController.java
│   │   │   │   ├── service/                   # 服务层
│   │   │   │   │   └── impl/                  # 服务实现类
│   │   │   │   ├── mapper/                    # 数据访问层
│   │   │   │   │   ├── OrderMapper.java
│   │   │   │   │   ├── PetMapper.java
│   │   │   │   │   ├── ProductMapper.java
│   │   │   │   │   └── UserMapper.java
│   │   │   │   ├── entity/                    # 实体类
│   │   │   │   │   ├── CartItem.java
│   │   │   │   │   ├── Pet.java
│   │   │   │   │   ├── Product.java
│   │   │   │   │   └── User.java
│   │   │   │   ├── dto/                       # 数据传输对象
│   │   │   │   │   ├── CartItemResponse.java
│   │   │   │   │   └── ProductDetailResponse.java
│   │   │   │   ├── common/                    # 通用类
│   │   │   │   │   └── Result.java
│   │   │   │   ├── config/                    # 配置类
│   │   │   │   │   └── CorsConfig.java
│   │   │   │   ├── util/                      # 工具类
│   │   │   │   └── BackendApplication.java    # 启动类
│   │   │   └── resources/
│   │   │       ├── sql/                       # SQL脚本
│   │   │       │   ├── add_user_role.sql
│   │   │       │   └── create_addresses_table.sql
│   │   │       ├── static/                    # 静态资源
│   │   │       ├── templates/                 # 模板文件
│   │   │       └── application.yml            # 配置文件
│   │   └── test/java/                         # 测试代码
│   ├── target/                                # 编译输出目录
│   ├── .gitignore
│   ├── mvnw                                   # Maven包装器脚本
│   ├── mvnw.cmd                               # Windows Maven包装器
│   ├── pom.xml                                # Maven配置文件
│   └── HELP.md                                # 帮助文档
├── frontend/                                  # 前端Vue 3项目
│   ├── .vscode/                               # VS Code配置
│   ├── dist/                                  # 构建输出目录
│   ├── node_modules/                          # 依赖包
│   ├── public/                                # 公共资源
│   │   └── favicon.ico
│   ├── src/
│   │   ├── api/                               # API接口层
│   │   │   ├── address.js                     # 地址管理API
│   │   │   ├── admin.js                       # 管理员API
│   │   │   ├── ai.js                          # AI咨询API
│   │   │   ├── appointment.js                 # 预约服务API
│   │   │   ├── cart.js                        # 购物车API
│   │   │   ├── favorites.js                   # 收藏API
│   │   │   ├── order.js                       # 订单API
│   │   │   ├── pet.js                         # 宠物管理API
│   │   │   ├── product.js                     # 商品API
│   │   │   └── user.js                        # 用户API
│   │   ├── assets/                            # 静态资源
│   │   │   ├── base.css
│   │   │   ├── logo.svg
│   │   │   └── main.css
│   │   ├── components/                        # 通用组件
│   │   │   ├── AiFloatBall.vue               # AI悬浮球组件
│   │   │   ├── MyOrders.vue                  # 我的订单组件
│   │   │   └── ShoppingCart.vue              # 购物车组件
│   │   ├── router/                            # 路由配置
│   │   │   └── index.js
│   │   ├── stores/                            # 状态管理(Pinia)
│   │   │   ├── cart.js                        # 购物车状态
│   │   │   ├── counter.js                     # 计数器状态
│   │   │   ├── pet.js                         # 宠物状态
│   │   │   └── user.js                        # 用户状态
│   │   ├── utils/                             # 工具函数
│   │   │   └── request.js                     # HTTP请求配置
│   │   ├── views/                             # 页面组件
│   │   │   ├── admin/                         # 管理端页面
│   │   │   │   ├── AddProductView.vue         # 添加商品页面
│   │   │   │   ├── AdminLayout.vue            # 管理端布局
│   │   │   │   ├── AppointmentManagementView.vue # 预约管理
│   │   │   │   ├── PetManagementView.vue      # 宠物管理
│   │   │   │   ├── ProductManagementView.vue  # 商品管理
│   │   │   │   └── UserManagementView.vue     # 用户管理
│   │   │   ├── AddPetView.vue                 # 添加宠物页面
│   │   │   ├── AiConsultView.vue              # AI咨询页面
│   │   │   ├── AuthView.vue                   # 登录注册页面
│   │   │   ├── CheckoutView.vue               # 结算页面
│   │   │   ├── HomeView.vue                   # 首页
│   │   │   ├── PetManageView.vue              # 宠物管理页面
│   │   │   ├── ProductDetailView.vue          # 商品详情页面
│   │   │   ├── ProductListView.vue            # 商品列表页面
│   │   │   ├── ProductSearchView.vue          # 商品搜索页面
│   │   │   ├── ProfileView.vue                # 个人资料页面
│   │   │   ├── ServiceAppointmentDetailView.vue # 服务预约详情
│   │   │   ├── ServiceAppointmentView.vue     # 服务预约页面
│   │   │   ├── ServiceOrdersView.vue          # 服务订单页面
│   │   │   └── UserCenterView.vue             # 用户中心页面
│   │   ├── App.vue                            # 根组件
│   │   └── main.js                            # 入口文件
│   ├── .editorconfig                          # 编辑器配置
│   ├── .gitignore                             # Git忽略文件
│   ├── .prettierrc.json                       # Prettier配置
│   ├── eslint.config.js                       # ESLint配置
│   ├── index.html                             # HTML模板
│   ├── jsconfig.json                          # JavaScript配置
│   ├── package.json                           # 依赖配置
│   ├── package-lock.json                      # 依赖锁定文件
│   ├── user-center-test.html                  # 测试页面
│   ├── vite.config.js                         # Vite配置
│   └── README.md                              # 前端说明文档
├── pet_products_data/                         # 商品数据
│   ├── pet/                                   # 宠物相关数据
│   ├── datareadme.md                          # 数据说明
│   ├── pet_products_20251220_173944.csv      # 商品数据CSV
│   └── pet_products_20251220_173944.json     # 商品数据JSON
├── .git/                                      # Git版本控制
├── .idea/                                     # IntelliJ IDEA配置
├── .vscode/                                   # VS Code配置
├── .gitignore                                 # Git忽略文件
├── nginx.conf                                 # Nginx配置文件
├── pet_home_2026-01-03_213204.sql            # 数据库备份文件
├── README.md                                  # 项目说明文档
├── 宠物服务平台-系统使用说明文档.pdf          # 系统使用说明
├── 宠物服务平台-系统说明文档.pdf              # 系统说明文档
├── 张昕玥玥_个人开发日志.pdf                  # 开发日志
├── 梁笛笛_个人开发日志.pdf                    # 开发日志
└── 组内工作量说明文档.pdf                     # 工作量说明
```

## 快速开始

### 环境要求

- Java 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 1. 克隆项目

```bash
git clone https://github.com/your-username/pet-home.git
cd pet-home
```

### 2. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE pet_home CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据库结构
mysql -u root -p pet_home < sql/pet_home_structure.sql

-- 导入测试数据（可选）
mysql -u root -p pet_home < sql/pet_home_data.sql
```

### 3. 后端启动

```bash
cd backend

# 修改配置文件
vim src/main/resources/application.yml
# 配置数据库连接、Redis连接、DeepSeek API密钥等

# 启动后端服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 4. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 5. 访问应用

- **用户端**: http://localhost:5173
- **管理端**: http://localhost:5173/admin
- **API文档**: http://localhost:8080/swagger-ui.html

## 🔧 配置说明

### 后端配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_home
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password

deepseek:
  api:
    key: your_deepseek_api_key
    url: https://api.deepseek.com/v1/chat/completions

jwt:
  secret: your_jwt_secret
  expiration: 86400000
```

### 前端配置 (vite.config.js)

```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 📱 功能模块详解

### 用户端功能

#### 首页
- 商品推荐展示
- 分类导航
- 促销活动banner
- 热门商品列表

#### 商品模块
- 商品列表浏览
- 商品详情查看
- 商品搜索筛选
- 商品收藏功能

#### 购物车
- 商品添加/删除
- 数量修改
- 批量操作
- 价格计算

#### 订单支付
- 订单创建
- 收货地址选择
- 支付方式选择
- 支付状态跟踪
- 15分钟支付超时

#### 个人中心
- 个人信息管理
- 订单历史查询
- 收货地址管理
- 收藏夹管理
- 宠物信息管理

#### AI咨询
- 智能问答
- 推荐问题
- 专业宠物建议

#### 服务预约
- 服务类型选择
- 时间预约
- 预约记录查询
- 预约状态管理

### 管理端功能

#### 数据统计
- 销售数据分析
- 用户活跃度统计
- 订单趋势图表
- 商品销量排行

#### 商品管理
- 商品CRUD操作
- 分类管理
- 库存管理
- 商品上下架

#### 用户管理
- 用户列表查看
- 用户状态控制
- 权限管理
- 用户数据导出

#### 订单管理
- 订单列表查询
- 订单状态更新
- 发货处理
- 退款处理

## 权限系统

### 用户角色
- **普通用户**: 基础购物功能
- **VIP用户**: 享受折扣优惠
- **管理员**: 后台管理权限
- **超级管理员**: 全部权限

### 权限控制
- JWT Token认证
- 路由权限验证
- API接口权限控制
- 菜单权限过滤


## 测试

### 后端测试
```bash
cd backend
mvn test
```

### 前端测试
```bash
cd frontend
npm run test
```

### API测试
使用Apifox或其他API测试工具测试接口：
- 导入 `docs/api-collection.json`
- 配置环境变量
- 执行测试用例

## 部署

#### 后端部署
```bash
cd backend
mvn clean package
java -jar target/pet-home-backend.jar
```

#### 前端部署
```bash
cd frontend
npm run build
# 将dist目录部署到Nginx
```

### Nginx配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 代码规范
- 后端遵循阿里巴巴Java开发规范
- 前端使用ESLint + Prettier
- 提交信息遵循Conventional Commits规范