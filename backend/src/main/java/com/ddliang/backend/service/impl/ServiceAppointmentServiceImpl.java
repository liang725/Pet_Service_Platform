// ServiceAppointmentServiceImpl.java - 简化版
package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.ServiceAppointmentDetailResponse;
import com.ddliang.backend.dto.ServiceAppointmentRequest;
import com.ddliang.backend.dto.ServiceAppointmentResponse;
import com.ddliang.backend.entity.ServiceAppointment;
import com.ddliang.backend.entity.ServiceItem;
import com.ddliang.backend.entity.ServiceOrderItem;
import com.ddliang.backend.entity.ServiceCareItems;
import com.ddliang.backend.entity.Pet;
import com.ddliang.backend.mapper.*;
import com.ddliang.backend.service.ServiceAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 新增：获取预约详情（包含服务项目和护理项目）
     * 修改：支持管理员查看任意用户的预约（当userId为null时）
     */
    public ServiceAppointmentDetailResponse getAppointmentDetail(Integer id, Integer userId) {
        // 1. 获取基础预约信息
        ServiceAppointment appointment;
        if (userId != null) {
            // 普通用户查看自己的预约，需要权限验证
            appointment = appointmentMapper.findAppointmentByIdAndUserId(id, userId);
            if (appointment == null) {
                throw new RuntimeException("预约记录不存在或无权限查看");
            }
        } else {
            // 管理员查看任意预约，不需要用户权限验证
            appointment = appointmentMapper.findById(id);
            if (appointment == null) {
                throw new RuntimeException("预约记录不存在");
            }
        }

        // 2. 获取宠物信息
        Pet pet = petMapper.findById(appointment.getPetId());

        // 3. 创建响应对象
        ServiceAppointmentDetailResponse response = new ServiceAppointmentDetailResponse();
        response.setId(appointment.getId());
        response.setOrderNo(appointment.getOrderNo());
        response.setUserId(appointment.getUserId());
        response.setPetId(appointment.getPetId());

        // 设置宠物信息
        if (pet != null) {
            response.setPetName(pet.getName());
            response.setPetType(pet.getPetType());
            response.setBreed(pet.getBreed());
            response.setPersonality(pet.getPersonality());
        }

        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setTimeSlot(appointment.getTimeSlot());
        response.setItemsBaseTotal(appointment.getItemsBaseTotal());
        response.setItemsDiscountTotal(appointment.getItemsDiscountTotal());
        response.setCareItemsTotal(appointment.getCareItemsTotal());
        response.setOrderTotalAmount(appointment.getOrderTotalAmount());
        response.setSpecialNotes(appointment.getSpecialNotes());
        response.setStatus(appointment.getStatus());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        response.setCancellationReason(appointment.getCancellationReason());

        // 4. 获取服务项目（洗护项目）
        List<ServiceAppointmentDetailResponse.ServiceOrderItemDetail> serviceDetails = new ArrayList<>();

        // 使用可靠的旧方法获取订单项目
        ServiceOrderItem orderItem = orderItemMapper.findByOrderId(id);
        if (orderItem != null) {
            ServiceItem serviceItem = serviceItemMapper.findById(orderItem.getServiceId());
            if (serviceItem != null) {
                ServiceAppointmentDetailResponse.ServiceOrderItemDetail serviceDetail =
                        new ServiceAppointmentDetailResponse.ServiceOrderItemDetail();

                serviceDetail.setServiceId(serviceItem.getId());
                serviceDetail.setServiceName(serviceItem.getItemName());
                serviceDetail.setCategory(serviceItem.getCategory());
                serviceDetail.setDescription(serviceItem.getBriefDesc());
                serviceDetail.setBasePrice(serviceItem.getBasePrice());
                serviceDetail.setDiscountPercentage(serviceItem.getDiscountPercentage());
                serviceDetail.setDiscountAmount(orderItem.getItemDiscountAmount());
                serviceDetail.setFinalPrice(serviceItem.getFinalPrice());
                serviceDetail.setDuration(serviceItem.getDuration());
                serviceDetail.setQuantity(orderItem.getQuantity());
                serviceDetail.setThumbnailUrl(serviceItem.getThumbnailUrl());

                serviceDetails.add(serviceDetail);
            }
        }

        response.setServices(serviceDetails);

        // 5. 获取护理项目
        List<ServiceAppointmentDetailResponse.CareItemDetail> careItemDetails = new ArrayList<>();

        if (orderItem != null && orderItem.getCareItemsSelected() != null &&
                !orderItem.getCareItemsSelected().isEmpty()) {

            // 解析护理项JSON，支持多种格式
            String careItemsJson = orderItem.getCareItemsSelected();
            List<Integer> careItemIds = parseCareItemIds(careItemsJson);

            for (Integer careItemId : careItemIds) {
                try {
                    ServiceCareItems careItem = careItemsMapper.findById(careItemId);
                    if (careItem != null) {
                        ServiceAppointmentDetailResponse.CareItemDetail careDetail =
                                new ServiceAppointmentDetailResponse.CareItemDetail();

                        careDetail.setId(careItem.getId());
                        careDetail.setCareName(careItem.getCareName());
                        careDetail.setCareCategory(careItem.getCareCategory());
                        careDetail.setDescription(careItem.getDescription());
                        careDetail.setUnitPrice(careItem.getUnitPrice());

                        careItemDetails.add(careDetail);
                    }
                } catch (Exception e) {
                    System.err.println("获取护理项失败 ID=" + careItemId + ": " + e.getMessage());
                }
            }
        }

        response.setCareItems(careItemDetails);

        return response;
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
        List<String> availableSlots = new ArrayList<>();

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

    // 新增方法：获取每日预约次数统计
    public List<Map<String, Object>> getDailyAppointmentCounts(Integer userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 直接调用Mapper方法查询数据库
        return appointmentMapper.findDailyAppointmentCounts(userId, start, end);
    }

    // 新增方法：获取指定日期的预约次数
    public int getAppointmentCountByDate(Integer userId, String date) {
        LocalDate appointmentDate = LocalDate.parse(date);

        // 直接调用Mapper方法查询数据库
        return appointmentMapper.countByUserIdAndDate(userId, appointmentDate);
    }

    // 辅助方法：计算一段时间内的每日预约次数（用于日历显示）
    public Map<String, Integer> getAppointmentCountsForCalendar(Integer userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 获取每日预约次数
        List<Map<String, Object>> dailyCounts = appointmentMapper.findDailyAppointmentCounts(userId, start, end);

        // 转换为Map便于查询
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Object> dailyCount : dailyCounts) {
            String date = dailyCount.get("appointment_date").toString();
            Long count = (Long) dailyCount.get("count");
            result.put(date, count != null ? count.intValue() : 0);
        }

        return result;
    }

    // 辅助方法：解析护理项ID
    private List<Integer> parseCareItemIds(String json) {
        List<Integer> ids = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) {
            return ids;
        }

        // 去除空格
        json = json.trim();

        try {
            // 尝试解析标准JSON格式: {"care_items": [1, 2, 3]}
            if (json.startsWith("{")) {
                // 使用正则表达式提取数组部分
                Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
                Matcher matcher = pattern.matcher(json);
                if (matcher.find()) {
                    String arrayStr = matcher.group(1);
                    String[] idStrs = arrayStr.split(",");
                    for (String idStr : idStrs) {
                        try {
                            ids.add(Integer.parseInt(idStr.trim()));
                        } catch (NumberFormatException e) {
                            // 忽略格式错误
                        }
                    }
                }
            }
            // 尝试解析简单数组格式: [1,2,3]
            else if (json.startsWith("[")) {
                String cleanStr = json.substring(1, json.length() - 1);
                String[] idStrs = cleanStr.split(",");
                for (String idStr : idStrs) {
                    try {
                        ids.add(Integer.parseInt(idStr.trim()));
                    } catch (NumberFormatException e) {
                        // 忽略格式错误
                    }
                }
            }
            // 尝试解析逗号分隔的ID列表: "1,2,3"
            else if (json.contains(",")) {
                String[] idStrs = json.split(",");
                for (String idStr : idStrs) {
                    try {
                        ids.add(Integer.parseInt(idStr.trim()));
                    } catch (NumberFormatException e) {
                        // 忽略格式错误
                    }
                }
            }
            // 单个ID
            else {
                try {
                    ids.add(Integer.parseInt(json.trim()));
                } catch (NumberFormatException e) {
                    // 忽略格式错误
                }
            }
        } catch (Exception e) {
            System.err.println("解析护理项JSON失败: " + e.getMessage() + ", JSON: " + json);
        }

        return ids;
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(ORDER_NO_FORMATTER);
        String randomPart = String.format("%04d", random.nextInt(10000));
        return "ORD-" + datePart + "-" + randomPart;
    }
}