package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.CreateOrderRequest;
import com.ddliang.backend.dto.OrderResponse;
import com.ddliang.backend.entity.Order;
import com.ddliang.backend.entity.OrderItem;
import com.ddliang.backend.mapper.OrderMapper;
import com.ddliang.backend.mapper.ProductMapper;
import com.ddliang.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        try {
            // 生成订单号
            String orderNo = "PET" + System.currentTimeMillis();
            
            // 计算配送费
            BigDecimal deliveryFee = "express".equals(request.getDeliveryMethod()) ? 
                new BigDecimal("15.00") : BigDecimal.ZERO;
            
            // 计算优惠折扣
            BigDecimal couponDiscount = BigDecimal.ZERO;
            if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
                // 简单的优惠券逻辑
                switch (request.getCouponCode()) {
                    case "新用户优惠券":
                        couponDiscount = new BigDecimal("10.00");
                        break;
                    case "满减优惠券":
                        couponDiscount = new BigDecimal("20.00");
                        break;
                    case "VIP专享券":
                        couponDiscount = new BigDecimal("50.00");
                        break;
                }
            }
            
            // 计算商品总价
            BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算最终金额
            BigDecimal finalAmount = subtotal.add(deliveryFee).subtract(couponDiscount);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
            
            // 创建订单对象
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderNo(orderNo);
            order.setStatus("pending");
            order.setTotalAmount(subtotal);
            order.setShippingFee(deliveryFee);
            order.setDiscountAmount(BigDecimal.ZERO); // 设置折扣金额
            order.setCouponDiscount(couponDiscount);
            order.setMemberDiscount(BigDecimal.ZERO);
            order.setFinalAmount(finalAmount);
            order.setDeliveryMethod(request.getDeliveryMethod());
            order.setCouponCode(request.getCouponCode());
            order.setAddressId(request.getAddressId());
            
            // 设置收货人信息（暂时使用默认值，实际应该从地址信息中获取）
            order.setReceiverName("张先生");
            order.setReceiverPhone("13800138001");
            order.setReceiverAddress("北京市朝阳区三里屯街道1号");
            order.setRemark("系统自动生成订单");
            
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            
            // 保存订单到数据库
            try {
                System.out.println("开始保存订单到数据库，订单号: " + orderNo);
                int insertResult = orderMapper.insertOrder(order);
                System.out.println("订单插入结果: " + insertResult + ", 生成的订单ID: " + order.getId());
                
                if (insertResult <= 0) {
                    throw new RuntimeException("订单插入失败，影响行数为0");
                }
                
                // 保存订单商品
                for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
                    System.out.println("处理订单商品 - item.getId(): " + item.getId() + ", item.getName(): " + item.getName());
                    
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setProductId(item.getId().intValue()); // 转换Long为Integer
                    orderItem.setProductName(item.getName());
                    orderItem.setProductImage(item.getImage());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setOriginalPrice(item.getPrice()); // 设置原价
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setSpec(item.getSpec());
                    orderItem.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity()))); // 计算小计
                    orderItem.setCreatedAt(LocalDateTime.now());
                    
                    System.out.println("即将插入订单商品 - productId: " + orderItem.getProductId() + ", productName: " + orderItem.getProductName());
                    
                    int itemInsertResult = orderMapper.insertOrderItem(orderItem);
                    System.out.println("订单商品插入结果: " + itemInsertResult + ", 商品: " + item.getName());
                }
                
                System.out.println("订单保存成功，订单ID: " + order.getId());
                
            } catch (Exception e) {
                System.err.println("保存订单到数据库失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("创建订单失败: " + e.getMessage(), e);
            }
            
            // 构建返回对象
            OrderResponse response = new OrderResponse();
            response.setOrderId(orderNo);
            response.setOrderNo(orderNo);
            response.setStatus("pending");
            response.setTotalAmount(subtotal);
            response.setFinalAmount(finalAmount);
            response.setCreatedAt(LocalDateTime.now());
            response.setDeliveryMethod(request.getDeliveryMethod());
            response.setCouponCode(request.getCouponCode());
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("创建订单失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        
        // 为每个订单填充商品列表和摘要信息
        for (Order order : orders) {
            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(order.getId());
            order.setItems(items);
            
            // 构建订单摘要
            Order.OrderSummary summary = new Order.OrderSummary();
            summary.setSubtotal(order.getTotalAmount());
            summary.setShipping(order.getShippingFee());
            summary.setCoupon(order.getCouponDiscount());
            summary.setDiscount(order.getMemberDiscount());
            summary.setTotal(order.getFinalAmount());
            order.setSummary(summary);
        }
        
        return orders;
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order != null) {
            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(orderId);
            order.setItems(items);
            
            // 构建订单摘要
            Order.OrderSummary summary = new Order.OrderSummary();
            summary.setSubtotal(order.getTotalAmount());
            summary.setShipping(order.getShippingFee());
            summary.setCoupon(order.getCouponDiscount());
            summary.setDiscount(order.getMemberDiscount());
            summary.setTotal(order.getFinalAmount());
            order.setSummary(summary);
        }
        return order;
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(order.getId());
            order.setItems(items);
            
            // 构建订单摘要
            Order.OrderSummary summary = new Order.OrderSummary();
            summary.setSubtotal(order.getTotalAmount());
            summary.setShipping(order.getShippingFee());
            summary.setCoupon(order.getCouponDiscount());
            summary.setDiscount(order.getMemberDiscount());
            summary.setTotal(order.getFinalAmount());
            order.setSummary(summary);
        }
        return order;
    }
    
    @Override
    public boolean cancelOrder(Long orderId, Long userId) {
        // 验证订单是否属于当前用户
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有待支付状态的订单可以取消
        if (!"pending".equals(order.getStatus())) {
            return false;
        }
        
        return orderMapper.updateOrderStatus(orderId, "cancelled") > 0;
    }
    
    @Override
    public boolean processPayment(Long orderId, String paymentMethod) {
        try {
            System.out.println("开始处理支付，订单ID: " + orderId + ", 支付方式: " + paymentMethod);
            
            // 模拟支付处理逻辑
            Order order = orderMapper.findById(orderId);
            if (order == null) {
                System.err.println("订单不存在，订单ID: " + orderId);
                return false;
            }
            
            System.out.println("找到订单，当前状态: " + order.getStatus());
            
            if (!"pending".equals(order.getStatus())) {
                System.err.println("订单状态不允许支付，当前状态: " + order.getStatus());
                return false;
            }
            
            // 模拟支付成功率（这里设为100%成功，实际项目中会调用第三方支付接口）
            boolean paymentSuccess = true;
            
            if (paymentSuccess) {
                // 先尝试更新支付信息
                try {
                    System.out.println("先更新支付信息，订单ID: " + orderId + ", 支付方式: " + paymentMethod);
                    int paymentUpdated = orderMapper.updatePaymentInfo(orderId, paymentMethod);
                    System.out.println("更新支付信息结果: " + paymentUpdated);
                } catch (Exception e) {
                    System.err.println("更新支付信息失败: " + e.getMessage());
                }
                
                // 然后更新订单状态为已支付
                try {
                    System.out.println("准备更新订单状态，订单ID: " + orderId + ", 新状态: paid");
                    int updated = orderMapper.updateOrderStatus(orderId, "paid");
                    System.out.println("更新订单状态为paid结果: " + updated);
                    
                    if (updated > 0) {
                        System.out.println("支付处理成功，订单状态已更新为paid");
                        
                        // 支付成功后，更新商品销量
                        try {
                            List<OrderItem> items = orderMapper.findOrderItemsByOrderId(orderId);
                            for (OrderItem item : items) {
                                int salesUpdated = productMapper.increaseSales(item.getProductId(), item.getQuantity());
                                System.out.println("更新商品销量，商品ID: " + item.getProductId() + 
                                                 ", 数量: " + item.getQuantity() + 
                                                 ", 更新结果: " + salesUpdated);
                            }
                        } catch (Exception e) {
                            System.err.println("更新商品销量失败: " + e.getMessage());
                            // 销量更新失败不影响支付流程，只记录日志
                        }
                        
                        return true;
                    } else {
                        System.err.println("更新订单状态失败，影响行数为0");
                        return false;
                    }
                } catch (Exception e) {
                    System.err.println("更新订单状态时发生异常: " + e.getMessage());
                    e.printStackTrace();
                    
                    // 如果paid失败，尝试shipped状态作为备用
                    try {
                        System.out.println("尝试使用shipped状态作为备用");
                        int updated = orderMapper.updateOrderStatus(orderId, "shipped");
                        System.out.println("使用shipped状态更新结果: " + updated);
                        return updated > 0;
                    } catch (Exception e2) {
                        System.err.println("使用shipped状态也失败: " + e2.getMessage());
                        return false;
                    }
                }
            } else {
                System.err.println("模拟支付失败");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("处理支付失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean confirmDelivery(Long orderId, Long userId) {
        // 验证订单是否属于当前用户
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已发货状态的订单可以确认收货
        if (!"shipped".equals(order.getStatus())) {
            return false;
        }
        
        return orderMapper.updateOrderStatus(orderId, "delivered") > 0;
    }
    
    @Override
    public boolean deleteOrder(Long orderId, Long userId) {
        // 验证订单是否属于当前用户
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已取消状态的订单可以删除
        if (!"cancelled".equals(order.getStatus())) {
            return false;
        }
        
        // 删除订单（物理删除）
        return orderMapper.deleteOrder(orderId) > 0;
    }
    
    @Override
    public int clearCancelledOrders(Long userId) {
        // 批量删除用户的所有已取消订单
        return orderMapper.deleteCancelledOrdersByUserId(userId);
    }
    
    @Override
    public int clearAllOrders(Long userId) {
        // 删除用户的所有订单
        return orderMapper.deleteAllOrdersByUserId(userId);
    }
}