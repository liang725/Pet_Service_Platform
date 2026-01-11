// src/api/pet.js
import request from '@/utils/request'

/**
 * 宠物相关API
 */

// 获取用户所有宠物
export const getUserPets = () => {
  return request({
    url: '/api/pets',
    method: 'GET'
  })
}

// 根据ID获取宠物详情
export const getPetById = (id) => {
  return request({
    url: `/api/pets/${id}`,
    method: 'GET'
  })
}

// 添加新宠物
export const addPet = (petData) => {
  return request({
    url: '/api/pets',
    method: 'POST',
    data: petData
  })
}

// 更新宠物信息
export const updatePet = (id, petData) => {
  return request({
    url: `/api/pets/${id}`,
    method: 'PUT',
    data: petData
  })
}

// 删除宠物
export const deletePet = (id) => {
  return request({
    url: `/api/pets/${id}`,
    method: 'DELETE'
  })
}

// 获取用户宠物数量
export const getPetCount = () => {
  return request({
    url: '/api/pets/count',
    method: 'GET'
  })
}
