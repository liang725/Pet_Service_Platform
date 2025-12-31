package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.UserFavorite;
import com.ddliang.backend.dto.ProductResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏Mapper接口
 */
@Mapper
public interface FavoriteMapper {

    /**
     * 添加收藏
     */
    @Insert("INSERT INTO user_favorites (user_id, product_id) VALUES (#{userId}, #{productId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addFavorite(UserFavorite favorite);

    /**
     * 删除收藏
     */
    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    int removeFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 检查是否已收藏
     */
    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    int checkFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 获取用户收藏列表（连接商品表）
     */
    @Select("SELECT f.id, f.user_id, f.product_id, f.created_at, f.updated_at, " +
            "p.id as product_id, p.name, p.brand, p.price, p.original_price, " +
            "p.rating, p.rating_count, p.image_url, p.tag, p.sales " +
            "FROM user_favorites f " +
            "LEFT JOIN products p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} AND p.status = 'in_stock' " +
            "ORDER BY f.created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "product.id", column = "product_id"),
        @Result(property = "product.name", column = "name"),
        @Result(property = "product.brand", column = "brand"),
        @Result(property = "product.price", column = "price"),
        @Result(property = "product.originalPrice", column = "original_price"),
        @Result(property = "product.rating", column = "rating"),
        @Result(property = "product.ratingCount", column = "rating_count"),
        @Result(property = "product.imageUrl", column = "image_url"),
        @Result(property = "product.tag", column = "tag"),
        @Result(property = "product.sales", column = "sales")
    })
    List<UserFavorite> getFavoritesByUserId(@Param("userId") Long userId);

    /**
     * 获取用户收藏数量
     */
    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId}")
    int getFavoriteCount(@Param("userId") Long userId);
}