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
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
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
            
            // 从数据库查询完整的用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getUsername());
            userInfo.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
            userInfo.put("level", "黄金会员");
            userInfo.put("memberLevel", "黄金会员");
            userInfo.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
            userInfo.put("phone", user.getPhone() != null ? user.getPhone() : "");
            userInfo.put("email", user.getEmail() != null ? user.getEmail() : "");
            userInfo.put("role", user.getRole());
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param request HTTP请求对象
     * @param userInfo 用户信息
     * @return 操作结果
     */
    @PutMapping("/info")
    public Result<String> updateUserInfo(HttpServletRequest request, @RequestBody Map<String, Object> userInfo) {
        try {
            // 获取当前用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            // 获取更新的字段
            String username = userInfo.get("name") != null ? userInfo.get("name").toString() : null;
            String nickname = userInfo.get("nickname") != null ? userInfo.get("nickname").toString() : null;
            String avatar = userInfo.get("avatar") != null ? userInfo.get("avatar").toString() : null;
            String phone = userInfo.get("phone") != null ? userInfo.get("phone").toString() : null;
            String email = userInfo.get("email") != null ? userInfo.get("email").toString() : null;

            // 查询当前用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            // 如果要修改用户名，检查是否已被占用
            if (username != null && !username.equals(user.getUsername())) {
                User existingUser = userMapper.findByUsername(username);
                if (existingUser != null) {
                    return Result.error(400, "用户名已被占用");
                }
            }

            // 使用现有值填充空字段
            if (username == null) username = user.getUsername();
            if (nickname == null) nickname = user.getNickname();
            if (avatar == null) avatar = user.getAvatar();
            if (phone == null) phone = user.getPhone();
            if (email == null) email = user.getEmail();

            // 更新用户信息
            int rows = userMapper.updateUserInfo(userId, username, nickname, avatar, phone, email);
            if (rows > 0) {
                return Result.success("更新用户信息成功");
            }
            return Result.error(500, "更新用户信息失败");
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