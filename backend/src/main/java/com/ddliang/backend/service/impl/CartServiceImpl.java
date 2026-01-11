package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.CartItemRequest;
import com.ddliang.backend.dto.CartItemResponse;
import com.ddliang.backend.entity.CartItem;
import com.ddliang.backend.entity.Product;
import com.ddliang.backend.mapper.CartMapper;
import com.ddliang.backend.mapper.ProductMapper;
import com.ddliang.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    
    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartMapper.findByUserId(userId);
        return cartItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CartItemResponse addToCart(Long userId, CartItemRequest request) {
        // 添加调试日志
        System.out.println("添加购物车请求 - userId: " + userId + ", productId: " + request.getProductId() + ", spec: " + request.getSpec() + ", quantity: " + request.getQuantity());
        
        // 验证请求参数
        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new RuntimeException("商品ID无效");
        }
        
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("商品数量必须大于0");
        }
        
        // 检查商品是否存在
        Product product = productMapper.findById(request.getProductId());
        if (product == null) {
            System.out.println("商品不存在 - productId: " + request.getProductId());
            throw new RuntimeException("商品不存在，商品ID: " + request.getProductId());
        }
        
        System.out.println("找到商品 - id: " + product.getId() + ", name: " + product.getName() + ", price: " + product.getPrice());
        
        // 检查商品是否可购买（可以添加库存检查等）
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("商品价格异常，无法添加到购物车");
        }
        
        // 设置默认规格
        String spec = request.getSpec();
        if (spec == null || spec.trim().isEmpty()) {
            spec = "标准装";
        }
        
        // 检查是否已经在购物车中
        CartItem existingItem = cartMapper.findByUserIdAndProductIdAndSpec(
                userId, request.getProductId(), spec);
        
        if (existingItem != null) {
            System.out.println("商品已在购物车中，更新数量 - 原数量: " + existingItem.getQuantity() + ", 新增数量: " + request.getQuantity());
            // 更新数量
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            
            // 检查数量限制（例如最多10件）
            if (newQuantity > 10) {
                throw new RuntimeException("该商品在购物车中的数量不能超过10件");
            }
            
            cartMapper.updateQuantity(existingItem.getId(), newQuantity);
            existingItem.setQuantity(newQuantity);
            System.out.println("购物车商品数量更新成功 - 新数量: " + newQuantity);
            return convertToResponse(existingItem);
        } else {
            System.out.println("新增购物车项");
            // 新增购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setProductImage(product.getImageUrl());
            cartItem.setSpec(spec);
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(request.getQuantity());
            
            try {
                cartMapper.insert(cartItem);
                System.out.println("购物车项添加成功 - id: " + cartItem.getId());
                return convertToResponse(cartItem);
            } catch (Exception e) {
                System.err.println("插入购物车项失败: " + e.getMessage());
                throw new RuntimeException("添加到购物车失败，请稍后重试");
            }
        }
    }
    
    @Override
    @Transactional
    public CartItemResponse updateCartItem(Long userId, Long itemId, Integer quantity) {
        CartItem cartItem = cartMapper.findByIdAndUserId(itemId, userId);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }
        
        if (quantity <= 0) {
            throw new RuntimeException("数量必须大于0");
        }
        
        cartMapper.updateQuantity(itemId, quantity);
        cartItem.setQuantity(quantity);
        return convertToResponse(cartItem);
    }
    
    @Override
    @Transactional
    public void removeCartItem(Long userId, Long itemId) {
        int deleted = cartMapper.deleteByIdAndUserId(itemId, userId);
        if (deleted == 0) {
            throw new RuntimeException("购物车项不存在");
        }
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
    
    private CartItemResponse convertToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProductId());
        response.setName(cartItem.getProductName());
        response.setImage(cartItem.getProductImage());
        response.setSpec(cartItem.getSpec());
        response.setPrice(cartItem.getPrice());
        response.setQuantity(cartItem.getQuantity());
        return response;
    }
}
