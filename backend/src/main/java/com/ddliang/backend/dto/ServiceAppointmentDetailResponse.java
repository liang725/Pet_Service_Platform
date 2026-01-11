package com.ddliang.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ServiceAppointmentDetailResponse {
    private Integer id;
    private String orderNo;
    private Integer userId;

    // 宠物信息
    private Integer petId;
    private String petName;
    private String petType;
    private String breed;
    private String personality;

    // 预约时间
    private LocalDate appointmentDate;
    private String timeSlot;

    // 服务项目（洗护项目）
    private List<ServiceOrderItemDetail> services;

    // 护理项目
    private List<CareItemDetail> careItems;

    // 价格信息
    private BigDecimal itemsBaseTotal;
    private BigDecimal itemsDiscountTotal;
    private BigDecimal careItemsTotal;
    private BigDecimal orderTotalAmount;

    // 其他信息
    private String specialNotes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cancellationReason;

    @Data
    public static class ServiceOrderItemDetail {
        private Integer serviceId;
        private String serviceName;
        private String category;
        private String description;
        private BigDecimal basePrice;
        private BigDecimal discountPercentage;
        private BigDecimal discountAmount;
        private BigDecimal finalPrice;
        private Integer duration;
        private Integer quantity = 1;
        private String thumbnailUrl;
    }

    @Data
    public static class CareItemDetail {
        private Integer id;
        private String careName;
        private String careCategory;
        private String description;
        private BigDecimal unitPrice;
    }
}