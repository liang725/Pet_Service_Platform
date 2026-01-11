package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.LoginRequest;
import com.ddliang.backend.dto.RegisterRequest;
import com.ddliang.backend.entity.User;
import com.ddliang.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.error(400, bindingResult.getFieldError().getDefaultMessage());
        }

        try {
            User user = userService.register(request);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            return Result.success("注册成功", data);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.error(400, bindingResult.getFieldError().getDefaultMessage());
        }

        try {
            String token = userService.login(request);
            User user = userService.findByUsername(request.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("avatar", user.getAvatar());
            data.put("role", user.getRole()); // 返回用户角色
            
            // 同时返回 userInfo 对象，方便前端使用
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("role", user.getRole());
            data.put("userInfo", userInfo);
            
            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        // 这里可以添加JWT验证逻辑
        // 暂时简化处理
        return Result.success("获取用户信息成功");
    }
}