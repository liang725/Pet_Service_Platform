package com.ddliang.backend.service;

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
}
