package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper {

    /**
     * 根据分类ID查询商品列表
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND status = 'in_stock' ORDER BY sales DESC LIMIT #{limit}")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId, @Param("limit") Integer limit);

    /**
     * 查询热门商品
     */
    @Select("SELECT * FROM products WHERE is_hot = 1 AND status = 'in_stock' ORDER BY sales DESC LIMIT #{limit}")
    List<Product> findHotProducts(@Param("limit") Integer limit);

    /**
     * 根据ID查询商品
     */
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(@Param("id") Integer id);

    /**
     * 查询推荐商品(同分类的其他商品)
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND id != #{excludeId} AND status = 'in_stock' ORDER BY sales DESC LIMIT #{limit}")
    List<Product> findRecommendedProducts(@Param("categoryId") Integer categoryId, @Param("excludeId") Integer excludeId, @Param("limit") Integer limit);

    /**
     * 查询商品规格
     */
    @Select("SELECT * FROM product_specifications WHERE product_id = #{productId} AND is_active = 1 ORDER BY sort_order")
    List<ProductSpecification> findSpecificationsByProductId(@Param("productId") Integer productId);

    /**
     * 查询商品参数
     */
    @Select("SELECT * FROM product_params WHERE product_id = #{productId} ORDER BY sort_order")
    List<ProductParam> findParamsByProductId(@Param("productId") Integer productId);

    /**
     * 查询商品评价分布
     */
    @Select("SELECT * FROM product_rating_distribution WHERE product_id = #{productId} ORDER BY star DESC")
    List<ProductRatingDistribution> findRatingDistributionByProductId(@Param("productId") Integer productId);

    /**
     * 查询商品常见问题
     */
    @Select("SELECT * FROM product_faqs WHERE product_id = #{productId} AND is_active = 1 ORDER BY sort_order")
    List<ProductFaq> findFaqsByProductId(@Param("productId") Integer productId);

    /**
     * 查询商品评价（从product_reviews表）
     */
    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} ORDER BY created_at DESC LIMIT #{limit}")
    List<ProductReview> findReviewsByProductId(@Param("productId") Integer productId, @Param("limit") Integer limit);
}
