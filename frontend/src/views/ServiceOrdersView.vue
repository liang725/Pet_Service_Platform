<!-- src/views/ServiceOrdersView.vue -->
<template>
  <div class="service-orders">
    <!-- 头部 -->
    <div class="orders-header">
      <div>
        <h1><i class="fas fa-list-alt"></i> 我的预约</h1>
        <p class="subtitle">查看和管理您的所有预约记录</p>
      </div>
      <router-link to="/service/appointment" class="btn-new-appointment">
        <i class="fas fa-plus"></i> 新预约
      </router-link>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card" @click="activeTab = 'pending'" :class="{ active: activeTab === 'pending' }">
        <div class="stat-icon pending">
          <i class="fas fa-clock"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.pending || 0 }}</div>
          <div class="stat-label">待服务</div>
        </div>
      </div>
      <div class="stat-card" @click="activeTab = 'confirmed'" :class="{ active: activeTab === 'confirmed' }">
        <div class="stat-icon confirmed">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.confirmed || 0 }}</div>
          <div class="stat-label">已确认</div>
        </div>
      </div>
      <div class="stat-card" @click="activeTab = 'completed'" :class="{ active: activeTab === 'completed' }">
        <div class="stat-icon completed">
          <i class="fas fa-check-double"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.completed || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card" @click="activeTab = 'cancelled'" :class="{ active: activeTab === 'cancelled' }">
        <div class="stat-icon cancelled">
          <i class="fas fa-times-circle"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.cancelled || 0 }}</div>
          <div class="stat-label">已取消</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filters-section">
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input 
          v-model="searchKeyword" 
          type="text" 
          placeholder="搜索订单号、宠物名称或服务项目..."
          @input="onSearch"
        >
      </div>
      <div class="filter-options">
        <select v-model="dateFilter" class="filter-select" @change="fetchAppointments">
          <option value="">所有日期</option>
          <option value="today">今天</option>
          <option value="week">本周</option>
          <option value="month">本月</option>
          <option value="last-month">上月</option>
        </select>
        <select v-model="petFilter" class="filter-select" @change="fetchAppointments">
          <option value="">所有宠物</option>
          <option v-for="pet in pets" :key="pet.id" :value="pet.id">
            {{ pet.name }}
          </option>
        </select>
        <button class="btn-reset" @click="resetFilters">
          <i class="fas fa-redo"></i> 重置
        </button>
      </div>
    </div>

    <!-- 标签页 -->
    <div class="orders-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.value"
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
        <span class="badge" v-if="stats[tab.value]">
          {{ stats[tab.value] }}
        </span>
      </button>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="spinner">
          <i class="fas fa-spinner fa-spin"></i>
        </div>
        <p>加载预约记录中...</p>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="filteredAppointments.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-calendar-times"></i>
        </div>
        <h3>暂无{{ getTabLabel(activeTab) }}</h3>
        <p v-if="activeTab === 'pending'">
          您还没有预约记录，立即为您的爱宠预约服务吧！
        </p>
        <p v-else-if="activeTab === 'completed'">
          暂无已完成的服务记录
        </p>
        <p v-else>
          暂无相关预约记录
        </p>
        <router-link 
          v-if="activeTab === 'pending'" 
          to="/service/appointment" 
          class="btn-primary"
        >
          <i class="fas fa-plus"></i> 立即预约
        </router-link>
      </div>
      
      <!-- 订单列表 -->
      <div v-else class="appointments-grid">
        <div 
          v-for="appointment in filteredAppointments" 
          :key="appointment.id"
          :class="['appointment-card', appointment.status]"
        >
          <div class="card-header">
            <div class="order-info">
              <div class="order-no">
                <i class="fas fa-receipt"></i>
                {{ appointment.orderNo }}
              </div>
              <div :class="['status-badge', appointment.status]">
                {{ getStatusText(appointment.status) }}
              </div>
            </div>
            <div class="order-date">
              <i class="far fa-calendar"></i>
              {{ formatDate(appointment.appointmentDate) }} {{ appointment.timeSlot }}
            </div>
          </div>
          
          <div class="card-body">
            <div class="service-info">
              <div class="service-icon">
                <i :class="getServiceIcon(appointment.serviceName)"></i>
              </div>
              <div class="service-details">
                <h4>{{ appointment.serviceName }}</h4>
                <div class="pet-info">
                  <span class="pet-avatar">
                    <i :class="getPetIcon(appointment.petType)"></i>
                  </span>
                  <span class="pet-name">{{ appointment.petName }}</span>
                  <span class="pet-type">{{ appointment.petType }}</span>
                </div>
              </div>
              <div class="service-price">
                ¥{{ appointment.orderTotalAmount }}
              </div>
            </div>
            
            <div class="special-notes" v-if="appointment.specialNotes">
              <i class="fas fa-sticky-note"></i>
              <span>{{ appointment.specialNotes }}</span>
            </div>
            
            <div class="card-footer">
              <div class="created-time">
                创建时间：{{ formatDateTime(appointment.createdAt) }}
              </div>
              <div class="card-actions">
                <button 
                  class="btn-action view"
                  @click="viewAppointment(appointment.id)"
                >
                  <i class="fas fa-eye"></i> 查看详情
                </button>
                
                <template v-if="appointment.status === 'pending'">
                  <button 
                    class="btn-action reschedule"
                    @click="rescheduleAppointment(appointment)"
                  >
                    <i class="fas fa-calendar-alt"></i> 改期
                  </button>
                  <button 
                    class="btn-action cancel"
                    @click="confirmCancel(appointment)"
                  >
                    <i class="fas fa-times"></i> 取消
                  </button>
                </template>
                
                <button 
                  v-if="appointment.status === 'completed'"
                  class="btn-action review"
                  @click="addReview(appointment)"
                >
                  <i class="fas fa-star"></i> 评价
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination">
        <button 
          :disabled="currentPage === 1" 
          @click="prevPage"
          class="page-btn"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <template v-for="page in visiblePages" :key="page">
          <button 
            :class="['page-btn', { active: page === currentPage }]"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>
        </template>
        
        <button 
          :disabled="currentPage === totalPages" 
          @click="nextPage"
          class="page-btn"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
        
        <span class="page-info">
          第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
        </span>
      </div>
    </div>
  </div>

  <!-- 取消确认弹窗 -->
  <div v-if="showCancelModal" class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <i class="fas fa-exclamation-triangle warning-icon"></i>
        <h3>确认取消预约</h3>
      </div>
      <div class="modal-body">
        <p>确定要取消预约 <strong>{{ cancelTarget?.orderNo }}</strong> 吗？</p>
        <div class="cancel-details">
          <p><i class="fas fa-paw"></i> 宠物：{{ cancelTarget?.petName }}</p>
          <p><i class="fas fa-spa"></i> 服务：{{ cancelTarget?.serviceName }}</p>
          <p><i class="far fa-calendar"></i> 时间：{{ formatDate(cancelTarget?.appointmentDate) }} {{ cancelTarget?.timeSlot }}</p>
        </div>
        <div class="cancel-reason">
          <label for="cancelReason">
            <i class="fas fa-comment-dots"></i> 取消原因（可选）：
          </label>
          <textarea 
            id="cancelReason"
            v-model="cancelReason"
            placeholder="请告诉我们取消的原因..."
            rows="3"
            maxlength="200"
          ></textarea>
          <div class="char-count">{{ cancelReason?.length || 0 }}/200</div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn-secondary" @click="showCancelModal = false">
          再考虑一下
        </button>
        <button class="btn-danger" @click="cancelAppointment">
          确认取消
        </button>
      </div>
    </div>
  </div>

  <!-- 改期弹窗 -->
  <div v-if="showRescheduleModal" class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <i class="fas fa-calendar-alt"></i>
        <h3>修改预约时间</h3>
      </div>
      <div class="modal-body">
        <div class="reschedule-info">
          <p>正在为预约 <strong>{{ rescheduleTarget?.orderNo }}</strong> 修改时间</p>
          <div class="current-time">
            原时间：{{ formatDate(rescheduleTarget?.appointmentDate) }} {{ rescheduleTarget?.timeSlot }}
          </div>
        </div>
        
        <div class="reschedule-form">
          <div class="form-group">
            <label><i class="fas fa-calendar-day"></i> 选择新日期</label>
            <input 
              type="date" 
              v-model="newAppointmentDate"
              :min="minDate"
              class="form-control"
            >
          </div>
          
          <div class="form-group">
            <label><i class="fas fa-clock"></i> 选择新时间</label>
            <div v-if="loadingTimeSlots" class="loading-small">
              <i class="fas fa-spinner fa-spin"></i> 加载时间段...
            </div>
            <div v-else class="time-slots-grid">
              <div 
                v-for="slot in availableTimeSlots" 
                :key="slot"
                :class="['time-slot', { selected: newTimeSlot === slot }]"
                @click="newTimeSlot = slot"
              >
                {{ slot }}
              </div>
            </div>
            <div v-if="!loadingTimeSlots && availableTimeSlots.length === 0" class="no-slots">
              该日期暂无可用时间段
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn-secondary" @click="showRescheduleModal = false">
          取消
        </button>
        <button 
          class="btn-primary" 
          @click="rescheduleAppointmentConfirm"
          :disabled="!canReschedule"
        >
          确认改期
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 标签页配置
const tabs = [
  { value: 'pending', label: '待服务' },
  { value: 'confirmed', label: '已确认' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' },
  { value: 'all', label: '全部' }
]

// 状态
const activeTab = ref('pending')
const loading = ref(false)
const appointments = ref([])
const pets = ref([])
const stats = ref({})

// 筛选相关
const searchKeyword = ref('')
const dateFilter = ref('')
const petFilter = ref('')
const currentPage = ref(1)
const pageSize = 10

// 模态框相关
const showCancelModal = ref(false)
const showRescheduleModal = ref(false)
const cancelTarget = ref(null)
const rescheduleTarget = ref(null)
const cancelReason = ref('')
const newAppointmentDate = ref('')
const newTimeSlot = ref('')
const availableTimeSlots = ref([])
const loadingTimeSlots = ref(false)

// 计算属性
const filteredAppointments = computed(() => {
  let filtered = [...appointments.value]
  
  // 按标签筛选
  if (activeTab.value !== 'all') {
    filtered = filtered.filter(app => app.status === activeTab.value)
  }
  
  // 搜索筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(app => 
      app.orderNo.toLowerCase().includes(keyword) ||
      app.petName.toLowerCase().includes(keyword) ||
      app.serviceName.toLowerCase().includes(keyword)
    )
  }
  
  // 日期筛选
  if (dateFilter.value) {
    const now = new Date()
    filtered = filtered.filter(app => {
      const appDate = new Date(app.appointmentDate)
      switch (dateFilter.value) {
        case 'today':
          return appDate.toDateString() === now.toDateString()
        case 'week':
          const startOfWeek = new Date(now.setDate(now.getDate() - now.getDay()))
          return appDate >= startOfWeek
        case 'month':
          return appDate.getMonth() === now.getMonth() && 
                 appDate.getFullYear() === now.getFullYear()
        case 'last-month':
          const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1)
          const endOfLastMonth = new Date(now.getFullYear(), now.getMonth(), 0)
          return appDate >= lastMonth && appDate <= endOfLastMonth
        default:
          return true
      }
    })
  }
  
  // 宠物筛选
  if (petFilter.value) {
    filtered = filtered.filter(app => app.petId === parseInt(petFilter.value))
  }
  
  // 分页
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filtered.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(appointments.value.length / pageSize)
})

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  
  if (totalPages.value <= maxVisible) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i)
    }
  } else {
    let start = Math.max(1, currentPage.value - 2)
    let end = Math.min(totalPages.value, start + maxVisible - 1)
    
    if (end - start + 1 < maxVisible) {
      start = end - maxVisible + 1
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }
  }
  
  return pages
})

