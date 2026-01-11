<!-- src/views/AddPetView.vue -->
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePetStore } from '@/stores/pet'
import { useUserStore } from '@/stores/user'
import { getUserInfo } from '@/api/user'

const router = useRouter()
const route = useRoute()
const petStore = usePetStore()
const userStore = useUserStore()

// 模式：add 或 edit
const mode = computed(() => route.name === 'editPet' ? 'edit' : 'add')
const petId = ref(route.params.id || null)
const isEditing = computed(() => mode.value === 'edit')

// 表单数据
const formData = ref({
  petType: '',
  name: '',
  breed: '',
  gender: '未知',
  birthDate: '',
  color: '',
  weight: '',
  avatarUrl: '',
  personality: '',
  notes: ''
})

// 表单验证
const errors = ref({})
const submitting = ref(false)

// 宠物类型选项
const petTypeOptions = ref([
  { value: '猫', label: '猫咪', icon: 'mdi:cat' },
  { value: '狗', label: '狗狗', icon: 'mdi:dog' }
])

// 性别选项
const genderOptions = ref([
  { value: '公', label: '公' },
  { value: '母', label: '母' },
  { value: '未知', label: '未知' }
])

// 计算页面标题
const pageTitle = computed(() => {
  return isEditing.value ? '编辑宠物信息' : '添加新宠物'
})

// 添加登录状态检查函数
const checkLoginStatus = () => {
  console.log('检查登录状态 - userStore.userInfo:', userStore.userInfo)
  console.log('检查登录状态 - userStore.token:', userStore.token)
  console.log('检查登录状态 - userStore.isLoggedIn:', userStore.isLoggedIn)

  // 先检查是否已登录
  if (userStore.isLoggedIn && userStore.token) {
    // 如果已登录但没有用户信息，尝试获取
    if (!userStore.userInfo?.id) {
      console.log('已登录但缺少用户信息，尝试获取')
      fetchUserInfo()
      return !!userStore.userInfo?.id
    }
    return true
  }

  // 检查 localStorage
  const storedToken = localStorage.getItem('token')
  const storedUserRole = localStorage.getItem('userRole')

  if (storedToken) {
    console.log('发现 localStorage 中的 token:', storedToken)

    // 如果有 token 但 store 中没有，设置到 store 中
    if (!userStore.token) {
      userStore.token = storedToken
      userStore.userRole = storedUserRole || ''
      userStore.isLoggedIn = true

      // 尝试从 token 中提取用户信息
      try {
        const tokenParts = storedToken.split('.')
        if (tokenParts.length === 3) {
          const payload = JSON.parse(atob(tokenParts[1]))
          console.log('从 token 中提取的用户信息:', payload)

          if (payload.userId) {
            // 设置用户信息
            userStore.userInfo = {
              id: payload.userId,
              username: payload.sub || '用户',
              role: payload.role || 'user'
            }
            console.log('已设置用户信息:', userStore.userInfo)
          }
        }
      } catch (e) {
        console.error('解析 token 失败:', e)
      }
    }

    // 获取完整的用户信息
    if (!userStore.userInfo?.id) {
      fetchUserInfo()
    }

    return !!userStore.userInfo?.id
  }

  // 未登录，跳转到登录页面
  console.log('未登录，跳转到登录页面')
  alert('请先登录后再操作')
  router.push('/auth')
  return false
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    if (!userStore.token) {
      console.log('没有 token，无法获取用户信息')
      return false
    }

    const response = await getUserInfo()
    if (response.code === 200) {
      // 确保正确设置用户信息到 store
      userStore.userInfo = response.data
      userStore.isLoggedIn = true
      console.log('成功获取用户信息:', userStore.userInfo)
      return true
    }
    console.log('获取用户信息失败')
    return false
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return false
  }
}

// 获取宠物详情（编辑模式）
const fetchPetDetail = async () => {
  if (!isEditing.value || !petId.value) return

  // 先检查登录状态
  if (!checkLoginStatus()) {
    return
  }

  try {
    await petStore.fetchPetDetail(petId.value)

    if (petStore.currentPet) {
      const pet = petStore.currentPet
      // 填充表单数据
      formData.value = {
        petType: pet.petType || '',
        name: pet.name || '',
        breed: pet.breed || '',
        gender: pet.gender || '未知',
        birthDate: pet.birthDate || '',
        color: pet.color || '',
        weight: pet.weight || '',
        avatarUrl: pet.avatarUrl || '',
        personality: pet.personality || '',
        notes: pet.notes || ''
      }
    }
  } catch (err) {
    alert(err.message || '获取宠物信息失败')
    router.push('/pets')
  }
}

