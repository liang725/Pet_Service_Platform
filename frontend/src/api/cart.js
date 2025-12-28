import request from '@/utils/request'

// 获取购物车列表
export function getCartItems() {
  return request({
    url: '/api/cart',
    method: 'get'
  })
}

// 添加到购物车
export function addToCart(data) {
  return request({
    url: '/api/cart',
    method: 'post',
    data
  })
}

// 更新购物车商品数量
export function updateCartItem(itemId, quantity) {
  return request({
    url: `/api/cart/${itemId}`,
    method: 'put',
    data: { quantity }
  })
}

// 删除购物车商品
export function removeCartItem(itemId) {
  return request({
    url: `/api/cart/${itemId}`,
    method: 'delete'
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/api/cart',
    method: 'delete'
  })
}
