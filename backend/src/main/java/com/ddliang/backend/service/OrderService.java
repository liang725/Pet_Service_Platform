package com.ddliang.backend.service;

import com.ddliang.backend.dto.CreateOrderRequest;
import com.ddliang.backend.dto.OrderResponse;
import com.ddliang.backend.entity.Order;

import java.util.List;

public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderResponse createOrder(Long userId, CreateOrderRequest request);
    
    /**
     * 根据用户ID获取订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 根据订单ID获取订单详情
     */
    Order getOrderById(Long orderId);
    
    /**
     * 根据订单号获取订单详情
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 确认收货
     */
    boolean confirmDelivery(Long orderId, Long userId);
}