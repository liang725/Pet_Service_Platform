<!-- src/views/ServiceAppointmentView.vue -->
<template>
  <div class="appointment-container">
    <!-- 头部 -->
    <div class="appointment-header">
      <h1><i class="fas fa-calendar-check"></i> 预约洗护服务</h1>
      <p>为您爱宠选择最合适的洗护服务，专业护理，健康安全</p>
    </div>

    <!-- 主要内容区 -->
    <div class="appointment-main">
      <!-- 左侧：预约表单 -->
      <div class="appointment-form">
        <h2><i class="fas fa-plus-circle"></i> 创建新预约</h2>
        
        <!-- 选择宠物 -->
        <div class="form-section">
            <label class="form-label">
            <i class="fas fa-paw"></i> 选择宠物
            </label>
            <select v-model="formData.petId" class="form-control" :disabled="loading.pets">
            <option value="">请选择您的宠物</option>
            <option v-for="pet in pets" :key="pet.id" :value="pet.id">
                {{ pet.name }} ({{ pet.breed || '未知品种' }})
            </option>
            </select>
            <div v-if="loading.pets" class="loading-small">
            <i class="fas fa-spinner fa-spin"></i> 加载中...
            </div>
            <!-- 修改这里：添加 v-if 检查 pets 是否存在 -->
            <div v-if="!loading.pets && (!pets || pets.length === 0)" class="no-data">
            暂无宠物，请先<a href="/pets/add">添加宠物</a>
            </div>
        </div>

        <!-- 选择日期 -->
        <div class="form-section">
          <label class="form-label">
            <i class="fas fa-calendar-day"></i> 选择预约日期
          </label>
          <div class="date-selector">
            <div 
              v-for="date in availableDates" 
              :key="date.value"
              :class="['date-item', { 
                selected: formData.appointmentDate === date.value,
                disabled: date.disabled
              }]"
              @click="selectDate(date.value)"
            >
              <div class="date-day">{{ date.day }}</div>
              <div class="date-number">{{ date.number }}</div>
              <div class="date-month">{{ date.month }}月</div>
            </div>
          </div>
          <input 
            type="date" 
            v-model="formData.appointmentDate" 
            class="form-control"
            @change="onDateChange"
            :min="minDate"
          >
        </div>

        <!-- 选择时间 -->
        <div class="form-section">
          <label class="form-label">
            <i class="fas fa-clock"></i> 选择服务时间
          </label>
          <div v-if="loading.timeSlots" class="loading-small">
            <i class="fas fa-spinner fa-spin"></i> 加载可用时间段...
          </div>
          <div v-else class="time-slots">
            <div 
              v-for="timeSlot in timeSlots" 
              :key="timeSlot"
              :class="['time-slot', { 
                selected: formData.timeSlot === timeSlot,
                disabled: !availableTimeSlots.includes(timeSlot)
              }]"
              @click="selectTimeSlot(timeSlot)"
            >
              {{ timeSlot }}
              <span v-if="!availableTimeSlots.includes(timeSlot)" class="slot-tip">已满</span>
            </div>
          </div>
          <div v-if="!loading.timeSlots && (!availableTimeSlots || availableTimeSlots.length === 0)" class="no-data">
        该日期暂无可用时间段，请选择其他日期
        </div>
        </div>

        <!-- 选择服务 -->
        <div class="form-section">
          <label class="form-label">
            <i class="fas fa-spa"></i> 选择洗护项目
          </label>
          <div v-if="loading.services" class="loading-small">
            <i class="fas fa-spinner fa-spin"></i> 加载服务项目...
          </div>
          <div v-else class="service-options">
            <div 
              v-for="service in services" 
              :key="service.id"
              :class="['service-option', { selected: formData.serviceId === service.id }]"
              @click="selectService(service)"
            >
              <div class="service-icon">
                <i :class="getServiceIcon(service.category)"></i>
              </div>
              <div class="service-info">
                <h4>{{ service.itemName }}</h4>
                <p>{{ service.briefDesc }}</p>
                <div class="service-price">
                  <span v-if="service.isDiscount" class="original-price">
                    ¥{{ service.basePrice }}
                  </span>
                  <span class="final-price">
                    ¥{{ service.finalPrice }}
                  </span>
                  <span v-if="service.isDiscount" class="discount-badge">
                    -{{ service.discountPercentage }}%
                  </span>
                </div>
                <div class="service-duration">
                  <i class="fas fa-clock"></i> 约{{ service.duration }}分钟
                </div>
              </div>
              <div v-if="service.isRecommended" class="recommended-badge">
                推荐
              </div>
            </div>
          </div>
          <div v-if="!loading.services && (!services || services.length === 0)" class="no-data">
            暂无可用服务
            </div>
        </div>

        <!-- 特殊要求 -->
        <div class="form-section">
          <label class="form-label">
            <i class="fas fa-comment-alt"></i> 特殊要求 (可选)
          </label>
          <textarea 
            v-model="formData.specialNotes" 
            class="form-control" 
            rows="3" 
            placeholder="请告知您的特殊要求，如对某些洗护用品过敏等"
            maxlength="500"
          ></textarea>
          <div class="char-count">
            {{ formData.specialNotes?.length || 0 }}/500
          </div>
        </div>

        <!-- 价格汇总 -->
        <div class="price-summary" v-if="selectedService">
          <div class="summary-item">
            <span>服务费用：</span>
            <span>¥{{ selectedService.finalPrice }}</span>
          </div>
          <div class="summary-total">
            <span>总计：</span>
            <span class="total-price">¥{{ selectedService.finalPrice }}</span>
          </div>
        </div>

        <!-- 提交按钮 -->
        <button 
          class="btn-submit" 
          :disabled="!isFormValid || submitting" 
          @click="submitAppointment"
        >
          <i v-if="submitting" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-calendar-check"></i>
          {{ submitting ? '提交中...' : '确认预约' }}
        </button>
      </div>

      <!-- 右侧：日历和订单 -->
      <div class="appointment-sidebar">
        <!-- 日历 -->
        <div class="calendar-container">
          <div class="calendar-header">
            <h3>{{ calendarTitle }}</h3>
            <div class="calendar-nav">
              <button @click="prevMonth"><i class="fas fa-chevron-left"></i></button>
              <button @click="goToToday">今天</button>
              <button @click="nextMonth"><i class="fas fa-chevron-right"></i></button>
            </div>
          </div>
          <div class="calendar-weekdays">
            <div v-for="day in weekdays" :key="day">{{ day }}</div>
          </div>
          <div class="calendar-days">
            <div 
              v-for="day in calendarDays" 
              :key="day.id"
              :class="['calendar-day', {
                'other-month': !day.isCurrentMonth,
                'today': day.isToday,
                'selected': day.isSelected,
                'has-appointment': day.appointmentCount > 0
              }]"
              @click="selectCalendarDay(day)"
            >
              <div class="day-number">{{ day.date.getDate() }}</div>
              <div v-if="day.appointmentCount > 0" class="appointment-indicator">
                {{ day.appointmentCount }}个预约
              </div>
            </div>
          </div>
        </div>

        <!-- 近期预约 -->
        <div class="recent-appointments">
          <div class="sidebar-header">
            <h3><i class="fas fa-history"></i> 近期预约</h3>
            <router-link to="/service/orders" class="view-all">查看全部</router-link>
          </div>
          <div v-if="loading.appointments" class="loading-small">
            <i class="fas fa-spinner fa-spin"></i> 加载中...
          </div>
          <div v-else-if="!recentAppointments || recentAppointments.length === 0" class="empty-state">
            <i class="fas fa-calendar-times"></i>
            <p>暂无预约记录</p>
            <router-link to="/service/appointment" class="btn-primary">
                立即预约
            </router-link>
            </div>
          <div v-else class="appointments-list">
            <div 
              v-for="appointment in recentAppointments" 
              :key="appointment.id"
              class="appointment-item"
            >
              <div class="appointment-header">
                <span class="order-no">{{ appointment.orderNo }}</span>
                <span :class="['status', appointment.status]">
                  {{ getStatusText(appointment.status) }}
                </span>
              </div>
              <div class="appointment-details">
                <div class="service-info">
                  <strong>{{ appointment.serviceName }}</strong>
                  <span class="pet-name">{{ appointment.petName }}</span>
                </div>
                <div class="appointment-time">
                  <i class="far fa-calendar"></i>
                  {{ formatDate(appointment.appointmentDate) }} {{ appointment.timeSlot }}
                </div>
                <div class="appointment-price">
                  <i class="fas fa-yen-sign"></i> ¥{{ appointment.orderTotalAmount }}
                </div>
              </div>
              <div class="appointment-actions">
                <button 
                  class="btn-view"
                  @click="viewAppointment(appointment.id)"
                >
                  查看详情
                </button>
                <button 
                  v-if="appointment.status === 'pending'"
                  class="btn-cancel"
                  @click="handleCancelAppointment(appointment)"
                >
                  取消
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 成功弹窗 -->
  <div v-if="showSuccessModal" class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <i class="fas fa-check-circle success-icon"></i>
        <h3>预约成功！</h3>
      </div>
      <div class="modal-body">
        <div class="success-details">
          <div class="detail-item">
            <i class="fas fa-receipt"></i>
            <div>
              <div class="detail-label">订单编号</div>
              <div class="detail-value">{{ successData.orderNo }}</div>
            </div>
          </div>
          <div class="detail-item">
            <i class="far fa-calendar"></i>
            <div>
              <div class="detail-label">预约时间</div>
              <div class="detail-value">{{ successData.appointmentDate }} {{ successData.timeSlot }}</div>
            </div>
          </div>
          <div class="detail-item">
            <i class="fas fa-yen-sign"></i>
            <div>
              <div class="detail-label">支付金额</div>
              <div class="detail-value">¥{{ successData.totalAmount }}</div>
            </div>
          </div>
        </div>
        <p class="success-tip">
          您可以在"我的预约"中查看预约详情和取消预约
        </p>
      </div>
      <div class="modal-footer">
        <button class="btn-secondary" @click="goToOrders">查看我的预约</button>
        <button class="btn-primary" @click="showSuccessModal = false">继续预约</button>
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

