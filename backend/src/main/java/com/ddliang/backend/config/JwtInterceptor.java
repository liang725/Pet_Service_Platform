// src/main/java/com/ddliang/backend/config/JwtInterceptor.java
package com.ddliang.backend.config;

import com.ddliang.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.Set;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    // 定义公开接口（不需要认证）
    private static final Set<String> PUBLIC_PATHS = new HashSet<>() {{
        // 认证相关
        add("/api/auth/login");
        add("/api/auth/register");
        add("/api/auth/refresh-token");

        // 服务项目相关
        add("/api/service/items");
        add("/api/service/items/recommended");
        
        // 商品相关（公开接口，无需登录）
        add("/api/products");
        // 注意：动态路径需要单独判断，这里先添加静态路径
    }};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 检查是否是公开接口
        if (isPublicPath(path)) {
            return true;
        }

        // 验证Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的Token\"}");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token已过期\"}");
                return false;
            }

            // 将用户信息存入request属性
            String username = jwtUtil.getUsernameFromToken(token);
            Integer userId = jwtUtil.getUserIdFromToken(token);
            request.setAttribute("username", username);
            request.setAttribute("userId", userId);

            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token验证失败\"}");
            return false;
        }
    }

    /**
     * 判断是否是公开路径
     */
    private boolean isPublicPath(String path) {
        // 检查完全匹配的路径
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }

        // 检查商品相关路径（支持查询参数）
        if (path.startsWith("/api/products")) {
            return true;
        }

        // 检查服务项目详情路径（如：/api/service/items/1）
        if (path.matches("^/api/service/items/\\d+$")) {
            return true;
        }

        // 检查按分类查询路径（如：/api/service/items/category/洗澡）
        if (path.matches("^/api/service/items/category/[^/]+$")) {
            return true;
        }

        // 检查获取可用时间段
        if (path.startsWith("/api/service/time-slots")) {
            return true;
        }

        return false;
    }
}