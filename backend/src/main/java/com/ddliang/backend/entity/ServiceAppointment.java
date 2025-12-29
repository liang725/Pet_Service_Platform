// ServiceAppointment.java
package com.ddliang.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ServiceAppointment {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer petId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private BigDecimal itemsBaseTotal = BigDecimal.ZERO;
    private BigDecimal itemsDiscountTotal = BigDecimal.ZERO;
    private BigDecimal careItemsTotal = BigDecimal.ZERO;
    private BigDecimal orderTotalAmount = BigDecimal.ZERO;
    private String specialNotes;
    private String status = "pending";
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}