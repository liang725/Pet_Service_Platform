// ServiceAppointmentController.java - 修正版
package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.ServiceAppointmentDetailResponse;
import com.ddliang.backend.dto.ServiceAppointmentRequest;
import com.ddliang.backend.dto.ServiceAppointmentResponse;
import com.ddliang.backend.service.ServiceAppointmentService;
import com.ddliang.backend.service.ServiceItemService;
import com.ddliang.backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service")
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
public class ServiceAppointmentController {

    @Autowired
    private ServiceAppointmentService appointmentService;

    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建预约
     */
    @PostMapping("/appointments")
    public Result<?> createAppointment(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ServiceAppointmentRequest request) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            var appointment = appointmentService.createAppointment(userId, request);

            Map<String, Object> data = new HashMap<>();
            data.put("orderId", appointment.getId());
            data.put("orderNo", appointment.getOrderNo());
            data.put("appointmentDate", appointment.getAppointmentDate());
            data.put("timeSlot", appointment.getTimeSlot());
            data.put("totalAmount", appointment.getOrderTotalAmount());

            return Result.success("预约成功", data);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取用户预约列表（按状态过滤）
     */
    @GetMapping("/appointments")
    public Result<?> getUserAppointments(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            List<ServiceAppointmentResponse> appointments;

            if (status != null && !status.isEmpty() && !"all".equals(status)) {
                appointments = appointmentService.getUserAppointments(userId, status);
            } else {
                // 获取所有预约
                appointments = ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                        .getAllUserAppointments(userId);
            }

            return Result.success("获取预约列表成功", appointments);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取用户所有预约（不按状态过滤）
     */
    @GetMapping("/appointments/all")
    public Result<?> getAllUserAppointments(
            @RequestHeader("Authorization") String token) {
        try {
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            List<ServiceAppointmentResponse> appointments =
                    ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                            .getAllUserAppointments(userId);

            return Result.success("获取预约列表成功", appointments);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取预约统计
     */
    @GetMapping("/appointments/stats")
    public Result<?> getAppointmentStats(
            @RequestHeader("Authorization") String token) {
        try {
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            Map<String, Integer> stats =
                    ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                            .getAppointmentStats(userId);

            return Result.success("获取预约统计成功", stats);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取用户每日预约次数
     */
    @GetMapping("/appointments/daily-counts")
    public Result<?> getDailyAppointmentCounts(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            List<Map<String, Object>> dailyCounts =
                    ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                            .getDailyAppointmentCounts(userId, startDate, endDate);

            return Result.success("获取每日预约次数成功", dailyCounts);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取指定日期的预约次数
     */
    @GetMapping("/appointments/date-count")
    public Result<?> getAppointmentCountByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam String date) {
        try {
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            int count = ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                    .getAppointmentCountByDate(userId, date);

            Map<String, Object> data = new HashMap<>();
            data.put("date", date);
            data.put("count", count);

            return Result.success("获取预约次数成功", data);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取预约详情（包含服务项目和护理项目）- 新接口
     */
    @GetMapping("/appointments/{id}/detail")
    public Result<?> getAppointmentDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            ServiceAppointmentDetailResponse appointmentDetail =
                    ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                            .getAppointmentDetail(id, userId);

            return Result.success("获取预约详情成功", appointmentDetail);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取预约基础信息 - 保持向后兼容
     * 这个接口返回基础信息，不包含服务项目和护理项目详情
     */
    @GetMapping("/appointments/{id}/basic")
    public Result<?> getAppointmentBasicInfo(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            ServiceAppointmentResponse appointment =
                    appointmentService.getAppointmentDetails(id, userId);

            return Result.success("获取预约信息成功", appointment);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取预约详情 - 主接口，根据需求返回不同类型的数据
     * 这个接口可以返回基础信息或详细信息，通过参数控制
     */
    @GetMapping("/appointments/{id}")
    public Result<?> getAppointmentDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "basic") String type) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            if ("detail".equalsIgnoreCase(type)) {
                // 返回详细信息
                ServiceAppointmentDetailResponse appointmentDetail =
                        ((com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl) appointmentService)
                                .getAppointmentDetail(id, userId);
                return Result.success("获取预约详情成功", appointmentDetail);
            } else {
                // 默认返回基础信息
                ServiceAppointmentResponse appointment =
                        appointmentService.getAppointmentDetails(id, userId);
                return Result.success("获取预约信息成功", appointment);
            }
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    @PutMapping("/appointments/{id}/cancel")
    public Result<?> cancelAppointment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            appointmentService.cancelAppointment(id, userId, reason != null ? reason : "用户主动取消");

            return Result.success("取消预约成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 删除预约
     */
    @DeleteMapping("/appointments/{id}")
    public Result<?> deleteAppointment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        try {
            // 验证token并获取userId
            Integer userId = validateAndGetUserId(token);
            if (userId == null) {
                return Result.error(401, "用户未认证或token无效");
            }

            appointmentService.deleteAppointment(id, userId);

            return Result.success("删除预约成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取可用时间段
     */
    @GetMapping("/time-slots")
    public Result<?> getAvailableTimeSlots(
            @RequestParam String date) {
        try {
            List<String> timeSlots = appointmentService.getAvailableTimeSlots(date);
            return Result.success("获取可用时间段成功", timeSlots);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取所有服务项目
     */
    @GetMapping("/items")
    public Result<?> getAllServiceItems() {
        try {
            var items = serviceItemService.getAllServiceItems();
            return Result.success("获取服务项目成功", items);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取推荐服务
     */
    @GetMapping("/items/recommended")
    public Result<?> getRecommendedServices() {
        try {
            var items = serviceItemService.getRecommendedServices();
            return Result.success("获取推荐服务成功", items);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取服务详情
     */
    @GetMapping("/items/{id}")
    public Result<?> getServiceById(@PathVariable Integer id) {
        try {
            var item = serviceItemService.getServiceById(id);
            if (item == null) {
                return Result.error(404, "服务项目不存在");
            }
            return Result.success("获取服务详情成功", item);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 按分类获取服务
     */
    @GetMapping("/items/category/{category}")
    public Result<?> getServicesByCategory(@PathVariable String category) {
        try {
            var items = serviceItemService.getServicesByCategory(category);
            return Result.success("获取服务项目成功", items);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 验证token并获取用户ID
     */
    private Integer validateAndGetUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        String jwtToken = token.substring(7); // 去掉"Bearer "前缀

        try {
            // 验证token是否过期
            if (jwtUtil.isTokenExpired(jwtToken)) {
                return null;
            }

            // 从token中获取用户ID
            return jwtUtil.getUserIdFromToken(jwtToken);
        } catch (Exception e) {
            // token解析失败
            return null;
        }
    }
}