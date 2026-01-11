// src/stores/pet.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request.js'

export const usePetStore = defineStore('pet', () => {
  const pets = ref([])
  const currentPet = ref(null)
  const loading = ref(false)
  const error = ref('')

  const userStore = useUserStore()

  // 获取用户所有宠物
  const fetchUserPets = async () => {
    loading.value = true
    error.value = ''
    try {
      const response = await request({
        url: '/api/pets',
        method: 'GET'
      })
      if (response.code === 200) {
        pets.value = response.data || []
      } else {
        throw new Error(response.message)
      }
    } catch (err) {
      console.error('获取宠物列表失败:', err)
      error.value = err.message
      // 开发阶段：设置示例数据
      setExamplePets()
    } finally {
      loading.value = false
    }
  }

  // 开发用示例数据
  const setExamplePets = () => {
    pets.value = [
      {
        id: 1,
        name: '巧克力',
        petType: '狗',
        breed: '金毛寻回犬',
        gender: '公',
        birthDate: '2020-06-15',
        color: '金色',
        weight: 28.0,
        personality: '温顺、友好、贪吃',
        notes: '喜欢玩接球游戏，每天需要散步两次，对小朋友特别友善。',
        createdAt: '2023-01-01T10:00:00'
      },
      {
        id: 2,
        name: '棉花糖',
        petType: '猫',
        breed: '布偶猫',
        gender: '母',
        birthDate: '2021-08-10',
        color: '白色带棕色斑点',
        weight: 4.5,
        personality: '温顺、粘人、安静',
        notes: '喜欢被抚摸下巴，不喜欢独处，每天需要梳理毛发。',
        createdAt: '2023-02-15T14:30:00'
      }
    ]
  }

  // 获取宠物详情
  const fetchPetDetail = async (id) => {
    loading.value = true
    error.value = ''
    try {
      const response = await request({
        url: `/api/pets/${id}`,
        method: 'GET'
      })
      if (response.code === 200) {
        currentPet.value = response.data
      } else {
        throw new Error(response.message)
      }
    } catch (err) {
      console.error('获取宠物详情失败:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  // 添加宠物
  const addPet = async (petData) => {
    loading.value = true
    error.value = ''
    try {
      // 动态导入 request 模块
      const { default: requestModule } = await import('@/utils/request.js')

      // 从 userStore.userInfo.id 获取用户ID
      const userId = userStore.userInfo?.id
      if (!userId) {
        console.error('用户ID获取失败:', {
          userInfo: userStore.userInfo,
          user: userStore.user
        })
        throw new Error('用户未登录，无法添加宠物')
      }

      console.log('添加宠物 - 用户ID:', userId)

      // 添加 userId 到请求数据中
      const requestData = {
        ...petData,
        userId: parseInt(userId), // 后端需要 Integer 类型
      }

      console.log('添加宠物 - 请求数据:', requestData)

      const response = await requestModule({
        url: '/api/pets',
        method: 'POST',
        data: requestData
      })

      if (response.code === 200) {
        await fetchUserPets()
        return response.data
      } else {
        throw new Error(response.message)
      }
    } catch (err) {
      console.error('添加宠物失败:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新宠物
  const updatePet = async (id, petData) => {
    loading.value = true
    error.value = ''
    try {
      // 从 userStore.userInfo.id 获取用户ID
      const userId = userStore.userInfo?.id
      if (!userId) {
        console.error('用户ID获取失败:', {
          userInfo: userStore.userInfo,
          user: userStore.user
        })
        throw new Error('用户未登录，无法更新宠物信息')
      }

      console.log('更新宠物 - 用户ID:', userId)

      // 添加 userId 到请求数据中
      const requestData = {
        ...petData,
        userId: parseInt(userId), // 后端需要 Integer 类型
      }

      console.log('更新宠物 - 请求数据:', requestData)

      const response = await request({
        url: `/api/pets/${id}`,
        method: 'PUT',
        data: requestData
      })

      if (response.code === 200) {
        currentPet.value = response.data

        // 更新列表中的宠物信息
        const index = pets.value.findIndex(pet => pet.id === id)
        if (index !== -1) {
          pets.value[index] = { ...pets.value[index], ...petData }
        }

        return response.data
      } else {
        throw new Error(response.message)
      }
    } catch (err) {
      console.error('更新宠物失败:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  // 删除宠物
  const deletePet = async (id) => {
    loading.value = true
    error.value = ''
    try {
      const response = await request({
        url: `/api/pets/${id}`,
        method: 'DELETE'
      })

      if (response.code === 200) {
        // 从列表中移除
        const index = pets.value.findIndex(pet => pet.id === id)
        if (index !== -1) {
          pets.value.splice(index, 1)
        }
      } else {
        throw new Error(response.message)
      }
    } catch (err) {
      console.error('删除宠物失败:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取宠物数量
  const getPetCount = async () => {
    try {
      const response = await request({
        url: '/api/pets/count',
        method: 'GET'
      })
      return response.data?.count || 0
    } catch (err) {
      console.error('获取宠物数量失败:', err)
      return pets.value.length
    }
  }

  // 计算属性
  const hasPets = computed(() => pets.value.length > 0)
  const petCount = computed(() => pets.value.length)

  // 根据宠物类型获取图标
  const getPetTypeIcon = (petType) => {
    const icons = {
      '狗': 'mdi:dog',
      '猫': 'mdi:cat',
      '兔兔': 'mdi:rabbit',
      '小鸟': 'mdi:bird',
      '其他': 'mdi:paw'
    }
    return icons[petType] || icons['其他']
  }

  // 根据宠物类型获取颜色
  const getPetTypeColor = (petType) => {
    const colors = {
      '狗': '#8B7355',
      '猫': '#FF9A3C',
      '兔兔': '#FFB6C1',
      '小鸟': '#87CEEB',
      '其他': '#A0A0A0'
    }
    return colors[petType] || colors['其他']
  }

  return {
    // 状态
    pets,
    currentPet,
    loading,
    error,

    // 方法
    fetchUserPets,
    fetchPetDetail,
    addPet,
    updatePet,
    deletePet,
    getPetCount,
    getPetTypeIcon,
    getPetTypeColor,

    // 计算属性
    hasPets,
    petCount
  }
})
