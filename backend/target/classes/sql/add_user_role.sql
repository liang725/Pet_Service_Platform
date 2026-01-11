-- 为users表添加role字段（管理员功能）
-- 执行前请先备份数据库

-- 1. 添加role字段
ALTER TABLE `users` 
ADD COLUMN `role` ENUM('admin', 'user') NOT NULL DEFAULT 'user' 
COMMENT '用户角色：admin-管理员，user-普通用户' 
AFTER `phone`;

-- 2. 创建一个测试管理员账号（密码是加密后的 "123456"）
-- 如果你想创建管理员，可以执行以下SQL：
-- INSERT INTO users(username, password, nickname, role, email, status) 
-- VALUES('admin1', '$2a$10$OJ6cb/O/cUEFjZWmUj0E/.vGsUK4isXXXTvpGHS9nYU/QHrzobDQ6', '测试管理员', 'admin', 'admin@example.com', 1);

-- 3. 或者将现有用户升级为管理员：
-- UPDATE users SET role = 'admin' WHERE username = '你的用户名';
