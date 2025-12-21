// ServiceOrderItemMapper.java
package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.ServiceOrderItem;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ServiceOrderItemMapper {

    @Insert("INSERT INTO service_order_items " +
            "(order_id, service_id, item_base_price, item_discount_percentage, item_discount_amount, " +
            "item_final_price, care_items_selected, care_items_total, item_total_amount, quantity) " +
            "VALUES (#{orderId}, #{serviceId}, #{itemBasePrice}, #{itemDiscountPercentage}, #{itemDiscountAmount}, " +
            "#{itemFinalPrice}, #{careItemsSelected}, #{careItemsTotal}, #{itemTotalAmount}, #{quantity})")
    int insert(ServiceOrderItem orderItem);

    @Select("SELECT * FROM service_order_items WHERE order_id = #{orderId}")
    ServiceOrderItem findByOrderId(Integer orderId);
}