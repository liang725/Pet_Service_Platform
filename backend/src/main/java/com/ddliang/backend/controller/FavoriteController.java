package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.ProductResponse;
import com.ddliang.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:5173")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取收藏列表
     * @param request HTTP请求对象
     * @return 收藏商品列表
     */
    @GetMapping
    public Result<List<ProductResponse>> getFavorites(HttpServletRequest request) {
        try {
            // 从JWT拦截器设置的request属性中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户信息获取失败，请重新登录");
            }
            
            List<ProductResponse> favorites = favoriteService.getFavoriteProducts(userId.longValue());
            return Result.success(favorites);
        } catch (Exception e) {
            return Result.error("获取收藏列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加收藏
     * @param request 收藏请求
     * @param httpRequest HTTP请求对象
     * @return 操作结果
     */
    @PostMapping
    public Result<String> addFavorite(@RequestBody FavoriteRequest request, HttpServletRequest httpRequest) {
        try {
            // 从JWT拦截器设置的request属性中获取用户ID
            Integer userId = (Integer) httpRequest.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户信息获取失败，请重新登录");
            }
            
            if (request.getProductId() == null) {
                return Result.error("商品ID不能为空");
            }

            boolean success = favoriteService.addFavorite(userId.longValue(), request.getProductId().longValue());
            if (success) {
                return Result.success("添加收藏成功");
            } else {
                return Result.error("商品已在收藏列表中");
            }
        } catch (Exception e) {
            return Result.error("添加收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏
     * @param productId 商品ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @DeleteMapping("/{productId}")
    public Result<String> removeFavorite(@PathVariable Integer productId, HttpServletRequest request) {
        try {
            // 从JWT拦截器设置的request属性中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户信息获取失败，请重新登录");
            }
            
            boolean success = favoriteService.removeFavorite(userId.longValue(), productId.longValue());
            if (success) {
                return Result.success("取消收藏成功");
            } else {
                return Result.error("商品不在收藏列表中");
            }
        } catch (Exception e) {
            return Result.error("取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否已收藏
     * @param productId 商品ID
     * @param request HTTP请求对象
     * @return 是否已收藏
     */
    @GetMapping("/check/{productId}")
    public Result<Boolean> checkFavorite(@PathVariable Integer productId, HttpServletRequest request) {
        try {
            // 从JWT拦截器设置的request属性中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户信息获取失败，请重新登录");
            }
            
            boolean isFavorite = favoriteService.isFavorite(userId.longValue(), productId.longValue());
            return Result.success(isFavorite);
        } catch (Exception e) {
            return Result.error("检查收藏状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取收藏数量
     * @param request HTTP请求对象
     * @return 收藏数量
     */
    @GetMapping("/count")
    public Result<Integer> getFavoriteCount(HttpServletRequest request) {
        try {
            // 从JWT拦截器设置的request属性中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户信息获取失败，请重新登录");
            }
            
            int count = favoriteService.getFavoriteCount(userId.longValue());
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("获取收藏数量失败: " + e.getMessage());
        }
    }

    /**
     * 收藏请求DTO
     */
    public static class FavoriteRequest {
        private Integer productId;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }
    }
}