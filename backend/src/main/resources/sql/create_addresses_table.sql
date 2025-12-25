-- 检查并添加 full_address 字段
ALTER TABLE user_addresses 
ADD COLUMN full_address VARCHAR(300) COMMENT '完整地址' AFTER address;

-- 更新现有记录的完整地址字段
UPDATE user_addresses 
SET full_address = CONCAT(IFNULL(province, ''), IFNULL(city, ''), IFNULL(district, ''), IFNULL(address, ''))
WHERE full_address IS NULL OR full_address = '';

-- 插入测试数据（如果不存在）
INSERT IGNORE INTO user_addresses (user_id, receiver_name, receiver_phone, province, city, district, address, full_address, is_default) VALUES
(1, '张先生', '13888888888', '浙江省', '杭州市', '钱塘区', '宠物之家大厦1号楼101室', '浙江省杭州市钱塘区宠物之家大厦1号楼101室', 1),
(1, '李女士', '13999999999', '浙江省', '杭州市', '西湖区', '文三路123号', '浙江省杭州市西湖区文三路123号', 0);