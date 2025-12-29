<!-- src/views/PetManageView.vue -->
<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePetStore } from '@/stores/pet'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const petStore = usePetStore()
const userStore = useUserStore()

// 获取年龄显示
const getAgeDisplay = (birthDate) => {
  if (!birthDate) return '未知'
  
  const birth = new Date(birthDate)
  const now = new Date()
  let years = now.getFullYear() - birth.getFullYear()
  let months = now.getMonth() - birth.getMonth()
  
  if (months < 0) {
    years--
    months += 12
  }
  
  if (years === 0) {
    return months + '个月'
  } else if (months === 0) {
    return years + '岁'
  } else {
    return years + '岁' + months + '个月'
  }
}

// 获取体重显示
const getWeightDisplay = (weight) => {
  if (!weight) return '未记录'
  return `${weight}kg`
}

// 删除宠物
const deletePet = async (petId, petName) => {
  if (!confirm(`确定要删除宠物 "${petName}" 吗？此操作不可撤销。`)) {
    return
  }
  
  try {
    await petStore.deletePet(petId)
    alert(`宠物"${petName}"已成功删除`)
  } catch (err) {
    alert(err.message || '删除失败，请重试')
  }
}

// 导航到添加页面
const navigateToAdd = () => {
  router.push('/pets/add')
}

// 导航到编辑页面
const navigateToEdit = (petId) => {
  router.push(`/pets/${petId}/edit`)
}

// 刷新数据
const refreshData = () => {
  petStore.fetchUserPets()
}

// 组件挂载时加载数据
onMounted(() => {
  petStore.fetchUserPets()
})
</script>

<template>
  <div class="pet-manage-view">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <Icon icon="mdi:paw" class="title-icon" />
        我的宠物档案
      </h1>
      <p class="page-subtitle">
        管理您的宠物信息，记录它们的成长点滴
        <span class="pet-count">{{ petStore.pets.length }} 只宠物</span>
        <button @click="refreshData" class="refresh-btn" :disabled="petStore.loading">
          <Icon icon="mdi:refresh" :class="{ 'spin': petStore.loading }" />
        </button>
      </p>
    </div>

    <!-- 加载状态 -->
    <div v-if="petStore.loading" class="loading-container">
      <div class="loading-spinner">
        <Icon icon="mdi:loading" spin />
      </div>
      <p>加载中...</p>
    </div>

    <!-- 错误信息 -->
    <div v-else-if="petStore.error" class="error-container">
      <div class="error-icon">
        <Icon icon="mdi:alert-circle" />
      </div>
      <h3>加载失败</h3>
      <p>{{ petStore.error }}</p>
      <button @click="refreshData" class="retry-btn">
        <Icon icon="mdi:refresh" />
        重新加载
      </button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!petStore.hasPets" class="empty-state">
      <div class="empty-icon">
        <Icon icon="mdi:cat" />
      </div>
      <h3>还没有宠物档案哦</h3>
      <p>快添加你的第一个宠物伙伴吧！记录下TA的点点滴滴，让爱永不褪色。</p>
      <button @click="navigateToAdd" class="add-first-pet-btn">
        <Icon icon="mdi:plus-circle" />
        添加第一只宠物
      </button>
    </div>

    <!-- 宠物列表 -->
    <div v-else class="pets-container">
      <!-- 添加宠物卡片 -->
      <div class="pet-card add-pet-card" @click="navigateToAdd">
        <div class="add-pet-icon">
          <Icon icon="mdi:plus-circle" />
        </div>
        <h3>添加新宠物</h3>
        <p>记录新的宠物伙伴</p>
      </div>

      <!-- 宠物卡片 -->
      <div 
        v-for="pet in petStore.pets" 
        :key="pet.id" 
        class="pet-card" 
        :style="{ 'border-top-color': petStore.getPetTypeColor(pet.petType) }"
      >
        <div class="pet-card-header">
          <div 
            class="pet-avatar" 
            :style="{ backgroundColor: petStore.getPetTypeColor(pet.petType) + '20' }"
          >
            <Icon 
              :icon="petStore.getPetTypeIcon(pet.petType)" 
              :style="{ color: petStore.getPetTypeColor(pet.petType) }" 
            />
          </div>
          <div class="pet-basic-info">
            <div class="pet-name-row">
              <h3 class="pet-name">{{ pet.name }}</h3>
              <span 
                class="pet-type" 
                :style="{ 
                  backgroundColor: petStore.getPetTypeColor(pet.petType) + '20', 
                  color: petStore.getPetTypeColor(pet.petType) 
                }"
              >
                {{ pet.petType }}
              </span>
            </div>
            <div class="pet-meta">
              <span class="pet-gender">
                <Icon icon="mdi:gender-male-female" />
                {{ pet.gender || '未知' }}
              </span>
              <span class="pet-age">
                <Icon icon="mdi:cake" />
                {{ getAgeDisplay(pet.birthDate) }}
              </span>
            </div>
          </div>
        </div>

        <div class="pet-details">
          <div class="pet-detail-item">
            <Icon icon="mdi:dna" class="detail-icon" />
            <div class="detail-content">
              <span class="detail-label">品种</span>
              <span class="detail-value">{{ pet.breed || '未记录' }}</span>
            </div>
          </div>
          <div class="pet-detail-item">
            <Icon icon="mdi:weight" class="detail-icon" />
            <div class="detail-content">
              <span class="detail-label">体重</span>
              <span class="detail-value">{{ getWeightDisplay(pet.weight) }}</span>
            </div>
          </div>
          <div class="pet-detail-item">
            <Icon icon="mdi:palette" class="detail-icon" />
            <div class="detail-content">
              <span class="detail-label">毛色</span>
              <span class="detail-value">{{ pet.color || '未记录' }}</span>
            </div>
          </div>
          <div class="pet-detail-item">
            <Icon icon="mdi:heart" class="detail-icon" />
            <div class="detail-content">
              <span class="detail-label">性格</span>
              <span class="detail-value">{{ pet.personality || '未记录' }}</span>
            </div>
          </div>
        </div>

        <div class="pet-card-footer">
          <button @click="navigateToEdit(pet.id)" class="action-btn edit-btn">
            <Icon icon="mdi:pencil" />
            编辑
          </button>
          <button @click="deletePet(pet.id, pet.name)" class="action-btn delete-btn">
            <Icon icon="mdi:trash-can" />
            删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pet-manage-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 120px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 40px;
  text-align: center;
}

