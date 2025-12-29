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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES (1,1,'猫','黑蛋','中华田园猫','公','2021-08-25','黑色',5.80,'/uploads/pets/xiaohei.jpg','好奇、活泼、喜欢攀爬','领养的流浪猫，已打疫苗和驱虫','2025-12-19 09:14:37','2025-12-19 16:44:02'),(2,2,'猫','咪咪','英国短毛猫','公','2023-01-10','蓝灰色',4.20,NULL,'安静、独立、喜欢睡觉','已绝育，偶尔会吐毛球，需要定期梳毛','2025-12-19 09:14:51','2025-12-19 10:54:05'),(3,1,'狗','旺旺','金毛寻回犬','公','2022-03-15','金色',28.50,'/uploads/pets/wangcai.jpg','温顺、活泼、喜欢玩球','定期接种疫苗，对小孩友好，喜欢游泳','2025-12-19 09:14:53','2025-12-19 16:44:02');
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_appointments`
--

/*!40000 ALTER TABLE `service_appointments` DISABLE KEYS */;
INSERT INTO `service_appointments` VALUES (1,'ORDER20250001',1,1,'2025-12-20','10:30',120.00,18.00,55.00,157.00,'猫咪比较胆小，请温柔对待','confirmed',NULL,'2025-12-19 16:05:48','2025-12-19 16:05:48'),(2,'ORDER20250002',1,3,'2025-12-21','14:00',150.00,0.00,85.00,235.00,'狗狗喜欢玩球，可以带玩具安抚','completed',NULL,'2025-12-19 16:05:48','2025-12-19 16:05:48'),(3,'ORDER20250003',2,2,'2025-12-22','15:30',250.00,25.00,120.00,345.00,'需要贵宾标准造型，修剪成圆头','cancelled','用户主动取消','2025-12-19 16:05:48','2025-12-20 05:45:40'),(4,'ORD-20251220-5539',2,2,'2025-12-21','10:30',0.00,0.00,0.00,80.00,'','pending',NULL,'2025-12-19 16:52:14','2025-12-19 16:52:14');
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约订单项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_order_items`
--

/*!40000 ALTER TABLE `service_order_items` DISABLE KEYS */;
INSERT INTO `service_order_items` VALUES (1,1,2,120.00,15.00,18.00,102.00,'{\"care_items\": [1, 2, 8]}',55.00,157.00,1,'2025-12-19 16:05:50'),(2,2,3,150.00,NULL,0.00,150.00,'{\"care_items\": [1, 2, 6, 8]}',85.00,235.00,1,'2025-12-19 16:05:50'),(3,3,4,250.00,10.00,25.00,225.00,'{\"care_items\": [1, 2, 3, 4, 5, 8]}',120.00,345.00,1,'2025-12-19 16:05:50'),(4,4,1,80.00,NULL,0.00,80.00,NULL,0.00,80.00,1,'2025-12-19 16:52:14');
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
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认地址：1-是，0-否',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`),
  CONSTRAINT `user_addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_addresses`
--

/*!40000 ALTER TABLE `user_addresses` DISABLE KEYS */;
INSERT INTO `user_addresses` VALUES (1,1,'张三','13800138001','北京市','北京市','朝阳区','三里屯街道1号',1,'2025-12-19 03:09:41','2025-12-19 03:09:41');
/*!40000 ALTER TABLE `user_addresses` ENABLE KEYS */;

--
-- Table structure for table `user_login_logs`
--

DROP TABLE IF EXISTS `user_login_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_login_logs` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `login_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录IP',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理信息',
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
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
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
INSERT INTO `users` VALUES (1,'testuser1','$2a$10$OJ6cb/O/cUEFjZWmUj0E/.vGsUK4isXXXTvpGHS9nYU/QHrzobDQ6','测试用户1',NULL,'13800138001','test1@example.com',1,'2025-12-19 03:09:41','2025-12-19 08:03:01',NULL),(2,'calla','$2a$10$OJ6cb/O/cUEFjZWmUj0E/.vGsUK4isXXXTvpGHS9nYU/QHrzobDQ6','calla',NULL,NULL,'123@123.com',1,'2025-12-19 03:13:39','2025-12-20 05:47:53','2025-12-20 05:47:53');
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

-- Dump completed on 2025-12-20 14:27:35
