package com.ddliang.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceOrderItem {
    private Integer id;
    private Integer orderId;
    private Integer serviceId;
    private BigDecimal itemBasePrice = BigDecimal.ZERO;
    private BigDecimal itemDiscountPercentage;
    private BigDecimal itemDiscountAmount = BigDecimal.ZERO;
    private BigDecimal itemFinalPrice = BigDecimal.ZERO;
    private String careItemsSelected;
    private BigDecimal careItemsTotal = BigDecimal.ZERO;
    private BigDecimal itemTotalAmount = BigDecimal.ZERO;
    private Integer quantity = 1;
    private LocalDateTime createdAt;
}