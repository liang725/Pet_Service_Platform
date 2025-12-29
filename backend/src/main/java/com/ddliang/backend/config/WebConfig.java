// src/main/java/com/ddliang/backend/config/WebConfig.java
package com.ddliang.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 认证相关接口
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/refresh-token",

                        // 服务项目相关公开接口
                        "/api/service/items",
                        "/api/service/items/**",  // 这个通配符很重要！
                        "/api/service/time-slots",

                        // 商品相关公开接口（无需登录）
                        "/api/products",
                        "/api/products/**",

                        // 其他可能需要公开的接口
                        "/api/public/**"
                );
    }
}