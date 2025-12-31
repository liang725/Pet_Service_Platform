# 宠物之家 - 全栈宠物服务平台

一个现代化的宠物服务平台，提供宠物商品购买、服务预约、用户管理和管理员后台等完整功能。

## 项目特色

- **完整的电商功能**：商品浏览、购物车、订单管理、支付流程
- **宠物服务预约**：在线预约宠物洗护、美容等服务
- **用户中心**：个人信息管理、订单查看、收藏管理
- **现代化UI设计**：响应式设计，支持移动端和桌面端
- **完整的后端API**：Spring Boot + MyBatis 构建的RESTful API
- **权限管理系统**：基于JWT的用户认证和权限控制

## 技术栈

### 前端技术
- **Vue 3** - 渐进式JavaScript框架
- **Vue Router** - 官方路由管理器
- **Pinia** - 状态管理
- **Vite** - 现代化构建工具
- **Iconify** - 图标库
- **CSS3** - 现代化样式设计

### 后端技术
- **Spring Boot 3** - Java应用框架
- **MyBatis** - 持久层框架
- **MySQL** - 关系型数据库
- **JWT** - 身份认证
- **Maven** - 项目管理工具
- **Spring Security** - 安全框架

## 项目结构

```
pet/
├── frontend/                    # 前端Vue项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── AuthView.vue    # 登录注册页面
│   │   │   ├── HomeView.vue    # 首页
│   │   │   ├── ProductListView.vue
│   │   │   ├── ProductDetailView.vue
│   │   │   ├── CheckoutView.vue
│   │   │   ├── UserCenterView.vue
│   │   │   └── ServiceAppointmentView.vue
│   │   ├── components/         # 通用组件
│   │   │   └── ShoppingCart.vue
│   │   ├── stores/            # 状态管理
│   │   │   ├── user.js
│   │   │   ├── cart.js
│   │   │   └── pet.js
│   │   ├── api/               # API接口
│   │   │   ├── address.js
│   │   │   ├── appointment.js
│   │   │   ├── cart.js
│   │   │   ├── favorites.js
│   │   │   ├── order.js
│   │   │   └── product.js
│   │   ├── utils/             # 工具函数
│   │   │   └── request.js
│   │   └── router/            # 路由配置
│   │       └── index.js
│   ├── package.json
│   └── vite.config.js
├── backend/                     # 后端Spring Boot项目
│   ├── src/main/java/com/ddliang/backend/
│   │   ├── controller/         # 控制器层
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── ProductController.java
│   │   │   ├── OrderController.java
│   │   │   ├── CartController.java
│   │   │   ├── ServiceAppointmentController.java
│   │   │   ├── PetController.java
│   │   │   ├── AddressController.java
│   │   │   └── FavoriteController.java
│   │   ├── service/           # 业务逻辑层
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── CartService.java
│   │   │   └── ServiceAppointmentService.java
│   │   ├── service/impl/      # 业务实现层
│   │   ├── entity/            # 实体类
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── CartItem.java
│   │   │   ├── Pet.java
│   │   │   └── ServiceAppointment.java
│   │   ├── mapper/            # 数据访问层
│   │   ├── dto/               # 数据传输对象
│   │   ├── config/            # 配置类
│   │   │   ├── CorsConfig.java
│   │   │   ├── JwtInterceptor.java
│   │   │   └── WebConfig.java
│   │   ├── common/            # 通用类
│   │   │   └── Result.java
│   │   └── util/              # 工具类
│   │       └── JwtUtil.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── mapper/            # MyBatis映射文件
│   └── pom.xml
├── pet_products_data/          # 测试数据
│   ├── pet_products_20251220_173944.csv
│   ├── pet_products_20251220_173944.json
│   └── datareadme.md
├── pet_home_2025-12-29_160059.sql  # 数据库脚本
└── README.md
```

## 主要功能

### 用户端功能
- 用户注册和登录系统
- 个人信息管理和修改
- 宠物信息档案管理
- 收货地址管理
- 商品浏览
- 商品详情查看
- 购物车管理
- 商品收藏功能
- 立即购买和批量购买
- 订单创建和管理
- 结算页面
- 支付流程（模拟扫码支付）
- 订单状态跟踪
- 订单取消和退款
- 宠物服务预约
- 预约管理和历史查看
- 预约状态筛选

## 快速开始

### 环境要求
- Node.js 16.0 或更高版本
- Java 17 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.6 或更高版本

### 数据库配置
1. 创建MySQL数据库 `pet_home`
2. 导入数据库脚本 `pet_home_2025-12-29_160059.sql`
3. 导入测试数据（pet_products_data目录中的数据文件）
4. 修改 `backend/src/main/resources/application.yml` 中的数据库连接配置

### 后端启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
后端服务将在 http://localhost:8080 启动

### 前端启动
```bash
cd frontend
npm install
npm run dev
```
前端应用将在 http://localhost:5173 启动

