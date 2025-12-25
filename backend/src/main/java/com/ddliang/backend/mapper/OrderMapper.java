package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.Order;
import com.ddliang.backend.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    
    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单ID查询订单详情
     */
    @Select("SELECT * FROM orders WHERE id = #{orderId}")
    Order findById(@Param("orderId") Long orderId);
    
    /**
     * 根据订单号查询订单详情
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order findByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 根据订单ID查询订单商品列表
     */
    @Select("SELECT *, product_image as image, product_name as name FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 更新订单状态
     */
    @Update("UPDATE orders SET status = #{status}, updated_at = NOW() WHERE id = #{orderId}")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    /**
     * 创建订单
     */
    @Insert("INSERT INTO orders (order_no, user_id, status, total_amount, shipping_fee, discount_amount, " +
            "coupon_discount, member_discount, final_amount, receiver_name, receiver_phone, receiver_address, remark) " +
            "VALUES (#{orderNo}, #{userId}, #{status}, #{totalAmount}, #{shippingFee}, #{discountAmount}, " +
            "#{couponDiscount}, #{memberDiscount}, #{finalAmount}, #{receiverName}, #{receiverPhone}, #{receiverAddress}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);
    
    /**
     * 创建订单商品
     */
    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_image, spec, price, " +
            "original_price, quantity, total_price) VALUES (#{orderId}, #{productId}, #{productName}, " +
            "#{productImage}, #{spec}, #{price}, #{originalPrice}, #{quantity}, #{totalPrice})")
    int insertOrderItem(OrderItem orderItem);
}