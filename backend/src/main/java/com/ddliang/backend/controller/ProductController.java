package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.ProductDetailResponse;
import com.ddliang.backend.dto.ProductResponse;
import com.ddliang.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     * @param category 分类: hot/food/supply/toy
     * @param limit 返回数量限制，默认5
     * @param keyword 搜索关键词
     * @param page 页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 商品列表
     */
    @GetMapping
    public Result<List<ProductResponse>> getProductList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "5") Integer limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<ProductResponse> products;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 如果有搜索关键词，进行搜索
                products = productService.searchProducts(keyword, page, pageSize);
            } else if (category != null) {
                // 按分类获取商品
                products = productService.getProductsByCategory(category, limit);
            } else {
                // 获取所有商品（分页）
                products = productService.getAllProducts(page, pageSize);
            }
            
            return Result.success(products);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/detail/{id}")
    public Result<ProductDetailResponse> getProductDetail(@PathVariable Integer id) {
        try {
            ProductDetailResponse product = productService.getProductDetail(id);
            return Result.success(product);
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取商品详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐商品
     * @param id 当前商品ID
     * @param limit 返回数量限制，默认4
     * @return 推荐商品列表
     */
    @GetMapping("/detail/{id}/recommended")
    public Result<List<ProductResponse>> getRecommendedProducts(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "4") Integer limit) {
        try {
            List<ProductResponse> products = productService.getRecommendedProducts(id, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("获取推荐商品失败: " + e.getMessage());
        }
    }
}