// 表单数据
const formData = ref({
  petId: '',
  appointmentDate: '',
  timeSlot: '',
  serviceId: '',
  specialNotes: ''
})

// 数据
const pets = ref([])
const services = ref([])
const timeSlots = ref(['09:00', '10:30', '14:00', '15:30', '17:00'])
const availableTimeSlots = ref([])
const recentAppointments = ref([])
const selectedService = ref(null)

// 加载状态
const loading = ref({
  pets: false,
  services: false,
  timeSlots: false,
  appointments: false
})
const submitting = ref(false)

// 日历相关
const currentDate = ref(new Date())
const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

// 成功弹窗
const showSuccessModal = ref(false)
const successData = ref({})

// 计算属性
const isFormValid = computed(() => {
  return formData.value.petId && 
         formData.value.appointmentDate && 
         formData.value.timeSlot && 
         formData.value.serviceId
})

const calendarTitle = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth() + 1
  return `${year}年${month}月`
})

const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  const today = new Date()
  
  // 获取当月第一天
  const firstDay = new Date(year, month, 1)
  // 获取当月最后一天
  const lastDay = new Date(year, month + 1, 0)
  // 第一天是星期几
  const firstDayIndex = firstDay.getDay()
  // 当月天数
  const daysInMonth = lastDay.getDate()
  
  const days = []
  
  // 添加上个月的最后几天
  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = firstDayIndex; i > 0; i--) {
    const date = new Date(year, month - 1, prevMonthLastDay - i + 1)
    days.push({
      id: `prev-${i}`,
      date,
      isCurrentMonth: false,
      isToday: false,
      isSelected: false,
      appointmentCount: 0
    })
  }
  
  // 添加当月日期
  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(year, month, i)
    const dateStr = formatDateForInput(date)
    const isToday = date.toDateString() === today.toDateString()
    const isSelected = formData.value.appointmentDate === dateStr
    
    // 获取该日期的预约数量（这里可以调用API获取真实数据）
    const appointmentCount = getAppointmentCountForDate(dateStr)
    
    days.push({
      id: `current-${i}`,
      date,
      isCurrentMonth: true,
      isToday,
      isSelected,
      appointmentCount
    })
  }
  
  // 添加下个月的日期
  const totalCells = 42 // 6行 × 7列
  const nextDays = totalCells - (firstDayIndex + daysInMonth)
  for (let i = 1; i <= nextDays; i++) {
    const date = new Date(year, month + 1, i)
    days.push({
      id: `next-${i}`,
      date,
      isCurrentMonth: false,
      isToday: false,
      isSelected: false,
      appointmentCount: 0
    })
  }
  
  return days
})

