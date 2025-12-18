import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref(null)
  const token = ref(null)
  const isLoggedIn = computed(() => !!user.value && !!token.value)

  // 从本地存储恢复用户状态
  const initializeAuth = () => {
    const savedUser = localStorage.getItem('petHome_user')
    const savedToken = localStorage.getItem('petHome_token')

    if (savedUser && savedToken) {
      try {
        user.value = JSON.parse(savedUser)
        token.value = savedToken
      } catch (error) {
        console.error('Failed to parse saved user data:', error)
        localStorage.removeItem('petHome_user')
        localStorage.removeItem('petHome_token')
      }
    }
  }

  // API请求函数
  const apiRequest = async (url, options = {}) => {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...(token.value && { 'Authorization': `Bearer ${token.value}` }),
        ...options.headers,
      },
      ...options,
    })

    const data = await response.json()

    if (!response.ok) {
      throw new Error(data.message || '请求失败')
    }

    return data
  }

  // 注册
  const register = async (registerData) => {
    try {
      const response = await apiRequest('/auth/register', {
        method: 'POST',
        body: JSON.stringify(registerData),
      })

      return response
    } catch (error) {
      throw error
    }
  }

  // 登录
  const login = async (loginData, remember = false) => {
    try {
      const response = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify(loginData),
      })

      if (response.code === 200) {
        const userData = response.data

        user.value = {
          id: userData.userId,
          username: userData.username,
          nickname: userData.nickname || userData.username,
          avatar: userData.avatar || '🐾',
          loginTime: new Date().toISOString(),
        }

        token.value = userData.token

        // 如果选择记住登录状态，保存到本地存储
        if (remember) {
          localStorage.setItem('petHome_user', JSON.stringify(user.value))
          localStorage.setItem('petHome_token', token.value)
        } else {
          // 否则保存到会话存储
          sessionStorage.setItem('petHome_user', JSON.stringify(user.value))
          sessionStorage.setItem('petHome_token', token.value)
        }

        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  }

  // 退出登录
  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('petHome_user')
    localStorage.removeItem('petHome_token')
    sessionStorage.removeItem('petHome_user')
    sessionStorage.removeItem('petHome_token')
  }

  // 更新用户信息
  const updateProfile = (profileData) => {
    if (user.value) {
      user.value = {
        ...user.value,
        ...profileData,
        updatedAt: new Date().toISOString(),
      }

      // 更新存储的用户信息
      const savedUser =
        localStorage.getItem('petHome_user') || sessionStorage.getItem('petHome_user')
      if (savedUser) {
        if (localStorage.getItem('petHome_user')) {
          localStorage.setItem('petHome_user', JSON.stringify(user.value))
        } else {
          sessionStorage.setItem('petHome_user', JSON.stringify(user.value))
        }
      }
    }
  }

  // 检查登录状态
  const checkAuthStatus = () => {
    const localUser = localStorage.getItem('petHome_user')
    const localToken = localStorage.getItem('petHome_token')
    const sessionUser = sessionStorage.getItem('petHome_user')
    const sessionToken = sessionStorage.getItem('petHome_token')

    if (localUser && localToken) {
      try {
        user.value = JSON.parse(localUser)
        token.value = localToken
        return true
      } catch (error) {
        localStorage.removeItem('petHome_user')
        localStorage.removeItem('petHome_token')
      }
    } else if (sessionUser && sessionToken) {
      try {
        user.value = JSON.parse(sessionUser)
        token.value = sessionToken
        return true
      } catch (error) {
        sessionStorage.removeItem('petHome_user')
        sessionStorage.removeItem('petHome_token')
      }
    }

    return false
  }

  // 获取用户显示名称
  const getDisplayName = computed(() => {
    if (!user.value) return '游客'
    return user.value.nickname || user.value.username
  })

  // 获取用户头像
  const getAvatar = computed(() => {
    if (!user.value) return '👤'
    return user.value.avatar || '🐾'
  })

  // 初始化认证状态
  initializeAuth()

  return {
    // 状态
    user,
    token,
    isLoggedIn,

    // 计算属性
    getDisplayName,
    getAvatar,

    // 方法
    register,
    login,
    logout,
    updateProfile,
    checkAuthStatus,
    initializeAuth,
  }
})
