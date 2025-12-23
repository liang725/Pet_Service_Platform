package com.ddliang.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceItemResponse {
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
    private String thumbnailUrl;
}