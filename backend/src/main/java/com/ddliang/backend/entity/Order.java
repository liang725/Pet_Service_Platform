package com.ddliang.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal couponDiscount;
    private BigDecimal memberDiscount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String deliveryMethod;
    private String couponCode;
    private Long addressId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shippingTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;
    
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 订单商品列表
    private List<OrderItem> items;
    
    // 订单摘要信息
    private OrderSummary summary;
    
    @Data
    public static class OrderSummary {
        private BigDecimal subtotal;
        private BigDecimal shipping;
        private BigDecimal coupon;
        private BigDecimal discount;
        private BigDecimal total;
    }
}