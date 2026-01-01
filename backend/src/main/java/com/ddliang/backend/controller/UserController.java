package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.mapper.UserMapper;
import com.ddliang.backend.entity.User;
import com.ddliang.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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

    /**
     * 修改密码
     * @param request HTTP请求对象
     * @param passwordData 密码数据（包含当前密码和新密码）
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<String> changePassword(HttpServletRequest request, @RequestBody Map<String, String> passwordData) {
        try {
            // 获取当前用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            // 获取密码参数
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            // 验证参数
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return Result.error(400, "当前密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error(400, "新密码不能为空");
            }
            if (newPassword.length() < 6) {
                return Result.error(400, "新密码长度不能少于6位");
            }

            // 查询用户
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            // 验证当前密码
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return Result.error(400, "当前密码错误");
            }

            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);

            // 更新密码
            int rows = userMapper.updatePassword(userId, encodedPassword);
            if (rows > 0) {
                return Result.success("密码修改成功");
            }
            return Result.error(500, "密码修改失败");
        } catch (Exception e) {
            return Result.error(500, "密码修改失败：" + e.getMessage());
        }
    }
}