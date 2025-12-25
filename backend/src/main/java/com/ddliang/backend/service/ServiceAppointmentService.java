// ServiceAppointmentService.java
package com.ddliang.backend.service;

import com.ddliang.backend.dto.ServiceAppointmentRequest;
import com.ddliang.backend.dto.ServiceAppointmentResponse;
import com.ddliang.backend.entity.ServiceAppointment;
import java.util.List;

public interface ServiceAppointmentService {

    ServiceAppointment createAppointment(Integer userId, ServiceAppointmentRequest request);

    List<ServiceAppointmentResponse> getUserAppointments(Integer userId, String status);

    ServiceAppointmentResponse getAppointmentDetails(Integer id, Integer userId);

    void cancelAppointment(Integer id, Integer userId, String reason);

    void deleteAppointment(Integer id, Integer userId);

    List<String> getAvailableTimeSlots(String date);

    boolean isTimeSlotAvailable(String date, String timeSlot);
}