const canReschedule = computed(() => {
  return newAppointmentDate.value && newTimeSlot.value
})

const minDate = computed(() => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return tomorrow.toISOString().split('T')[0]
})

// 方法
function getTabLabel(tab) {
  const tabMap = {
    pending: '待服务预约',
    confirmed: '已确认预约',
    completed: '已完成预约',
    cancelled: '已取消预约',
    all: '预约记录'
  }
  return tabMap[tab] || '预约'
}

function getStatusText(status) {
  const statusMap = {
    pending: '待服务',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

function getServiceIcon(serviceName) {
  if (serviceName.includes('洗澡')) return 'fas fa-shower'
  if (serviceName.includes('美容')) return 'fas fa-cut'
  if (serviceName.includes('SPA') || serviceName.includes('护理')) return 'fas fa-spa'
  if (serviceName.includes('驱虫')) return 'fas fa-bug'
  return 'fas fa-paw'
}

function getPetIcon(petType) {
  return petType === '狗' ? 'fas fa-dog' : 'fas fa-cat'
}

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

function formatDateTime(dateTimeString) {
  if (!dateTimeString) return ''
  const date = new Date(dateTimeString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${month}月${day}日 ${hours}:${minutes}`
}

async function fetchAppointments() {
  loading.value = true
  try {
    console.log('开始获取预约列表...')
    
    // 并行获取数据
    const [allAppointmentsData, statsData] = await Promise.all([
      userStore.getAllUserAppointments(),
      userStore.getAppointmentStats()
    ])
    
    console.log('预约列表原始数据:', allAppointmentsData)
    console.log('统计原始数据:', statsData)
    
    // 处理预约数据
    let appointmentsArray = []
    if (Array.isArray(allAppointmentsData)) {
      appointmentsArray = allAppointmentsData
    } else if (allAppointmentsData && typeof allAppointmentsData === 'object') {
      if (Array.isArray(allAppointmentsData.data)) {
        appointmentsArray = allAppointmentsData.data
      } else if (allAppointmentsData.list) {
        appointmentsArray = allAppointmentsData.list
      }
    }
    
    // 格式化预约数据
    appointmentsArray = appointmentsArray.map(app => ({
      id: parseInt(app.id) || app.id,
      orderNo: app.orderNo || `ORD-${new Date().getTime()}`,
      petId: parseInt(app.petId) || app.petId,
      petName: app.petName || app.pet?.name || '未命名',
      petType: app.petType || app.pet?.type || '未知',
      serviceName: app.serviceName || app.service?.name || '未知服务',
      appointmentDate: app.appointmentDate || new Date().toISOString().split('T')[0],
      timeSlot: app.timeSlot || '09:00',
      orderTotalAmount: parseFloat(app.orderTotalAmount) || parseFloat(app.totalAmount) || 0,
      specialNotes: app.specialNotes || '',
      status: app.status || 'pending',
      createdAt: app.createdAt || new Date().toISOString()
    }))
    
    console.log('格式化后预约数据:', appointmentsArray)
    appointments.value = appointmentsArray
    
    // 处理统计数据
    let statsObj = {}
    if (statsData && typeof statsData === 'object') {
      // 如果后端返回的是 { data: { pending: 1, ... } } 格式
      if (statsData.data && typeof statsData.data === 'object') {
        statsObj = statsData.data
      } else {
        statsObj = statsData
      }
    }
    
    // 确保所有状态都有值
    stats.value = {
      pending: parseInt(statsObj.pending) || 0,
      confirmed: parseInt(statsObj.confirmed) || 0,
      completed: parseInt(statsObj.completed) || 0,
      cancelled: parseInt(statsObj.cancelled) || 0,
      all: parseInt(statsObj.all) || appointmentsArray.length
    }
    
    console.log('最终统计:', stats.value)
    
  } catch (error) {
    console.error('获取预约列表失败:', error)
    // 使用模拟数据作为fallback
    appointments.value = generateMockAppointments()
    calculateMockStats()
  } finally {
    loading.value = false
  }
}

async function fetchPets() {
  try {
    pets.value = await userStore.getUserPets()
  } catch (error) {
    console.error('获取宠物列表失败:', error)
  }
}

async function fetchAvailableTimeSlots(date) {
  if (!date) return
  
  loadingTimeSlots.value = true
  try {
    availableTimeSlots.value = await userStore.getAvailableTimeSlots(date)
    if (availableTimeSlots.value.length > 0) {
      newTimeSlot.value = availableTimeSlots.value[0]
    }
  } catch (error) {
    console.error('获取可用时间段失败:', error)
    availableTimeSlots.value = ['09:00', '10:30', '14:00', '15:30', '17:00']
  } finally {
    loadingTimeSlots.value = false
  }
}

function switchTab(tab) {
  activeTab.value = tab
  currentPage.value = 1
}

function viewAppointment(id) {
  router.push(`/service/appointment/${id}`)
}

function confirmCancel(appointment) {
  cancelTarget.value = appointment
  cancelReason.value = ''
  showCancelModal.value = true
}

function rescheduleAppointment(appointment) {
  rescheduleTarget.value = appointment
  newAppointmentDate.value = ''
  newTimeSlot.value = ''
  availableTimeSlots.value = []
  showRescheduleModal.value = true
}

async function cancelAppointment() {
  if (!cancelTarget.value) return
  
  try {
    await userStore.cancelAppointment(
      cancelTarget.value.id, 
      cancelReason.value || '用户主动取消'
    )
    showCancelModal.value = false
    alert('预约已取消')
    // 刷新数据
    await fetchAppointments()
  } catch (error) {
    console.error('取消预约失败:', error)
    alert(error.message || '取消预约失败')
  }
}

async function rescheduleAppointmentConfirm() {
  if (!rescheduleTarget.value || !canReschedule.value) return
  
  // 这里应该调用改期API
  // 暂时模拟成功
  try {
    showRescheduleModal.value = false
    alert('预约时间已修改')
    fetchAppointments()
  } catch (error) {
    console.error('改期失败:', error)
    alert(error.message || '改期失败')
  }
}

function addReview(appointment) {
  // 跳转到评价页面或显示评价弹窗
  alert(`为预约 ${appointment.orderNo} 添加评价功能开发中...`)
}

function resetFilters() {
  searchKeyword.value = ''
  dateFilter.value = ''
  petFilter.value = ''
  currentPage.value = 1
  fetchAppointments()
}

function onSearch() {
  currentPage.value = 1
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

function goToPage(page) {
  currentPage.value = page
}

// 模拟数据生成
function generateMockAppointments() {
  const services = [
    { name: '基础洗澡', price: 80, category: '洗澡' },
    { name: '精致洗护', price: 102, category: '洗澡' },
    { name: '基础美容', price: 150, category: '美容' },
    { name: 'SPA护理', price: 160, category: '护理' }
  ]
  
  const pets = [
    { id: 1, name: '旺财', type: '狗' },
    { id: 2, name: '咪咪', type: '猫' }
  ]
  
  const statuses = ['pending', 'confirmed', 'completed', 'cancelled']
  const timeSlots = ['09:00', '10:30', '14:00', '15:30', '17:00']
  
  const appointments = []
  const today = new Date()
  
  for (let i = 0; i < 15; i++) {
    const daysAgo = Math.floor(Math.random() * 30)
    const appointmentDate = new Date()
    appointmentDate.setDate(today.getDate() - daysAgo + (i < 5 ? 3 : 0)) // 前5个是未来预约
    
    const service = services[Math.floor(Math.random() * services.length)]
    const pet = pets[Math.floor(Math.random() * pets.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    
    appointments.push({
      id: i + 1,
      orderNo: `ORD-${today.getFullYear()}${String(today.getMonth() + 1).padStart(2, '0')}${String(today.getDate()).padStart(2, '0')}-${String(i + 1).padStart(3, '0')}`,
      petId: pet.id,
      petName: pet.name,
      petType: pet.type,
      serviceName: service.name,
      appointmentDate: appointmentDate.toISOString().split('T')[0],
      timeSlot: timeSlots[Math.floor(Math.random() * timeSlots.length)],
      orderTotalAmount: service.price,
      specialNotes: Math.random() > 0.7 ? '对某些洗护用品过敏，请使用天然产品' : null,
      status: status,
      createdAt: new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000).toISOString()
    })
  }
  
  return appointments.sort((a, b) => new Date(b.appointmentDate) - new Date(a.appointmentDate))
}

function calculateMockStats() {
  const mockApps = generateMockAppointments()
  stats.value = {
    pending: mockApps.filter(a => a.status === 'pending').length,
    confirmed: mockApps.filter(a => a.status === 'confirmed').length,
    completed: mockApps.filter(a => a.status === 'completed').length,
    cancelled: mockApps.filter(a => a.status === 'cancelled').length,
    all: mockApps.length
  }
}

// 监听
watch(() => newAppointmentDate.value, (newDate) => {
  if (newDate) {
    fetchAvailableTimeSlots(newDate)
  }
})

// 生命周期
onMounted(() => {
  fetchAppointments()
  fetchPets()
})
</script>

<style scoped>
.service-orders {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 25px;
  background: linear-gradient(135deg, #FFEAA7 0%, #FFD166 100%);
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(255, 200, 100, 0.3);
}

.orders-header h1 {
  color: #E17055;
  font-size: 2rem;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.subtitle {
  color: #8B5A2B;
  font-size: 1rem;
  margin: 0;
  opacity: 0.9;
}

.btn-new-appointment {
  background: linear-gradient(135deg, #FF9F43 0%, #E17055 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 25px;
  text-decoration: none;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 159, 67, 0.3);
}

.btn-new-appointment:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 159, 67, 0.4);
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: white;
  border-radius: 15px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-card.active {
  border-color: #FF9F43;
  background-color: #FFF9E6;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
}

.stat-icon.pending {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.stat-icon.confirmed {
  background-color: #C8E6C9;
  color: #2E7D32;
}

.stat-icon.completed {
  background-color: #4CAF50;
  color: white;
}

.stat-icon.cancelled {
  background-color: #FFCDD2;
  color: #C62828;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #5A4B3A;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #8B5A2B;
  font-weight: 500;
}

/* 筛选区域 */
.filters-section {
  background-color: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.search-box {
  position: relative;
  margin-bottom: 20px;
}

.search-box i {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #8B5A2B;
}

.search-box input {
  width: 100%;
  padding: 14px 16px 14px 45px;
  border: 2px solid #FFEAA7;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background-color: #FFFCF5;
}

.search-box input:focus {
  outline: none;
  border-color: #FF9F43;
  box-shadow: 0 0 0 4px rgba(255, 159, 67, 0.15);
}

.filter-options {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 10px 16px;
  border: 2px solid #FFEAA7;
  border-radius: 10px;
  font-size: 0.9rem;
  background-color: white;
  color: #5A4B3A;
  min-width: 150px;
  cursor: pointer;
}

.filter-select:focus {
  outline: none;
  border-color: #FF9F43;
}

.btn-reset {
  background-color: #FFEAA7;
  color: #8B5A2B;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 0.9rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-reset:hover {
  background-color: #FFD166;
}

/* 标签页 */
.orders-tabs {
  display: flex;
  gap: 5px;
  background-color: #FFF9E6;
  padding: 8px;
  border-radius: 12px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.tab-btn {
  flex: 1;
  min-width: 120px;
  padding: 14px 20px;
  border: none;
  background: transparent;
  color: #8B5A2B;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.tab-btn:hover:not(.active) {
  background-color: rgba(255, 234, 167, 0.5);
}

.tab-btn.active {
  background-color: #FF9F43;
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 159, 67, 0.3);
}

.badge {
  background-color: #FF4757;
  color: white;
  font-size: 0.8rem;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 24px;
  text-align: center;
}

.tab-btn.active .badge {
  background-color: white;
  color: #FF9F43;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  font-size: 3rem;
  color: #FF9F43;
  margin-bottom: 20px;
}

.loading-state p {
  color: #8B5A2B;
  font-size: 1.1rem;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background-color: white;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.empty-icon {
  font-size: 4rem;
  color: #FFD166;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-state h3 {
  color: #E17055;
  margin-bottom: 15px;
  font-size: 1.5rem;
}

.empty-state p {
  color: #8B5A2B;
  margin-bottom: 25px;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}

/* 预约卡片网格 */
.appointments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

@media (max-width: 768px) {
  .appointments-grid {
    grid-template-columns: 1fr;
  }
}

.appointment-card {
  background-color: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.appointment-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.12);
}

.appointment-card.pending {
  border-left: 6px solid #FFD166;
}

.appointment-card.confirmed {
  border-left: 6px solid #4CAF50;
}

.appointment-card.completed {
  border-left: 6px solid #2196F3;
}

.appointment-card.cancelled {
  border-left: 6px solid #f44336;
}

.card-header {
  padding: 20px;
  background: linear-gradient(135deg, #FFF9E6 0%, #FFEAA7 100%);
  border-bottom: 2px solid #FFEAA7;
}

.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.order-no {
  font-family: monospace;
  font-weight: bold;
  color: #E17055;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: bold;
}

.status-badge.pending {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.status-badge.confirmed {
  background-color: #C8E6C9;
  color: #2E7D32;
}

.status-badge.completed {
  background-color: #4CAF50;
  color: white;
}

.status-badge.cancelled {
  background-color: #FFCDD2;
  color: #C62828;
}

.order-date {
  color: #8B5A2B;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-body {
  padding: 20px;
}

.service-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 2px solid #FFEAA7;
}

.service-icon {
  font-size: 2rem;
  color: #FF9F43;
  width: 50px;
  height: 50px;
  background-color: #FFF9E6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.service-details {
  flex: 1;
}

.service-details h4 {
  margin: 0 0 10px 0;
  color: #5A4B3A;
  font-size: 1.1rem;
}

.pet-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.pet-avatar {
  width: 28px;
  height: 28px;
  background-color: #FFEAA7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  color: #8B5A2B;
}

.pet-name {
  color: #5A4B3A;
  font-weight: 500;
}

.pet-type {
  background-color: #E8F5E9;
  color: #2E7D32;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.service-price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #E17055;
}

.special-notes {
  background-color: #FFF9E6;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 0.9rem;
  color: #8B5A2B;
  display: flex;
  gap: 10px;
}

.special-notes i {
  color: #FF9F43;
  flex-shrink: 0;
  margin-top: 2px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.created-time {
  color: #8B5A2B;
  font-size: 0.85rem;
  opacity: 0.8;
}

.card-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn-action {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-action.view {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.btn-action.reschedule {
  background-color: #C8E6C9;
  color: #2E7D32;
}

.btn-action.cancel {
  background-color: #FFCDD2;
  color: #C62828;
}

.btn-action.review {
  background-color: #FFF9E6;
  color: #FF9F43;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.btn-action.view:hover {
  background-color: #FFD166;
}

.btn-action.reschedule:hover {
  background-color: #A5D6A7;
}

.btn-action.cancel:hover {
  background-color: #EF9A9A;
}

.btn-action.review:hover {
  background-color: #FFEAA7;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  padding: 30px 0;
  flex-wrap: wrap;
}

.page-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #FFEAA7;
  background-color: white;
  border-radius: 10px;
  color: #5A4B3A;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: #FFEAA7;
}

.page-btn.active {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
  font-weight: bold;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #8B5A2B;
  font-size: 0.9rem;
  margin-left: 20px;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideIn 0.3s ease;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  border-top: 8px solid #FF9F43;
}

@keyframes slideIn {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  text-align: center;
  margin-bottom: 25px;
}

.warning-icon {
  font-size: 3.5rem;
  color: #FFA726;
  margin-bottom: 15px;
}

.modal-header h3 {
  color: #E17055;
  font-size: 1.6rem;
  margin: 0;
}

.modal-body {
  margin-bottom: 25px;
}

.modal-body p {
  color: #5A4B3A;
  margin-bottom: 20px;
  line-height: 1.6;
}

.cancel-details {
  background-color: #FFF9E6;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
}

.cancel-details p {
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.cancel-reason label {
  display: block;
  margin-bottom: 10px;
  color: #5A4B3A;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.cancel-reason textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #FFEAA7;
  border-radius: 10px;
  font-size: 0.95rem;
  resize: vertical;
  min-height: 80px;
  background-color: #FFFCF5;
}

.cancel-reason textarea:focus {
  outline: none;
  border-color: #FF9F43;
}

.char-count {
  text-align: right;
  font-size: 0.85rem;
  color: #8B5A2B;
  margin-top: 5px;
}

.modal-footer {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.btn-secondary {
  background-color: #FFEAA7;
  color: #8B5A2B;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background-color: #FFD166;
}

.btn-danger {
  background: linear-gradient(135deg, #FF4757 0%, #C62828 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 71, 87, 0.3);
}

/* 改期模态框特定样式 */
.reschedule-info {
  background-color: #FFF9E6;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 25px;
}

.current-time {
  background-color: white;
  padding: 10px 15px;
  border-radius: 8px;
  margin-top: 10px;
  color: #E17055;
  font-weight: 500;
}

.reschedule-form .form-group {
  margin-bottom: 20px;
}

.reschedule-form label {
  display: block;
  margin-bottom: 8px;
  color: #5A4B3A;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.reschedule-form .form-control {
  width: 100%;
  padding: 12px;
  border: 2px solid #FFEAA7;
  border-radius: 10px;
  font-size: 1rem;
  background-color: #FFFCF5;
}

.reschedule-form .form-control:focus {
  outline: none;
  border-color: #FF9F43;
}

.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 10px;
  margin-top: 10px;
}

.time-slot {
  padding: 12px 8px;
  background-color: #FFF9E6;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.time-slot:hover {
  background-color: #FFEAA7;
}

.time-slot.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #E17055;
  font-weight: bold;
}

.no-slots {
  text-align: center;
  padding: 20px;
  color: #8B5A2B;
  background-color: #FFF9E6;
  border-radius: 10px;
  font-size: 0.9rem;
}

.loading-small {
  text-align: center;
  padding: 20px;
  color: #8B5A2B;
}

.loading-small i {
  margin-right: 8px;
}
</style>