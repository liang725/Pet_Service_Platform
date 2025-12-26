package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    /**
     * 获取用户信息
     * @param request HTTP请求对象
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        try {
            // 从JWT拦截器设置的request属性中获取用户信息
            String username = (String) request.getAttribute("username");
            Integer userId = (Integer) request.getAttribute("userId");
            
            if (username == null || userId == null) {
                return Result.error("用户信息获取失败，请重新登录");
            }
            
            // TODO: 根据userId从数据库查询完整的用户信息
            // 这里暂时返回基于token的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userId);
            userInfo.put("username", username);
            userInfo.put("name", username);
            userInfo.put("nickname", username);
            userInfo.put("level", "黄金会员");
            userInfo.put("memberLevel", "黄金会员");
            userInfo.put("avatar", "");
            userInfo.put("phone", "138****8888");
            userInfo.put("email", "user@example.com");
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @return 操作结果
     */
    @PutMapping("/info")
    public Result<String> updateUserInfo(@RequestBody Map<String, Object> userInfo) {
        try {
            // TODO: 实现更新用户信息功能
            return Result.success("更新用户信息成功");
        } catch (Exception e) {
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }
}