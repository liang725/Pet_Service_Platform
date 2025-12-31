// ServiceAppointmentServiceImpl.java - 完整修复版
package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.ServiceAppointmentRequest;
import com.ddliang.backend.dto.ServiceAppointmentResponse;
import com.ddliang.backend.entity.ServiceAppointment;
import com.ddliang.backend.entity.ServiceItem;
import com.ddliang.backend.entity.ServiceOrderItem;
import com.ddliang.backend.mapper.*;
import com.ddliang.backend.service.ServiceAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ServiceAppointmentServiceImpl implements ServiceAppointmentService {

    @Autowired
    private ServiceAppointmentMapper appointmentMapper;

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private ServiceOrderItemMapper orderItemMapper;

    @Autowired
    private ServiceCareItemsMapper careItemsMapper;

    @Autowired
    private PetMapper petMapper;

    private final String[] TIME_SLOTS = {"09:00", "10:30", "14:00", "15:30", "17:00"};
    private final Random random = new Random();
    private final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional
    public ServiceAppointment createAppointment(Integer userId, ServiceAppointmentRequest request) {
        // 验证时间是否可用
        if (!isTimeSlotAvailable(request.getAppointmentDate().toString(), request.getTimeSlot())) {
            throw new RuntimeException("该时间段已被预约，请选择其他时间");
        }

        // 验证宠物是否存在且属于该用户
        var pet = petMapper.findById(request.getPetId());
        if (pet == null || !pet.getUserId().equals(userId)) {
            throw new RuntimeException("宠物不存在或无权限");
        }

        // 获取服务信息
        ServiceItem serviceItem = serviceItemMapper.findById(request.getServiceId());
        if (serviceItem == null) {
            throw new RuntimeException("服务项目不存在");
        }

        // 计算金额
        BigDecimal itemsBaseTotal = serviceItem.getBasePrice();
        BigDecimal itemsDiscountTotal = BigDecimal.ZERO;
        BigDecimal careItemsTotal = BigDecimal.ZERO;

        // 计算折扣
        if (serviceItem.getIsDiscount() && serviceItem.getDiscountPercentage() != null) {
            itemsDiscountTotal = serviceItem.getBasePrice()
                    .multiply(serviceItem.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        }

        // 计算护理项费用（如果选择了护理项）
        if (request.getCareItems() != null && !request.getCareItems().isEmpty()) {
            for (Integer careItemId : request.getCareItems()) {
                var careItem = careItemsMapper.findById(careItemId);
                if (careItem != null) {
                    careItemsTotal = careItemsTotal.add(careItem.getUnitPrice());
                }
            }
        }

        BigDecimal orderTotalAmount = itemsBaseTotal
                .subtract(itemsDiscountTotal)
                .add(careItemsTotal);

        // 创建预约订单
        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setOrderNo(generateOrderNo());
        appointment.setUserId(userId);
        appointment.setPetId(request.getPetId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setItemsBaseTotal(itemsBaseTotal);
        appointment.setItemsDiscountTotal(itemsDiscountTotal);
        appointment.setCareItemsTotal(careItemsTotal);
        appointment.setOrderTotalAmount(orderTotalAmount);
        appointment.setSpecialNotes(request.getSpecialNotes());
        appointment.setStatus("pending");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        // 保存预约
        int result = appointmentMapper.insert(appointment);
        if (result == 0) {
            throw new RuntimeException("创建预约失败");
        }

        // 创建订单项目
        ServiceOrderItem orderItem = new ServiceOrderItem();
        orderItem.setOrderId(appointment.getId());
        orderItem.setServiceId(request.getServiceId());
        orderItem.setItemBasePrice(serviceItem.getBasePrice());
        orderItem.setItemDiscountPercentage(serviceItem.getDiscountPercentage());
        orderItem.setItemDiscountAmount(itemsDiscountTotal);
        orderItem.setItemFinalPrice(serviceItem.getFinalPrice());
        orderItem.setItemTotalAmount(orderTotalAmount);

        // 保存护理项选择
        if (request.getCareItems() != null && !request.getCareItems().isEmpty()) {
            StringBuilder careItemsJson = new StringBuilder("{\"care_items\": [");
            for (int i = 0; i < request.getCareItems().size(); i++) {
                if (i > 0) careItemsJson.append(", ");
                careItemsJson.append(request.getCareItems().get(i));
            }
            careItemsJson.append("]}");
            orderItem.setCareItemsSelected(careItemsJson.toString());
        }

        orderItem.setCareItemsTotal(careItemsTotal);
        orderItem.setCreatedAt(LocalDateTime.now());

        orderItemMapper.insert(orderItem);

        return appointment;
    }

    @Override
    public List<ServiceAppointmentResponse> getUserAppointments(Integer userId, String status) {
        return appointmentMapper.findAppointmentsWithDetails(userId, status);
    }

    @Override
    public ServiceAppointmentResponse getAppointmentDetails(Integer id, Integer userId) {
        ServiceAppointmentResponse appointment = appointmentMapper.findByIdAndUserId(id, userId);
        if (appointment == null) {
            throw new RuntimeException("预约记录不存在或无权限查看");
        }
        return appointment;
    }

    @Override
    public void cancelAppointment(Integer id, Integer userId, String reason) {
        int result = appointmentMapper.updateStatus(id, userId, "cancelled", reason);
        if (result == 0) {
            throw new RuntimeException("取消预约失败，预约记录不存在或无权限");
        }
    }

    @Override
    public void deleteAppointment(Integer id, Integer userId) {
        int result = appointmentMapper.deleteByIdAndUserId(id, userId);
        if (result == 0) {
            throw new RuntimeException("删除预约失败，预约记录不存在或无权限");
        }
    }

    @Override
    public List<String> getAvailableTimeSlots(String date) {
        LocalDate appointmentDate = LocalDate.parse(date);
        List<String> availableSlots = new java.util.ArrayList<>();

        for (String timeSlot : TIME_SLOTS) {
            int count = appointmentMapper.countByDateAndTimeSlot(appointmentDate, timeSlot);
            // 假设每个时间段最多接受3个预约
            if (count < 3) {
                availableSlots.add(timeSlot);
            }
        }

        return availableSlots;
    }

    @Override
    public boolean isTimeSlotAvailable(String date, String timeSlot) {
        LocalDate appointmentDate = LocalDate.parse(date);
        int count = appointmentMapper.countByDateAndTimeSlot(appointmentDate, timeSlot);
        return count < 3; // 最多接受3个预约
    }

    // 新增方法：获取所有预约（不按状态过滤）
    public List<ServiceAppointmentResponse> getAllUserAppointments(Integer userId) {
        return appointmentMapper.findAppointmentsWithDetails(userId, null);
    }

    // 新增方法：获取预约统计
    public Map<String, Integer> getAppointmentStats(Integer userId) {
        List<Map<String, Object>> stats = appointmentMapper.countAppointmentsByStatus(userId);
        Map<String, Integer> result = new HashMap<>();

        // 初始化所有状态
        result.put("pending", 0);
        result.put("confirmed", 0);
        result.put("completed", 0);
        result.put("cancelled", 0);
        result.put("all", 0);

        for (Map<String, Object> stat : stats) {
            String status = (String) stat.get("status");
            Long count = (Long) stat.get("count");
            result.put(status, count != null ? count.intValue() : 0);
        }

        // 计算总数
        int total = appointmentMapper.countAllByUserId(userId);
        result.put("all", total);

        return result;
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(ORDER_NO_FORMATTER);
        String randomPart = String.format("%04d", random.nextInt(10000));
        return "ORD-" + datePart + "-" + randomPart;
    }
}