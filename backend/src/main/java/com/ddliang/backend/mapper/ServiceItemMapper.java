package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.ServiceItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceItemMapper {

    // 修改前：SELECT * FROM service_items WHERE status = 1 ORDER BY sort_order
    // 修改后：删除 WHERE status = 1 条件
    @Select("SELECT * FROM service_items ORDER BY sort_order")
    List<ServiceItem> findAllActive();

    @Select("SELECT * FROM service_items WHERE id = #{id}")
    ServiceItem findById(Integer id);

    // 修改前：SELECT * FROM service_items WHERE is_recommended = 1 ORDER BY sort_order LIMIT 4
    // 修改后：保持不变，因为 is_recommended 字段存在
    @Select("SELECT * FROM service_items WHERE is_recommended = 1 ORDER BY sort_order LIMIT 4")
    List<ServiceItem> findRecommended();

    @Select("SELECT * FROM service_items WHERE category = #{category} ORDER BY sort_order")
    List<ServiceItem> findByCategory(String category);

    // 可选：添加新方法获取所有服务（包括非活跃的）
    @Select("SELECT * FROM service_items")
    List<ServiceItem> findAll();
}