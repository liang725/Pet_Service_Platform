package com.ddliang.backend.service;

import com.ddliang.backend.dto.ProductResponse;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean removeFavorite(Long userId, Long productId);

    /**
     * 检查是否已收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);

    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @return 收藏商品列表
     */
    List<ProductResponse> getFavoriteProducts(Long userId);

    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    int getFavoriteCount(Long userId);
}