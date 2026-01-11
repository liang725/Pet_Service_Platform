package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.entity.Product;
import com.ddliang.backend.mapper.ProductMapper;
import com.ddliang.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员商品管理控制器
 */
@RestController
@RequestMapping("/api/admin/products")
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
public class AdminProductController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取商品列表（分页+搜索）
     */
    @GetMapping
    public Result<?> getProductList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            int offset = (page - 1) * pageSize;
            List<Product> products;
            int total;

            // 根据是否有搜索关键词选择不同的查询方法
            if (keyword != null && !keyword.trim().isEmpty()) {
                products = productMapper.searchProductsForAdmin(keyword.trim(), offset, pageSize);
                total = productMapper.countSearchProducts(keyword.trim());
            } else {
                products = productMapper.findAllProductsForAdmin(offset, pageSize);
                total = productMapper.countAllProducts();
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", products);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "获取商品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取单个商品详情
     */
    @GetMapping("/{id}")
    public Result<?> getProduct(HttpServletRequest request, @PathVariable Integer id) {
        
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            Product product = productMapper.findById(id);
            if (product == null) {
                return Result.error(404, "商品不存在");
            }
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(500, "获取商品详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建新商品
     */
    @PostMapping
    public Result<?> createProduct(HttpServletRequest request, @RequestBody Product product) {
        
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 验证必填字段
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return Result.error(400, "商品名称不能为空");
            }
            if (product.getPrice() == null) {
                return Result.error(400, "商品价格不能为空");
            }

            // 设置默认值
            if (product.getStatus() == null) {
                product.setStatus("in_stock");
            }
            if (product.getStock() == null) {
                product.setStock(0);
            }
            // 添加销量字段的默认值
            if (product.getSales() == null) {
                product.setSales(0);
            }
            // 添加评分相关字段的默认值
            if (product.getRating() == null) {
                product.setRating(BigDecimal.valueOf(5.00));
            }
            if (product.getRatingCount() == null) {
                product.setRatingCount(0);
            }
            // 添加布尔字段的默认值
            if (product.getIsRecommended() == null) {
                product.setIsRecommended(false);
            }
            if (product.getIsHot() == null) {
                product.setIsHot(false);
            }
            if (product.getIsNew() == null) {
                product.setIsNew(false);
            }
            // 添加排序字段的默认值
            if (product.getSortOrder() == null) {
                product.setSortOrder(0);
            }

            int rows = productMapper.insert(product);
            if (rows > 0) {
                return Result.success(product);
            }
            return Result.error(500, "创建商品失败");
        } catch (Exception e) {
            return Result.error(500, "创建商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping("/{id}")
    public Result<?> updateProduct(HttpServletRequest request, @PathVariable Integer id, @RequestBody Product product) {
        
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 检查商品是否存在
            Product existing = productMapper.findById(id);
            if (existing == null) {
                return Result.error(404, "商品不存在");
            }

            // 设置商品ID
            product.setId(id);
            
            // 如果没有设置status，使用现有的status
            if (product.getStatus() == null) {
                product.setStatus(existing.getStatus());
            }
            
            // 为其他可能为null的字段设置默认值
            if (product.getStock() == null) {
                product.setStock(existing.getStock());
            }
            // 添加销量字段的处理
            if (product.getSales() == null) {
                product.setSales(existing.getSales());
            }
            if (product.getRating() == null) {
                product.setRating(existing.getRating());
            }
            if (product.getRatingCount() == null) {
                product.setRatingCount(existing.getRatingCount());
            }
            if (product.getIsRecommended() == null) {
                product.setIsRecommended(existing.getIsRecommended());
            }
            if (product.getIsHot() == null) {
                product.setIsHot(existing.getIsHot());
            }
            if (product.getIsNew() == null) {
                product.setIsNew(existing.getIsNew());
            }
            if (product.getSortOrder() == null) {
                product.setSortOrder(existing.getSortOrder());
            }
            
            int rows = productMapper.update(product);
            if (rows > 0) {
                // 返回更新后的商品信息
                return Result.success(productMapper.findById(id));
            }
            return Result.error(500, "更新商品失败");
        } catch (Exception e) {
            return Result.error(500, "更新商品失败：" + e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteProduct(HttpServletRequest request, @PathVariable Integer id) {
        
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 检查商品是否存在
            Product existing = productMapper.findById(id);
            if (existing == null) {
                return Result.error(404, "商品不存在");
            }

            int rows = productMapper.deleteById(id);
            if (rows > 0) {
                return Result.success("删除成功");
            }
            return Result.error(500, "删除商品失败");
        } catch (Exception e) {
            return Result.error(500, "删除商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品状态（上架/下架）
     */
    @PutMapping("/{id}/status")
    public Result<?> updateProductStatus(HttpServletRequest request, @PathVariable Integer id, @RequestBody Map<String, String> body) {
        
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            String status = body.get("status");
            if (status == null || (!status.equals("in_stock") && !status.equals("out_of_stock"))) {
                return Result.error(400, "无效的状态值");
            }

            int rows = productMapper.updateStatus(id, status);
            if (rows > 0) {
                return Result.success("状态更新成功");
            }
            return Result.error(500, "状态更新失败");
        } catch (Exception e) {
            return Result.error(500, "状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            return "admin".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}