-- 修改 products 表的 image_url 字段类型，支持 base64 图片
-- 执行此脚本前请备份数据库

USE pet_home;

-- 将 image_url 字段从 varchar(500) 改为 LONGTEXT
ALTER TABLE products 
MODIFY COLUMN image_url LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '主图URL（支持base64）';

-- 验证修改
DESCRIBE products;
