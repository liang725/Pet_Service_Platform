// ServiceCareItems.java
package com.ddliang.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceCareItems {
    private Integer id;
    private String careName;
    private String careCategory; // 局部护理、深度护理、精致修剪、赠送护理
    private String description;
    private BigDecimal unitPrice;
    private Boolean isSinglePurchase;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}