.page-title {
  font-size: 2.5rem;
  color: var(--pet-primary-dark);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.title-icon {
  font-size: 2.5rem;
  color: var(--pet-primary);
}

.page-subtitle {
  font-size: 1.1rem;
  color: var(--pet-text-secondary);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.pet-count {
  display: inline-block;
  background-color: var(--pet-primary-light);
  color: var(--pet-text-light);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.refresh-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--pet-primary);
  padding: 4px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.refresh-btn:hover:not(:disabled) {
  background-color: var(--pet-primary-lighter);
  transform: rotate(45deg);
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.refresh-btn .spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  font-size: 3rem;
  color: var(--pet-primary);
  margin-bottom: 20px;
}

.loading-container p {
  color: var(--pet-text-secondary);
  font-size: 1.1rem;
}

/* 错误状态 */
.error-container {
  text-align: center;
  padding: 60px 20px;
  background-color: #fff5f5;
  border-radius: 15px;
  border: 2px solid #ffcccc;
}

.error-icon {
  font-size: 3rem;
  color: #ff4757;
  margin-bottom: 20px;
}

.error-container h3 {
  font-size: 1.5rem;
  color: #ff4757;
  margin-bottom: 10px;
}

.error-container p {
  color: #666;
  margin-bottom: 25px;
}

.retry-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  background-color: #ff2e43;
  transform: translateY(-2px);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background-color: var(--pet-bg-secondary);
  border-radius: 20px;
  border: 2px dashed var(--pet-primary-light);
}

.empty-icon {
  font-size: 4rem;
  color: var(--pet-primary-light);
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 1.8rem;
  color: var(--pet-primary-dark);
  margin-bottom: 15px;
}

.empty-state p {
  color: var(--pet-text-secondary);
  max-width: 500px;
  margin: 0 auto 30px;
  line-height: 1.6;
}

.add-first-pet-btn {
  background: linear-gradient(to right, var(--pet-primary), var(--pet-primary-dark));
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 154, 60, 0.3);
}