### 默认账户
- 普通用户：username/password
- 管理员：admin/admin123

## 页面展示

### 用户端页面
- **首页** - 商品展示、分类导航、推荐商品
- **商品详情** - 详细商品信息、规格选择、购买选项
- **购物车** - 商品管理、数量调整、批量操作
- **结算页面** - 地址选择、支付方式、订单确认
- **用户中心** - 订单管理、个人设置、收藏管理
- **服务预约** - 宠物服务在线预约、时间选择
- **个人设置** - 账户信息、地址管理

### 管理员后台页面
- **控制面板** - 系统概览、数据统计、快捷操作
- **用户管理** - 用户列表、信息编辑、状态管理
- **商品管理** - 商品CRUD、库存管理、分类设置
- **订单管理** - 订单查看、状态更新、退款处理
- **预约管理** - 预约查看、状态跟踪、服务安排
- **宠物管理** - 宠物档案、健康记录、信息统计

## 核心功能流程

### 购物流程
```
用户注册/登录 → 浏览商品 → 查看详情 → 加入购物车/立即购买 → 选择收货地址 → 确认订单信息 → 选择支付方式 → 扫码支付 → 支付成功 → 订单完成
```

### 预约流程
```
用户登录 → 选择服务类型 → 填写宠物信息 → 选择预约时间 → 确认预约信息 → 提交预约 → 等待确认 → 服务完成
```

### 管理员管理流程
```
管理员登录 → 进入后台系统 → 查看数据统计 → 管理用户/商品/订单 → 处理预约请求 → 更新系统数据
```

## API接口文档

### 基础信息
- 基础URL：`http://localhost:8080/api`
- 认证方式：JWT Token（Header: Authorization: Bearer <token>）
- 数据格式：JSON
- 字符编码：UTF-8

### 主要接口分类
- `/api/auth/*` - 用户认证相关接口
  - POST /api/auth/login - 用户登录
  - POST /api/auth/register - 用户注册
  - POST /api/auth/logout - 用户登出
- `/api/users/*` - 用户管理接口
  - GET /api/users/profile - 获取用户信息
  - PUT /api/users/profile - 更新用户信息
- `/api/products/*` - 商品管理接口
  - GET /api/products - 获取商品列表
  - GET /api/products/{id} - 获取商品详情
- `/api/cart/*` - 购物车接口
  - GET /api/cart - 获取购物车
  - POST /api/cart/add - 添加商品到购物车
- `/api/orders/*` - 订单管理接口
  - POST /api/orders - 创建订单
  - GET /api/orders - 获取订单列表
- `/api/appointments/*` - 服务预约接口
  - POST /api/appointments - 创建预约
  - GET /api/appointments - 获取预约列表
- `/api/addresses/*` - 地址管理接口
  - GET /api/addresses - 获取地址列表
  - POST /api/addresses - 添加新地址
- `/api/favorites/*` - 收藏管理接口
  - GET /api/favorites - 获取收藏列表
  - POST /api/favorites - 添加收藏
- `/api/admin/*` - 管理员接口
  - GET /api/admin/dashboard - 获取统计数据
  - GET /api/admin/users - 获取用户列表
  - GET /api/admin/products - 获取商品管理列表

## 设计特色

### 用户体验设计
- **现代化UI**：简洁美观的界面设计，符合现代审美
- **响应式布局**：完美适配手机、平板、桌面等各种设备
- **流畅交互**：页面切换流畅，操作反馈及时
- **直观导航**：清晰的导航结构，用户易于理解和使用

### 商业化功能
- **支付体验**：模拟真实的扫码支付流程

### 技术特色
- **前后端分离**：Vue3 + Spring Boot 架构
- **RESTful API**：标准化的API接口设计
- **JW*：无状态的用户认证机制
- **权限控制**：
- **数据安全**：密码加密、SQL注入防护

## 核心亮点

### 完整的电商生态
- 从商品展示到支付完成的完整购物流程
- 支持立即购买和购物车两种购买方式
- 完整的订单状态流转和管理
- 真实的支付体验模拟

### 专业的宠物服务
- 专门针对宠物行业的服务预约系统
- 宠物档案管理和健康记录
- 个性化的宠物服务推荐
- 专业的宠物护理知识整合

### 强大的管理后台
- 全面的数据统计和分析功能
- 直观的管理界面和操作流程
- 完善的权限管理和角色分配
- 实时的业务数据监控

### 优秀的技术架构
- 现代化的前端技术栈
- 稳定可靠的后端架构
- 清晰的代码结构和注释
- 良好的扩展性和维护性

## 部署说明

### 开发环境部署
1. 克隆项目到本地
2. 配置数据库连接
3. 启动后端服务
4. 启动前端应用
5. 访问应用进行测试

### 生产环境部署
1. 构建前端项目：`npm run build`
2. 打包后端项目：`mvn clean package`
3. 配置生产环境数据库
4. 部署到服务器