const availableDates = computed(() => {
  const dates = []
  const today = new Date()
  
  for (let i = 0; i < 14; i++) { // 显示未来14天
    const date = new Date()
    date.setDate(today.getDate() + i)
    const dateStr = formatDateForInput(date)
    
    const dayNames = ['日', '一', '二', '三', '四', '五', '六']
    const isWeekend = date.getDay() === 0 || date.getDay() === 6
    
    dates.push({
      value: dateStr,
      day: `周${dayNames[date.getDay()]}`,
      number: date.getDate(),
      month: date.getMonth() + 1,
      disabled: i === 0, // 暂时禁用今天，可以修改
      isWeekend
    })
  }
  
  return dates
})

// 方法
function getServiceIcon(category) {
  const icons = {
    '洗澡': 'fas fa-shower',
    '美容': 'fas fa-cut',
    '护理': 'fas fa-spa',
    '医疗': 'fas fa-bug'
  }
  return icons[category] || 'fas fa-paw'
}

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

function formatDateForInput(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function getStatusText(status) {
  const statusMap = {
    'pending': '待服务',
    'confirmed': '已确认',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

function getAppointmentCountForDate(dateStr) {
  // 模拟数据，实际应该调用API
  return Math.random() > 0.7 ? Math.floor(Math.random() * 3) + 1 : 0
}

async function fetchPets() {
  loading.value.pets = true
  try {
    console.log('开始获取宠物列表...')
    const petsData = await userStore.getUserPets()
    console.log('获取到宠物列表原始数据:', petsData)
    
    // 处理可能的嵌套结构
    let petsArray = []
    if (Array.isArray(petsData)) {
      petsArray = petsData
    } else if (petsData && typeof petsData === 'object') {
      // 如果返回的是对象，尝试提取数组
      if (Array.isArray(petsData.data)) {
        petsArray = petsData.data
      } else if (petsData.list) {
        petsArray = petsData.list
      }
    }
    
    // 确保格式正确
    petsArray = petsArray.map(pet => ({
      id: parseInt(pet.id) || pet.id,
      name: pet.name || '未命名',
      breed: pet.breed || '未知品种',
      type: pet.type || pet.pet_type || '未知'
    }))
    
    console.log('格式化后宠物列表:', petsArray)
    
    pets.value = petsArray
    
    if (pets.value.length > 0 && !formData.value.petId) {
      formData.value.petId = pets.value[0].id.toString()
    }
    
  } catch (error) {
    console.error('获取宠物失败:', error)
    pets.value = []
    alert('获取宠物列表失败，请稍后重试')
  } finally {
    loading.value.pets = false
  }
}

async function fetchServices() {
  loading.value.services = true
  try {
    console.log('开始获取服务项目...') // 添加日志
    
    const servicesData = await userStore.getServiceItems()
    console.log('获取到服务项目原始数据:', servicesData) // 添加日志
    
    if (Array.isArray(servicesData)) {
      // 处理数据，确保字段匹配
      services.value = servicesData.map(service => ({
        id: service.id,
        category: service.category || '其他',
        itemName: service.itemName || service.name || '未命名服务',
        briefDesc: service.briefDesc || service.description || '',
        basePrice: service.basePrice || service.price || 0,
        isDiscount: service.isDiscount || false,
        discountPercentage: service.discountPercentage || 0,
        finalPrice: service.finalPrice || service.basePrice || 0,
        duration: service.duration || 60,
        isRecommended: service.isRecommended || false,
        thumbnailUrl: service.thumbnailUrl || null,
        isSinglePurchase: service.isSinglePurchase || false
      }))
      console.log('格式化后服务项目:', services.value) // 添加日志
    } else {
      console.warn('服务项目数据不是数组:', servicesData)
      services.value = []
    }
    
  } catch (error) {
    console.error('获取服务项目失败:', error)
    // 显示错误信息
    services.value = []
    // 注意：这里不要alert，避免干扰用户体验
  } finally {
    loading.value.services = false
  }
}


async function fetchAvailableTimeSlots(date) {
  if (!date) return
  
  loading.value.timeSlots = true
  try {
    availableTimeSlots.value = await userStore.getAvailableTimeSlots(date)
  } catch (error) {
    console.error('获取可用时间段失败:', error)
    // 默认所有时间段都可用
    availableTimeSlots.value = timeSlots.value
  } finally {
    loading.value.timeSlots = false
  }
}

async function fetchRecentAppointments() {
  loading.value.appointments = true
  try {
    const appointmentsData = await userStore.getAllUserAppointments()
    
    if (Array.isArray(appointmentsData)) {
      recentAppointments.value = appointmentsData.slice(0, 3).map(app => ({
        id: app.id,
        orderNo: app.orderNo || `ORD-${new Date().getTime()}`,
        petName: app.petName || app.pet?.name || '未命名',
        serviceName: app.serviceName || app.service?.name || '未知服务',
        appointmentDate: app.appointmentDate || new Date().toISOString().split('T')[0],
        timeSlot: app.timeSlot || '09:00',
        orderTotalAmount: app.orderTotalAmount || app.totalAmount || 0,
        status: app.status || 'pending'
      }))
    } else {
      recentAppointments.value = []
    }
    
  } catch (error) {
    console.error('获取预约记录失败:', error)
    recentAppointments.value = []
  } finally {
    loading.value.appointments = false
  }
}

function isTimeSlotAvailable(timeSlot) {
  return availableTimeSlots.value.includes(timeSlot)
}

function selectDate(date) {
  formData.value.appointmentDate = date
  formData.value.timeSlot = '' // 重置时间选择
  fetchAvailableTimeSlots(date)
}

function selectTimeSlot(timeSlot) {
  if (isTimeSlotAvailable(timeSlot)) {
    formData.value.timeSlot = timeSlot
  }
}

function selectService(service) {
  formData.value.serviceId = service.id
  selectedService.value = service
}

function selectCalendarDay(day) {
  if (day.isCurrentMonth) {
    formData.value.appointmentDate = formatDateForInput(day.date)
    fetchAvailableTimeSlots(formData.value.appointmentDate)
  }
}

function prevMonth() {
  currentDate.value.setMonth(currentDate.value.getMonth() - 1)
}

function nextMonth() {
  currentDate.value.setMonth(currentDate.value.getMonth() + 1)
}

function goToToday() {
  currentDate.value = new Date()
  const todayStr = formatDateForInput(new Date())
  formData.value.appointmentDate = todayStr
  fetchAvailableTimeSlots(todayStr)
}

function viewAppointment(id) {
  router.push(`/service/appointment/${id}`)
}

async function handleCancelAppointment(appointment) {
  if (!confirm(`确定要取消预约 ${appointment.orderNo} 吗？`)) {
    return
  }
  
  try {
    await userStore.cancelAppointment(appointment.id, '用户主动取消')
    alert('预约已取消')
    fetchRecentAppointments()
  } catch (error) {
    console.error('取消预约失败:', error)
    alert(error.message || '取消预约失败')
  }
}

async function submitAppointment() {
  if (!isFormValid.value) {
    alert('请填写完整的预约信息')
    return
  }
  
  submitting.value = true
  try {
    // 构建预约数据
    const appointmentData = {
      petId: parseInt(formData.value.petId),
      serviceId: parseInt(formData.value.serviceId),
      appointmentDate: formData.value.appointmentDate,
      timeSlot: formData.value.timeSlot,
      specialNotes: formData.value.specialNotes || '',
      // 如果有护理项，需要传递
      careItems: [] // 暂时为空，您可以根据需要添加
    }
    
    console.log('提交的预约数据:', appointmentData)
    
    const result = await userStore.createAppointment(appointmentData)
    
    console.log('预约结果:', result)
    
    // 显示成功弹窗
    successData.value = {
      orderNo: result.orderNo,
      appointmentDate: formData.value.appointmentDate,
      timeSlot: formData.value.timeSlot,
      totalAmount: result.totalAmount || selectedService.value?.finalPrice || 0
    }
    showSuccessModal.value = true
    
    // 重置表单
    if (pets.value && pets.value.length > 0) {
      formData.value.petId = pets.value[0].id.toString()
    } else {
      formData.value.petId = ''
    }
    
    // 设置默认日期为明天
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    formData.value.appointmentDate = formatDateForInput(tomorrow)
    
    formData.value.timeSlot = ''
    formData.value.serviceId = ''
    formData.value.specialNotes = ''
    selectedService.value = null
    
    // 刷新数据
    await fetchRecentAppointments()
    await fetchAvailableTimeSlots(formData.value.appointmentDate)
    
  } catch (error) {
    console.error('预约失败:', error)
    alert(error.message || '预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

function onDateChange() {
  fetchAvailableTimeSlots(formData.value.appointmentDate)
}

function goToOrders() {
  showSuccessModal.value = false
  router.push('/service/orders')
}

// 监听
watch(() => formData.value.appointmentDate, (newDate) => {
  if (newDate && formData.value.timeSlot && !isTimeSlotAvailable(formData.value.timeSlot)) {
    formData.value.timeSlot = '' // 如果选择的时间段不可用，清空选择
  }
})

// 生命周期
onMounted(async () => {
  try {
    // 检查登录状态但不强制跳转
    const isLoggedIn = userStore.checkAuthStatus()
    
    if (!isLoggedIn) {
      console.warn('用户未登录，部分功能受限')
    }
    
    // 获取宠物列表（需要登录）
    if (isLoggedIn) {
      await fetchPets()
    } else {
      pets.value = []
    }
    
    // 获取服务项目（不需要登录）
    await fetchServices()
    
    // 获取近期预约（需要登录）
    if (isLoggedIn) {
      await fetchRecentAppointments()
    } else {
      recentAppointments.value = []
    }
    
    // 设置默认日期为明天
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    formData.value.appointmentDate = formatDateForInput(tomorrow)
    
    // 获取可用时间段（可能不需要登录）
    await fetchAvailableTimeSlots(formData.value.appointmentDate)
    
  } catch (error) {
    console.error('初始化失败:', error)
  }
})
</script>

<style scoped>
.appointment-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

.appointment-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 20px;
  background: linear-gradient(135deg, #FFEAA7 0%, #FFD166 100%);
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(255, 200, 100, 0.3);
}

.appointment-header h1 {
  color: #E17055;
  font-size: 2.2rem;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.appointment-header p {
  color: #8B5A2B;
  font-size: 1.1rem;
  margin: 0;
}

.appointment-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 20px;
}

@media (max-width: 992px) {
  .appointment-main {
    grid-template-columns: 1fr;
  }
}

/* 表单样式 */
.appointment-form {
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 25px rgba(255, 159, 67, 0.15);
  border-top: 8px solid #FF9F43;
  position: relative;
  overflow: hidden;
}

.appointment-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
  background: linear-gradient(90deg, #FF9F43, #FFD166, #FF9F43);
  background-size: 200% 100%;
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.appointment-form h2 {
  color: #E17055;
  margin-bottom: 30px;
  font-size: 1.6rem;
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 15px;
  border-bottom: 3px solid #FFEAA7;
}

.form-section {
  margin-bottom: 28px;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 12px;
  color: #E17055;
  font-weight: 600;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-control {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #FFEAA7;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background-color: #FFFCF5;
  color: #5A4B3A;
}

.form-control:focus {
  outline: none;
  border-color: #FF9F43;
  box-shadow: 0 0 0 4px rgba(255, 159, 67, 0.15);
  background-color: white;
}

.form-control:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

/* 日期选择器 */
.date-selector {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.date-item {
  text-align: center;
  padding: 12px 6px;
  background-color: #FFF9E6;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.date-item:hover:not(.disabled) {
  background-color: #FFEAA7;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(255, 159, 67, 0.2);
}

.date-item.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
  font-weight: bold;
}

.date-item.disabled {
  background-color: #f0f0f0;
  color: #aaa;
  cursor: not-allowed;
  opacity: 0.6;
}

.date-item.is-weekend {
  color: #FF6B6B;
}

.date-day {
  font-size: 0.85rem;
  color: #8B5A2B;
  margin-bottom: 5px;
}

.date-number {
  font-size: 1.4rem;
  font-weight: bold;
  color: #E17055;
  margin-bottom: 3px;
}

.date-month {
  font-size: 0.8rem;
  color: #8B5A2B;
}

.date-item.selected .date-day,
.date-item.selected .date-number,
.date-item.selected .date-month {
  color: white;
}

/* 时间段选择 */
.time-slots {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 10px;
}

.time-slot {
  padding: 14px 10px;
  background-color: #FFF9E6;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  font-weight: 500;
  position: relative;
}

.time-slot:hover:not(.disabled) {
  background-color: #FFEAA7;
  transform: translateY(-2px);
}

.time-slot.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
  font-weight: bold;
}

.time-slot.disabled {
  background-color: #f5f5f5;
  color: #aaa;
  cursor: not-allowed;
}

.slot-tip {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #FF4757;
  color: white;
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: bold;
}

/* 服务选项 */
.service-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (max-width: 768px) {
  .service-options {
    grid-template-columns: 1fr;
  }
}

.service-option {
  display: flex;
  align-items: center;
  padding: 18px;
  background-color: #FFF9E6;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.service-option:hover {
  background-color: #FFEAA7;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 159, 67, 0.15);
}

.service-option.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
}

.service-icon {
  font-size: 2rem;
  margin-right: 18px;
  color: #E17055;
  flex-shrink: 0;
}

.service-option.selected .service-icon {
  color: white;
}

.service-info {
  flex: 1;
}

.service-info h4 {
  margin-bottom: 8px;
  font-size: 1.1rem;
  color: #5A4B3A;
}

.service-option.selected .service-info h4 {
  color: white;
}

.service-info p {
  font-size: 0.9rem;
  color: #8B5A2B;
  margin-bottom: 10px;
  line-height: 1.4;
}

.service-option.selected .service-info p {
  color: rgba(255, 255, 255, 0.9);
}

.service-price {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 0.9rem;
}

.final-price {
  font-weight: bold;
  color: #E17055;
  font-size: 1.2rem;
}

.service-option.selected .final-price {
  color: white;
}

.discount-badge {
  background-color: #FF4757;
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: bold;
}

.service-duration {
  font-size: 0.85rem;
  color: #8B5A2B;
  display: flex;
  align-items: center;
  gap: 6px;
}

.service-option.selected .service-duration {
  color: rgba(255, 255, 255, 0.8);
}

.recommended-badge {
  position: absolute;
  top: 10px;
  right: -30px;
  background: #FF4757;
  color: white;
  padding: 4px 35px;
  font-size: 0.8rem;
  font-weight: bold;
  transform: rotate(45deg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

/* 字符计数 */
.char-count {
  text-align: right;
  font-size: 0.85rem;
  color: #8B5A2B;
  margin-top: 5px;
}

/* 价格汇总 */
.price-summary {
  background-color: #FFF9E6;
  border-radius: 12px;
  padding: 20px;
  margin: 25px 0;
  border: 2px solid #FFEAA7;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #5A4B3A;
  font-size: 1rem;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 2px solid #FFEAA7;
  font-size: 1.2rem;
  font-weight: bold;
  color: #E17055;
}

.total-price {
  font-size: 1.4rem;
  color: #FF6B6B;
}

/* 提交按钮 */
.btn-submit {
  width: 100%;
  background: linear-gradient(135deg, #FF9F43 0%, #E17055 100%);
  color: white;
  padding: 18px;
  font-size: 1.2rem;
  font-weight: bold;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  letter-spacing: 1px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(255, 159, 67, 0.4);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

/* 侧边栏样式 */
.appointment-sidebar {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 日历样式 */
.calendar-container {
  background-color: white;
  border-radius: 20px;
  padding: 25px;
  box-shadow: 0 8px 25px rgba(255, 200, 100, 0.15);
  border-top: 8px solid #FFB347;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.calendar-header h3 {
  font-size: 1.4rem;
  color: #E17055;
  margin: 0;
}

.calendar-nav {
  display: flex;
  gap: 8px;
}

.calendar-nav button {
  background-color: #FFEAA7;
  border: none;
  border-radius: 50%;
  width: 38px;
  height: 38px;
  cursor: pointer;
  font-size: 1.1rem;
  color: #8B5A2B;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.calendar-nav button:hover {
  background-color: #FFD166;
  transform: scale(1.1);
}

.calendar-nav button:nth-child(2) {
  background-color: #FF9F43;
  color: white;
  padding: 0 15px;
  width: auto;
  border-radius: 19px;
  font-size: 0.9rem;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 12px;
  text-align: center;
  font-weight: bold;
  color: #E17055;
  font-size: 0.9rem;
  padding: 8px 0;
  border-bottom: 2px solid #FFEAA7;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #FFF9E6;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 6px;
  position: relative;
  border: 2px solid transparent;
}

.calendar-day:hover:not(.other-month) {
  background-color: #FFEAA7;
  transform: translateY(-2px);
}

.calendar-day.other-month {
  color: #ccc;
  opacity: 0.6;
}

.calendar-day.today {
  background-color: #FFD166;
  font-weight: bold;
  border-color: #FF9F43;
}

.calendar-day.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #E17055;
}

.calendar-day.has-appointment::before {
  content: '';
  position: absolute;
  top: 5px;
  right: 5px;
  width: 6px;
  height: 6px;
  background-color: #FF9F43;
  border-radius: 50%;
}

.day-number {
  font-size: 1.1rem;
  margin-bottom: 2px;
}

.appointment-indicator {
  font-size: 0.7rem;
  color: #FF9F43;
  margin-top: 3px;
  font-weight: bold;
}

.calendar-day.selected .appointment-indicator {
  color: white;
}

/* 近期预约样式 */
.recent-appointments {
  background-color: white;
  border-radius: 20px;
  padding: 25px;
  box-shadow: 0 8px 25px rgba(255, 200, 100, 0.15);
  border-top: 8px solid #FFD166;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sidebar-header h3 {
  font-size: 1.4rem;
  color: #E17055;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.view-all {
  color: #FF9F43;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.view-all:hover {
  color: #E17055;
  text-decoration: underline;
}

/* 加载状态 */
.loading-small {
  text-align: center;
  padding: 20px;
  color: #8B5A2B;
  font-size: 0.9rem;
}

.loading-small i {
  margin-right: 8px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #8B5A2B;
}

.empty-state i {
  font-size: 3.5rem;
  margin-bottom: 15px;
  color: #FFD166;
  opacity: 0.7;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 1rem;
}

.btn-primary {
  background: linear-gradient(135deg, #FF9F43 0%, #E17055 100%);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 159, 67, 0.3);
}

/* 无数据提示 */
.no-data {
  text-align: center;
  padding: 15px;
  color: #8B5A2B;
  font-size: 0.9rem;
  background-color: #FFF9E6;
  border-radius: 10px;
  margin-top: 10px;
}

.no-data a {
  color: #FF9F43;
  text-decoration: none;
  font-weight: 500;
}

.no-data a:hover {
  text-decoration: underline;
}

/* 预约列表样式 */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.appointment-item {
  background-color: #FFFCF5;
  border-radius: 12px;
  padding: 18px;
  border-left: 4px solid #FFD166;
  transition: all 0.3s ease;
}

.appointment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.appointment-item.completed {
  border-left-color: #4CAF50;
}

.appointment-item.cancelled {
  border-left-color: #f44336;
}

.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: bold;
  color: #E17055;
  font-size: 0.9rem;
  font-family: monospace;
}

.status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.status.pending {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.status.confirmed {
  background-color: #C8E6C9;
  color: #2E7D32;
}

.status.completed {
  background-color: #4CAF50;
  color: white;
}

.status.cancelled {
  background-color: #FFCDD2;
  color: #C62828;
}

.appointment-details {
  margin-bottom: 15px;
}

.service-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.service-info strong {
  color: #5A4B3A;
  font-size: 1rem;
}

.pet-name {
  color: #8B5A2B;
  font-size: 0.9rem;
  background-color: #FFEAA7;
  padding: 3px 8px;
  border-radius: 10px;
}

.appointment-time {
  color: #8B5A2B;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.appointment-price {
  color: #E17055;
  font-weight: bold;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.appointment-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-view, .btn-cancel {
  padding: 6px 14px;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-view {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.btn-view:hover {
  background-color: #FFD166;
  transform: translateY(-1px);
}

.btn-cancel {
  background-color: #FFEAA7;
  color: #FF6B6B;
}

.btn-cancel:hover {
  background-color: #FFCDD2;
  transform: translateY(-1px);
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
  width: 90%;
  animation: slideIn 0.3s ease;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  border-top: 8px solid #4CAF50;
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

.success-icon {
  font-size: 4rem;
  color: #4CAF50;
  margin-bottom: 15px;
  animation: bounce 0.6s ease;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.modal-header h3 {
  color: #2E7D32;
  font-size: 1.8rem;
  margin: 0;
}

.modal-body {
  margin-bottom: 25px;
}

.success-details {
  background-color: #F1F8E9;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item i {
  font-size: 1.5rem;
  color: #4CAF50;
  width: 30px;
}

.detail-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 4px;
}

.detail-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
}

.success-tip {
  text-align: center;
  color: #8B5A2B;
  font-size: 0.95rem;
  padding: 10px;
  background-color: #FFF9E6;
  border-radius: 10px;
}

.modal-footer {
  display: flex;
  gap: 15px;
  justify-content: center;
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
  transform: translateY(-2px);
}

.modal-footer .btn-primary {
  padding: 12px 24px;
  border-radius: 25px;
}
</style>