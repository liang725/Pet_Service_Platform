// ServiceAppointmentMapper.java - 完整修复版
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

    // ServiceAppointmentMapper.java - 修复SQL语法
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
}