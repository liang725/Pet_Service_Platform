// src/stores/user.js - 删除报错的getUserAppointments方法
import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
    isLoggedIn: false,
    userRole: localStorage.getItem('userRole') || '' // 用户角色
  }),

  getters: {
    // 检查是否为管理员
    isAdmin: (state) => {
      return state.userRole === 'admin'
    },

    // 获取显示名称
    getDisplayName: (state) => {
      if (state.userInfo) {
        return state.userInfo.nickname || state.userInfo.username || state.userInfo.name || '用户'
      }
      return '游客'
    },

    // 获取头像字符
    getAvatar: (state) => {
      if (state.userInfo) {
        const name = state.userInfo.username || state.userInfo.name || state.userInfo.nickname || '用户'
        return name.charAt(0).toUpperCase()
      }
      return '游'
    }
  },

  actions: {
    // 检查认证状态
    checkAuthStatus() {
      const token = localStorage.getItem('token')
      const role = localStorage.getItem('userRole')
      this.token = token || ''
      this.userRole = role || ''
      this.isLoggedIn = !!token
      return this.isLoggedIn
    },

    // 设置token和用户信息
    setToken(token, userInfo = null) {
      this.token = token
      localStorage.setItem('token', token)
      this.isLoggedIn = true

      if (userInfo) {
        this.userInfo = userInfo
        this.userRole = userInfo.role || 'user'
        localStorage.setItem('userRole', this.userRole)
      }
    },

    // 清除token
    clearToken() {
      this.token = ''
      this.userInfo = null
      this.userRole = ''
      this.isLoggedIn = false
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
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
          this.setToken(response.data.token, response.data.userInfo || response.data)
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        console.error('登录失败:', error)
        return { success: false, message: error.message }
      }
    },

    // 注册
    async register(registerData) {
      try {
        const response = await request({
          url: '/api/auth/register',
          method: 'POST',
          data: registerData
        })

        if (response.code === 200) {
          return { success: true, data: response.data }
        } else {
          throw new Error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        throw new Error(error.message || '注册失败，请重试')
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

    // 获取所有护理项目
    async getCareItems() {
      try {
        const response = await request({
          url: '/api/service/care/items',
          method: 'GET'
        })

        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取护理项目失败:', error)
        return []
      }
    },

    // 获取按分类的护理项目
    async getCareItemsByCategory(category) {
      try {
        const response = await request({
          url: `/api/service/care/items/category/${encodeURIComponent(category)}`,
          method: 'GET'
        })

        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取护理项目失败:', error)
        return []
      }
    },

    // 获取分组护理项目
    async getGroupedCareItems() {
      try {
        const response = await request({
          url: '/api/service/care/items/grouped',
          method: 'GET'
        })

        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取分组护理项目失败:', error)
        return []
      }
    },

    // 获取指定日期的预约次数
    async getAppointmentCountByDate(date) {
      try {
        const response = await request({
          url: '/api/service/appointments/date-count',
          method: 'GET',
          params: { date },
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200) {
          return response.data || { count: 0 }
        }
        return { count: 0 }
      } catch (error) {
        console.error('获取预约次数失败:', error)
        return { count: 0 }
      }
    },

    // 获取每日预约次数统计
    async getDailyAppointmentCounts(startDate, endDate) {
      try {
        const response = await request({
          url: '/api/service/appointments/daily-counts',
          method: 'GET',
          params: { startDate, endDate },
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取每日预约统计失败:', error)
        return []
      }
    },

    // 获取用户所有预约 - 保留这个可以正常使用的接口
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
async getAppointmentDetail(id, type = 'detail') {
  try {
    // 使用新接口获取包含服务项目和护理项目的详情
    const response = await request({
      url: `/api/service/appointments/${id}?type=${type}`, // 使用主接口，通过type参数控制
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${this.token}`
      }
    })

    if (response.code === 200) {
      console.log('获取预约详情成功:', response.data);
      return response.data;
    }
    throw new Error(response.message || '获取详情失败');
  } catch (error) {
    console.error('获取预约详情失败:', error);
    throw error;
  }
},

// 辅助方法：将旧接口数据转换为新接口格式
transformOldDataToNewFormat(oldData) {
  // 如果已经是新格式，直接返回
  if (oldData.services || oldData.careItems) {
    return oldData;
  }

  // 转换逻辑
  const newData = {
    ...oldData,
    services: [],
    careItems: []
  };

  // 如果有serviceName字段，创建一个服务项
  if (oldData.serviceName) {
    newData.services.push({
      serviceId: 0, // 未知ID
      serviceName: oldData.serviceName,
      category: oldData.serviceCategory || '洗护服务',
      description: oldData.serviceDescription || '',
      basePrice: oldData.orderTotalAmount || 0,
      discountPercentage: 0,
      discountAmount: 0,
      finalPrice: oldData.orderTotalAmount || 0,
      duration: 60,
      quantity: 1,
      thumbnailUrl: oldData.serviceImage || ''
    });
  }

  return newData;
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

    // 获取用户信息
    async fetchUserInfo() {
  try {
    if (!this.token) {
      console.warn('未登录，无法获取用户信息')
      return null
    }

    const response = await request({
      url: '/api/user/info',
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${this.token}`
      }
    })

    if (response.code === 200) {
      this.userInfo = response.data
      this.userRole = response.data.role || 'user'  // 确保角色信息正确
      localStorage.setItem('userRole', this.userRole)
      return response.data
    } else {
      console.warn('获取用户信息失败:', response.message)
      return null
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)

    // 如果是401错误，清除token
    if (error.message && error.message.includes('401')) {
      this.clearToken()
    }
    return null
  }
},

    // 获取用户收藏列表
    async getUserFavorites() {
      try {
        const response = await request({
          url: '/api/favorites',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取收藏列表失败:', error)
        return []
      }
    },

    // 添加收藏
    async addFavorite(productId) {
      try {
        const response = await request({
          url: '/api/favorites',
          method: 'POST',
          data: { productId },
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return true
        }
        throw new Error(response.message || '添加收藏失败')
      } catch (error) {
        console.error('添加收藏失败:', error)
        throw error
      }
    },

    // 取消收藏
    async removeFavorite(productId) {
      try {
        const response = await request({
          url: `/api/favorites/${productId}`,
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return true
        }
        throw new Error(response.message || '取消收藏失败')
      } catch (error) {
        console.error('取消收藏失败:', error)
        throw error
      }
    },

    // 获取用户地址列表
    async getUserAddresses() {
      try {
        const response = await request({
          url: '/api/addresses',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取地址列表失败:', error)
        return []
      }
    },

    // 添加地址
    async addAddress(addressData) {
      try {
        const response = await request({
          url: '/api/addresses',
          method: 'POST',
          data: addressData,
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return response.data
        }
        throw new Error(response.message || '添加地址失败')
      } catch (error) {
        console.error('添加地址失败:', error)
        throw error
      }
    },

    // 更新地址
    async updateAddress(addressId, addressData) {
      try {
        const response = await request({
          url: `/api/addresses/${addressId}`,
          method: 'PUT',
          data: addressData,
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return response.data
        }
        throw new Error(response.message || '更新地址失败')
      } catch (error) {
        console.error('更新地址失败:', error)
        throw error
      }
    },

    // 获取用户订单列表
    async getUserOrders(status = null) {
      try {
        const url = status ? `/api/orders?status=${status}` : '/api/orders'
        const response = await request({
          url,
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return response.data || []
        }
        return []
      } catch (error) {
        console.error('获取订单列表失败:', error)
        return []
      }
    },

    // 获取订单详情
    async getOrderDetail(orderId) {
      try {
        const response = await request({
          url: `/api/orders/${orderId}`,
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return response.data
        }
        throw new Error(response.message || '获取订单详情失败')
      } catch (error) {
        console.error('获取订单详情失败:', error)
        throw error
      }
    },

    // 取消订单
    async cancelOrder(orderId, reason) {
      try {
        const response = await request({
          url: `/api/orders/${orderId}/cancel`,
          method: 'PUT',
          data: { reason: reason || '用户主动取消' },
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return true
        }
        throw new Error(response.message || '取消订单失败')
      } catch (error) {
        console.error('取消订单失败:', error)
        throw error
      }
    },

    // 确认收货
    async confirmOrder(orderId) {
      try {
        const response = await request({
          url: `/api/orders/${orderId}/confirm`,
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return true
        }
        throw new Error(response.message || '确认收货失败')
      } catch (error) {
        console.error('确认收货失败:', error)
        throw error
      }
    },

    // 删除地址
    async deleteAddress(addressId) {
      try {
        const response = await request({
          url: `/api/addresses/${addressId}`,
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200 || response.success === true) {
          return true
        }
        throw new Error(response.message || '删除地址失败')
      } catch (error) {
        console.error('删除地址失败:', error)
        throw error
      }
    },

    // 退出登录
    logout() {
      this.clearToken()
    },

    // 获取管理员仪表板数据
    async getAdminDashboard() {
      try {
        const response = await request({
          url: '/api/admin/dashboard',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200) {
          return response.data
        }
        throw new Error(response.message || '获取仪表板数据失败')
      } catch (error) {
        console.error('获取管理员仪表板数据失败:', error)
        throw error
      }
    },

    // 获取用户列表（管理员）
    async getAdminUserList() {
      try {
        const response = await request({
          url: '/api/admin/users',
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.code === 200) {
          return response.data || []
        }
        throw new Error(response.message || '获取用户列表失败')
      } catch (error) {
        console.error('获取用户列表失败:', error)
        throw error
      }
    }
  }
})
