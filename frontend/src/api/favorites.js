// src/api/favorites.js - 收藏相关API
import request from '@/utils/request'

/**
 * 获取收藏列表
 * @returns {Promise}
 */
export function getFavorites() {
  return request({
    url: '/api/favorites',
    method: 'GET'
  })
}

/**
 * 添加收藏
 * @param {number} productId - 商品ID
 * @returns {Promise}
 */
export function addFavorite(productId) {
  return request({
    url: '/api/favorites',
    method: 'POST',
    data: { productId }
  })
}

/**
 * 取消收藏
 * @param {number} productId - 商品ID
 * @returns {Promise}
 */
export function removeFavorite(productId) {
  return request({
    url: `/api/favorites/${productId}`,
    method: 'DELETE'
  })
}

/**
 * 检查是否已收藏
 * @param {number} productId - 商品ID
 * @returns {Promise}
 */
export function checkFavorite(productId) {
  return request({
    url: `/api/favorites/check/${productId}`,
    method: 'GET'
  })
}
