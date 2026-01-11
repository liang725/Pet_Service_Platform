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
     * 处理支付
     */
    boolean processPayment(Long orderId, String paymentMethod);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 确认收货
     */
    boolean confirmDelivery(Long orderId, Long userId);
    
    /**
     * 删除订单（仅限已取消的订单）
     */
    boolean deleteOrder(Long orderId, Long userId);
    
    /**
     * 批量删除用户的所有已取消订单（清空回收站）
     */
    int clearCancelledOrders(Long userId);
    
    /**
     * 清空用户的所有订单
     */
    int clearAllOrders(Long userId);
}