// src/api/product.js - 商品相关API
import request from '@/utils/request'

/**
 * 获取商品详情
 * @param {number|string} productId - 商品ID
 * @returns {Promise}
 */
export function getProductDetail(productId) {
  return request({
    url: `/api/products/detail/${productId}`,
    method: 'GET'
  })
}

/**
 * 获取推荐商品列表
 * @param {number|string} productId - 当前商品ID
 * @param {number} limit - 返回数量限制
 * @returns {Promise}
 */
export function getRecommendedProducts(productId, limit = 4) {
  return request({
    url: `/api/products/detail/${productId}/recommended`,
    method: 'GET',
    params: { limit }
  })
}

/**
 * 添加商品到购物车
 * @param {Object} data - 购物车数据
 * @param {number} data.productId - 商品ID
 * @param {number} data.specId - 规格ID
 * @param {number} data.quantity - 数量
 * @returns {Promise}
 */
export function addToCart(data) {
  return request({
    url: '/api/cart/add',
    method: 'POST',
    data
  })
}

/**
 * 获取商品列表
 * @param {Object} params - 查询参数
 * @param {string} params.category - 分类
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.keyword - 搜索关键词
 * @param {number} params.limit - 数量限制
 * @returns {Promise}
 */
export function getProductList(params) {
  return request({
    url: '/api/products',
    method: 'GET',
    params
  })
}

/**
 * 搜索商品
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码
 * @param {number} pageSize - 每页数量
 * @returns {Promise}
 */
export function searchProducts(keyword, page = 1, pageSize = 10) {
  return request({
    url: '/api/products',
    method: 'GET',
    params: {
      keyword,
      page,
      pageSize
    }
  })
}
