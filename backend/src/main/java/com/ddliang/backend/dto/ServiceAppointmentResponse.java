package com.ddliang.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class ServiceAppointmentResponse {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer petId;
    private String petName;
    private String petType;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String serviceName;
    private BigDecimal orderTotalAmount;
    private String specialNotes;
    private String status;
    private LocalDateTime createdAt;
    private String statusText;
}