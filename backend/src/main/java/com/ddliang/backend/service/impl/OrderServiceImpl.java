package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.CreateOrderRequest;
import com.ddliang.backend.dto.OrderResponse;
import com.ddliang.backend.entity.Order;
import com.ddliang.backend.entity.OrderItem;
import com.ddliang.backend.mapper.OrderMapper;
import com.ddliang.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        try {
            // 生成订单号
            String orderNo = "PET" + System.currentTimeMillis();
            
            // 计算配送费
            BigDecimal deliveryFee = "express".equals(request.getDeliveryMethod()) ? 
                new BigDecimal("15.00") : BigDecimal.ZERO;
            
            // 计算优惠折扣
            BigDecimal couponDiscount = BigDecimal.ZERO;
            if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
                // 简单的优惠券逻辑
                switch (request.getCouponCode()) {
                    case "新用户优惠券":
                        couponDiscount = new BigDecimal("10.00");
                        break;
                    case "满减优惠券":
                        couponDiscount = new BigDecimal("20.00");
                        break;
                    case "VIP专享券":
                        couponDiscount = new BigDecimal("50.00");
                        break;
                }
            }
            
            // 计算商品总价
            BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算最终金额
            BigDecimal finalAmount = subtotal.add(deliveryFee).subtract(couponDiscount);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
            
            // 创建订单对象
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderNo(orderNo);
            order.setStatus("pending");
            order.setTotalAmount(subtotal);
            order.setShippingFee(deliveryFee);
            order.setCouponDiscount(couponDiscount);
            order.setMemberDiscount(BigDecimal.ZERO);
            order.setFinalAmount(finalAmount);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            
            // 保存订单（这里需要实现orderMapper.insert方法）
            // orderMapper.insert(order);
            
            // 构建返回对象
            OrderResponse response = new OrderResponse();
            response.setOrderId(orderNo);
            response.setOrderNo(orderNo);
            response.setStatus("pending");
            response.setTotalAmount(subtotal);
            response.setFinalAmount(finalAmount);
            response.setCreatedAt(LocalDateTime.now());
            response.setDeliveryMethod(request.getDeliveryMethod());
            response.setCouponCode(request.getCouponCode());
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("创建订单失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        
        // 为每个订单填充商品列表和摘要信息
        for (Order order : orders) {
            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(order.getId());
            order.setItems(items);
            
            // 构建订单摘要
            Order.OrderSummary summary = new Order.OrderSummary();
            summary.setSubtotal(order.getTotalAmount());
            summary.setShipping(order.getShippingFee());
            summary.setCoupon(order.getCouponDiscount());
            summary.setDiscount(order.getMemberDiscount());
            summary.setTotal(order.getFinalAmount());
            order.setSummary(summary);
        }
        
        return orders;
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order != null) {
            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(orderId);
            order.setItems(items);
            
            // 构建订单摘要
            Order.OrderSummary summary = new Order.OrderSummary();
            summary.setSubtotal(order.getTotalAmount());
            summary.setShipping(order.getShippingFee());
            summary.setCoupon(order.getCouponDiscount());
            summary.setDiscount(order.getMemberDiscount());
            summary.setTotal(order.getFinalAmount());
            order.setSummary(summary);
        }
        return order;
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(order.getId());
            order.setItems(items);
            
            // 构建订单摘要
            Order.OrderSummary summary = new Order.OrderSummary();
            summary.setSubtotal(order.getTotalAmount());
            summary.setShipping(order.getShippingFee());
            summary.setCoupon(order.getCouponDiscount());
            summary.setDiscount(order.getMemberDiscount());
            summary.setTotal(order.getFinalAmount());
            order.setSummary(summary);
        }
        return order;
    }
    
    @Override
    public boolean cancelOrder(Long orderId, Long userId) {
        // 验证订单是否属于当前用户
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有待支付状态的订单可以取消
        if (!"pending".equals(order.getStatus())) {
            return false;
        }
        
        return orderMapper.updateOrderStatus(orderId, "cancelled") > 0;
    }
    
    @Override
    public boolean confirmDelivery(Long orderId, Long userId) {
        // 验证订单是否属于当前用户
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已发货状态的订单可以确认收货
        if (!"shipped".equals(order.getStatus())) {
            return false;
        }
        
        return orderMapper.updateOrderStatus(orderId, "delivered") > 0;
    }
}