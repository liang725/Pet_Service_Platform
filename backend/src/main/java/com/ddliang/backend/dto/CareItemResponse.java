package com.ddliang.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CareItemResponse {
    private Integer id;
    private String careName;
    private String careCategory;
    private String description;
    private BigDecimal unitPrice;
    private Boolean isSinglePurchase;
    private Integer sortOrder;
}