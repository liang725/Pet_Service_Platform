package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.*;
import org.apache.ibatis.annotations.*;

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

    /**
     * 搜索商品
     */
    @Select("SELECT * FROM products WHERE (name LIKE CONCAT('%', #{keyword}, '%') OR brand LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) AND status = 'in_stock' ORDER BY sales DESC LIMIT #{offset}, #{pageSize}")
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 获取所有商品（分页）
     */
    @Select("SELECT * FROM products WHERE status = 'in_stock' ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Product> findAllProducts(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 获取所有商品（管理端，包含所有状态）
     */
    @Select("SELECT * FROM products ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Product> findAllProductsForAdmin(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 获取商品总数（管理端）
     */
    @Select("SELECT COUNT(*) FROM products")
    int countAllProducts();

    /**
     * 搜索商品（管理端，包含所有状态）
     */
    @Select("SELECT * FROM products WHERE name LIKE CONCAT('%', #{keyword}, '%') OR brand LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Product> searchProductsForAdmin(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 搜索商品总数（管理端）
     */
    @Select("SELECT COUNT(*) FROM products WHERE name LIKE CONCAT('%', #{keyword}, '%') OR brand LIKE CONCAT('%', #{keyword}, '%')")
    int countSearchProducts(@Param("keyword") String keyword);

    /**
     * 新增商品
     */
    @Insert("INSERT INTO products (category_id, name, brand, short_description, description, price, original_price, discount, stock, sales, rating, rating_count, status, image_url, tag, is_recommended, is_hot, is_new, sort_order, created_at, updated_at) " +
            "VALUES (#{categoryId}, #{name}, #{brand}, #{shortDescription}, #{description}, #{price}, #{originalPrice}, #{discount}, #{stock}, #{sales}, #{rating}, #{ratingCount}, #{status}, #{imageUrl}, #{tag}, #{isRecommended}, #{isHot}, #{isNew}, #{sortOrder}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 更新商品
     */
    @Update("UPDATE products SET category_id = #{categoryId}, name = #{name}, brand = #{brand}, short_description = #{shortDescription}, description = #{description}, price = #{price}, original_price = #{originalPrice}, discount = #{discount}, stock = #{stock}, status = #{status}, image_url = #{imageUrl}, tag = #{tag}, is_recommended = #{isRecommended}, is_hot = #{isHot}, is_new = #{isNew}, sort_order = #{sortOrder}, updated_at = NOW() WHERE id = #{id}")
    int update(Product product);

    /**
     * 删除商品
     */
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 更新商品状态
     */
    @Update("UPDATE products SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    /**
     * 更新商品库存
     */
    @Update("UPDATE products SET stock = #{stock}, updated_at = NOW() WHERE id = #{id}")
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);

    /**
     * 增加商品销量
     */
    @Update("UPDATE products SET sales = sales + #{quantity}, updated_at = NOW() WHERE id = #{id}")
    int increaseSales(@Param("id") Integer id, @Param("quantity") Integer quantity);
}
