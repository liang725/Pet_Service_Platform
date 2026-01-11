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
    @Select("SELECT id, order_id, product_id, product_name, product_image, spec, price, " +
            "original_price, quantity, total_price, created_at, " +
            "product_image as image, product_name as name " +
            "FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 更新订单状态
     */
    @Update("UPDATE orders SET status = #{status}, updated_at = NOW() WHERE id = #{orderId}")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    /**
     * 更新支付信息
     */
    @Update("UPDATE orders SET payment_method = #{paymentMethod}, payment_time = NOW(), updated_at = NOW() WHERE id = #{orderId}")
    int updatePaymentInfo(@Param("orderId") Long orderId, @Param("paymentMethod") String paymentMethod);
    
    /**
     * 创建订单
     */
    @Insert("INSERT INTO orders (order_no, user_id, status, total_amount, shipping_fee, discount_amount, " +
            "coupon_discount, member_discount, final_amount, receiver_name, receiver_phone, receiver_address, remark, created_at, updated_at) " +
            "VALUES (#{orderNo}, #{userId}, #{status}, #{totalAmount}, #{shippingFee}, #{discountAmount}, " +
            "#{couponDiscount}, #{memberDiscount}, #{finalAmount}, #{receiverName}, #{receiverPhone}, #{receiverAddress}, #{remark}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);
    
    /**
     * 创建订单商品
     */
    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_image, spec, price, " +
            "original_price, quantity, total_price) VALUES (#{orderId}, #{productId}, #{productName}, " +
            "#{productImage}, #{spec}, #{price}, #{originalPrice}, #{quantity}, #{totalPrice})")
    int insertOrderItem(OrderItem orderItem);
    
    /**
     * 删除订单（物理删除）
     */
    @Delete("DELETE FROM orders WHERE id = #{orderId}")
    int deleteOrder(@Param("orderId") Long orderId);
    
    /**
     * 删除订单商品
     */
    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    int deleteOrderItems(@Param("orderId") Long orderId);
    
    /**
     * 批量删除用户的所有已取消订单
     */
    @Delete("DELETE FROM orders WHERE user_id = #{userId} AND status = 'cancelled'")
    int deleteCancelledOrdersByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户的所有订单
     */
    @Delete("DELETE FROM orders WHERE user_id = #{userId}")
    int deleteAllOrdersByUserId(@Param("userId") Long userId);
}