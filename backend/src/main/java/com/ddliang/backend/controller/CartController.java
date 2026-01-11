package com.ddliang.backend.controller;

import com.ddliang.backend.dto.CartItemRequest;
import com.ddliang.backend.dto.CartItemResponse;
import com.ddliang.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCartItems(@RequestAttribute("userId") Integer userIdInt) {
        Long userId = userIdInt.longValue();
        List<CartItemResponse> items = cartService.getCartItems(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("items", items);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestAttribute("userId") Integer userIdInt,
            @Valid @RequestBody CartItemRequest request) {
        try {
            Long userId = userIdInt.longValue();
            System.out.println("CartController - 接收到添加购物车请求: userId=" + userId + ", request=" + request);
            CartItemResponse item = cartService.addToCart(userId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "添加成功");
            response.put("data", item);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("CartController - 添加购物车失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PutMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestAttribute("userId") Integer userIdInt,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        Long userId = userIdInt.longValue();
        Integer quantity = body.get("quantity");
        if (quantity == null) {
            throw new RuntimeException("数量不能为空");
        }
        CartItemResponse item = cartService.updateCartItem(userId, itemId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", item);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestAttribute("userId") Integer userIdInt,
            @PathVariable Long itemId) {
        Long userId = userIdInt.longValue();
        cartService.removeCartItem(userId, itemId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearCart(@RequestAttribute("userId") Integer userIdInt) {
        Long userId = userIdInt.longValue();
        cartService.clearCart(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "清空成功");
        return ResponseEntity.ok(response);
    }
}
