// src/api/appointment.js - 预约相关API
import request from '@/utils/request'

/**
 * 获取预约列表
 * @returns {Promise}
 */
export function getAppointments() {
  return request({
    url: '/api/service/appointments/all',
    method: 'GET'
  })
}

/**
 * 创建预约
 * @param {Object} data - 预约数据
 * @returns {Promise}
 */
export function createAppointment(data) {
  return request({
    url: '/api/service/appointments',
    method: 'POST',
    data
  })
}

/**
 * 获取预约详情
 * @param {number} id - 预约ID
 * @returns {Promise}
 */
export function getAppointmentDetail(id) {
  return request({
    url: `/api/service/appointments/${id}`,
    method: 'GET'
  })
}

/**
 * 取消预约
 * @param {number} id - 预约ID
 * @param {string} reason - 取消原因
 * @returns {Promise}
 */
export function cancelAppointment(id, reason) {
  return request({
    url: `/api/service/appointments/${id}/cancel`,
    method: 'PUT',
    data: { reason }
  })
}

/**
 * 获取可用时间段
 * @param {string} date - 日期
 * @returns {Promise}
 */
export function getAvailableTimeSlots(date) {
  return request({
    url: '/api/service/time-slots',
    method: 'GET',
    params: { date }
  })
}

/**
 * 获取服务项目列表
 * @returns {Promise}
 */
export function getServices() {
  return request({
    url: '/api/service/items',
    method: 'GET'
  })
}

/**
 * 获取宠物列表
 * @returns {Promise}
 */
export function getPets() {
  return request({
    url: '/api/pets',
    method: 'GET'
  })
}
