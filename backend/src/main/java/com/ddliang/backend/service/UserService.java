package com.ddliang.backend.service;

import com.ddliang.backend.dto.LoginRequest;
import com.ddliang.backend.dto.RegisterRequest;
import com.ddliang.backend.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    String login(LoginRequest request);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查找用户
     */
    User findById(Integer id);

    /**
     * 检查用户是否为管理员
     */
    boolean isAdmin(Integer userId);

    /**
     * 获取用户角色
     */
    String getUserRole(Integer userId);
}