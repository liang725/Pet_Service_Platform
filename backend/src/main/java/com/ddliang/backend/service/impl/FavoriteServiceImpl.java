package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.ProductResponse;
import com.ddliang.backend.entity.Product;
import com.ddliang.backend.entity.UserFavorite;
import com.ddliang.backend.mapper.FavoriteMapper;
import com.ddliang.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean addFavorite(Long userId, Long productId) {
        try {
            // 检查是否已经收藏
            if (isFavorite(userId, productId)) {
                return false; // 已经收藏过了
            }

            UserFavorite favorite = new UserFavorite(userId, productId);
            int result = favoriteMapper.addFavorite(favorite);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeFavorite(Long userId, Long productId) {
        try {
            int result = favoriteMapper.removeFavorite(userId, productId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        try {
            int count = favoriteMapper.checkFavorite(userId, productId);
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ProductResponse> getFavoriteProducts(Long userId) {
        try {
            List<UserFavorite> favorites = favoriteMapper.getFavoritesByUserId(userId);
            
            return favorites.stream()
                    .filter(favorite -> favorite.getProduct() != null) // 过滤掉商品不存在的收藏
                    .map(favorite -> convertToProductResponse(favorite.getProduct()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 返回空列表
        }
    }

    @Override
    public int getFavoriteCount(Long userId) {
        try {
            return favoriteMapper.getFavoriteCount(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将Product实体转换为ProductResponse DTO
     */
    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setRating(product.getRating());
        response.setRatingCount(product.getRatingCount());
        response.setImage(product.getImageUrl());
        response.setTag(product.getTag());
        response.setSales(product.getSales());
        return response;
    }
}