.add-first-pet-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 154, 60, 0.4);
}

/* 宠物容器 */
.pets-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
  margin-top: 20px;
}

/* 宠物卡片 */
.pet-card {
  background-color: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-top: 5px solid var(--pet-primary);
  cursor: default;
}

.pet-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
}

/* 添加宠物卡片 */
.add-pet-card {
  border: 2px dashed var(--pet-primary-light);
  background-color: var(--pet-primary-lighter);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-top: 5px solid var(--pet-primary-light);
}

.add-pet-card:hover {
  background-color: var(--pet-primary-light);
  transform: translateY(-8px);
  border-color: var(--pet-primary);
}

.add-pet-icon {
  font-size: 3rem;
  color: var(--pet-primary);
  margin-bottom: 15px;
}

.add-pet-card h3 {
  font-size: 1.5rem;
  color: var(--pet-primary-dark);
  margin-bottom: 10px;
}

.add-pet-card p {
  color: var(--pet-text-secondary);
  font-size: 0.95rem;
}

/* 宠物卡片头部 */
.pet-card-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
}

.pet-avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 2.5rem;
}

.pet-basic-info {
  flex: 1;
}

.pet-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.pet-name {
  font-size: 1.6rem;
  color: var(--pet-text-primary);
  margin: 0;
  font-weight: 700;
}

.pet-type {
  font-size: 0.8rem;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.pet-meta {
  display: flex;
  gap: 15px;
  font-size: 0.9rem;
  color: var(--pet-text-secondary);
}

.pet-gender,
.pet-age {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 宠物详情 */
.pet-details {
  margin-bottom: 25px;
}

.pet-detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.pet-detail-item:last-child {
  border-bottom: none;
}

.detail-icon {
  font-size: 1.2rem;
  color: var(--pet-primary);
  flex-shrink: 0;
}

.detail-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 0.9rem;
  color: var(--pet-text-secondary);
  font-weight: 500;
}

.detail-value {
  font-size: 0.95rem;
  color: var(--pet-text-primary);
  font-weight: 600;
  text-align: right;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 宠物卡片底部 */
.pet-card-footer {
  display: flex;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  flex: 1;
  padding: 10px 15px;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  border: none;
}

.edit-btn {
  background-color: var(--pet-primary-lighter);
  color: var(--pet-primary-dark);
}

.edit-btn:hover {
  background-color: var(--pet-primary-light);
  color: white;
  transform: translateY(-2px);
}

.delete-btn {
  background-color: #fff5f5;
  color: #ff4757;
}

.delete-btn:hover {
  background-color: #ff4757;
  color: white;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .pets-container {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}

@media (max-width: 992px) {
  .pet-manage-view {
    padding: 20px 15px;
  }
  
  .page-title {
    font-size: 2.2rem;
  }
  
  .pets-container {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 25px;
  }
}

@media (max-width: 768px) {
  .pets-container {
    grid-template-columns: 1fr;
    max-width: 500px;
    margin: 0 auto;
  }
  
  .page-title {
    font-size: 2rem;
    flex-direction: column;
    gap: 10px;
  }
  
  .pet-card {
    padding: 20px;
  }
  
  .pet-card-header {
    gap: 15px;
  }
  
  .pet-avatar {
    width: 60px;
    height: 60px;
    font-size: 2rem;
  }
  
  .pet-name {
    font-size: 1.4rem;
  }
}

@media (max-width: 480px) {
  .pet-manage-view {
    padding: 15px 10px;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
    flex-direction: column;
    gap: 5px;
  }
  
  .pets-container {
    gap: 20px;
  }
  
  .pet-card {
    padding: 15px;
  }
  
  .pet-card-footer {
    flex-direction: column;
    gap: 8px;
  }
  
  .action-btn {
    padding: 12px;
  }
}
</style>