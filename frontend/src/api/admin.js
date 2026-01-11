// src/api/admin.js
import request from '@/utils/request'

/**
 * 管理员相关API
 */

// ==================== 商品管理 ====================

// 获取商品列表（管理员）
export const getAdminProducts = (params) => {
  return request({
    url: '/api/admin/products',
    method: 'GET',
    params
  })
}

// 获取商品详情（管理员）
export const getAdminProduct = (id) => {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'GET'
  })
}

// 创建商品
export const createProduct = (data) => {
  return request({
    url: '/api/admin/products',
    method: 'POST',
    data
  })
}

// 更新商品
export const updateProduct = (id, data) => {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'PUT',
    data
  })
}

// 删除商品
export const deleteProduct = (id) => {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'DELETE'
  })
}

// 更新商品状态
export const updateProductStatus = (id, status) => {
  return request({
    url: `/api/admin/products/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

// ==================== 用户管理 ====================

// 获取用户列表（管理员）
export const getAdminUsers = (params) => {
  return request({
    url: '/api/admin/users',
    method: 'GET',
    params
  })
}

// 获取用户详情（管理员）
export const getAdminUser = (id) => {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'GET'
  })
}

// 更新用户状态
export const updateUserStatus = (id, status) => {
  return request({
    url: `/api/admin/users/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

// 删除用户
export const deleteUser = (id) => {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'DELETE'
  })
}

// ==================== 宠物管理 ====================

// 获取宠物列表（管理员）
export const getAdminPets = (params) => {
  return request({
    url: '/api/admin/pets',
    method: 'GET',
    params
  })
}

// 获取宠物详情（管理员）
export const getAdminPet = (id) => {
  return request({
    url: `/api/admin/pets/${id}`,
    method: 'GET'
  })
}

// 删除宠物（管理员）
export const deleteAdminPet = (id) => {
  return request({
    url: `/api/admin/pets/${id}`,
    method: 'DELETE'
  })
}

// ==================== 预约管理 ====================

// 获取预约列表（管理员）
export const getAdminAppointments = (params) => {
  return request({
    url: '/api/admin/appointments',
    method: 'GET',
    params
  })
}

// 更新预约状态
export const updateAppointmentStatus = (id, status) => {
  return request({
    url: `/api/admin/appointments/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

// ==================== 统计数据 ====================

// 获取管理员仪表板数据
export const getDashboardStats = () => {
  return request({
    url: '/api/admin/dashboard/stats',
    method: 'GET'
  })
}

// 获取销售统计
export const getSalesStats = (params) => {
  return request({
    url: '/api/admin/stats/sales',
    method: 'GET',
    params
  })
}

// 获取用户统计
export const getUserStats = (params) => {
  return request({
    url: '/api/admin/stats/users',
    method: 'GET',
    params
  })
}
