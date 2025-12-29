-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pet_home
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '???ID',
  `product_id` int NOT NULL COMMENT '???ID',
  `product_name` varchar(255) NOT NULL COMMENT '??????',
  `product_image` varchar(500) DEFAULT NULL COMMENT '??????',
  `spec` varchar(100) DEFAULT NULL COMMENT '???',
  `price` decimal(10,2) NOT NULL COMMENT '???',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '???',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '??????',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '??????',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='??????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (3,2,22,'耐咬磨牙球 发声款','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_2_NOLrgL3hUJg.jpg','标准装',37.31,4,'2025-12-21 13:23:31','2025-12-26 13:29:02'),(12,2,20,'宠物牵引绳 反光款','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_10_bXwPUdSxe_E.jpg','标准装',36.57,1,'2025-12-26 06:39:18','2025-12-26 06:39:18'),(13,2,2,'冠能大型犬幼犬粮 3kg','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_2_CRN2fR9NRE8.jpg','标准装',179.47,1,'2025-12-26 06:39:29','2025-12-26 06:39:29'),(14,2,12,'智能宠物饮水机 2L容量','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_2_IHZdiqWOgBk.jpg','标准装',237.45,3,'2025-12-26 06:47:56','2025-12-26 15:53:03'),(15,2,23,'宠物漏食球 益智款','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_3_g2FtlFrc164.jpg','标准装',49.67,1,'2025-12-26 11:36:40','2025-12-26 11:36:40');
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片',
  `spec` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `total_price` decimal(10,2) NOT NULL COMMENT '小计金额',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,'皇家小型犬成犬粮 2kg','https://images.unsplash.com/photo-1595435934247-5d33b7f92c70?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：2kg装 | 口味：原味',128.00,158.00,2,256.00,'2025-12-26 01:54:23'),(2,1,22,'耐咬磨牙发声玩具球','https://images.unsplash.com/photo-1545243421-89e5c9b6d12c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：大号 | 颜色：红色',35.00,45.00,1,35.00,'2025-12-26 01:54:23'),(3,1,21,'逗猫棒羽毛玩具','https://images.unsplash.com/photo-1533738363-b7f9aef128ce?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：标准款 | 类型：羽毛',19.90,29.90,3,59.70,'2025-12-26 01:54:23'),(4,2,12,'智能宠物饮水机 2L容量','https://images.unsplash.com/photo-1583337130417-3346a1be7dee?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：3支装 | 适用：10kg以下犬只',185.00,220.00,1,185.00,'2025-12-26 01:54:23'),(5,1,1,'皇家小型犬成犬粮 2kg','https://images.unsplash.com/photo-1595435934247-5d33b7f92c70?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：2kg装 | 口味：原味',128.00,158.00,2,256.00,'2025-12-26 02:47:17'),(6,1,22,'耐咬磨牙发声玩具球','https://images.unsplash.com/photo-1545243421-89e5c9b6d12c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：大号 | 颜色：红色',35.00,45.00,1,35.00,'2025-12-26 02:47:17'),(7,1,21,'逗猫棒羽毛玩具','https://images.unsplash.com/photo-1533738363-b7f9aef128ce?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：标准款 | 类型：羽毛',19.90,29.90,3,59.70,'2025-12-26 02:47:17'),(8,2,12,'智能宠物饮水机 2L容量','https://images.unsplash.com/photo-1583337130417-3346a1be7dee?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：3支装 | 适用：10kg以下犬只',185.00,220.00,1,185.00,'2025-12-26 02:47:17'),(9,1,1,'皇家小型犬成犬粮 2kg','https://images.unsplash.com/photo-1595435934247-5d33b7f92c70?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：2kg装 | 口味：原味',128.00,158.00,2,256.00,'2025-12-26 03:33:51'),(10,1,22,'耐咬磨牙发声玩具球','https://images.unsplash.com/photo-1545243421-89e5c9b6d12c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：大号 | 颜色：红色',35.00,45.00,1,35.00,'2025-12-26 03:33:51'),(11,1,21,'逗猫棒羽毛玩具','https://images.unsplash.com/photo-1533738363-b7f9aef128ce?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：标准款 | 类型：羽毛',19.90,29.90,3,59.70,'2025-12-26 03:33:51'),(12,2,12,'智能宠物饮水机 2L容量','https://images.unsplash.com/photo-1583337130417-3346a1be7dee?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80','规格：3支装 | 适用：10kg以下犬只',185.00,220.00,1,185.00,'2025-12-26 03:33:51');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` enum('pending','paid','shipped','delivered','cancelled','refunded') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '订单状态',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `shipping_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `coupon_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券折扣',
  `member_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员折扣',
  `final_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额',
  `payment_method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式',
  `payment_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `shipping_time` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `delivery_time` timestamp NULL DEFAULT NULL COMMENT '送达时间',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货地址',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '订单备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'DD20231015001',2,'delivered',421.90,0.00,38.44,30.00,8.44,383.46,'alipay','2023-10-15 06:35:25','2023-10-15 08:00:00','2023-10-16 02:30:00','张先生','13800138001','北京市朝阳区三里屯街道1号','猫咪比较胆小，请轻拿轻放','2023-10-15 06:30:25','2023-10-16 02:30:00'),(2,'DD20231012001',2,'cancelled',185.00,0.00,0.00,0.00,0.00,185.00,NULL,NULL,NULL,NULL,'张先生','13800138001','北京市朝阳区三里屯街道1号','','2023-10-12 01:15:42','2025-12-26 03:46:42');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pets` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '宠物ID，主键',
  `user_id` int NOT NULL COMMENT '所属用户ID，外键（关联users.id）',
  `pet_type` enum('猫','狗') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '宠物类型：猫、狗',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '宠物名字',
  `breed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '品种（如：金毛寻回犬、英国短毛猫）',
  `gender` enum('公','母','未知') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未知' COMMENT '性别',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毛色描述',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重（kg）',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像图片URL',
  `personality` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性格描述',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '特别记录、健康情况等备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_type` (`pet_type`),
  KEY `idx_gender` (`gender`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `pets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES (1,1,'猫','黑蛋','中华田园猫','公','2021-08-25','黑色',5.80,'/uploads/pets/xiaohei.jpg','好奇、活泼、喜欢攀爬','领养的流浪猫，已打疫苗和驱虫','2025-12-19 09:14:37','2025-12-19 16:44:02'),(2,2,'猫','咪咪','英国短毛猫','公','2023-01-10','蓝灰色',4.20,NULL,'安静、独立、喜欢睡觉','已绝育，偶尔会吐毛球，需要定期梳毛','2025-12-19 09:14:51','2025-12-19 10:54:05'),(3,1,'狗','旺旺','金毛寻回犬','公','2022-03-15','金色',28.50,'/uploads/pets/wangcai.jpg','温顺、活泼、喜欢玩球','定期接种疫苗，对小孩友好，喜欢游泳','2025-12-19 09:14:53','2025-12-19 16:44:02'),(5,2,'猫','乐迪',NULL,'母',NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-26 13:47:43','2025-12-26 13:47:43');
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类代码',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_code` (`category_code`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,'food','宠物食品','各类宠物主粮、零食、营养品',NULL,1,1,'2025-12-21 10:17:02','2025-12-21 10:17:02'),(2,'supply','宠物用品','宠物日常用品、护理用品',NULL,2,1,'2025-12-21 10:17:02','2025-12-21 10:17:02'),(3,'toy','宠物玩具','各类宠物玩具、互动用品',NULL,3,1,'2025-12-21 10:17:02','2025-12-21 10:17:02'),(4,'hot','热门推荐','热销商品推荐',NULL,0,1,'2025-12-21 10:17:02','2025-12-21 10:17:02');
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;

--
-- Table structure for table `product_details`
--

DROP TABLE IF EXISTS `product_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_details` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `detail_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '详情HTML内容',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品详细描述表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_details`
--

/*!40000 ALTER TABLE `product_details` DISABLE KEYS */;
INSERT INTO `product_details` VALUES (1,1,'<div class=\"product-detail-content\">\r\n<h3>产品特点</h3>\r\n<ul class=\"feature-list\">\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>专为小型犬设计</strong>：颗粒大小适中，易于咀嚼和消化</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>优质蛋白质</strong>：精选鸡肉为主要蛋白来源，易消化吸收</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>皮肤毛发护理</strong>：富含EPA/DHA和维生素，保持皮肤健康和毛发光泽</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>口腔健康</strong>：特殊颗粒设计，有助于减少牙垢形成</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>消化系统支持</strong>：添加益生元，促进肠道健康</li>\r\n</ul>\r\n\r\n<h3>适用对象</h3>\r\n<p>适合10个月以上的小型犬成犬，成年体重在1-10kg之间，如：</p>\r\n<ul>\r\n<li>泰迪犬（贵宾犬）</li>\r\n<li>比熊犬</li>\r\n<li>博美犬</li>\r\n<li>吉娃娃</li>\r\n<li>约克夏</li>\r\n<li>雪纳瑞</li>\r\n</ul>\r\n\r\n<h3>喂食指南</h3>\r\n<p>每日建议喂食量（可根据狗狗活动量适当调整）：</p>\r\n<table class=\"feeding-guide\">\r\n<tr><th>体重</th><th>每日喂食量</th></tr>\r\n<tr><td>2kg</td><td>40g</td></tr>\r\n<tr><td>4kg</td><td>70g</td></tr>\r\n<tr><td>6kg</td><td>95g</td></tr>\r\n<tr><td>8kg</td><td>115g</td></tr>\r\n<tr><td>10kg</td><td>135g</td></tr>\r\n</table>\r\n<p>建议分2-3次喂食，并提供充足的饮用水。</p>\r\n\r\n<h3>储存方式</h3>\r\n<p>请存放在阴凉干燥处，避免阳光直射。开封后请密封保存，建议在1个月内食用完毕，以保持最佳新鲜度。</p>\r\n</div>','2025-12-21 14:02:25'),(2,12,'<div class=\"product-detail-content\">\r\n<h3>产品亮点</h3>\r\n<ul class=\"feature-list\">\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>循环过滤系统</strong>：三重过滤，活性炭+离子交换树脂+海绵，确保水质清洁</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>超静音设计</strong>：≤30dB工作噪音，不打扰宠物和主人休息</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>2L大容量</strong>：适合多只宠物或长时间外出使用</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>食品级材质</strong>：ABS+不锈钢，安全无毒，耐用易清洁</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>USB供电</strong>：5V/1A低功耗，可连接充电宝使用</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>流动活水</strong>：模拟自然流水，吸引宠物多喝水</li>\r\n</ul>\r\n\r\n<h3>为什么选择流动饮水机？</h3>\r\n<p>1. <strong>增加饮水量</strong>：流动的水更能吸引宠物注意，让它们主动多喝水</p>\r\n<p>2. <strong>保持水质新鲜</strong>：循环过滤系统持续净化水质，避免细菌滋生</p>\r\n<p>3. <strong>预防疾病</strong>：充足的饮水有助于预防泌尿系统疾病和肾脏问题</p>\r\n<p>4. <strong>方便省心</strong>：大容量设计，减少频繁加水的麻烦</p>\r\n\r\n<h3>使用说明</h3>\r\n<p><strong>首次使用：</strong></p>\r\n<ol>\r\n<li>取出所有配件，用清水冲洗干净</li>\r\n<li>按照说明书组装滤芯和水泵</li>\r\n<li>加入清水至MAX水位线</li>\r\n<li>连接USB电源，开始使用</li>\r\n</ol>\r\n\r\n<p><strong>日常维护：</strong></p>\r\n<ul>\r\n<li>每天更换新鲜水源</li>\r\n<li>每周清洁一次饮水机各部件</li>\r\n<li>每2-4周更换一次滤芯</li>\r\n<li>定期检查水泵是否正常工作</li>\r\n</ul>\r\n\r\n<h3>注意事项</h3>\r\n<ul>\r\n<li>请使用凉白开或纯净水，水质更好</li>\r\n<li>水位不要低于MIN线，避免水泵空转</li>\r\n<li>清洁时不要使用化学清洁剂</li>\r\n<li>长时间不使用请断电并清空水箱</li>\r\n</ul>\r\n</div>','2025-12-21 14:02:28'),(3,21,'<div class=\"product-detail-content\">\r\n<h3>产品特色</h3>\r\n<ul class=\"feature-list\">\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>天然羽毛</strong>：精选优质天然羽毛，柔软蓬松，吸引猫咪注意</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>伸缩设计</strong>：40-95cm可调节长度，使用方便，收纳省空间</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>不锈钢杆</strong>：坚固耐用，不易变形，使用寿命长</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>灵活弹性</strong>：钢丝弹性好，模拟猎物动作，激发猫咪狩猎本能</li>\r\n<li><i class=\"fas fa-check-circle\"></i> <strong>安全无毒</strong>：材质安全，通过质量检测，放心使用</li>\r\n</ul>\r\n\r\n<h3>为什么猫咪需要逗猫棒？</h3>\r\n<p>1. <strong>运动锻炼</strong>：帮助猫咪消耗精力，保持健康体重</p>\r\n<p>2. <strong>心理健康</strong>：满足狩猎本能，减少焦虑和破坏行为</p>\r\n<p>3. <strong>增进感情</strong>：主人与猫咪的互动时光，增进彼此感情</p>\r\n<p>4. <strong>智力开发</strong>：追逐游戏有助于猫咪智力发展</p>\r\n\r\n<h3>使用技巧</h3>\r\n<p><strong>基础玩法：</strong></p>\r\n<ul>\r\n<li>模拟小鸟飞行：上下摆动，吸引猫咪跳跃</li>\r\n<li>模拟老鼠爬行：贴地快速移动，激发追逐欲望</li>\r\n<li>躲猫猫：藏在家具后面，若隐若现</li>\r\n<li>慢速诱导：缓慢移动，让猫咪潜行靠近</li>\r\n</ul>\r\n\r\n<p><strong>进阶玩法：</strong></p>\r\n<ul>\r\n<li>障碍追逐：利用家具设置障碍，增加难度</li>\r\n<li>高低变化：结合跳台，锻炼猫咪跳跃能力</li>\r\n<li>节奏变化：快慢结合，保持新鲜感</li>\r\n</ul>\r\n\r\n<h3>注意事项</h3>\r\n<ul>\r\n<li>每次游戏时间控制在10-15分钟，避免猫咪过度疲劳</li>\r\n<li>游戏结束后及时收起，避免猫咪独自玩耍时误食</li>\r\n<li>定期检查羽毛和连接处，如有损坏及时更换</li>\r\n<li>不要让猫咪咬住不放，避免吞食羽毛</li>\r\n<li>适合3个月以上的猫咪使用</li>\r\n</ul>\r\n\r\n<h3>温馨提示</h3>\r\n<p>逗猫棒是消耗品，羽毛在使用过程中可能会有一定程度的磨损，这是正常现象。建议定期更换新的逗猫棒，保持猫咪的游戏兴趣。</p>\r\n</div>','2025-12-21 14:02:30');
/*!40000 ALTER TABLE `product_details` ENABLE KEYS */;

--
-- Table structure for table `product_faqs`
--

DROP TABLE IF EXISTS `product_faqs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_faqs` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `question` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '答案',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品常见问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_faqs`
--

/*!40000 ALTER TABLE `product_faqs` DISABLE KEYS */;
INSERT INTO `product_faqs` VALUES (1,21,'逗猫棒的杆子可以伸缩吗？','是的，采用伸缩设计，长度可在40-95cm之间调节，方便收纳和使用。',1,1,'2025-12-21 14:01:38','2025-12-21 14:01:38'),(2,21,'羽毛会不会容易掉？','采用优质天然羽毛，经过特殊处理，不易脱落。但建议定期检查，如有松动及时更换。',2,1,'2025-12-21 14:01:38','2025-12-21 14:01:38'),(3,21,'猫咪会不会误食羽毛？','建议在主人监督下使用，玩耍后及时收起。如发现羽毛损坏，请立即更换，避免猫咪误食。',3,1,'2025-12-21 14:01:38','2025-12-21 14:01:38'),(4,21,'适合多大的猫咪？','适合3个月以上的猫咪，幼猫到成猫都可以使用。可以增进主人与猫咪的互动。',4,1,'2025-12-21 14:01:38','2025-12-21 14:01:38'),(5,12,'饮水机的噪音大吗？','采用静音水泵设计，工作噪音≤30dB，相当于轻声耳语，不会影响宠物和主人休息。',1,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(6,12,'多久需要更换滤芯？','建议每2-4周更换一次滤芯，具体根据使用频率和水质情况调整。滤芯可单独购买。',2,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(7,12,'如何清洁饮水机？','建议每周清洁一次。拆卸后用清水冲洗各部件，水泵可用小刷子清理。不要使用洗洁精等化学清洁剂。',3,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(8,12,'可以使用自来水吗？','可以使用自来水，但建议使用凉白开或纯净水，水质更好，对宠物健康更有利。',4,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(9,12,'停电后会怎样？','停电后水泵停止工作，但水盆中仍有水可供饮用。来电后自动恢复工作，无需手动操作。',5,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(10,1,'这款狗粮适合多大的狗狗？','适合10个月以上的小型犬成犬（成年体重1-10kg），如泰迪、比熊、博美等品种。',1,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(11,1,'狗粮的保质期是多久？','未开封保质期18个月，开封后建议在1个月内食用完毕，并密封保存在阴凉干燥处。',2,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(12,1,'每天喂食量是多少？','根据狗狗体重：2kg约40g/天，4kg约70g/天，6kg约95g/天，8kg约115g/天，10kg约135g/天。建议分2-3次喂食。',3,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(13,1,'可以和其他品牌狗粮混合喂食吗？','不建议混合喂食。如需更换粮食，建议用7-10天时间逐步替换，避免肠胃不适。',4,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(14,1,'狗粮是否含有防腐剂？','本产品采用天然抗氧化剂（维生素E、维生素C），不含人工防腐剂，请放心使用。',5,1,'2025-12-21 14:01:39','2025-12-21 14:01:39'),(15,1,'这款狗粮适合多大的狗狗？','适合10个月以上的小型犬成犬（成年体重1-10kg），如泰迪、比熊、博美等品种。',1,1,'2025-12-21 14:02:22','2025-12-21 14:02:22'),(16,1,'狗粮的保质期是多久？','未开封保质期18个月，开封后建议在1个月内食用完毕，并密封保存在阴凉干燥处。',2,1,'2025-12-21 14:02:22','2025-12-21 14:02:22'),(17,1,'每天喂食量是多少？','根据狗狗体重：2kg约40g/天，4kg约70g/天，6kg约95g/天，8kg约115g/天，10kg约135g/天。建议分2-3次喂食。',3,1,'2025-12-21 14:02:22','2025-12-21 14:02:22'),(18,1,'可以和其他品牌狗粮混合喂食吗？','不建议混合喂食。如需更换粮食，建议用7-10天时间逐步替换，避免肠胃不适。',4,1,'2025-12-21 14:02:22','2025-12-21 14:02:22'),(19,1,'狗粮是否含有防腐剂？','本产品采用天然抗氧化剂（维生素E、维生素C），不含人工防腐剂，请放心使用。',5,1,'2025-12-21 14:02:22','2025-12-21 14:02:22'),(20,21,'逗猫棒的杆子可以伸缩吗？','是的，采用伸缩设计，长度可在40-95cm之间调节，方便收纳和使用。',1,1,'2025-12-21 14:02:24','2025-12-21 14:02:24'),(21,21,'羽毛会不会容易掉？','采用优质天然羽毛，经过特殊处理，不易脱落。但建议定期检查，如有松动及时更换。',2,1,'2025-12-21 14:02:24','2025-12-21 14:02:24'),(22,21,'猫咪会不会误食羽毛？','建议在主人监督下使用，玩耍后及时收起。如发现羽毛损坏，请立即更换，避免猫咪误食。',3,1,'2025-12-21 14:02:24','2025-12-21 14:02:24'),(23,21,'适合多大的猫咪？','适合3个月以上的猫咪，幼猫到成猫都可以使用。可以增进主人与猫咪的互动。',4,1,'2025-12-21 14:02:24','2025-12-21 14:02:24');
/*!40000 ALTER TABLE `product_faqs` ENABLE KEYS */;

--
-- Table structure for table `product_params`
--

DROP TABLE IF EXISTS `product_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_params` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '参数ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `param_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名称',
  `param_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数值',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品参数表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_params`
--

/*!40000 ALTER TABLE `product_params` DISABLE KEYS */;
INSERT INTO `product_params` VALUES (1,1,'品牌','皇家 Royal Canin',1,'2025-12-21 13:55:41'),(2,1,'产地','法国',2,'2025-12-21 13:55:41'),(3,1,'适用对象','小型犬成犬',3,'2025-12-21 13:55:41'),(4,1,'主要成分','鸡肉、大米、玉米、动物脂肪',4,'2025-12-21 13:55:41'),(5,1,'粗蛋白','≥25%',5,'2025-12-21 13:55:41'),(6,1,'粗脂肪','≥14%',6,'2025-12-21 13:55:41'),(7,1,'粗纤维','≤3.5%',7,'2025-12-21 13:55:41'),(8,1,'水分','≤10%',8,'2025-12-21 13:55:41'),(9,1,'保质期','18个月',9,'2025-12-21 13:55:41'),(10,1,'储存方式','阴凉干燥处密封保存',10,'2025-12-21 13:55:41'),(11,12,'品名','智能宠物饮水机',1,'2025-12-21 13:55:43'),(12,12,'容量','2L',2,'2025-12-21 13:55:43'),(13,12,'材质','食品级ABS+不锈钢',3,'2025-12-21 13:55:43'),(14,12,'过滤系统','三重过滤（活性炭+离子交换树脂+海绵）',4,'2025-12-21 13:55:43'),(15,12,'功率','5W',5,'2025-12-21 13:55:43'),(16,12,'电源','USB供电 5V/1A',6,'2025-12-21 13:55:43'),(17,12,'噪音','≤30dB',7,'2025-12-21 13:55:43'),(18,12,'尺寸','19×19×15cm',8,'2025-12-21 13:55:43'),(19,12,'重量','0.8kg',9,'2025-12-21 13:55:43'),(20,12,'适用对象','猫狗通用',10,'2025-12-21 13:55:43'),(21,21,'品名','逗猫棒羽毛款',1,'2025-12-21 13:55:44'),(22,21,'材质','天然羽毛+不锈钢杆',2,'2025-12-21 13:55:44'),(23,21,'长度','可伸缩 40-95cm',3,'2025-12-21 13:55:44'),(24,21,'重量','50g',4,'2025-12-21 13:55:44'),(25,21,'颜色','随机发货',5,'2025-12-21 13:55:44'),(26,21,'适用对象','猫咪',6,'2025-12-21 13:55:44'),(27,21,'品名','逗猫棒羽毛款',1,'2025-12-21 14:01:45'),(28,21,'材质','天然羽毛+不锈钢杆',2,'2025-12-21 14:01:45'),(29,21,'长度','可伸缩 40-95cm',3,'2025-12-21 14:01:45'),(30,21,'重量','50g',4,'2025-12-21 14:01:45'),(31,21,'颜色','随机发货',5,'2025-12-21 14:01:45'),(32,21,'适用对象','猫咪',6,'2025-12-21 14:01:45'),(33,12,'品名','智能宠物饮水机',1,'2025-12-21 14:01:46'),(34,12,'容量','2L',2,'2025-12-21 14:01:46'),(35,12,'材质','食品级ABS+不锈钢',3,'2025-12-21 14:01:46'),(36,12,'过滤系统','三重过滤（活性炭+离子交换树脂+海绵）',4,'2025-12-21 14:01:46'),(37,12,'功率','5W',5,'2025-12-21 14:01:46'),(38,12,'电源','USB供电 5V/1A',6,'2025-12-21 14:01:46'),(39,12,'噪音','≤30dB',7,'2025-12-21 14:01:46'),(40,12,'尺寸','19×19×15cm',8,'2025-12-21 14:01:46'),(41,12,'重量','0.8kg',9,'2025-12-21 14:01:46'),(42,12,'适用对象','猫狗通用',10,'2025-12-21 14:01:46'),(43,1,'品牌','皇家 Royal Canin',1,'2025-12-21 14:01:47'),(44,1,'产地','法国',2,'2025-12-21 14:01:47'),(45,1,'适用对象','小型犬成犬',3,'2025-12-21 14:01:47'),(46,1,'主要成分','鸡肉、大米、玉米、动物脂肪',4,'2025-12-21 14:01:47'),(47,1,'粗蛋白','≥25%',5,'2025-12-21 14:01:47'),(48,1,'粗脂肪','≥14%',6,'2025-12-21 14:01:47'),(49,1,'粗纤维','≤3.5%',7,'2025-12-21 14:01:47'),(50,1,'水分','≤10%',8,'2025-12-21 14:01:47'),(51,1,'保质期','18个月',9,'2025-12-21 14:01:47'),(52,1,'储存方式','阴凉干燥处密封保存',10,'2025-12-21 14:01:47'),(53,1,'品牌','皇家 Royal Canin',1,'2025-12-21 14:02:03'),(54,1,'产地','法国',2,'2025-12-21 14:02:03'),(55,1,'适用对象','小型犬成犬',3,'2025-12-21 14:02:03'),(56,1,'主要成分','鸡肉、大米、玉米、动物脂肪',4,'2025-12-21 14:02:03'),(57,1,'粗蛋白','≥25%',5,'2025-12-21 14:02:03'),(58,1,'粗脂肪','≥14%',6,'2025-12-21 14:02:03'),(59,1,'粗纤维','≤3.5%',7,'2025-12-21 14:02:03'),(60,1,'水分','≤10%',8,'2025-12-21 14:02:03'),(61,1,'保质期','18个月',9,'2025-12-21 14:02:03'),(62,1,'储存方式','阴凉干燥处密封保存',10,'2025-12-21 14:02:03'),(63,12,'品名','智能宠物饮水机',1,'2025-12-21 14:02:04'),(64,12,'容量','2L',2,'2025-12-21 14:02:04'),(65,12,'材质','食品级ABS+不锈钢',3,'2025-12-21 14:02:04'),(66,12,'过滤系统','三重过滤（活性炭+离子交换树脂+海绵）',4,'2025-12-21 14:02:04'),(67,12,'功率','5W',5,'2025-12-21 14:02:04'),(68,12,'电源','USB供电 5V/1A',6,'2025-12-21 14:02:04'),(69,12,'噪音','≤30dB',7,'2025-12-21 14:02:04'),(70,12,'尺寸','19×19×15cm',8,'2025-12-21 14:02:04'),(71,12,'重量','0.8kg',9,'2025-12-21 14:02:04'),(72,12,'适用对象','猫狗通用',10,'2025-12-21 14:02:04'),(73,21,'品名','逗猫棒羽毛款',1,'2025-12-21 14:02:06'),(74,21,'材质','天然羽毛+不锈钢杆',2,'2025-12-21 14:02:06'),(75,21,'长度','可伸缩 40-95cm',3,'2025-12-21 14:02:06'),(76,21,'重量','50g',4,'2025-12-21 14:02:06'),(77,21,'颜色','随机发货',5,'2025-12-21 14:02:06'),(78,21,'适用对象','猫咪',6,'2025-12-21 14:02:06');
/*!40000 ALTER TABLE `product_params` ENABLE KEYS */;

--
-- Table structure for table `product_rating_distribution`
--

DROP TABLE IF EXISTS `product_rating_distribution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_rating_distribution` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `star` tinyint NOT NULL COMMENT '星级(1-5)',
  `count` int NOT NULL DEFAULT '0' COMMENT '数量',
  `percentage` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '百分比',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_star` (`product_id`,`star`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价分布表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_rating_distribution`
--

/*!40000 ALTER TABLE `product_rating_distribution` DISABLE KEYS */;
INSERT INTO `product_rating_distribution` VALUES (1,1,5,4710,70.00,'2025-12-21 13:55:45'),(2,1,4,1346,20.00,'2025-12-21 13:55:45'),(3,1,3,404,6.00,'2025-12-21 13:55:45'),(4,1,2,202,3.00,'2025-12-21 13:55:45'),(5,1,1,67,1.00,'2025-12-21 13:55:45'),(6,12,5,3788,60.00,'2025-12-21 13:55:47'),(7,12,4,1578,25.00,'2025-12-21 13:55:47'),(8,12,3,631,10.00,'2025-12-21 13:55:47'),(9,12,2,253,4.00,'2025-12-21 13:55:47'),(10,12,1,63,1.00,'2025-12-21 13:55:47'),(11,22,5,5013,80.00,'2025-12-21 13:55:49'),(12,22,4,940,15.00,'2025-12-21 13:55:49'),(13,22,3,188,3.00,'2025-12-21 13:55:49'),(14,22,2,94,1.50,'2025-12-21 13:55:49'),(15,22,1,32,0.50,'2025-12-21 13:55:49'),(16,2,5,3663,70.00,'2025-12-21 14:01:28'),(17,6,5,1052,70.00,'2025-12-21 14:01:28'),(18,8,5,4425,70.00,'2025-12-21 14:01:28'),(19,10,5,4523,70.00,'2025-12-21 14:01:28'),(20,13,5,5189,70.00,'2025-12-21 14:01:28'),(21,15,5,3298,70.00,'2025-12-21 14:01:28'),(22,16,5,3229,70.00,'2025-12-21 14:01:28'),(23,19,5,3413,70.00,'2025-12-21 14:01:28'),(24,20,5,3626,70.00,'2025-12-21 14:01:28'),(25,23,5,1661,70.00,'2025-12-21 14:01:28'),(26,25,5,440,70.00,'2025-12-21 14:01:28'),(27,27,5,597,70.00,'2025-12-21 14:01:28'),(28,29,5,4700,70.00,'2025-12-21 14:01:28'),(29,30,5,1890,70.00,'2025-12-21 14:01:28'),(30,2,4,1046,20.00,'2025-12-21 14:01:28'),(31,6,4,300,20.00,'2025-12-21 14:01:28'),(32,8,4,1264,20.00,'2025-12-21 14:01:28'),(33,10,4,1292,20.00,'2025-12-21 14:01:28'),(34,13,4,1482,20.00,'2025-12-21 14:01:28'),(35,15,4,942,20.00,'2025-12-21 14:01:28'),(36,16,4,922,20.00,'2025-12-21 14:01:28'),(37,19,4,975,20.00,'2025-12-21 14:01:28'),(38,20,4,1036,20.00,'2025-12-21 14:01:28'),(39,23,4,474,20.00,'2025-12-21 14:01:28'),(40,25,4,125,20.00,'2025-12-21 14:01:28'),(41,27,4,170,20.00,'2025-12-21 14:01:28'),(42,29,4,1343,20.00,'2025-12-21 14:01:28'),(43,30,4,540,20.00,'2025-12-21 14:01:28'),(44,2,3,366,7.00,'2025-12-21 14:01:28'),(45,6,3,105,7.00,'2025-12-21 14:01:28'),(46,8,3,442,7.00,'2025-12-21 14:01:28'),(47,10,3,452,7.00,'2025-12-21 14:01:28'),(48,13,3,518,7.00,'2025-12-21 14:01:28'),(49,15,3,329,7.00,'2025-12-21 14:01:28'),(50,16,3,322,7.00,'2025-12-21 14:01:28'),(51,19,3,341,7.00,'2025-12-21 14:01:28'),(52,20,3,362,7.00,'2025-12-21 14:01:28'),(53,23,3,166,7.00,'2025-12-21 14:01:28'),(54,25,3,44,7.00,'2025-12-21 14:01:28'),(55,27,3,59,7.00,'2025-12-21 14:01:28'),(56,29,3,470,7.00,'2025-12-21 14:01:28'),(57,30,3,189,7.00,'2025-12-21 14:01:28'),(58,2,2,104,2.00,'2025-12-21 14:01:28'),(59,6,2,30,2.00,'2025-12-21 14:01:28'),(60,8,2,126,2.00,'2025-12-21 14:01:28'),(61,10,2,129,2.00,'2025-12-21 14:01:28'),(62,13,2,148,2.00,'2025-12-21 14:01:28'),(63,15,2,94,2.00,'2025-12-21 14:01:28'),(64,16,2,92,2.00,'2025-12-21 14:01:28'),(65,19,2,97,2.00,'2025-12-21 14:01:28'),(66,20,2,103,2.00,'2025-12-21 14:01:28'),(67,23,2,47,2.00,'2025-12-21 14:01:28'),(68,25,2,12,2.00,'2025-12-21 14:01:28'),(69,27,2,17,2.00,'2025-12-21 14:01:28'),(70,29,2,134,2.00,'2025-12-21 14:01:28'),(71,30,2,54,2.00,'2025-12-21 14:01:28'),(72,2,1,52,1.00,'2025-12-21 14:01:28'),(73,6,1,15,1.00,'2025-12-21 14:01:28'),(74,8,1,63,1.00,'2025-12-21 14:01:28'),(75,10,1,64,1.00,'2025-12-21 14:01:28'),(76,13,1,74,1.00,'2025-12-21 14:01:28'),(77,15,1,47,1.00,'2025-12-21 14:01:28'),(78,16,1,46,1.00,'2025-12-21 14:01:28'),(79,19,1,48,1.00,'2025-12-21 14:01:28'),(80,20,1,51,1.00,'2025-12-21 14:01:28'),(81,23,1,23,1.00,'2025-12-21 14:01:28'),(82,25,1,6,1.00,'2025-12-21 14:01:28'),(83,27,1,8,1.00,'2025-12-21 14:01:28'),(84,29,1,67,1.00,'2025-12-21 14:01:28'),(85,30,1,27,1.00,'2025-12-21 14:01:28');
/*!40000 ALTER TABLE `product_rating_distribution` ENABLE KEYS */;

--
-- Table structure for table `product_specifications`
--

DROP TABLE IF EXISTS `product_specifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_specifications` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `spec_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规格名称',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '规格价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '规格原价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '规格库存',
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格标签',
  `tag_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签颜色',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_specifications`
--

/*!40000 ALTER TABLE `product_specifications` DISABLE KEYS */;
INSERT INTO `product_specifications` VALUES (1,1,'2kg 标准装',124.87,140.07,1907,'热销','#ff6b6b',1,1,'2025-12-21 13:55:37','2025-12-21 13:55:37'),(2,1,'5kg 家庭装',289.00,320.00,856,'划算','#51cf66',2,1,'2025-12-21 13:55:37','2025-12-21 13:55:37'),(3,1,'10kg 超值装',549.00,620.00,423,'省心','#339af0',3,1,'2025-12-21 13:55:37','2025-12-21 13:55:37'),(4,2,'3kg 标准装',179.47,221.64,1239,NULL,NULL,1,1,'2025-12-21 13:55:39','2025-12-21 13:55:39'),(5,2,'7kg 家庭装',399.00,480.00,567,NULL,NULL,2,1,'2025-12-21 13:55:39','2025-12-21 13:55:39'),(6,3,'1.8kg 尝鲜装',325.65,359.60,896,'进口','#845ef7',1,1,'2025-12-21 13:55:40','2025-12-21 13:55:40'),(7,3,'6kg 家庭装',1089.00,1200.00,234,'推荐','#51cf66',2,1,'2025-12-21 13:55:40','2025-12-21 13:55:40'),(8,4,'标准装',368.02,448.92,1365,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(9,5,'标准装',90.20,104.78,835,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(10,6,'标准装',248.81,316.82,1587,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(11,7,'标准装',258.32,340.26,711,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(12,8,'标准装',163.57,195.41,1347,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(13,9,'标准装',131.89,175.79,896,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(14,10,'标准装',309.55,366.80,1192,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(15,11,'标准装',173.93,209.16,1651,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(16,12,'标准装',237.45,288.78,829,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(17,13,'标准装',519.11,586.51,220,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(18,14,'标准装',239.63,301.36,456,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(19,15,'标准装',158.94,196.01,596,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(20,16,'标准装',365.93,442.76,598,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(21,17,'标准装',470.28,616.63,1450,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(22,18,'标准装',115.01,148.66,949,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(23,19,'标准装',183.56,217.05,1038,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(24,20,'标准装',36.57,45.87,1387,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(25,21,'标准装',20.48,23.87,1680,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(26,22,'标准装',37.31,42.81,1718,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(27,23,'标准装',49.67,55.75,1448,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(28,24,'标准装',26.33,32.37,1593,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(29,25,'标准装',54.59,67.32,1013,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(30,26,'标准装',31.38,36.85,729,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(31,27,'标准装',23.09,29.00,499,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(32,28,'标准装',15.43,19.78,480,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(33,29,'标准装',67.28,77.39,157,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(34,30,'标准装',16.92,22.49,164,NULL,NULL,1,1,'2025-12-21 14:01:29','2025-12-21 14:01:29'),(39,3,'1.8kg 尝鲜装',325.65,359.60,896,'进口','#845ef7',1,1,'2025-12-21 14:01:48','2025-12-21 14:01:48'),(40,3,'6kg 家庭装',1089.00,1200.00,234,'推荐','#51cf66',2,1,'2025-12-21 14:01:48','2025-12-21 14:01:48'),(41,3,'1.8kg 尝鲜装',325.65,359.60,896,'进口','#845ef7',1,1,'2025-12-21 14:01:50','2025-12-21 14:01:50'),(42,3,'6kg 家庭装',1089.00,1200.00,234,'推荐','#51cf66',2,1,'2025-12-21 14:01:50','2025-12-21 14:01:50'),(43,2,'3kg 标准装',179.47,221.64,1239,NULL,NULL,1,1,'2025-12-21 14:01:50','2025-12-21 14:01:50'),(44,2,'7kg 家庭装',399.00,480.00,567,NULL,NULL,2,1,'2025-12-21 14:01:50','2025-12-21 14:01:50'),(45,1,'2kg 标准装',124.87,140.07,1907,'热销','#ff6b6b',1,1,'2025-12-21 14:01:51','2025-12-21 14:01:51'),(46,1,'5kg 家庭装',289.00,320.00,856,'划算','#51cf66',2,1,'2025-12-21 14:01:51','2025-12-21 14:01:51'),(47,1,'10kg 超值装',549.00,620.00,423,'省心','#339af0',3,1,'2025-12-21 14:01:51','2025-12-21 14:01:51'),(48,1,'2kg 标准装',124.87,140.07,1907,'热销','#ff6b6b',1,1,'2025-12-21 14:01:59','2025-12-21 14:01:59'),(49,1,'5kg 家庭装',289.00,320.00,856,'划算','#51cf66',2,1,'2025-12-21 14:01:59','2025-12-21 14:01:59'),(50,1,'10kg 超值装',549.00,620.00,423,'省心','#339af0',3,1,'2025-12-21 14:01:59','2025-12-21 14:01:59'),(51,2,'3kg 标准装',179.47,221.64,1239,NULL,NULL,1,1,'2025-12-21 14:02:01','2025-12-21 14:02:01'),(52,2,'7kg 家庭装',399.00,480.00,567,NULL,NULL,2,1,'2025-12-21 14:02:01','2025-12-21 14:02:01'),(53,3,'1.8kg 尝鲜装',325.65,359.60,896,'进口','#845ef7',1,1,'2025-12-21 14:02:02','2025-12-21 14:02:02'),(54,3,'6kg 家庭装',1089.00,1200.00,234,'推荐','#51cf66',2,1,'2025-12-21 14:02:02','2025-12-21 14:02:02');
/*!40000 ALTER TABLE `product_specifications` ENABLE KEYS */;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` int NOT NULL COMMENT '分类ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '品牌',
  `short_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '简短描述',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '详细描述',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `discount` int DEFAULT '0' COMMENT '折扣百分比',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `rating` decimal(3,2) DEFAULT '5.00' COMMENT '评分',
  `rating_count` int NOT NULL DEFAULT '0' COMMENT '评价数量',
  `status` enum('in_stock','out_of_stock','discontinued') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'in_stock' COMMENT '商品状态',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主图URL',
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品标签(热销/新品/推荐等)',
  `is_recommended` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热销',
  `is_new` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否新品',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_brand` (`brand`),
  KEY `idx_price` (`price`),
  KEY `idx_sales` (`sales`),
  KEY `idx_rating` (`rating`),
  KEY `idx_status` (`status`),
  KEY `idx_is_recommended` (`is_recommended`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_is_new` (`is_new`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,1,'皇家小型犬成犬粮 2kg','皇家','优质皇家小型犬成犬粮 2kg，品质保证，宠物喜爱',NULL,124.87,140.07,10,1907,33249,4.70,6729,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_1_GhY3ECDrwUw.jpg','热销',0,1,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(2,1,'冠能大型犬幼犬粮 3kg','冠能','优质冠能大型犬幼犬粮 3kg，品质保证，宠物喜爱',NULL,179.47,221.64,19,1239,22322,4.60,5234,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_2_CRN2fR9NRE8.jpg','热销',0,1,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(3,1,'渴望六种鱼全犬粮 1.8kg','渴望','优质渴望六种鱼全犬粮 1.8kg，品质保证，宠物喜爱',NULL,325.65,359.60,9,896,12022,4.90,3836,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_3_vkXJP8UiVoU.jpg','进口',0,0,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(4,1,'比瑞吉天然粮 10kg','比瑞吉','优质比瑞吉天然粮 10kg，品质保证，宠物喜爱',NULL,368.02,448.92,18,1365,3797,4.50,4471,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_4_yUr_afmmRR0.jpg',NULL,0,0,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(5,1,'麦富迪冻干双拼粮 2kg','麦富迪','优质麦富迪冻干双拼粮 2kg，品质保证，宠物喜爱',NULL,90.20,104.78,13,835,2932,4.90,1267,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_5_fnDFy-BSoxg.jpg','国产',0,0,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(6,1,'伯纳天纯无谷鸡肉粮 5kg','伯纳天纯','优质伯纳天纯无谷鸡肉粮 5kg，品质保证，宠物喜爱',NULL,248.81,316.82,21,1587,38512,5.00,1503,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_6_NOBRu2TAqcs.jpg','热销',0,1,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(7,1,'爱肯拿鸭肉梨配方 2kg','爱肯拿','优质爱肯拿鸭肉梨配方 2kg，品质保证，宠物喜爱',NULL,258.32,340.26,24,711,6018,4.60,1706,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_7_Qvbr5Uxgz_Q.jpg','进口',0,0,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(8,1,'素力高金装成犬粮 3kg','素力高','优质素力高金装成犬粮 3kg，品质保证，宠物喜爱',NULL,163.57,195.41,16,1347,27819,4.60,6322,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_8_qevdiAV9H8M.jpg','热销',0,1,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(9,1,'皇家英短成猫粮 2kg','皇家','优质皇家英短成猫粮 2kg，品质保证，宠物喜爱',NULL,131.89,175.79,24,896,12526,4.70,810,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_9__AJyvVIzSWE.jpg',NULL,0,0,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(10,1,'渴望六种鱼猫粮 1.8kg','渴望','优质渴望六种鱼猫粮 1.8kg，品质保证，宠物喜爱',NULL,309.55,366.80,15,1192,40786,4.90,6462,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/food_10_4amQU28yNyg.jpg','热销',0,1,0,0,'2025-12-21 10:17:05','2025-12-21 10:17:05'),(11,2,'宠物航空箱 L号',NULL,'优质宠物航空箱 L号，品质保证，宠物喜爱',NULL,173.93,209.16,16,1651,4755,4.90,7159,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_1_ZKY7_gzHlOI.jpg',NULL,0,0,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(12,2,'智能宠物饮水机 2L容量',NULL,'优质智能宠物饮水机 2L容量，品质保证，宠物喜爱',NULL,237.45,288.78,17,829,42966,4.50,6313,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_2_IHZdiqWOgBk.jpg','智能',0,1,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(13,2,'宠物智能定时喂食器 4L容量',NULL,'优质宠物智能定时喂食器 4L容量，品质保证，宠物喜爱',NULL,519.11,586.51,11,220,17223,4.50,7414,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_3_FFtx3AlDkr4.jpg','智能',0,1,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(14,2,'宠物GPS定位器 防水款',NULL,'优质宠物GPS定位器 防水款，品质保证，宠物喜爱',NULL,239.63,301.36,20,456,3842,4.80,3167,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_4_jeIVe5L4bgg.jpg','新品',0,0,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(15,2,'宠物美容洗澡盆 可折叠',NULL,'优质宠物美容洗澡盆 可折叠，品质保证，宠物喜爱',NULL,158.94,196.01,18,596,37963,5.00,4712,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_5_pD7SpU1H7zU.jpg','实用',0,1,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(16,2,'宠物专用吹水机 大功率',NULL,'优质宠物专用吹水机 大功率，品质保证，宠物喜爱',NULL,365.93,442.76,17,598,20088,4.80,4613,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_6_w-eUGI_5bJU.jpg','专业',0,1,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(17,2,'宠物豪华别墅窝 L号',NULL,'优质宠物豪华别墅窝 L号，品质保证，宠物喜爱',NULL,470.28,616.63,23,1450,1018,4.90,3135,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_7_L08FlzZm3eg.jpg','豪华',0,0,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(18,2,'宠物外出背包 透气款',NULL,'优质宠物外出背包 透气款，品质保证，宠物喜爱',NULL,115.01,148.66,22,949,13555,4.50,4056,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_8_OBu_nabEpAw.jpg','便携',0,0,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(19,2,'宠物自动饮水器 循环过滤',NULL,'优质宠物自动饮水器 循环过滤，品质保证，宠物喜爱',NULL,183.56,217.05,15,1038,34982,4.90,4877,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_9_kU17SsEoATo.jpg','热销',0,1,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(20,2,'宠物牵引绳 反光款',NULL,'优质宠物牵引绳 反光款，品质保证，宠物喜爱',NULL,36.57,45.87,20,1387,48037,4.60,5180,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/supply_10_bXwPUdSxe_E.jpg','安全',0,1,0,0,'2025-12-21 10:17:06','2025-12-21 10:17:06'),(21,3,'逗猫棒 羽毛款',NULL,'优质逗猫棒 羽毛款，品质保证，宠物喜爱',NULL,20.48,23.87,14,1680,3136,4.70,1822,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_1_ZpXl0sMEzmM.jpg','互动',0,0,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(22,3,'耐咬磨牙球 发声款',NULL,'优质耐咬磨牙球 发声款，品质保证，宠物喜爱',NULL,37.31,42.81,12,1718,47324,4.90,6267,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_2_NOLrgL3hUJg.jpg','热销',0,1,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(23,3,'宠物漏食球 益智款',NULL,'优质宠物漏食球 益智款，品质保证，宠物喜爱',NULL,49.67,55.75,10,1448,48361,4.80,2373,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_3_g2FtlFrc164.jpg','益智',0,1,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(24,3,'宠物尖叫鸡 橡胶材质',NULL,'优质宠物尖叫鸡 橡胶材质，品质保证，宠物喜爱',NULL,26.33,32.37,18,1593,16003,4.50,1984,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_4_oD8uurkrH4c.jpg','趣味',0,0,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(25,3,'猫抓板 瓦楞纸',NULL,'优质猫抓板 瓦楞纸，品质保证，宠物喜爱',NULL,54.59,67.32,18,1013,21551,4.60,629,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_5_pUF0_9-A_rc.jpg','实用',0,1,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(26,3,'宠物互动激光笔 USB充电',NULL,'优质宠物互动激光笔 USB充电，品质保证，宠物喜爱',NULL,31.38,36.85,14,729,9350,5.00,4721,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_6_-Elf7vDV7Rk.jpg','互动',0,0,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(27,3,'宠物飞盘 软胶材质',NULL,'优质宠物飞盘 软胶材质，品质保证，宠物喜爱',NULL,23.09,29.00,20,499,40657,4.70,854,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_7_ZnHEa8mHOxw.jpg','运动',0,1,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(28,3,'宠物拔河绳 棉绳编织',NULL,'优质宠物拔河绳 棉绳编织，品质保证，宠物喜爱',NULL,15.43,19.78,21,480,14019,4.70,3107,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_8_VQy7AxjWsIE.jpg','耐用',0,0,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(29,3,'猫隧道 可折叠',NULL,'优质猫隧道 可折叠，品质保证，宠物喜爱',NULL,67.28,77.39,13,157,48539,4.80,6715,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_9_Imgk3SzeRxU.jpg','趣味',0,1,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07'),(30,3,'宠物弹力球 橡胶',NULL,'优质宠物弹力球 橡胶，品质保证，宠物喜爱',NULL,16.92,22.49,24,164,32005,4.60,2700,'in_stock','https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/goods/toy_10_9lVdutKM9vg.jpg','热销',0,1,0,0,'2025-12-21 10:17:07','2025-12-21 10:17:07');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;

--
-- Table structure for table `service_appointments`
--

DROP TABLE IF EXISTS `service_appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_appointments` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `pet_id` int NOT NULL COMMENT '宠物ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `time_slot` enum('09:00','10:30','14:00','15:30','17:00') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约时间段',
  `items_base_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '基础价格总和',
  `items_discount_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '折扣总额',
  `care_items_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '护理项总价',
  `order_total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `special_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '特殊要求',
  `status` enum('pending','confirmed','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '订单状态',
  `cancellation_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消原因',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `service_appointments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `service_appointments_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_appointments`
--

/*!40000 ALTER TABLE `service_appointments` DISABLE KEYS */;
INSERT INTO `service_appointments` VALUES (1,'ORDER20250001',1,1,'2025-12-20','10:30',120.00,18.00,55.00,157.00,'猫咪比较胆小，请温柔对待','confirmed',NULL,'2025-12-19 16:05:48','2025-12-19 16:05:48'),(2,'ORDER20250002',1,3,'2025-12-21','14:00',150.00,0.00,85.00,235.00,'狗狗喜欢玩球，可以带玩具安抚','completed',NULL,'2025-12-19 16:05:48','2025-12-19 16:05:48'),(3,'ORDER20250003',2,2,'2025-12-22','15:30',250.00,25.00,120.00,345.00,'需要贵宾标准造型，修剪成圆头','cancelled','用户主动取消','2025-12-19 16:05:48','2025-12-20 05:45:40'),(4,'ORD-20251220-5539',2,2,'2025-12-21','10:30',0.00,0.00,0.00,80.00,'','pending',NULL,'2025-12-19 16:52:14','2025-12-19 16:52:14'),(5,'ORD-20251226-9632',2,5,'2025-12-30','14:00',250.00,25.00,0.00,225.00,'','cancelled','用户主动取消','2025-12-26 14:10:24','2025-12-26 14:11:45');
/*!40000 ALTER TABLE `service_appointments` ENABLE KEYS */;

--
-- Table structure for table `service_care_items`
--

DROP TABLE IF EXISTS `service_care_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_care_items` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '护理项ID',
  `care_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '护理项名称',
  `care_category` enum('局部护理','深度护理','精致修剪','赠送护理') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '局部护理' COMMENT '护理项分类',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '护理项描述',
  `unit_price` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '护理项单价',
  `is_single_purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否单次选购',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_care_category` (`care_category`),
  KEY `idx_unit_price` (`unit_price`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_care_items`
--

/*!40000 ALTER TABLE `service_care_items` DISABLE KEYS */;
INSERT INTO `service_care_items` VALUES (1,'耳朵清洁','局部护理','专业清洁耳道，预防耳螨感染',30.00,1,10,'2025-12-19 16:05:46'),(2,'眼睛护理','局部护理','温和清洁眼周，预防泪痕',25.00,1,20,'2025-12-19 16:05:46'),(3,'牙齿清洁','局部护理','专业刷牙，清新口气',40.00,1,30,'2025-12-19 16:05:46'),(4,'脚底修剪','精致修剪','修剪脚底毛发，防止打滑',20.00,1,40,'2025-12-19 16:05:46'),(5,'肛周修剪','精致修剪','修剪肛周毛发，保持卫生',25.00,0,50,'2025-12-19 16:05:46'),(6,'精油按摩','深度护理','放松肌肉，促进血液循环',80.00,0,60,'2025-12-19 16:05:46'),(7,'去毛结服务','深度护理','专业处理毛结，减少拉扯疼痛',60.00,0,70,'2025-12-19 16:05:46'),(8,'指甲美容','赠送护理','修剪并打磨指甲，安全美观',0.00,0,80,'2025-12-19 16:05:46');
/*!40000 ALTER TABLE `service_care_items` ENABLE KEYS */;

--
-- Table structure for table `service_details`
--

DROP TABLE IF EXISTS `service_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_details` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `service_id` int NOT NULL COMMENT '服务项目ID',
  `service_intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务详细介绍',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '预约须知',
  `suitable_pets` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用宠物类型',
  `recommended_care_items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '推荐护理项ID列表',
  `main_images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情页主图',
  `average_rating` decimal(3,2) DEFAULT NULL COMMENT '平均评分',
  `review_count` int NOT NULL DEFAULT '0' COMMENT '评价总数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_id` (`service_id`),
  KEY `idx_average_rating` (`average_rating`),
  KEY `idx_review_count` (`review_count`),
  CONSTRAINT `service_details_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `service_items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约服务详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_details`
--

/*!40000 ALTER TABLE `service_details` DISABLE KEYS */;
INSERT INTO `service_details` VALUES (1,1,'专业基础洗护流程：1. 预洗去污 2. 深层清洁 3. 护毛素滋养 4. 吹干拉毛','请提前预约，宠物需健康状况良好','所有猫犬','1,2,8','/uploads/services/details/basic_bath_1.jpg,/uploads/services/details/basic_bath_2.jpg',4.50,12,'2025-12-19 16:05:47','2025-12-19 16:05:47'),(2,2,'高端精致洗护：1. 香薰SPA 2. 精油按摩 3. 丝蛋白护理 4. 专业造型','建议提前1-2天预约，需提供宠物健康证明','长毛犬、需要特殊护理的宠物','1,2,3,6,8','/uploads/services/details/deluxe_bath_1.jpg,/uploads/services/details/deluxe_bath_2.jpg',4.80,8,'2025-12-19 16:05:47','2025-12-19 16:05:47'),(3,3,'标准美容流程：洗澡+修剪指甲+清理耳朵+全身修剪+造型设计','宠物需提前禁食4小时','所有猫犬，特别是需要定期美容的品种','1,2,4,5,8','/uploads/services/details/basic_grooming_1.jpg,/uploads/services/details/basic_grooming_2.jpg',4.70,15,'2025-12-19 16:05:47','2025-12-19 16:05:47'),(4,4,'贵宾专属美容：贵宾造型+染色可选+专业拍照+精美饰品','需提前3天预约，提供参考造型图片','贵宾犬、比熊犬等需要造型的犬种','1,2,3,4,5,6,8','/uploads/services/details/poodle_grooming_1.jpg,/uploads/services/details/poodle_grooming_2.jpg',4.90,20,'2025-12-19 16:05:47','2025-12-19 16:05:47'),(5,5,'宠物SPA疗程：香薰沐浴+精油按摩+死海泥护理+红外线理疗','疗程约2-3小时，请预留充足时间','老年宠物、关节不适宠物、皮肤敏感宠物','1,2,6,7,8','/uploads/services/details/spa_care_1.jpg,/uploads/services/details/spa_care_2.jpg',4.85,18,'2025-12-19 16:05:47','2025-12-19 16:05:47'),(6,6,'医疗级驱虫洗护：药浴驱虫+皮肤检查+专业护理+预防建议','需提供近期驱虫记录，皮肤病宠物需提前告知','有体外寄生虫风险、皮肤敏感的宠物','1,2,3,8','/uploads/services/details/deworming_1.jpg,/uploads/services/details/deworming_2.jpg',4.75,25,'2025-12-19 16:05:47','2025-12-19 16:05:47');
/*!40000 ALTER TABLE `service_details` ENABLE KEYS */;

--
-- Table structure for table `service_items`
--

DROP TABLE IF EXISTS `service_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_items` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务分类',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
  `brief_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '简短描述',
  `base_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '基础价格',
  `is_discount` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否打折',
  `discount_percentage` decimal(5,2) DEFAULT NULL COMMENT '折扣百分比',
  `final_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '最终价格',
  `is_recommended` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐',
  `duration` int NOT NULL DEFAULT '60' COMMENT '预估总时长(分钟)',
  `is_single_purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否单次选购',
  `thumbnail_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_is_recommended` (`is_recommended`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_final_price` (`final_price`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约服务项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_items`
--

/*!40000 ALTER TABLE `service_items` DISABLE KEYS */;
INSERT INTO `service_items` VALUES (1,'洗澡','基础洗澡','基础清洁洗护，让宠物清爽舒适',80.00,0,NULL,80.00,0,60,0,'/uploads/services/basic_bath.jpg',1,'2025-12-19 14:04:53','2025-12-19 14:04:53'),(2,'洗澡','精致洗护','深层清洁+精油护理，呵护宠物毛发',120.00,1,15.00,102.00,1,90,0,'/uploads/services/deluxe_bath.jpg',2,'2025-12-19 14:04:53','2025-12-19 14:04:53'),(3,'美容','基础美容','洗澡+修剪+造型',150.00,0,NULL,150.00,0,120,0,'/uploads/services/basic_grooming.jpg',3,'2025-12-19 14:04:53','2025-12-19 14:04:53'),(4,'美容','贵宾美容','专业贵宾造型设计',250.00,1,10.00,225.00,1,180,0,'/uploads/services/poodle_grooming.jpg',4,'2025-12-19 14:04:53','2025-12-19 14:04:53'),(5,'护理','SPA护理','精油按摩+深层护理',200.00,1,20.00,160.00,1,150,0,'/uploads/services/spa_care.jpg',5,'2025-12-19 14:04:53','2025-12-19 14:04:53'),(6,'医疗','驱虫洗护','药浴驱虫+专业护理',180.00,0,NULL,180.00,0,120,0,'/uploads/services/deworming.jpg',6,'2025-12-19 14:04:53','2025-12-19 14:04:53');
/*!40000 ALTER TABLE `service_items` ENABLE KEYS */;

--
-- Table structure for table `service_order_items`
--

DROP TABLE IF EXISTS `service_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_order_items` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单项目ID',
  `order_id` int NOT NULL COMMENT '主订单ID',
  `service_id` int NOT NULL COMMENT '服务项目ID',
  `item_base_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '项目基础价格',
  `item_discount_percentage` decimal(5,2) DEFAULT NULL COMMENT '项目折扣百分比',
  `item_discount_amount` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '项目折扣金额',
  `item_final_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '项目最终价格',
  `care_items_selected` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '选择的护理项信息',
  `care_items_total` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '护理项总价',
  `item_total_amount` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '项目总金额',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_service_id` (`service_id`),
  CONSTRAINT `service_order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `service_appointments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `service_order_items_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service_items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约订单项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_order_items`
--

/*!40000 ALTER TABLE `service_order_items` DISABLE KEYS */;
INSERT INTO `service_order_items` VALUES (1,1,2,120.00,15.00,18.00,102.00,'{\"care_items\": [1, 2, 8]}',55.00,157.00,1,'2025-12-19 16:05:50'),(2,2,3,150.00,NULL,0.00,150.00,'{\"care_items\": [1, 2, 6, 8]}',85.00,235.00,1,'2025-12-19 16:05:50'),(3,3,4,250.00,10.00,25.00,225.00,'{\"care_items\": [1, 2, 3, 4, 5, 8]}',120.00,345.00,1,'2025-12-19 16:05:50'),(4,4,1,80.00,NULL,0.00,80.00,NULL,0.00,80.00,1,'2025-12-19 16:52:14'),(5,5,4,250.00,10.00,25.00,225.00,NULL,0.00,225.00,1,'2025-12-26 14:10:24');
/*!40000 ALTER TABLE `service_order_items` ENABLE KEYS */;

--
-- Table structure for table `service_records`
--

DROP TABLE IF EXISTS `service_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_records` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `appointment_id` int NOT NULL COMMENT '预约订单ID',
  `payment_status` enum('unpaid','paid','refunded') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unpaid' COMMENT '支付状态',
  `payment_method` enum('wechat','alipay','cash','card','none') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'wechat' COMMENT '支付方式',
  `payment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付流水号',
  `paid_amount` decimal(10,2) DEFAULT NULL COMMENT '实际支付金额',
  `paid_at` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `rating` tinyint NOT NULL DEFAULT '0' COMMENT '评分',
  `review_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名',
  `reviewed_at` timestamp NULL DEFAULT NULL COMMENT '评价时间',
  `service_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '服务过程记录',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '服务完成时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appointment_id` (`appointment_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_rating` (`rating`),
  KEY `idx_completed_at` (`completed_at`),
  CONSTRAINT `service_records_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `service_appointments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_records`
--

/*!40000 ALTER TABLE `service_records` DISABLE KEYS */;
INSERT INTO `service_records` VALUES (1,1,'paid','alipay','PAY20250001',157.00,'2025-12-20 02:45:00',5,'服务非常专业，猫咪很享受，下次还会来！',0,'2025-12-20 06:30:00','猫咪配合良好，完成耳朵清洁和眼睛护理','2025-12-20 04:30:00','2025-12-19 16:05:51','2025-12-19 16:05:51'),(2,2,'paid','wechat','PAY20250002',235.00,'2025-12-21 06:15:00',4,'美容师很有耐心，狗狗造型很好看',0,'2025-12-21 09:00:00','狗狗洗澡时有点兴奋，但修剪时很配合','2025-12-21 08:00:00','2025-12-19 16:05:51','2025-12-19 16:05:51'),(3,3,'unpaid','none',NULL,NULL,NULL,0,NULL,0,NULL,NULL,NULL,'2025-12-19 16:05:51','2025-12-19 16:05:51');
/*!40000 ALTER TABLE `service_records` ENABLE KEYS */;

--
-- Table structure for table `user_addresses`
--

DROP TABLE IF EXISTS `user_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_addresses` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `full_address` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '完整地址',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认地址：1-是，0-否',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`),
  CONSTRAINT `user_addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_addresses`
--

/*!40000 ALTER TABLE `user_addresses` DISABLE KEYS */;
INSERT INTO `user_addresses` VALUES (1,1,'张三','13800138001','北京市','北京市','朝阳区','三里屯街道1号','北京市北京市朝阳区三里屯街道1号',0,'2025-12-19 03:09:41','2025-12-26 08:13:11'),(8,1,'sdv','18198650774','浙江省','宁波市','鄞州区','1潍坊save都是','浙江省宁波市鄞州区1潍坊save都是',1,'2025-12-26 14:31:38','2025-12-26 15:07:54');
/*!40000 ALTER TABLE `user_addresses` ENABLE KEYS */;

--
-- Table structure for table `user_favorites`
--

DROP TABLE IF EXISTS `user_favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`) COMMENT '用户商品唯一索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_product_id` (`product_id`) COMMENT '商品ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorites`
--

/*!40000 ALTER TABLE `user_favorites` DISABLE KEYS */;
INSERT INTO `user_favorites` VALUES (1,1,6,'2025-12-26 09:08:03','2025-12-26 09:08:03'),(3,1,22,'2025-12-26 09:08:10','2025-12-26 09:08:10'),(5,1,12,'2025-12-26 09:15:21','2025-12-26 09:15:21'),(7,1,20,'2025-12-26 11:44:25','2025-12-26 11:44:25'),(10,2,23,'2025-12-26 13:25:32','2025-12-26 13:25:32'),(11,2,22,'2025-12-26 13:27:22','2025-12-26 13:27:22'),(12,2,20,'2025-12-26 13:27:22','2025-12-26 13:27:22'),(13,2,12,'2025-12-26 13:27:23','2025-12-26 13:27:23'),(14,2,10,'2025-12-26 13:27:25','2025-12-26 13:27:25');
/*!40000 ALTER TABLE `user_favorites` ENABLE KEYS */;

--
-- Table structure for table `user_login_logs`
--

DROP TABLE IF EXISTS `user_login_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_login_logs` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录IP',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理信息',
  `login_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_status` tinyint DEFAULT '1' COMMENT '登录状态：1-成功，0-失败',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_login_status` (`login_status`),
  CONSTRAINT `user_login_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_login_logs`
--

/*!40000 ALTER TABLE `user_login_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_login_logs` ENABLE KEYS */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT '1' COMMENT '账号状态：1-正常，0-禁用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'testuser1','$2a$10$OJ6cb/O/cUEFjZWmUj0E/.vGsUK4isXXXTvpGHS9nYU/QHrzobDQ6','测试用户1',NULL,'13800138001','test1@example.com',1,'2025-12-19 03:09:41','2025-12-19 08:03:01',NULL),(2,'calla','$2a$10$OJ6cb/O/cUEFjZWmUj0E/.vGsUK4isXXXTvpGHS9nYU/QHrzobDQ6','calla',NULL,NULL,'123@123.com',1,'2025-12-19 03:13:39','2025-12-26 12:43:20','2025-12-26 12:43:20');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

--
-- Dumping routines for database 'pet_home'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-27  0:27:42
