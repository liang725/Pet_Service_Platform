package com.ddliang.backend.controller;

import com.ddliang.backend.entity.Address;
import com.ddliang.backend.dto.AddressRequest;
import com.ddliang.backend.service.AddressService;
import com.ddliang.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取用户地址列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserAddresses(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取用户ID（临时跳过JWT验证）
            Long userId = 1L; // 临时使用固定用户ID
            
            List<Address> addresses = addressService.getUserAddresses(userId);
            
            response.put("success", true);
            response.put("data", addresses);
            response.put("message", "获取地址列表成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取地址列表失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 添加地址
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addAddress(
            @Valid @RequestBody AddressRequest addressRequest,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取用户ID（临时跳过JWT验证）
            Long userId = 1L; // 临时使用固定用户ID
            
            Address address = addressService.addAddress(userId, addressRequest);
            
            response.put("success", true);
            response.put("data", address);
            response.put("message", "添加地址成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加地址失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新地址
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest addressRequest,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取用户ID（临时跳过JWT验证）
            Long userId = 1L; // 临时使用固定用户ID
            
            Address address = addressService.updateAddress(id, userId, addressRequest);
            
            response.put("success", true);
            response.put("data", address);
            response.put("message", "更新地址成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新地址失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable Long id,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取用户ID（临时跳过JWT验证）
            Long userId = 1L; // 临时使用固定用户ID
            
            boolean success = addressService.deleteAddress(id, userId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "删除地址成功");
            } else {
                response.put("success", false);
                response.put("message", "删除地址失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除地址失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @PathVariable Long id,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取用户ID（临时跳过JWT验证）
            Long userId = 1L; // 临时使用固定用户ID
            
            boolean success = addressService.setDefaultAddress(id, userId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "设置默认地址成功");
            } else {
                response.put("success", false);
                response.put("message", "设置默认地址失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "设置默认地址失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> getDefaultAddress(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取用户ID（临时跳过JWT验证）
            Long userId = 1L; // 临时使用固定用户ID
            
            Address address = addressService.getDefaultAddress(userId);
            
            response.put("success", true);
            response.put("data", address);
            response.put("message", "获取默认地址成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取默认地址失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}