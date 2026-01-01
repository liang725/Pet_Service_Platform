package com.ddliang.backend.controller;

import com.ddliang.backend.dto.CreateOrderRequest;
import com.ddliang.backend.dto.OrderResponse;
import com.ddliang.backend.entity.Order;
import com.ddliang.backend.service.OrderService;
import com.ddliang.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 测试接口 - 不需要认证
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testOrders() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "订单接口测试成功");
        response.put("data", "Hello from Orders API");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestAttribute("userId") Long userId,
            @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("创建订单，用户ID: " + userId);
            System.out.println("订单商品数量: " + (request.getItems() != null ? request.getItems().size() : 0));
            
            OrderResponse orderResponse = orderService.createOrder(userId, request);
            
            response.put("success", true);
            response.put("code", 200);
            response.put("data", orderResponse);
            response.put("message", "订单创建成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("创建订单失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "创建订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserOrders(
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("获取用户订单列表，用户ID: " + userId);
            
            List<Order> orders = orderService.getOrdersByUserId(userId);
            
            System.out.println("查询到订单数量: " + (orders != null ? orders.size() : 0));
            
            // 确保返回格式与前端期望一致
            response.put("success", true);
            response.put("code", 200);
            response.put("data", orders);
            response.put("message", "获取订单列表成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("获取订单列表失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "获取订单列表失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || jwtUtil.isTokenExpired(token)) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                return ResponseEntity.status(401).body(response);
            }
            
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                response.put("success", false);
                response.put("message", "订单不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取订单详情失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @RequestAttribute("userId") Long userId,
            @PathVariable String orderId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            
            System.out.println("取消订单请求，订单标识: " + orderId + ", 用户ID: " + userId);
            
            // 查找订单 - 优先按订单号查找
            Order order = orderService.getOrderByOrderNo(orderId);
            if (order == null) {
                try {
                    Long orderIdLong = Long.parseLong(orderId);
                    order = orderService.getOrderById(orderIdLong);
                } catch (NumberFormatException e) {
                    System.err.println("订单标识格式错误: " + orderId);
                }
            }
            
            if (order == null) {
                System.err.println("订单不存在，订单标识: " + orderId);
                response.put("success", false);
                response.put("message", "订单不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            System.out.println("找到订单，数据库ID: " + order.getId() + ", 订单号: " + order.getOrderNo() + ", 状态: " + order.getStatus());
            
            boolean success = orderService.cancelOrder(order.getId(), userId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "订单取消成功");
            } else {
                response.put("success", false);
                response.put("message", "订单取消失败，请检查订单状态");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("取消订单失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "取消订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(
            @RequestAttribute("userId") Long userId,
            @PathVariable String orderId,
            @RequestBody Map<String, String> paymentData,
            HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            
            String paymentMethod = paymentData.get("paymentMethod");
            System.out.println("支付订单请求，订单标识: " + orderId + ", 支付方式: " + paymentMethod);
            
            // 查找订单 - 优先按订单号查找，如果不存在再按ID查找
            Order order = orderService.getOrderByOrderNo(orderId);
            if (order == null) {
                try {
                    Long orderIdLong = Long.parseLong(orderId);
                    order = orderService.getOrderById(orderIdLong);
                } catch (NumberFormatException e) {
                    System.err.println("订单标识格式错误: " + orderId);
                }
            }
            
            if (order == null) {
                System.err.println("订单不存在，订单标识: " + orderId);
                response.put("success", false);
                response.put("message", "订单不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            System.out.println("找到订单，数据库ID: " + order.getId() + ", 订单号: " + order.getOrderNo() + ", 状态: " + order.getStatus());
            
            if (!"pending".equals(order.getStatus())) {
                response.put("success", false);
                response.put("message", "订单状态不允许支付，当前状态: " + order.getStatus());
                return ResponseEntity.status(400).body(response);
            }
            
            // 模拟支付处理
            boolean paymentSuccess = orderService.processPayment(order.getId(), paymentMethod);
            
            if (paymentSuccess) {
                response.put("success", true);
                response.put("message", "支付成功");
                response.put("data", Map.of(
                    "orderId", order.getOrderNo(),
                    "paymentMethod", paymentMethod,
                    "amount", order.getFinalAmount(),
                    "status", "paid"
                ));
            } else {
                response.put("success", false);
                response.put("message", "支付失败，请重试");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("支付订单失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "支付失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取订单支付状态
     */
    @GetMapping("/{orderId}/payment-status")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(
            @PathVariable String orderId,
            HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 查找订单
            Order order = null;
            try {
                Long orderIdLong = Long.parseLong(orderId);
                order = orderService.getOrderById(orderIdLong);
            } catch (NumberFormatException e) {
                order = orderService.getOrderByOrderNo(orderId);
            }
            
            if (order == null) {
                response.put("success", false);
                response.put("message", "订单不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("success", true);
            response.put("data", Map.of(
                "orderId", order.getOrderNo(),
                "status", order.getStatus(),
                "paymentMethod", order.getPaymentMethod() != null ? order.getPaymentMethod() : "",
                "paymentTime", order.getPaymentTime() != null ? order.getPaymentTime().toString() : ""
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("获取支付状态失败: " + e.getMessage());
            response.put("success", false);
            response.put("message", "获取支付状态失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmDelivery(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || jwtUtil.isTokenExpired(token)) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userIdInt = jwtUtil.getUserIdFromToken(token);
            Long userId = userIdInt.longValue();
            boolean success = orderService.confirmDelivery(orderId, userId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "确认收货成功");
            } else {
                response.put("success", false);
                response.put("message", "确认收货失败，请检查订单状态");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "确认收货失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}