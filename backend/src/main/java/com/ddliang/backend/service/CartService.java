package com.ddliang.backend.service;

import com.ddliang.backend.dto.CartItemRequest;
import com.ddliang.backend.dto.CartItemResponse;

import java.util.List;

public interface CartService {
    List<CartItemResponse> getCartItems(Long userId);
    CartItemResponse addToCart(Long userId, CartItemRequest request);
    CartItemResponse updateCartItem(Long userId, Long itemId, Integer quantity);
    void removeCartItem(Long userId, Long itemId);
    void clearCart(Long userId);
}
