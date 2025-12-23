package com.ddliang.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceItem {
    private Integer id;
    private String category;
    private String itemName;
    private String briefDesc;
    private BigDecimal basePrice;
    private Boolean isDiscount;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private Boolean isRecommended;
    private Integer duration;
    private Boolean isSinglePurchase;
    private String thumbnailUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}