// 表单验证
const validateForm = () => {
  errors.value = {}
  let isValid = true

  // 验证宠物类型
  if (!formData.value.petType) {
    errors.value.petType = '请选择宠物类型'
    isValid = false
  }

  // 验证名字
  if (!formData.value.name.trim()) {
    errors.value.name = '请输入宠物名字'
    isValid = false
  } else if (formData.value.name.trim().length > 50) {
    errors.value.name = '名字不能超过50个字符'
    isValid = false
  }

  // 验证品种
  if (formData.value.breed && formData.value.breed.length > 100) {
    errors.value.breed = '品种不能超过100个字符'
    isValid = false
  }

  // 验证毛色
  if (formData.value.color && formData.value.color.length > 50) {
    errors.value.color = '毛色描述不能超过50个字符'
    isValid = false
  }

  // 验证体重
  if (formData.value.weight) {
    const weightNum = parseFloat(formData.value.weight)
    if (isNaN(weightNum) || weightNum <= 0) {
      errors.value.weight = '请输入有效的体重（正数）'
      isValid = false
    } else if (weightNum > 100) {
      errors.value.weight = '体重不能超过100kg'
      isValid = false
    }
  }

  // 验证性格描述
  if (formData.value.personality && formData.value.personality.length > 255) {
    errors.value.personality = '性格描述不能超过255个字符'
    isValid = false
  }

  return isValid
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  // 检查登录状态
  if (!checkLoginStatus()) {
    return
  }

  // 确保用户信息存在
  if (!userStore.userInfo?.id) {
    alert('用户信息获取失败，请重新登录')
    router.push('/auth')
    return
  }

  console.log('提交表单 - 当前用户ID:', userStore.userInfo.id)

  try {
    submitting.value = true

    // 准备提交数据
    const submitData = {
      petType: formData.value.petType,
      name: formData.value.name.trim(),
      breed: formData.value.breed.trim() || null,
      gender: formData.value.gender,
      birthDate: formData.value.birthDate || null,
      color: formData.value.color.trim() || null,
      weight: formData.value.weight ? parseFloat(formData.value.weight) : null,
      avatarUrl: formData.value.avatarUrl.trim() || null,
      personality: formData.value.personality.trim() || null,
      notes: formData.value.notes.trim() || null
    }

    console.log('提交表单 - 提交数据:', submitData)
    console.log('提交表单 - 用户ID:', userStore.userInfo.id)

    let result
    if (isEditing.value) {
      result = await petStore.updatePet(petId.value, submitData)
    } else {
      result = await petStore.addPet(submitData)
    }

    const successMessage = isEditing.value ? '更新宠物成功' : '添加宠物成功'
    alert(successMessage)
    router.push('/pets')

  } catch (err) {
    console.error('表单提交错误:', err)
    alert(err.message || '提交失败，请重试')

    // 如果是未登录错误，跳转到登录页
    if (err.message.includes('未登录') || err.message.includes('登录')) {
      router.push('/auth')
    }
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (confirm('确定要重置表单吗？所有输入的内容都将丢失。')) {
    if (isEditing.value) {
      fetchPetDetail()
    } else {
      formData.value = {
        petType: '',
        name: '',
        breed: '',
        gender: '未知',
        birthDate: '',
        color: '',
        weight: '',
        avatarUrl: '',
        personality: '',
        notes: ''
      }
      errors.value = {}
    }
  }
}

// 返回列表
const handleCancel = () => {
  router.push('/pets')
}

// 组件挂载
onMounted(() => {
  // 检查登录状态
  if (!checkLoginStatus()) {
    return
  }

  if (isEditing.value) {
    fetchPetDetail()
  }
})

// 计算当前日期（用于生日最大限制）
const today = ref(new Date().toISOString().split('T')[0])

// 计算最小日期（假设宠物最多能活30年）
const minDate = computed(() => {
  const date = new Date()
  date.setFullYear(date.getFullYear() - 30)
  return date.toISOString().split('T')[0]
})
</script>

<template>
  <div class="add-pet-view">
    <!-- 页面头部 -->
    <div class="page-header">
      <button @click="handleCancel" class="back-btn">
        <Icon icon="mdi:arrow-left" />
        返回列表
      </button>
      <h1 class="page-title">
        <Icon :icon="isEditing ? 'mdi:pencil' : 'mdi:plus-circle'" class="title-icon" />
        {{ pageTitle }}
      </h1>
      <p class="page-subtitle">
        {{ isEditing ? '修改宠物的详细信息' : '填写宠物的详细信息，记录它的点点滴滴' }}
      </p>
    </div>

    <!-- 加载状态 -->
    <div v-if="petStore.loading && isEditing" class="loading-container">
      <div class="loading-spinner">
        <Icon icon="mdi:loading" spin />
      </div>
      <p>加载宠物信息中...</p>
    </div>

    <!-- 表单内容 -->
    <div v-else class="form-container">
      <form @submit.prevent="handleSubmit" class="pet-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <h2 class="section-title">
            <Icon icon="mdi:information" />
            基本信息
          </h2>

          <div class="form-grid">
            <!-- 宠物类型 -->
            <div class="form-group">
              <label for="petType" class="form-label required">
                <Icon icon="mdi:paw" />
                宠物类型
              </label>
              <div class="pet-type-selector">
                <div v-for="option in petTypeOptions" :key="option.value"
                     class="pet-type-option"
                     :class="{ 'selected': formData.petType === option.value }"
                     @click="formData.petType = option.value">
                  <Icon :icon="option.icon" class="option-icon" />
                  <span class="option-label">{{ option.label }}</span>
                </div>
              </div>
              <div v-if="errors.petType" class="error-message">{{ errors.petType }}</div>
            </div>

            <!-- 宠物名字 -->
            <div class="form-group">
              <label for="name" class="form-label required">
                <Icon icon="mdi:tag" />
                宠物名字
              </label>
              <input type="text"
                     id="name"
                     v-model="formData.name"
                     :class="['form-input', { 'error': errors.name }]"
                     placeholder="例如：小白、巧克力"
                     maxlength="50">
              <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
              <div class="input-hint">最多50个字符</div>
            </div>

            <!-- 品种 -->
            <div class="form-group">
              <label for="breed" class="form-label">
                <Icon icon="mdi:dna" />
                品种
              </label>
              <input type="text"
                     id="breed"
                     v-model="formData.breed"
                     :class="['form-input', { 'error': errors.breed }]"
                     placeholder="例如：金毛寻回犬、英国短毛猫"
                     maxlength="100">
              <div v-if="errors.breed" class="error-message">{{ errors.breed }}</div>
              <div class="input-hint">最多100个字符</div>
            </div>

            <!-- 性别 -->
            <div class="form-group">
              <label for="gender" class="form-label required">
                <Icon icon="mdi:gender-male-female" />
                性别
              </label>
              <div class="gender-selector">
                <div v-for="option in genderOptions" :key="option.value"
                     class="gender-option"
                     :class="{ 'selected': formData.gender === option.value }"
                     @click="formData.gender = option.value">
                  <span class="gender-label">{{ option.label }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 外观特征 -->
        <div class="form-section">
          <h2 class="section-title">
            <Icon icon="mdi:palette" />
            外观特征
          </h2>

          <div class="form-grid">
            <!-- 毛色 -->
            <div class="form-group">
              <label for="color" class="form-label">
                <Icon icon="mdi:palette" />
                毛色
              </label>
              <input type="text"
                     id="color"
                     v-model="formData.color"
                     :class="['form-input', { 'error': errors.color }]"
                     placeholder="例如：白色带棕色斑点"
                     maxlength="50">
              <div v-if="errors.color" class="error-message">{{ errors.color }}</div>
              <div class="input-hint">最多50个字符</div>
            </div>

            <!-- 体重 -->
            <div class="form-group">
              <label for="weight" class="form-label">
                <Icon icon="mdi:weight" />
                体重 (kg)
              </label>
              <input type="number"
                     id="weight"
                     v-model="formData.weight"
                     :class="['form-input', { 'error': errors.weight }]"
                     placeholder="例如：5.5"
                     step="0.1"
                     min="0.1"
                     max="100">
              <div v-if="errors.weight" class="error-message">{{ errors.weight }}</div>
              <div class="input-hint">单位：千克</div>
            </div>

            <!-- 生日 -->
            <div class="form-group">
              <label for="birthDate" class="form-label">
                <Icon icon="mdi:cake" />
                出生日期
              </label>
              <input type="date"
                     id="birthDate"
                     v-model="formData.birthDate"
                     class="form-input"
                     :max="today"
                     :min="minDate">
              <div class="input-hint">如果不知道确切日期，可以留空</div>
            </div>
          </div>
        </div>

        <!-- 性格与习惯 -->
        <div class="form-section">
          <h2 class="section-title">
            <Icon icon="mdi:heart" />
            性格与习惯
          </h2>

          <div class="form-grid">
            <!-- 性格特点 -->
            <div class="form-group full-width">
              <label for="personality" class="form-label">
                <Icon icon="mdi:star" />
                性格特点
              </label>
              <input type="text"
                     id="personality"
                     v-model="formData.personality"
                     :class="['form-input', { 'error': errors.personality }]"
                     placeholder="例如：活泼、温顺、胆小、粘人"
                     maxlength="255">
              <div v-if="errors.personality" class="error-message">{{ errors.personality }}</div>
              <div class="input-hint">最多255个字符</div>
            </div>

            <!-- 特别记录 -->
            <div class="form-group full-width">
              <label for="notes" class="form-label">
                <Icon icon="mdi:note-text" />
                特别记录
              </label>
              <textarea id="notes"
                        v-model="formData.notes"
                        class="form-textarea"
                        rows="4"
                        placeholder="记录宠物的特别习惯、喜好、健康情况、疫苗接种记录等"></textarea>
              <div class="input-hint">宠物的特别注意事项</div>
            </div>
          </div>
        </div>

        <!-- 头像URL（可选） -->
        <div class="form-section">
          <h2 class="section-title">
            <Icon icon="mdi:image" />
            宠物头像
          </h2>

          <div class="form-group full-width">
            <label for="avatarUrl" class="form-label">
              <Icon icon="mdi:link" />
              头像图片URL
            </label>
            <input type="text"
                   id="avatarUrl"
                   v-model="formData.avatarUrl"
                   class="form-input"
                   placeholder="例如：https://example.com/pet-avatar.jpg"
                   maxlength="255">
            <div class="input-hint">支持JPG、PNG格式，建议尺寸：200x200像素</div>

            <!-- 头像预览 -->
            <div v-if="formData.avatarUrl" class="avatar-preview">
              <div class="preview-label">头像预览：</div>
              <img :src="formData.avatarUrl"
                   alt="头像预览"
                   class="preview-image"
                   @error="formData.avatarUrl = ''">
            </div>
          </div>
        </div>

        <!-- 表单按钮 -->
        <div class="form-actions">
          <button type="button"
                  @click="handleCancel"
                  class="btn btn-secondary"
                  :disabled="submitting || petStore.loading">
            取消
          </button>
          <button type="button"
                  @click="handleReset"
                  class="btn btn-reset"
                  :disabled="submitting || petStore.loading">
            重置
          </button>
          <button type="submit"
                  class="btn btn-primary"
                  :disabled="submitting || petStore.loading">
            <Icon v-if="submitting" icon="mdi:loading" spin />
            {{ submitting ? '提交中...' : (isEditing ? '更新宠物' : '添加宠物') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.add-pet-view {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 120px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 40px;
  position: relative;
}

.back-btn {
  position: absolute;
  left: 0;
  top: 0;
  background: none;
  border: none;
  color: var(--pet-primary);
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background-color: var(--pet-primary-lighter);
  transform: translateX(-5px);
}

.page-title {
  text-align: center;
  font-size: 2.2rem;
  color: var(--pet-primary-dark);
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.title-icon {
  font-size: 2.2rem;
  color: var(--pet-primary);
}

.page-subtitle {
  text-align: center;
  font-size: 1.1rem;
  color: var(--pet-text-secondary);
  margin-bottom: 10px;
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

/* 表单容器 */
.form-container {
  background-color: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

/* 表单区域 */
.form-section {
  margin-bottom: 40px;
  padding-bottom: 40px;
  border-bottom: 2px dashed var(--pet-primary-light);
}

.form-section:last-child {
  border-bottom: none;
  margin-bottom: 30px;
}

.section-title {
  font-size: 1.5rem;
  color: var(--pet-primary-dark);
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 表单网格 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

/* 表单标签 */
.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--pet-text-primary);
  margin-bottom: 10px;
  font-size: 1rem;
}

.form-label.required::after {
  content: '*';
  color: #ff4757;
  margin-left: 4px;
}

/* 宠物类型选择器 */
.pet-type-selector {
  display: flex;
  gap: 15px;
}

.pet-type-option {
  flex: 1;
  padding: 20px;
  border: 2px solid var(--pet-primary-light);
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: white;
}

.pet-type-option:hover {
  border-color: var(--pet-primary);
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(255, 154, 60, 0.1);
}

.pet-type-option.selected {
  border-color: var(--pet-primary);
  background-color: var(--pet-primary-lighter);
}

.option-icon {
  font-size: 2.5rem;
  color: var(--pet-primary);
  margin-bottom: 10px;
}

.option-label {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--pet-primary-dark);
}

/* 性别选择器 */
.gender-selector {
  display: flex;
  gap: 10px;
}

.gender-option {
  flex: 1;
  padding: 12px;
  border: 2px solid var(--pet-primary-light);
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: white;
}

.gender-option:hover {
  border-color: var(--pet-primary);
}

.gender-option.selected {
  border-color: var(--pet-primary);
  background-color: var(--pet-primary-lighter);
  color: var(--pet-primary-dark);
  font-weight: 600;
}

.gender-label {
  font-size: 1rem;
}

/* 表单输入框 */
.form-input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid var(--pet-primary-light);
  border-radius: 8px;
  font-size: 1rem;
  color: var(--pet-text-primary);
  transition: all 0.3s ease;
  background-color: white;
}

.form-input:focus {
  outline: none;
  border-color: var(--pet-primary);
  box-shadow: 0 0 0 3px rgba(255, 154, 60, 0.1);
}

.form-input.error {
  border-color: #ff4757;
  background-color: #fff5f5;
}

.form-input::placeholder {
  color: #aaa;
}

/* 文本域 */
.form-textarea {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid var(--pet-primary-light);
  border-radius: 8px;
  font-size: 1rem;
  color: var(--pet-text-primary);
  transition: all 0.3s ease;
  background-color: white;
  resize: vertical;
  font-family: inherit;
}

.form-textarea:focus {
  outline: none;
  border-color: var(--pet-primary);
  box-shadow: 0 0 0 3px rgba(255, 154, 60, 0.1);
}

.form-textarea::placeholder {
  color: #aaa;
}

/* 输入提示 */
.input-hint {
  font-size: 0.85rem;
  color: #888;
  margin-top: 5px;
}

/* 错误信息 */
.error-message {
  color: #ff4757;
  font-size: 0.85rem;
  margin-top: 5px;
  font-weight: 500;
}

/* 头像预览 */
.avatar-preview {
  margin-top: 15px;
  padding: 15px;
  background-color: var(--pet-bg-secondary);
  border-radius: 8px;
  border: 1px solid var(--pet-primary-light);
}

.preview-label {
  font-size: 0.9rem;
  color: var(--pet-text-secondary);
  margin-bottom: 10px;
}

.preview-image {
  max-width: 100px;
  max-height: 100px;
  border-radius: 8px;
  border: 2px solid var(--pet-primary-light);
}

/* 表单按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid var(--pet-primary-light);
}

.btn {
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 120px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: var(--pet-primary);
  color: white;
  border-color: var(--pet-primary);
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--pet-primary-dark);
  border-color: var(--pet-primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 154, 60, 0.3);
}

.btn-secondary {
  background-color: white;
  color: var(--pet-primary-dark);
  border-color: var(--pet-primary-light);
}

.btn-secondary:hover:not(:disabled) {
  background-color: var(--pet-primary-lighter);
  border-color: var(--pet-primary);
  transform: translateY(-2px);
}

.btn-reset {
  background-color: #f8f8f8;
  color: #666;
  border-color: #ddd;
}

.btn-reset:hover:not(:disabled) {
  background-color: #eee;
  border-color: #ccc;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 992px) {
  .add-pet-view {
    padding: 20px 15px;
  }

  .form-container {
    padding: 30px;
  }

  .page-title {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .pet-type-selector {
    flex-direction: column;
  }

  .pet-type-option {
    padding: 15px;
  }

  .option-icon {
    font-size: 2rem;
  }

  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .btn {
    width: 100%;
  }

  .page-title {
    font-size: 1.8rem;
    margin-top: 20px;
  }

  .back-btn {
    position: relative;
    margin-bottom: 10px;
  }
}

@media (max-width: 480px) {
  .add-pet-view {
    padding: 15px 10px;
  }

  .form-container {
    padding: 20px 15px;
  }

  .form-section {
    padding-bottom: 30px;
    margin-bottom: 30px;
  }

  .section-title {
    font-size: 1.3rem;
  }

  .pet-type-option {
    padding: 12px;
  }

  .gender-selector {
    flex-direction: column;
  }

  .gender-option {
    padding: 10px;
  }
}
</style>
