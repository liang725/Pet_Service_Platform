package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.entity.Product;
import com.ddliang.backend.entity.User;
import com.ddliang.backend.mapper.ProductMapper;
import com.ddliang.backend.service.UserService;
import com.ddliang.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取管理员仪表板数据
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboardData(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("todayAppointments", 128);
        data.put("totalPets", 542);
        data.put("todayIncome", 15680);
        data.put("totalUsers", 3245);
        data.put("pendingOrders", 5);
        data.put("pendingReviews", 3);
        data.put("totalProducts", productMapper.countAllProducts());

        return Result.success(data);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<?> getUserList(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }
        List<User> userList = List.of();
        return Result.success(userList);
    }

    // 商品管理相关接口已删除，准备重新构建

    /**
     * 获取商品列表（分页+搜索）
     */
    @GetMapping("/products")
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
    @GetMapping("/products/{id}")
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
     * 删除商品
     */
    @DeleteMapping("/products/{id}")
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
    @PutMapping("/products/{id}/status")
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
