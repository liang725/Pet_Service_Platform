// ServiceAppointmentMapper.java - 修复版（直接在原文件上修改）
package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.ServiceAppointment;
import com.ddliang.backend.dto.ServiceAppointmentResponse;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceAppointmentMapper {

    @Insert("INSERT INTO service_appointments " +
            "(order_no, user_id, pet_id, appointment_date, time_slot, items_base_total, " +
            "items_discount_total, care_items_total, order_total_amount, special_notes, status) " +
            "VALUES (#{orderNo}, #{userId}, #{petId}, #{appointmentDate}, #{timeSlot}, #{itemsBaseTotal}, " +
            "#{itemsDiscountTotal}, #{careItemsTotal}, #{orderTotalAmount}, #{specialNotes}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceAppointment appointment);

    @Select("SELECT * FROM service_appointments WHERE id = #{id}")
    ServiceAppointment findById(Integer id);

    @Select("SELECT * FROM service_appointments WHERE user_id = #{userId} ORDER BY appointment_date DESC")
    List<ServiceAppointment> findByUserId(Integer userId);

    // 修复：使用 <![CDATA[ ]]> 包装复杂 SQL
    @Select("<script>" +
            "SELECT sa.*, " +
            "p.name as pet_name, " +
            "p.pet_type as pet_type, " +
            "p.breed as breed, " +
            "p.personality as personality, " +
            "MAX(si.item_name) as service_name, " +
            "MAX(si.category) as service_category, " +
            "MAX(sd.service_intro) as service_description, " +
            "MAX(si.thumbnail_url) as service_image, " +
            "COALESCE(sa.order_total_amount, 0) as order_total_amount " +
            "FROM service_appointments sa " +
            "LEFT JOIN pets p ON sa.pet_id = p.id " +
            "LEFT JOIN service_order_items soi ON sa.id = soi.order_id " +
            "LEFT JOIN service_items si ON soi.service_id = si.id " +
            "LEFT JOIN service_details sd ON si.id = sd.service_id " +
            "WHERE sa.user_id = #{userId} " +
            "<if test='status != null and status != \"all\" and status != \"\"'>" +
            "   AND sa.status = #{status}" +
            "</if>" +
            "GROUP BY sa.id, p.name, p.pet_type, p.breed, p.personality, sa.order_total_amount " +
            "ORDER BY sa.appointment_date DESC, sa.created_at DESC" +
            "</script>")
    List<ServiceAppointmentResponse> findAppointmentsWithDetails(@Param("userId") Integer userId, @Param("status") String status);

    @Select("SELECT sa.*, " +
            "p.name as pet_name, " +
            "p.pet_type as pet_type, " +
            "p.breed as breed, " +
            "p.personality as personality, " +
            "si.item_name as service_name, " +
            "si.category as service_category, " +
            "sd.service_intro as service_description, " +
            "si.thumbnail_url as service_image " +
            "FROM service_appointments sa " +
            "LEFT JOIN pets p ON sa.pet_id = p.id " +
            "LEFT JOIN service_order_items soi ON sa.id = soi.order_id " +
            "LEFT JOIN service_items si ON soi.service_id = si.id " +
            "LEFT JOIN service_details sd ON si.id = sd.service_id " +
            "WHERE sa.id = #{id} AND sa.user_id = #{userId} " +
            "LIMIT 1")
    ServiceAppointmentResponse findByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM service_appointments " +
            "WHERE appointment_date = #{date} AND time_slot = #{timeSlot} AND status IN ('pending', 'confirmed')")
    int countByDateAndTimeSlot(@Param("date") LocalDate date, @Param("timeSlot") String timeSlot);

    @Update("UPDATE service_appointments SET status = #{status}, cancellation_reason = #{cancellationReason} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int updateStatus(@Param("id") Integer id, @Param("userId") Integer userId,
                     @Param("status") String status, @Param("cancellationReason") String reason);

    @Delete("DELETE FROM service_appointments WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);

    @Select("SELECT status, COUNT(*) as count " +
            "FROM service_appointments " +
            "WHERE user_id = #{userId} " +
            "GROUP BY status")
    List<Map<String, Object>> countAppointmentsByStatus(Integer userId);

    @Select("SELECT COUNT(*) as count FROM service_appointments WHERE user_id = #{userId}")
    int countAllByUserId(Integer userId);

    // 预约日期显示在日历上
    @Select("SELECT DATE(appointment_date) as appointment_date, COUNT(*) as count " +
            "FROM service_appointments " +
            "WHERE user_id = #{userId} " +
            "AND appointment_date BETWEEN #{startDate} AND #{endDate} " +
            "AND status IN ('pending', 'confirmed', 'completed') " +
            "GROUP BY DATE(appointment_date) " +
            "ORDER BY appointment_date")
    List<Map<String, Object>> findDailyAppointmentCounts(@Param("userId") Integer userId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Select("SELECT COUNT(*) FROM service_appointments " +
            "WHERE user_id = #{userId} " +
            "AND DATE(appointment_date) = DATE(#{date}) " +
            "AND status IN ('pending', 'confirmed', 'completed')")
    int countByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    @Select("SELECT * FROM service_appointments WHERE id = #{id} AND user_id = #{userId}")
    ServiceAppointment findAppointmentByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);

    // 管理端预约列表查询 - 只保留当日筛选和动物种类筛选
    @Select("<script>" +
            "SELECT sa.*, " +
            "u.username as user_name, " +
            "u.phone as user_phone, " +
            "p.name as pet_name, " +
            "p.pet_type as pet_type, " +
            "si.item_name as service_name " +
            "FROM service_appointments sa " +
            "LEFT JOIN users u ON sa.user_id = u.id " +
            "LEFT JOIN pets p ON sa.pet_id = p.id " +
            "LEFT JOIN service_order_items soi ON sa.id = soi.order_id " +
            "LEFT JOIN service_items si ON soi.service_id = si.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (sa.order_no LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR si.item_name LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='status != null and status != \"\" and status != \"all\"'>" +
            "   AND sa.status = #{status} " +
            "</if>" +
            "<if test='today != null and today != \"\"'>" +
            "   AND DATE(sa.appointment_date) = DATE(#{today}) " +
            "</if>" +
            "<if test='petType != null and petType != \"\"'>" +
            "   AND p.pet_type = #{petType} " +
            "</if>" +
            "ORDER BY sa.appointment_date DESC, sa.created_at DESC " +
            "<if test='limit != null and offset != null'>" +
            "   LIMIT #{offset}, #{limit} " +
            "</if>" +
            "</script>")
    List<Map<String, Object>> findAppointmentsForAdmin(Map<String, Object> params);

    // 管理端预约数量统计 - 删除日期范围筛选条件
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM service_appointments sa " +
            "LEFT JOIN users u ON sa.user_id = u.id " +
            "LEFT JOIN pets p ON sa.pet_id = p.id " +
            "LEFT JOIN service_order_items soi ON sa.id = soi.order_id " +
            "LEFT JOIN service_items si ON soi.service_id = si.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (sa.order_no LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR si.item_name LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='status != null and status != \"\" and status != \"all\"'>" +
            "   AND sa.status = #{status} " +
            "</if>" +
            "<if test='today != null and today != \"\"'>" +
            "   AND DATE(sa.appointment_date) = DATE(#{today}) " +
            "</if>" +
            "<if test='petType != null and petType != \"\"'>" +
            "   AND p.pet_type = #{petType} " +
            "</if>" +
            "</script>")
    int countAppointmentsForAdmin(Map<String, Object> params);

    // 管理端获取单个预约
    @Select("SELECT sa.*, " +
            "u.username as user_name, " +
            "u.phone as user_phone, " +
            "p.name as pet_name, " +
            "p.pet_type as pet_type, " +
            "si.item_name as service_name " +
            "FROM service_appointments sa " +
            "LEFT JOIN users u ON sa.user_id = u.id " +
            "LEFT JOIN pets p ON sa.pet_id = p.id " +
            "LEFT JOIN service_order_items soi ON sa.id = soi.order_id " +
            "LEFT JOIN service_items si ON soi.service_id = si.id " +
            "WHERE sa.id = #{id} " +
            "LIMIT 1")
    Map<String, Object> findAppointmentForAdmin(Integer id);

    // 管理端更新状态 - 修复版
    @Update("<script>" +
            "UPDATE service_appointments " +
            "SET status = #{status} " +
            "<if test='reason != null and reason != \"\"'>" +
            "   , cancellation_reason = #{reason} " +
            "</if>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateStatusForAdmin(Map<String, Object> params);

    // 管理端删除预约
    @Delete("DELETE FROM service_appointments WHERE id = #{id}")
    int deleteForAdmin(Integer id);

    // 统计所有预约状态（管理员用）
    @Select("SELECT status, COUNT(*) as count " +
            "FROM service_appointments " +
            "GROUP BY status")
    List<Map<String, Object>> countAllAppointmentsByStatus();
}