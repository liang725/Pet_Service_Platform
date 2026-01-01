package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.entity.User;
import com.ddliang.backend.mapper.ProductMapper;
import com.ddliang.backend.service.UserService;
import com.ddliang.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取管理员仪表板数据
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboardData(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("todayAppointments", 128);
        data.put("totalPets", 542);
        data.put("todayIncome", 15680);
        data.put("totalUsers", 3245);
        data.put("pendingOrders", 5);
        data.put("pendingReviews", 3);
        data.put("totalProducts", productMapper.countAllProducts());

        return Result.success(data);
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
