package com.ddliang.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Integer productId;
    private String name;
    private String image;
    private String spec;
    private BigDecimal price;
    private Integer quantity;
}
