import request from '@/utils/request'

// 获取用户订单列表
export function getUserOrders(params = {}) {
  return request({
    url: '/api/orders',
    method: 'get',
    params
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/api/orders',
    method: 'post',
    data
  })
}

// 获取订单详情
export function getOrderDetail(orderId) {
  return request({
    url: `/api/orders/${orderId}`,
    method: 'get'
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: `/api/orders/${orderId}/cancel`,
    method: 'post'
  })
}

// 确认收货
export function confirmOrder(orderId) {
  return request({
    url: `/api/orders/${orderId}/confirm`,
    method: 'put'
  })
}

// 申请退款
export function refundOrder(orderId, reason) {
  return request({
    url: `/api/orders/${orderId}/refund`,
    method: 'put',
    data: { reason }
  })
}

// 支付订单
export function payOrder(orderId, paymentMethod = 'wechat') {
  return request({
    url: `/api/orders/${orderId}/pay`,
    method: 'post',
    data: { paymentMethod }
  })
}

// 获取订单状态
export function getOrderStatus(orderId) {
  return request({
    url: `/api/orders/${orderId}/status`,
    method: 'get'
  })
}

// 获取支付状态
export function getPaymentStatus(orderId) {
  return request({
    url: `/api/orders/${orderId}/payment-status`,
    method: 'get'
  })
}
// 删除订单
export function deleteOrder(orderId) {
  return request({
    url: `/api/orders/${orderId}`,
    method: 'delete'
  })
}

// 清空所有订单
export function clearAllOrders() {
  return request({
    url: '/api/orders/clear-all',
    method: 'post'
  })
}
