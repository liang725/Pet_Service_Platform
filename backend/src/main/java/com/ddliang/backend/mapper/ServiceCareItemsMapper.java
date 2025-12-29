// ServiceCareItemsMapper.java
package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.ServiceCareItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceCareItemsMapper {

    @Select("SELECT * FROM service_care_items WHERE id = #{id}")
    ServiceCareItems findById(Integer id);

    @Select("SELECT * FROM service_care_items WHERE care_category = #{category} ORDER BY sort_order")
    List<ServiceCareItems> findByCategory(String category);

    @Select("SELECT * FROM service_care_items WHERE is_single_purchase = 1 ORDER BY sort_order")
    List<ServiceCareItems> findSinglePurchaseItems();

    @Select("SELECT * FROM service_care_items ORDER BY sort_order")
    List<ServiceCareItems> findAll();
}