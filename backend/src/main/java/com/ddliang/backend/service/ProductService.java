package com.ddliang.backend.service;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.ProductDetailResponse;
import com.ddliang.backend.dto.ProductResponse;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 根据分类获取商品列表
     * @param category 分类代码: hot/food/supply/toy
     * @param limit 返回数量限制
     * @return 商品列表
     */
    List<ProductResponse> getProductsByCategory(String category, Integer limit);

    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<ProductResponse> searchProducts(String keyword, Integer page, Integer pageSize);

    /**
     * 获取所有商品（分页）
     * @param page 页码
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<ProductResponse> getAllProducts(Integer page, Integer pageSize);

    /**
     * 根据ID获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    ProductDetailResponse getProductDetail(Integer id);

    /**
     * 获取推荐商品
     * @param productId 当前商品ID
     * @param limit 返回数量限制
     * @return 推荐商品列表
     */
    List<ProductResponse> getRecommendedProducts(Integer productId, Integer limit);

    /**
     * 高级商品搜索（支持多条件筛选）
     * @param keyword 搜索关键词
     * @param category 分类
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param sortBy 排序方式
     * @param page 页码
     * @param pageSize 每页数量
     * @return 商品列表和总数
     */
    Result<?> advancedSearch(String keyword, String category, Double minPrice, Double maxPrice, 
                            String sortBy, Integer page, Integer pageSize);
}
