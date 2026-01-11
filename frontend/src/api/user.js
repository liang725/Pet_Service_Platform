// src/api/user.js
import request from '@/utils/request'

/**
 * 用户相关API
 */

// 获取用户信息
export const getUserInfo = () => {
  return request({
    url: '/api/user/info',
    method: 'GET'
  })
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request({
    url: '/api/user/info',
    method: 'PUT',
    data
  })
}

// 修改密码
export const changePassword = (data) => {
  return request({
    url: '/api/user/password',
    method: 'PUT',
    data
  })
}

// 获取用户统计信息
export const getUserStats = () => {
  return request({
    url: '/api/user/stats',
    method: 'GET'
  })
}

