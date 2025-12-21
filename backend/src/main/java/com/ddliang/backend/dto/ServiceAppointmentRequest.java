// ServiceAppointmentRequest.java
package com.ddliang.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class ServiceAppointmentRequest {
    @NotNull(message = "宠物ID不能为空")
    private Integer petId;

    @NotNull(message = "服务ID不能为空")
    private Integer serviceId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    @NotNull(message = "预约时间段不能为空")
    private String timeSlot;

    private String specialNotes;

    private List<Integer> careItems; // 护理项ID列表
}