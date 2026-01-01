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
     * 搜索商品（宽松匹配，忽略大小写）
     */
    @Select("SELECT * FROM products WHERE (LOWER(name) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR LOWER(brand) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR LOWER(short_description) LIKE LOWER(CONCAT('%', #{keyword}, '%'))) AND status = 'in_stock' ORDER BY sales DESC LIMIT #{offset}, #{pageSize}")
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

    /**
     * 高级搜索商品（支持多条件筛选和排序，宽松匹配，支持同义词）
     */
    @Select("<script>" +
            "SELECT * FROM products WHERE status = 'in_stock' " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (" +
            "LOWER(name) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(brand) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(short_description) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            // 同义词匹配 - 狗相关
            "(LOWER(#{keyword}) LIKE '%狗%' AND (LOWER(name) LIKE '%犬%' OR LOWER(description) LIKE '%犬%')) OR " +
            "(LOWER(#{keyword}) LIKE '%犬%' AND (LOWER(name) LIKE '%狗%' OR LOWER(description) LIKE '%狗%')) OR " +
            // 同义词匹配 - 狗粮相关
            "(LOWER(#{keyword}) LIKE '%狗粮%' AND (LOWER(name) LIKE '%犬粮%' OR LOWER(name) LIKE '%狗食%' OR LOWER(name) LIKE '%犬食%')) OR " +
            "(LOWER(#{keyword}) LIKE '%犬粮%' AND (LOWER(name) LIKE '%狗粮%' OR LOWER(name) LIKE '%狗食%' OR LOWER(name) LIKE '%犬食%')) OR " +
            // 同义词匹配 - 猫粮相关
            "(LOWER(#{keyword}) LIKE '%猫粮%' AND LOWER(name) LIKE '%猫食%') OR " +
            "(LOWER(#{keyword}) LIKE '%猫食%' AND LOWER(name) LIKE '%猫粮%')" +
            ") " +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND category_id = #{categoryId} " +
            "</if>" +
            "<if test='minPrice != null'>" +
            "AND price &gt;= #{minPrice} " +
            "</if>" +
            "<if test='maxPrice != null'>" +
            "AND price &lt;= #{maxPrice} " +
            "</if>" +
            "ORDER BY " +
            "<choose>" +
            "<when test='sortBy == \"price_asc\"'>price ASC</when>" +
            "<when test='sortBy == \"price_desc\"'>price DESC</when>" +
            "<when test='sortBy == \"rating\"'>rating DESC</when>" +
            "<otherwise>sales DESC</otherwise>" +
            "</choose> " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Product> advancedSearch(@Param("keyword") String keyword,
                                 @Param("categoryId") Integer categoryId,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice,
                                 @Param("sortBy") String sortBy,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    /**
     * 统计高级搜索结果数量（宽松匹配，支持同义词）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM products WHERE status = 'in_stock' " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (" +
            "LOWER(name) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(brand) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(short_description) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            // 同义词匹配 - 狗相关
            "(LOWER(#{keyword}) LIKE '%狗%' AND (LOWER(name) LIKE '%犬%' OR LOWER(description) LIKE '%犬%')) OR " +
            "(LOWER(#{keyword}) LIKE '%犬%' AND (LOWER(name) LIKE '%狗%' OR LOWER(description) LIKE '%狗%')) OR " +
            // 同义词匹配 - 狗粮相关
            "(LOWER(#{keyword}) LIKE '%狗粮%' AND (LOWER(name) LIKE '%犬粮%' OR LOWER(name) LIKE '%狗食%' OR LOWER(name) LIKE '%犬食%')) OR " +
            "(LOWER(#{keyword}) LIKE '%犬粮%' AND (LOWER(name) LIKE '%狗粮%' OR LOWER(name) LIKE '%狗食%' OR LOWER(name) LIKE '%犬食%')) OR " +
            // 同义词匹配 - 猫粮相关
            "(LOWER(#{keyword}) LIKE '%猫粮%' AND LOWER(name) LIKE '%猫食%') OR " +
            "(LOWER(#{keyword}) LIKE '%猫食%' AND LOWER(name) LIKE '%猫粮%')" +
            ") " +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND category_id = #{categoryId} " +
            "</if>" +
            "<if test='minPrice != null'>" +
            "AND price &gt;= #{minPrice} " +
            "</if>" +
            "<if test='maxPrice != null'>" +
            "AND price &lt;= #{maxPrice} " +
            "</if>" +
            "</script>")
    int countAdvancedSearch(@Param("keyword") String keyword,
                           @Param("categoryId") Integer categoryId,
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice);
}
