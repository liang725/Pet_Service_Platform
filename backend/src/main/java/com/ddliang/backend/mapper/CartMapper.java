package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    
    @Select("SELECT * FROM cart_items WHERE user_id = #{userId}")
    List<CartItem> findByUserId(Long userId);
    
    @Select("SELECT * FROM cart_items WHERE id = #{id} AND user_id = #{userId}")
    CartItem findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId} " +
            "AND (spec = #{spec} OR (spec IS NULL AND #{spec} IS NULL))")
    CartItem findByUserIdAndProductIdAndSpec(@Param("userId") Long userId, 
                                             @Param("productId") Integer productId, 
                                             @Param("spec") String spec);
    
    @Insert("INSERT INTO cart_items (user_id, product_id, product_name, product_image, spec, price, quantity, created_at, updated_at) " +
            "VALUES (#{userId}, #{productId}, #{productName}, #{productImage}, #{spec}, #{price}, #{quantity}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem cartItem);
    
    @Update("UPDATE cart_items SET quantity = #{quantity}, updated_at = NOW() WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Delete("DELETE FROM cart_items WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Delete("DELETE FROM cart_items WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
