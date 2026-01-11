// AdminAppointmentController.java
package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.ServiceAppointmentDetailResponse;
import com.ddliang.backend.dto.ServiceAppointmentResponse;
import com.ddliang.backend.mapper.ServiceAppointmentMapper;
import com.ddliang.backend.mapper.UserMapper;
import com.ddliang.backend.service.ServiceAppointmentService;
import com.ddliang.backend.service.impl.ServiceAppointmentServiceImpl;
import com.ddliang.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端预约管理控制器
 */
@RestController
@RequestMapping("/api/admin/appointments")
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
public class AdminAppointmentController {

    @Autowired
    private ServiceAppointmentMapper appointmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ServiceAppointmentService appointmentService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取预约列表（分页+筛选）
     */
    @GetMapping
    public Result<?> getAppointmentList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String today,           // 当日筛选
            @RequestParam(required = false) String petType) {      // 动物种类筛选

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> appointments;
            int total;

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("offset", offset);
            params.put("limit", pageSize);

            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }

            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
                params.put("status", status.trim());
            }

            // 当日筛选
            if (today != null && !today.trim().isEmpty()) {
                params.put("today", today.trim());
            }

            // 动物种类筛选
            if (petType != null && !petType.trim().isEmpty()) {
                params.put("petType", petType.trim());
            }

            // 查询数据
            appointments = appointmentMapper.findAppointmentsForAdmin(params);
            total = appointmentMapper.countAppointmentsForAdmin(params);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", appointments);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取预约列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<?> getAppointmentDetail(HttpServletRequest request, @PathVariable Integer id) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 使用ServiceAppointmentServiceImpl中的getAppointmentDetail方法
            // 注意：这里userId传null，因为是管理员查看所有用户的预约
            ServiceAppointmentDetailResponse appointmentDetail =
                    ((ServiceAppointmentServiceImpl) appointmentService).getAppointmentDetail(id, null);

            return Result.success(appointmentDetail);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取预约详情失败：" + e.getMessage());
        }
    }

    /**
     * 更新预约状态
     */
    @PutMapping("/{id}/status")
    public Result<?> updateAppointmentStatus(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            String status = body.get("status");
            String reason = body.get("reason");

            if (status == null || status.trim().isEmpty()) {
                return Result.error(400, "状态不能为空");
            }

            // 验证状态值是否合法
            if (!List.of("pending", "confirmed", "completed", "cancelled").contains(status)) {
                return Result.error(400, "无效的状态值");
            }

            // 如果取消预约，需要理由
            if ("cancelled".equals(status) && (reason == null || reason.trim().isEmpty())) {
                return Result.error(400, "取消预约必须提供原因");
            }

            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("status", status);
            params.put("reason", reason);

            int rows = appointmentMapper.updateStatusForAdmin(params);
            if (rows > 0) {
                return Result.success("状态更新成功");
            }
            return Result.error(500, "状态更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除预约
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteAppointment(HttpServletRequest request, @PathVariable Integer id) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 检查预约是否存在
            Map<String, Object> appointment = appointmentMapper.findAppointmentForAdmin(id);
            if (appointment == null) {
                return Result.error(404, "预约不存在");
            }

            // 删除预约（不再限制状态）
            int rows = appointmentMapper.deleteForAdmin(id);
            if (rows > 0) {
                return Result.success("删除成功");
            }
            return Result.error(500, "删除预约失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "删除预约失败：" + e.getMessage());
        }
    }

    /**
     * 获取预约统计
     */
    @GetMapping("/stats")
    public Result<?> getAppointmentStats(HttpServletRequest request) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error(403, "无权访问管理员功能");
        }

        try {
            // 获取各个状态的预约数量
            List<Map<String, Object>> stats = appointmentMapper.countAllAppointmentsByStatus();

            Map<String, Integer> result = new HashMap<>();
            result.put("pending", 0);
            result.put("confirmed", 0);
            result.put("completed", 0);
            result.put("cancelled", 0);

            // 填充统计结果
            for (Map<String, Object> stat : stats) {
                String status = (String) stat.get("status");
                Long count = (Long) stat.get("count");
                if (status != null && count != null) {
                    result.put(status, count.intValue());
                }
            }

            // 计算总数
            int total = result.values().stream().mapToInt(Integer::intValue).sum();
            result.put("all", total);

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            return "admin".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}