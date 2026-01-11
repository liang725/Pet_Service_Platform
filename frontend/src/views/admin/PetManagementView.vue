<template>
  <div class="pet-management">
    <div class="page-header">
      <h1>宠物档案管理</h1>
      <p>管理系统所有宠物信息</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards" v-if="stats">
      <div class="stat-card">
        <Icon icon="mdi:paw" class="stat-icon" />
        <div class="stat-content">
          <h3>{{ stats.totalPets || 0 }}</h3>
          <p>总宠物数</p>
        </div>
      </div>
      <div class="stat-card">
        <Icon icon="mdi:account-group" class="stat-icon" />
        <div class="stat-content">
          <h3>{{ stats.totalOwners || 0 }}</h3>
          <p>宠物主人数</p>
        </div>
      </div>
      <div class="stat-card">
        <Icon icon="mdi:cat" class="stat-icon" />
        <div class="stat-content">
          <h3>{{ stats.catCount || 0 }}</h3>
          <p>猫咪总数</p>
        </div>
      </div>
      <div class="stat-card">
        <Icon icon="mdi:dog" class="stat-icon" />
        <div class="stat-content">
          <h3>{{ stats.dogCount || 0 }}</h3>
          <p>狗狗总数</p>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="search-box">
        <Icon icon="mdi:magnify" class="search-icon" />
        <input
          v-model="searchKeyword"
          placeholder="搜索宠物名/品种/主人..."
          @keyup.enter="searchPets"
        />
        <button class="search-btn" @click="searchPets">搜索</button>
      </div>
      <button class="add-btn" @click="openAddDialog">
        <Icon icon="mdi:plus" /> 添加宠物
      </button>
    </div>

    <!-- 宠物列表 -->
    <div v-if="loading" class="loading-state">
      <Icon icon="mdi:loading" class="spin" />
      <span>加载中...</span>
    </div>

    <div v-else-if="pets.length === 0" class="empty-state">
      <Icon icon="mdi:paw" class="empty-icon" />
      <h3>暂无宠物档案</h3>
      <p>点击上方按钮添加第一个宠物</p>
    </div>

    <div v-else class="table-container">
      <table class="pet-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>宠物信息</th>
            <th>主人ID</th>
            <th>类型</th>
            <th>品种</th>
            <th>性别</th>
            <th>年龄/体重</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pet in pets" :key="pet.id">
            <td>{{ pet.id }}</td>
            <td>
              <div class="pet-info">
                <div class="pet-avatar">
                  <img v-if="pet.avatarUrl" :src="pet.avatarUrl" :alt="pet.name" />
                  <Icon v-else icon="mdi:paw" class="default-avatar" />
                </div>
                <div class="pet-details">
                  <strong>{{ pet.name }}</strong>
                  <small v-if="pet.color" class="pet-color">毛色：{{ pet.color }}</small>
                </div>
              </div>
            </td>
            <td>
              <div class="owner-info">
                <Icon icon="mdi:account" class="owner-icon" />
                <span>{{ pet.userId }}</span>
              </div>
            </td>
            <td>
              <span class="type-badge" :class="pet.petType">
                <Icon 
                  :icon="pet.petType === '狗' ? 'mdi:dog' : 'mdi:cat'" 
                  class="pet-type-icon" 
                />
                {{ pet.petType }}
              </span>
            </td>
            <td>{{ pet.breed || '-' }}</td>
            <td>
              <span class="gender-badge" :class="pet.gender">
                <Icon 
                  :icon="pet.gender === '公' ? 'mdi:gender-male' : pet.gender === '母' ? 'mdi:gender-female' : 'mdi:gender-non-binary'" 
                  class="gender-icon" 
                />
                {{ pet.gender }}
              </span>
            </td>
            <td>
              <div class="age-weight">
                <span v-if="pet.age">{{ pet.age }}</span>
                <span v-if="pet.weight" class="weight">{{ pet.weight }}kg</span>
              </div>
            </td>
            <td>{{ formatDate(pet.createdAt) }}</td>
            <td>
              <div class="action-buttons">
                <button class="action-btn view" @click="viewPetDetail(pet)" title="查看详情">
                  <Icon icon="mdi:eye" />
                </button>
                <button class="action-btn edit" @click="openEditDialog(pet)" title="编辑">
                  <Icon icon="mdi:pencil" />
                </button>
                <button
                  class="action-btn delete"
                  @click="deletePet(pet)"
                  title="删除"
                >
                  <Icon icon="mdi:delete" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination">
      <div class="page-size-selector">
        <span>每页显示</span>
        <select v-model.number="pageSize" @change="changePageSize">
          <option :value="10">10 条</option>
          <option :value="20">20 条</option>
          <option :value="50">50 条</option>
          <option :value="100">100 条</option>
        </select>
      </div>
      <div class="page-controls">
        <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
        <span class="page-info">第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
        <button :disabled="page >= totalPages" @click="changePage(page + 1)">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑宠物对话框 -->
    <div v-if="showDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑宠物' : '添加宠物' }}</h3>
          <button class="close-btn" @click="closeDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <form class="dialog-body" @submit.prevent="savePet">
          <div class="form-row">
            <div class="form-group">
              <label>宠物名称 <span class="required">*</span></label>
              <input
                v-model="form.name"
                required
                placeholder="请输入宠物名称"
                maxlength="50"
              />
            </div>

            <div class="form-group">
              <label>宠物类型 <span class="required">*</span></label>
              <select v-model="form.petType" required>
                <option value="">请选择</option>
                <option value="猫">猫</option>
                <option value="狗">狗</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>品种</label>
              <input
                v-model="form.breed"
                placeholder="请输入品种"
                maxlength="100"
              />
            </div>

            <div class="form-group">
              <label>性别 <span class="required">*</span></label>
              <select v-model="form.gender" required>
                <option value="">请选择</option>
                <option value="公">公</option>
                <option value="母">母</option>
                <option value="未知">未知</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>出生日期</label>
              <input
                v-model="form.birthDate"
                type="date"
                :max="new Date().toISOString().split('T')[0]"
              />
            </div>

            <div class="form-group">
              <label>体重 (kg)</label>
              <input
                v-model="form.weight"
                type="number"
                step="0.01"
                min="0"
                max="100"
                placeholder="请输入体重"
              />
            </div>
          </div>

          <div class="form-group">
            <label>毛色</label>
            <input
              v-model="form.color"
              placeholder="请输入毛色描述"
              maxlength="50"
            />
          </div>

          <div class="form-group">
            <label>主人用户ID <span class="required">*</span></label>
            <input
              v-model.number="form.userId"
              type="number"
              required
              min="1"
              placeholder="请输入用户ID"
            />
          </div>

          <div class="form-group">
            <label>头像URL</label>
            <input
              v-model="form.avatarUrl"
              placeholder="请输入头像图片URL"
              maxlength="255"
            />
            <small class="hint">支持网络图片地址</small>
          </div>

          <div class="form-group">
            <label>性格描述</label>
            <textarea
              v-model="form.personality"
              placeholder="请输入宠物的性格特点"
              rows="3"
              maxlength="255"
            />
          </div>

          <div class="form-group">
            <label>特别记录/备注</label>
            <textarea
              v-model="form.notes"
              placeholder="请输入健康情况等特别记录"
              rows="4"
            />
          </div>

          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
            <button type="submit" class="submit-btn" :disabled="saving">
              <Icon v-if="saving" icon="mdi:loading" class="spin" />
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 查看详情对话框 -->
    <div v-if="showDetailDialog" class="dialog-overlay" @click="closeDetailDialog">
      <div class="dialog detail-dialog" @click.stop>
        <div class="dialog-header">
          <h3>宠物详情</h3>
          <button class="close-btn" @click="closeDetailDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <div v-if="currentPet" class="detail-body">
          <div class="pet-header">
            <div class="detail-avatar">
              <img v-if="currentPet.avatarUrl" :src="currentPet.avatarUrl" :alt="currentPet.name" />
              <Icon v-else icon="mdi:paw" class="detail-default-avatar" />
            </div>
            <div class="detail-title">
              <h2>{{ currentPet.name }}</h2>
              <div class="pet-tags">
                <span class="type-tag">{{ currentPet.petType }}</span>
                <span class="gender-tag">{{ currentPet.gender }}</span>
                <span v-if="currentPet.breed" class="breed-tag">{{ currentPet.breed }}</span>
              </div>
            </div>
          </div>

          <div class="detail-grid">
            <div class="detail-item">
              <label>宠物ID</label>
              <span>{{ currentPet.id }}</span>
            </div>
            <div class="detail-item">
              <label>主人用户ID</label>
              <span>{{ currentPet.userId }}</span>
            </div>
            <div class="detail-item">
              <label>出生日期</label>
              <span>{{ currentPet.birthDate || '未知' }}</span>
            </div>
            <div class="detail-item">
              <label>年龄</label>
              <span>{{ calculateAge(currentPet.birthDate) || '未知' }}</span>
            </div>
            <div class="detail-item">
              <label>体重</label>
              <span>{{ currentPet.weight ? currentPet.weight + 'kg' : '未记录' }}</span>
            </div>
            <div class="detail-item">
              <label>毛色</label>
              <span>{{ currentPet.color || '未记录' }}</span>
            </div>
            <div class="detail-item full-width">
              <label>性格描述</label>
              <p>{{ currentPet.personality || '暂无描述' }}</p>
            </div>
            <div class="detail-item full-width">
              <label>特别记录/备注</label>
              <p class="notes-content">{{ currentPet.notes || '暂无记录' }}</p>
            </div>
            <div class="detail-item">
              <label>创建时间</label>
              <span>{{ formatDate(currentPet.createdAt) }}</span>
            </div>
            <div class="detail-item">
              <label>更新时间</label>
              <span>{{ formatDate(currentPet.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Icon } from '@iconify/vue'
import request from '@/utils/request'

// 宠物列表数据
const pets = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const stats = ref(null)

// 对话框状态
const showDialog = ref(false)
const showDetailDialog = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const currentPet = ref(null)

// 表单数据
const form = ref({
  name: '',
  petType: '',
  breed: '',
  gender: '',
  birthDate: '',
  weight: '',
  color: '',
  userId: '',
  avatarUrl: '',
  personality: '',
  notes: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => {
  loadPets()
  loadStats()
})

// 加载宠物列表
async function loadPets() {
  loading.value = true
  try {
    const params = { 
      page: page.value, 
      pageSize: pageSize.value 
    }
    
    if (searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim()
    }
    
    const res = await request({ 
      url: '/api/admin/pets', 
      method: 'GET', 
      params 
    })
    
    if (res.code === 200) {
      pets.value = res.data.list || []
      total.value = res.data.total || 0
      
      // 为每个宠物计算年龄
      pets.value.forEach(pet => {
        if (pet.birthDate) {
          pet.age = calculateAge(pet.birthDate)
        }
      })
    } else {
      alert(res.message || '加载宠物列表失败')
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    alert('加载宠物列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
async function loadStats() {
  try {
    const res = await request({ 
      url: '/api/admin/pets/stats', 
      method: 'GET' 
    })
    
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 搜索宠物
function searchPets() {
  page.value = 1
  loadPets()
}

// 分页
function changePage(newPage) {
  page.value = newPage
  loadPets()
}

function changePageSize() {
  page.value = 1
  loadPets()
}

// 打开添加对话框
function openAddDialog() {
  isEdit.value = false
  form.value = {
    name: '',
    petType: '',
    breed: '',
    gender: '',
    birthDate: '',
    weight: '',
    color: '',
    userId: '',
    avatarUrl: '',
    personality: '',
    notes: ''
  }
  showDialog.value = true
}

// 打开编辑对话框
function openEditDialog(pet) {
  isEdit.value = true
  form.value = {
    id: pet.id,
    name: pet.name,
    petType: pet.petType,
    breed: pet.breed || '',
    gender: pet.gender,
    birthDate: pet.birthDate ? formatDateForInput(pet.birthDate) : '',
    weight: pet.weight || '',
    color: pet.color || '',
    userId: pet.userId,
    avatarUrl: pet.avatarUrl || '',
    personality: pet.personality || '',
    notes: pet.notes || ''
  }
  showDialog.value = true
}

// 查看宠物详情
function viewPetDetail(pet) {
  currentPet.value = { ...pet }
  showDetailDialog.value = true
}

// 关闭对话框
function closeDialog() {
  showDialog.value = false
}

function closeDetailDialog() {
  showDetailDialog.value = false
  currentPet.value = null
}

// 保存宠物信息
async function savePet() {
  // 表单验证
  if (!form.value.name.trim()) {
    alert('请输入宠物名称')
    return
  }
  
  if (!form.value.petType) {
    alert('请选择宠物类型')
    return
  }
  
  if (!form.value.gender) {
    alert('请选择性别')
    return
  }
  
  if (!form.value.userId) {
    alert('请输入主人用户ID')
    return
  }
  
  saving.value = true
  try {
    // 准备数据
    const petData = {
      ...form.value,
      weight: form.value.weight ? parseFloat(form.value.weight) : null
    }
    
    // 移除空字符串
    Object.keys(petData).forEach(key => {
      if (petData[key] === '' || petData[key] === null) {
        delete petData[key]
      }
    })
    
    let res
    if (isEdit.value) {
      res = await request({
        url: `/api/admin/pets/${petData.id}`,
        method: 'PUT',
        data: petData
      })
    } else {
      res = await request({
        url: '/api/admin/pets',
        method: 'POST',
        data: petData
      })
    }
    
    if (res.code === 200) {
      alert(isEdit.value ? '更新成功' : '添加成功')
      closeDialog()
      loadPets()
      loadStats() // 刷新统计信息
    } else {
      alert(res.message || '操作失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  } finally {
    saving.value = false
  }
}

// 删除宠物
async function deletePet(pet) {
  if (!confirm(`确定要删除宠物"${pet.name}"吗？此操作不可恢复！`)) {
    return
  }
  
  try {
    const res = await request({
      url: `/api/admin/pets/${pet.id}`,
      method: 'DELETE'
    })
    
    if (res.code === 200) {
      alert('删除成功')
      loadPets()
      loadStats() // 刷新统计信息
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}

// 计算年龄
function calculateAge(birthDate) {
  if (!birthDate) return null
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  
  return age + '岁'
}

// 格式化日期为YYYY-MM-DD格式（用于input[type="date"]）
function formatDateForInput(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toISOString().split('T')[0]
}

// 格式化日期为可读格式
function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
/* 整体样式与用户管理保持一致 */
.pet-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 统计卡片 - 适配用户管理风格 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  border-left: 4px solid #e17055;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card:nth-child(2) {
  border-left-color: #3498db;
}

.stat-card:nth-child(3) {
  border-left-color: #9b59b6;
}

.stat-card:nth-child(4) {
  border-left-color: #2ecc71;
}

.stat-icon {
  font-size: 32px;
  color: #e17055;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #fff5f2;
}

.stat-card:nth-child(2) .stat-icon {
  color: #3498db;
  background: #e8f4fc;
}

.stat-card:nth-child(3) .stat-icon {
  color: #9b59b6;
  background: #f5eefa;
}

.stat-card:nth-child(4) .stat-icon {
  color: #2ecc71;
  background: #f0f9f4;
}

.stat-content h3 {
  margin: 0 0 4px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 工具栏 - 与用户管理一致 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 500px;
  display: flex;
  align-items: center;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 0 12px;
  transition: all 0.3s ease;
}

.search-box:focus-within {
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.search-icon {
  color: #999;
  font-size: 20px;
  margin-right: 8px;
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  padding: 10px 8px;
  font-size: 14px;
}

.search-btn {
  padding: 8px 20px;
  background: #e17055;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.search-btn:hover {
  background: #d35400;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #e17055;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background: #d35400;
  transform: translateY(-1px);
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #666;
}

.empty-icon {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #333;
}

.empty-state p {
  margin: 0;
  color: #999;
}

/* 表格容器 */
.table-container {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.pet-table {
  width: 100%;
  border-collapse: collapse;
}

.pet-table thead {
  background: #f8f9fa;
}

.pet-table th {
  padding: 14px 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  font-size: 13px;
  border-bottom: 2px solid #dee2e6;
}

.pet-table td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #333;
  vertical-align: middle;
}

.pet-table tbody tr:hover {
  background: #f8f9fa;
}

/* 宠物信息 */
.pet-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pet-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e0e6ed;
}

.pet-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  font-size: 22px;
  color: #adb5bd;
}

.pet-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pet-details strong {
  color: #333;
  font-size: 14px;
}

.pet-color {
  color: #7f8c8d;
  font-size: 12px;
}

/* 主人信息 */
.owner-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.owner-icon {
  font-size: 16px;
  color: #adb5bd;
}

/* 类型徽章 */
.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.type-badge.狗 {
  background: #fff3cd;
  color: #856404;
}

.type-badge.猫 {
  background: #d1ecf1;
  color: #0c5460;
}

.pet-type-icon {
  font-size: 14px;
}

/* 性别徽章 */
.gender-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.gender-badge.公 {
  background: #e3f2fd;
  color: #1976d2;
}

.gender-badge.母 {
  background: #fce4ec;
  color: #c2185b;
}

.gender-badge.未知 {
  background: #f5f5f5;
  color: #757575;
}

.gender-icon {
  font-size: 12px;
}

/* 年龄体重 */
.age-weight {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.age-weight span {
  padding: 4px 8px;
  background: #f8fafc;
  border-radius: 6px;
  font-size: 12px;
  color: #5a6268;
}

.weight {
  background: #e3f2fd !important;
  color: #1976d2 !important;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 16px;
}

.action-btn.view {
  background: #e3f2fd;
  color: #1976d2;
}

.action-btn.view:hover {
  background: #1976d2;
  color: white;
}

.action-btn.edit {
  background: #fff3e0;
  color: #f57c00;
}

.action-btn.edit:hover {
  background: #f57c00;
  color: white;
}

.action-btn.delete {
  background: #ffebee;
  color: #c62828;
}

.action-btn.delete:hover {
  background: #c62828;
  color: white;
}

/* 分页 - 与用户管理一致 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.page-size-selector select {
  padding: 6px 10px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.page-size-selector select:hover {
  border-color: #e17055;
}

.page-size-selector select:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
  color: #495057;
}

.pagination button:hover:not(:disabled) {
  background: #e17055;
  color: white;
  border-color: #e17055;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

/* 对话框样式 - 与用户管理一致 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog.detail-dialog {
  max-width: 700px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 20px;
  color: #666;
}

.close-btn:hover {
  background: #e9ecef;
  color: #333;
}

.dialog-body {
  padding: 20px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row .form-group {
  flex: 1;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.form-group input[type="number"] {
  -moz-appearance: textfield;
}

.form-group input[type="number"]::-webkit-outer-spin-button,
.form-group input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.form-group textarea {
  resize: vertical;
  min-height: 60px;
}

.hint {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.cancel-btn {
  padding: 10px 20px;
  border: 1px solid #dee2e6;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.submit-btn {
  padding: 10px 20px;
  border: none;
  background: #e17055;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.submit-btn:hover:not(:disabled) {
  background: #d35400;
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 详情对话框样式 */
.detail-body {
  padding: 0;
}

.pet-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: linear-gradient(135deg, #e17055 0%, #d35400 100%);
  border-radius: 12px 12px 0 0;
  color: white;
}

.detail-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid rgba(255, 255, 255, 0.3);
}

.detail-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-default-avatar {
  font-size: 36px;
  color: #e17055;
}

.detail-title h2 {
  margin: 0 0 12px 0;
  color: white;
  font-size: 24px;
  font-weight: 600;
}

.pet-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.type-tag,
.gender-tag,
.breed-tag {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding: 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-item span {
  font-size: 15px;
  color: #1f2937;
  font-weight: 500;
}

.detail-item p {
  margin: 0;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #f3f4f6;
}

.notes-content {
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}
</style>