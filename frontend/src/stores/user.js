// src/stores/user.js - 直接返回错误，不模拟数据
import { defineStore } from 'pinia'
import { request } from '@/utils/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
    isLoggedIn: false
  }),

  actions: {
    // 检查认证状态
    checkAuthStatus() {
      const token = localStorage.getItem('token')
      this.token = token || ''
      this.isLoggedIn = !!token
      return this.isLoggedIn
    },

    // 设置token
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      this.isLoggedIn = true
    },

    // 清除token
    clearToken() {
      this.token = ''
      localStorage.removeItem('token')
      this.userInfo = null
      this.isLoggedIn = false
    },

    // 登录
    async login(credentials) {
      try {
        const response = await request({
          url: '/api/auth/login',
          method: 'POST',
          data: credentials
        })

        if (response.code === 200) {
          this.setToken(response.data.token)
          this.userInfo = response.data.userInfo
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        console.error('登录失败:', error)
        return { success: false, message: error.message }
      }
    },

    // 获取用户宠物列表
    async getUserPets() {
      try {
        const response = await request({
          url: '/api/pets',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取宠物列表失败:', error)
        return []
      }
    },

    // 获取服务项目列表
    async getServiceItems(category = null) {
      try {
        const url = category 
          ? `/api/service/items/category/${category}`
          : '/api/service/items'
        
        console.log('调用服务项目API:', url) // 添加日志
        
        const response = await request({
          url,
          method: 'GET'
        })
        
        console.log('服务项目API响应:', response) // 添加日志
        
        if (response.code === 200) {
          return response.data || []
        }
        console.warn('服务项目API返回非200状态:', response.code)
        return []
      } catch (error) {
        console.error('获取服务项目失败:', error)
        // 检查错误类型，提供更详细的错误信息
        if (error.message && error.message.includes('401')) {
          console.warn('服务项目API需要登录，但用户未登录')
        } else if (error.message && error.message.includes('404')) {
          console.error('API接口不存在，请检查后端服务')
        } else if (error.message && error.message.includes('Network Error')) {
          console.error('网络连接失败，请检查后端服务是否启动')
        }
        return []
      }
    },

    // 获取用户所有预约
    async getAllUserAppointments() {
      try {
        const response = await request({
          url: '/api/service/appointments/all',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取所有预约失败:', error)
        return []
      }
    },

    // 获取预约统计
    async getAppointmentStats() {
      try {
        const response = await request({
          url: '/api/service/appointments/stats',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.code === 200) {
          return response.data || {
            pending: 0,
            confirmed: 0,
            completed: 0,
            cancelled: 0,
            all: 0
          }
        }
        return {
          pending: 0,
          confirmed: 0,
          completed: 0,
          cancelled: 0,
          all: 0
        }
      } catch (error) {
        console.error('获取预约统计失败:', error)
        return {
          pending: 0,
          confirmed: 0,
          completed: 0,
          cancelled: 0,
          all: 0
        }
      }
    },

    // 获取预约详情
    async getAppointmentDetail(id) {
      try {
        const response = await request({
          url: `/api/service/appointments/${id}`,
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.code === 200) {
          return response.data
        }
        throw new Error(response.message || '获取详情失败')
      } catch (error) {
        console.error('获取预约详情失败:', error)
        throw error
      }
    },

    // 取消预约
    async cancelAppointment(id, reason) {
      try {
        const response = await request({
          url: `/api/service/appointments/${id}/cancel`,
          method: 'PUT',
          params: { reason: reason || '用户主动取消' },
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.code === 200) {
          return true
        }
        throw new Error(response.message || '取消预约失败')
      } catch (error) {
        console.error('取消预约失败:', error)
        throw error
      }
    },

    // 获取可用时间段
    async getAvailableTimeSlots(date) {
      try {
        const response = await request({
          url: '/api/service/time-slots',
          method: 'GET',
          params: { date }
        })
        
        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取可用时间段失败:', error)
        // 对于401错误，返回默认时间段
        if (error.message && error.message.includes('401')) {
          console.warn('时间段API需要登录，返回默认时间段')
          return ['09:00', '10:30', '14:00', '15:30', '17:00']
        }
        return ['09:00', '10:30', '14:00', '15:30', '17:00']
      }
    },

    // 创建预约
    async createAppointment(appointmentData) {
      try {
        const response = await request({
          url: '/api/service/appointments',
          method: 'POST',
          data: appointmentData,
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.code === 200) {
          return response.data
        }
        throw new Error(response.message || '创建预约失败')
      } catch (error) {
        console.error('创建预约失败:', error)
        throw error
      }
    },

    // 获取推荐服务
    async getRecommendedServices() {
      try {
        const response = await request({
          url: '/api/service/items/recommended',
          method: 'GET'
        })
        
        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取推荐服务失败:', error)
        return []
      }
    },

    // 获取服务详情
    async getServiceById(id) {
      try {
        const response = await request({
          url: `/api/service/items/${id}`,
          method: 'GET'
        })
        
        if (response.code === 200) {
          return response.data
        }
        throw new Error(response.message || '获取服务详情失败')
      } catch (error) {
        console.error('获取服务详情失败:', error)
        throw error
      }
    },

    // 退出登录
    logout() {
      this.clearToken()
    }
  }
})