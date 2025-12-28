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
        // 检查商品是否存在
        Product product = productMapper.findById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查是否已经在购物车中
        CartItem existingItem = cartMapper.findByUserIdAndProductIdAndSpec(
                userId, request.getProductId(), request.getSpec());
        
        if (existingItem != null) {
            // 更新数量
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            cartMapper.updateQuantity(existingItem.getId(), newQuantity);
            existingItem.setQuantity(newQuantity);
            return convertToResponse(existingItem);
        } else {
            // 新增购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setProductImage(product.getImageUrl());
            cartItem.setSpec(request.getSpec());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(request.getQuantity());
            
            cartMapper.insert(cartItem);
            return convertToResponse(cartItem);
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
