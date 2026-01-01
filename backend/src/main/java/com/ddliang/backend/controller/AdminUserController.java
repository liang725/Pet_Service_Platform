package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.entity.User;
import com.ddliang.backend.mapper.UserMapper;
import com.ddliang.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 获取用户列表（分页+搜索）
     */
    @GetMapping
    public Result<?> getUserList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            int offset = (page - 1) * pageSize;
            List<User> users;
            int total;

            // 根据是否有搜索关键词选择不同的查询方法
            if (keyword != null && !keyword.trim().isEmpty()) {
                users = userMapper.searchUsersForAdmin(keyword.trim(), offset, pageSize);
                total = userMapper.countSearchUsers(keyword.trim());
            } else {
                users = userMapper.findAllUsersForAdmin(offset, pageSize);
                total = userMapper.countAllUsers();
            }

            // 移除密码字段
            users.forEach(user -> user.setPassword(null));

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", users);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "获取用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取单个用户详情
     */
    @GetMapping("/{id}")
    public Result<?> getUser(HttpServletRequest request, @PathVariable Integer id) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            User user = userMapper.findById(id);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            // 移除密码字段
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(500, "获取用户详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public Result<?> createUser(HttpServletRequest request, @RequestBody User user) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 验证必填字段
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error(400, "用户名不能为空");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.error(400, "密码不能为空");
            }

            // 检查用户名是否已存在
            User existingUser = userMapper.findByUsername(user.getUsername());
            if (existingUser != null) {
                return Result.error(400, "用户名已存在");
            }

            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 设置默认值
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("user");
            }
            if (user.getStatus() == null) {
                user.setStatus(1);
            }

            int rows = userMapper.insert(user);
            if (rows > 0) {
                // 移除密码字段
                user.setPassword(null);
                return Result.success(user);
            }
            return Result.error(500, "创建用户失败");
        } catch (Exception e) {
            return Result.error(500, "创建用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<?> updateUser(HttpServletRequest request, @PathVariable Integer id, @RequestBody User user) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 检查用户是否存在
            User existing = userMapper.findById(id);
            if (existing == null) {
                return Result.error(404, "用户不存在");
            }

            // 设置用户ID
            user.setId(id);

            // 如果没有设置角色和状态，使用现有的值
            if (user.getRole() == null) {
                user.setRole(existing.getRole());
            }
            if (user.getStatus() == null) {
                user.setStatus(existing.getStatus());
            }

            int rows = userMapper.update(user);
            if (rows > 0) {
                // 返回更新后的用户信息
                User updatedUser = userMapper.findById(id);
                updatedUser.setPassword(null);
                return Result.success(updatedUser);
            }
            return Result.error(500, "更新用户失败");
        } catch (Exception e) {
            return Result.error(500, "更新用户失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(HttpServletRequest request, @PathVariable Integer id) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 检查用户是否存在
            User existing = userMapper.findById(id);
            if (existing == null) {
                return Result.error(404, "用户不存在");
            }

            // 不允许删除管理员账号
            if ("admin".equals(existing.getRole())) {
                return Result.error(400, "不允许删除管理员账号");
            }

            int rows = userMapper.deleteById(id);
            if (rows > 0) {
                return Result.success("删除成功");
            }
            return Result.error(500, "删除用户失败");
        } catch (Exception e) {
            return Result.error(500, "删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    public Result<?> resetPassword(HttpServletRequest request, @PathVariable Integer id, @RequestBody Map<String, String> body) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            String newPassword = body.get("password");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error(400, "新密码不能为空");
            }

            // 检查用户是否存在
            User existing = userMapper.findById(id);
            if (existing == null) {
                return Result.error(404, "用户不存在");
            }

            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);

            int rows = userMapper.updatePassword(id, encodedPassword);
            if (rows > 0) {
                return Result.success("密码重置成功");
            }
            return Result.error(500, "密码重置失败");
        } catch (Exception e) {
            return Result.error(500, "密码重置失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PutMapping("/{id}/status")
    public Result<?> updateUserStatus(HttpServletRequest request, @PathVariable Integer id, @RequestBody Map<String, Integer> body) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            Integer status = body.get("status");
            if (status == null || (status != 0 && status != 1)) {
                return Result.error(400, "无效的状态值");
            }

            // 检查用户是否存在
            User existing = userMapper.findById(id);
            if (existing == null) {
                return Result.error(404, "用户不存在");
            }

            // 不允许禁用管理员账号
            if ("admin".equals(existing.getRole()) && status == 0) {
                return Result.error(400, "不允许禁用管理员账号");
            }

            int rows = userMapper.updateStatus(id, status);
            if (rows > 0) {
                return Result.success("状态更新成功");
            }
            return Result.error(500, "状态更新失败");
        } catch (Exception e) {
            return Result.error(500, "状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            return "admin".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}
