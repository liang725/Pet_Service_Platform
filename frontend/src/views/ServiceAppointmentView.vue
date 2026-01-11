<!-- src/views/ServiceAppointmentView.vue -->
<template>
  <div class="appointment-container">
    <!-- 主要内容区 - 修改为左右两栏布局 -->
    <div class="appointment-main-dual">
      <!-- 左侧区域 -->
      <div class="left-section">
        <!-- 预约表单 -->
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
            <div v-if="!loading.pets && (!pets || pets.length === 0)" class="no-data">
              暂无宠物，请先<a href="/pets/add">添加宠物</a>
            </div>
          </div>

          <!-- 选择预约日期 -->
          <div class="form-section">
            <label class="form-label">
              <i class="fas fa-calendar-day"></i> 选择预约日期
            </label>

            <!-- 日历选择器 -->
            <div class="calendar-container-main">
              <div class="calendar-header-main">
                <h3>{{ calendarTitle }}</h3>
                <div class="calendar-nav-main">
                  <button @click="prevMonth" class="nav-btn">
                    <i class="fas fa-chevron-left"></i>
                  </button>
                  <button @click="goToToday" class="today-btn">今天</button>
                  <button @click="nextMonth" class="nav-btn">
                    <i class="fas fa-chevron-right"></i>
                  </button>
                </div>
              </div>
              <div class="calendar-weekdays-main">
                <div v-for="day in weekdays" :key="day" class="weekday-cell">
                  {{ day }}
                </div>
              </div>
              <div class="calendar-days-main">
                <div
                  v-for="day in calendarDays"
                  :key="day.id"
                  :class="['calendar-day-main', {
                    'other-month': !day.isCurrentMonth,
                    'today': day.isToday,
                    'selected': day.isSelected,
                    'has-appointment': day.appointmentCount > 0,
                    'disabled': isDateDisabled(day.date)
                  }]"
                  @click="selectCalendarDay(day)"
                >
                  <div class="day-number-main">{{ day.date.getDate() }}</div>
                  <div v-if="day.appointmentCount > 0" class="appointment-indicator-main">
                    <div class="appointment-dot"></div>
                    <span class="appointment-count">{{ day.appointmentCount }}</span>
                  </div>
                  <div v-if="day.isToday && !day.isSelected" class="today-indicator-main">今</div>
                </div>
              </div>
            </div>

            <!-- 显示已选择的日期 -->
            <div v-if="formData.appointmentDate" class="selected-date-display">
              <i class="fas fa-calendar-check"></i>
              已选择: {{ formatSelectedDate(formData.appointmentDate) }}
            </div>
          </div>

          <!-- 选择预约时间 -->
          <div class="form-section">
            <label class="form-label">
              <i class="fas fa-clock"></i> 选择预约时间
            </label>
            <div v-if="loading.timeSlots" class="loading-small">
              <i class="fas fa-spinner fa-spin"></i> 加载可用时间段...
            </div>
            <div v-else class="time-slots-main">
              <div
                v-for="timeSlot in timeSlots"
                :key="timeSlot"
                :class="['time-slot-main', {
                  selected: formData.timeSlot === timeSlot,
                  disabled: !availableTimeSlots.includes(timeSlot)
                }]"
                @click="selectTimeSlot(timeSlot)"
              >
                <div class="time-slot-content">
                  <i class="far fa-clock"></i>
                  <span class="time-text">{{ timeSlot }}</span>
                  <span v-if="!availableTimeSlots.includes(timeSlot)" class="slot-status">已满</span>
                  <i v-if="formData.timeSlot === timeSlot" class="fas fa-check selected-icon"></i>
                </div>
              </div>
            </div>
            <div v-if="!loading.timeSlots && (!availableTimeSlots || availableTimeSlots.length === 0)" class="no-data">
              该日期暂无可用时间段，请选择其他日期
            </div>
          </div>

          <!-- 特殊要求 -->
          <div class="form-section">
            <label class="form-label">
              <i class="fas fa-comment-alt"></i> 特殊要求 (可选)
            </label>
            <textarea
              v-model="formData.specialNotes"
              class="form-control-textarea"
              rows="3"
              placeholder="请告知您的特殊要求，如对某些洗护用品过敏等"
              maxlength="500"
            ></textarea>
            <div class="char-count">
              {{ formData.specialNotes?.length || 0 }}/500
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="right-section">
        <div class="appointment-form">
          <!-- 选择洗护项目 -->
          <div class="form-section">
            <label class="form-label">
              <i class="fas fa-spa"></i> 选择洗护项目
            </label>
            <div v-if="loading.services" class="loading-small">
              <i class="fas fa-spinner fa-spin"></i> 加载服务项目...
            </div>
            <div v-else class="service-options-main">
              <div
                v-for="service in services"
                :key="service.id"
                :class="['service-option-main', {
                  selected: formData.serviceId === service.id,
                  recommended: service.isRecommended
                }]"
                @click="selectService(service)"
              >
                <div class="service-icon-main">
                  <i :class="getServiceIcon(service.category)"></i>
                </div>
                <div class="service-info-main">
                  <div class="service-header">
                    <h4>{{ service.itemName }}</h4>
                    <span v-if="service.isRecommended" class="recommended-tag">
                      <i class="fas fa-star"></i> 推荐
                    </span>
                  </div>
                  <p class="service-desc">{{ service.briefDesc }}</p>
                  <div class="service-footer">
                    <div class="price-section">
                      <span v-if="service.isDiscount" class="original-price-main">
                        ¥{{ service.basePrice }}
                      </span>
                      <span class="final-price-main">
                        ¥{{ service.finalPrice }}
                      </span>
                      <span v-if="service.isDiscount" class="discount-badge-main">
                        -{{ service.discountPercentage }}%
                      </span>
                    </div>
                    <div class="duration-section">
                      <i class="far fa-clock"></i>
                      <span>约{{ service.duration }}分钟</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="!loading.services && (!services || services.length === 0)" class="no-data">
              暂无可用服务
            </div>
          </div>

          <!-- 选择护理项目（可选） -->
          <div class="form-section">
            <label class="form-label">
              <i class="fas fa-hand-sparkles"></i> 选择护理项目（可选）
            </label>
            <div v-if="loading.careItems" class="loading-small">
              <i class="fas fa-spinner fa-spin"></i> 加载护理项目...
            </div>
            <div v-else class="care-items-container-main">
              <!-- 按分类显示护理项目 -->
              <div v-for="category in careCategories" :key="category">
                <h4 class="care-category-title-main">{{ category }}</h4>
                <div class="care-items-grid-main">
                  <div
                    v-for="item in getCareItemsByCategory(category)"
                    :key="item.id"
                    :class="['care-item-main', {
                      selected: isCareItemSelected(item.id),
                      free: item.unitPrice === 0
                    }]"
                    @click="toggleCareItem(item)"
                  >
                    <div class="care-item-header-main">
                      <h5>{{ item.careName }}</h5>
                      <div class="care-price-main" v-if="item.unitPrice > 0">
                        ¥{{ item.unitPrice }}
                      </div>
                      <div class="care-free-main" v-else>
                        <i class="fas fa-gift"></i> 免费
                      </div>
                    </div>
                    <p class="care-description-main">{{ item.description }}</p>
                    <div class="care-item-footer">
                      <div class="selection-indicator">
                        <i v-if="isCareItemSelected(item.id)" class="fas fa-check-circle"></i>
                        <i v-else class="far fa-circle"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="selectedCareItems.length > 0" class="selected-care-summary-main">
              <i class="fas fa-check-circle"></i>
              已选择 {{ selectedCareItems.length }} 项护理服务
              <span class="care-total-price">总计: ¥{{ totalCarePrice }}</span>
            </div>
          </div>

        </div>
      </div>

      <!-- 中间区域：价格汇总和确认按钮 -->
      <div class="middle-section">
        <!-- 价格汇总 -->
        <div class="price-summary-main" v-if="selectedService">
          <div class="summary-header">
            <h4><i class="fas fa-receipt"></i> 费用明细</h4>
          </div>
          <div class="summary-content">
            <div class="summary-item-main">
              <span>服务费用：</span>
              <span>¥{{ selectedService.finalPrice }}</span>
            </div>
            <div v-if="selectedCareItems.length > 0" class="summary-item-main">
              <span>护理项目：</span>
              <span>¥{{ totalCarePrice }}</span>
            </div>
            <div class="summary-total-main">
              <span>总计：</span>
              <span class="total-price-main">¥{{ totalPrice }}</span>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <button
          class="btn-submit-main"
          :disabled="!isFormValid || submitting"
          @click="submitAppointment"
        >
          <i v-if="submitting" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-calendar-check"></i>
          {{ submitting ? '提交中...' : '确认预约' }}
          <span v-if="selectedService" class="submit-price">¥{{ totalPrice }}</span>
        </button>
      </div>
    </div>

    <!-- 查看预约历史记录模块 - 修改为左右两栏布局 -->
    <div class="appointment-history-section">
      <div class="history-header">
        <h3><i class="fas fa-history"></i> 我的预约记录</h3>
        <router-link to="/service/orders" class="view-all-link">
          查看全部 <i class="fas fa-arrow-right"></i>
        </router-link>
      </div>

      <!-- 新增：历史记录左右两栏布局 -->
      <div class="history-dual-layout" v-if="!loading.appointments && recentAppointments && recentAppointments.length > 0">
        <!-- 左侧历史记录列 -->
        <div class="history-left-column">
          <div class="appointments-history-list compact">
            <div
              v-for="appointment in recentAppointmentsLeft"
              :key="appointment.id"
              :class="['appointment-history-card', 'compact-card', `status-${appointment.status}`]"
            >
              <div class="appointment-history-header">
                <div class="order-info">
                  <span class="order-id">#{{ appointment.orderNo }}</span>
                  <span :class="['status-badge', appointment.status]">
                    {{ getStatusText(appointment.status) }}
                  </span>
                </div>
                <div class="appointment-history-time compact-time">
                  <i class="far fa-calendar"></i>
                  {{ formatDate(appointment.appointmentDate) }} {{ appointment.timeSlot }}
                </div>
              </div>

              <div class="appointment-history-body">
                <div class="service-history-info compact-service">
                  <i class="fas fa-spa"></i>
                  <div class="service-history-details">
                    <h4>{{ appointment.serviceName }}</h4>
                    <div class="pet-history-info">
                      <i class="fas fa-paw"></i>
                      <span>{{ appointment.petName }}</span>
                    </div>
                  </div>
                </div>

                <div class="price-history-info compact-price">
                  <div class="price-history-amount">
                    <i class="fas fa-yen-sign"></i>
                    <span class="amount">¥{{ appointment.orderTotalAmount }}</span>
                  </div>
                </div>
              </div>

              <div class="appointment-history-footer compact-footer">
                <button
                  class="btn-view-detail compact-btn"
                  @click="viewAppointmentDetail(appointment.id)"
                >
                  <i class="fas fa-eye"></i> 详情
                </button>

                <button
                  v-if="appointment.status === 'pending'"
                  class="btn-cancel-appointment compact-btn"
                  @click="handleCancelAppointment(appointment)"
                >
                  <i class="fas fa-times"></i> 取消
                </button>

                <button
                  v-if="appointment.status === 'completed' && !appointment.reviewed"
                  class="btn-review compact-btn"
                  @click="gotoReview(appointment.id)"
                >
                  <i class="fas fa-star"></i> 评价
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧历史记录列 -->
        <div class="history-right-column">
          <div class="appointments-history-list compact">
            <div
              v-for="appointment in recentAppointmentsRight"
              :key="appointment.id"
              :class="['appointment-history-card', 'compact-card', `status-${appointment.status}`]"
            >
              <div class="appointment-history-header">
                <div class="order-info">
                  <span class="order-id">#{{ appointment.orderNo }}</span>
                  <span :class="['status-badge', appointment.status]">
                    {{ getStatusText(appointment.status) }}
                  </span>
                </div>
                <div class="appointment-history-time compact-time">
                  <i class="far fa-calendar"></i>
                  {{ formatDate(appointment.appointmentDate) }} {{ appointment.timeSlot }}
                </div>
              </div>

              <div class="appointment-history-body">
                <div class="service-history-info compact-service">
                  <i class="fas fa-spa"></i>
                  <div class="service-history-details">
                    <h4>{{ appointment.serviceName }}</h4>
                    <div class="pet-history-info">
                      <i class="fas fa-paw"></i>
                      <span>{{ appointment.petName }}</span>
                    </div>
                  </div>
                </div>

                <div class="price-history-info compact-price">
                  <div class="price-history-amount">
                    <i class="fas fa-yen-sign"></i>
                    <span class="amount">¥{{ appointment.orderTotalAmount }}</span>
                  </div>
                </div>
              </div>

              <div class="appointment-history-footer compact-footer">
                <button
                  class="btn-view-detail compact-btn"
                  @click="viewAppointmentDetail(appointment.id)"
                >
                  <i class="fas fa-eye"></i> 详情
                </button>

                <button
                  v-if="appointment.status === 'pending'"
                  class="btn-cancel-appointment compact-btn"
                  @click="handleCancelAppointment(appointment)"
                >
                  <i class="fas fa-times"></i> 取消
                </button>

                <button
                  v-if="appointment.status === 'completed' && !appointment.reviewed"
                  class="btn-review compact-btn"
                  @click="gotoReview(appointment.id)"
                >
                  <i class="fas fa-star"></i> 评价
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="loading.appointments" class="loading-container">
        <i class="fas fa-spinner fa-spin"></i> 加载预约记录...
      </div>

      <div v-else class="empty-history">
        <div class="empty-icon">
          <i class="fas fa-calendar-times"></i>
        </div>
        <p class="empty-text">暂无预约记录</p>
        <p class="empty-subtext">开始您的第一次宠物洗护预约吧</p>
      </div>

      <!-- 加载更多按钮 -->
      <div v-if="recentAppointments.length > 0" class="load-more-container">
        <router-link to="/service/orders" class="btn-load-more-link">
          <button
            class="btn-load-more"
            @click.prevent="goToAllAppointments"
            :disabled="loadingMoreAppointments"
          >
            <i v-if="loadingMoreAppointments" class="fas fa-spinner fa-spin"></i>
            <span v-else>加载更多记录</span>
          </button>
        </router-link>
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
  specialNotes: '',
  careItems: [] // 新增：护理项ID列表
})

// 数据
const pets = ref([])
const services = ref([])
const timeSlots = ref(['09:00', '10:30', '14:00', '15:30', '17:00'])
const availableTimeSlots = ref([])
const recentAppointments = ref([])
const selectedService = ref(null)
const careItems = ref([]) // 新增：护理项目列表
const selectedCareItems = ref([]) // 新增：选中的护理项ID

// 新增：分页相关
const appointmentPage = ref(1)
const appointmentPageSize = ref(4) // 改为4，只显示最近的4条
const hasMoreAppointments = ref(true)
const loadingMoreAppointments = ref(false)

// 新增：计算分割后的预约记录
const recentAppointmentsLeft = computed(() => {
  const recentFour = recentAppointments.value.slice(0, 4) // 只取最近4条
  // 左侧显示前2条
  return recentFour.slice(0, 2)
})

const recentAppointmentsRight = computed(() => {
  const recentFour = recentAppointments.value.slice(0, 4) // 只取最近4条
  // 右侧显示后2条
  return recentFour.slice(2, 4)
})

// 加载状态 - 修复：确保 loading 对象被正确初始化
const loading = ref({
  pets: false,
  services: false,
  timeSlots: false,
  appointments: false,
  careItems: false,
  appointmentStats: false
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

// 日历预约次数缓存
const appointmentCountsCache = ref({})

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

    // 从缓存中获取预约次数
    const appointmentCount = appointmentCountsCache.value[dateStr] || 0

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

// 新增：护理项目分类
const careCategories = computed(() => {
  const categories = new Set()
  if (careItems.value && careItems.value.length > 0) {
    careItems.value.forEach(item => {
      if (item.careCategory) {
        categories.add(item.careCategory)
      }
    })
  }
  return Array.from(categories)
})

// 新增：跳转到所有预约页面
function goToAllAppointments() {
  router.push('/service/orders')
}

// 新增：获取按分类的护理项目
function getCareItemsByCategory(category) {
  return careItems.value.filter(item => item.careCategory === category)
}

// 新增：检查护理项目是否被选中
function isCareItemSelected(itemId) {
  return selectedCareItems.value.includes(itemId)
}

// 新增：计算护理项目总价
const totalCarePrice = computed(() => {
  if (selectedCareItems.value.length === 0) return 0

  let total = 0
  selectedCareItems.value.forEach(itemId => {
    const item = careItems.value.find(c => c.id === itemId)
    if (item && item.unitPrice) {
      total += parseFloat(item.unitPrice)
    }
  })
  return total.toFixed(2)
})

// 新增：计算总价
const totalPrice = computed(() => {
  const servicePrice = selectedService.value ? parseFloat(selectedService.value.finalPrice) : 0
  const carePrice = parseFloat(totalCarePrice.value)
  return (servicePrice + carePrice).toFixed(2)
})

// 新增：格式化选择的日期显示
function formatSelectedDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = weekdays[date.getDay()]
  return `${year}年${month}月${day}日 星期${weekday}`
}

// 新增：检查日期是否禁用（只允许选择今天及之后的日期）
function isDateDisabled(date) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const compareDate = new Date(date)
  compareDate.setHours(0, 0, 0, 0)
  return compareDate < today
}

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
  const weekday = weekdays[date.getDay()]
  return `${month}月${day}日 星期${weekday}`
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

// 新增：查看预约详情
function viewAppointmentDetail(appointmentId) {
  router.push(`/service/appointment/${appointmentId}`)
}

// 新增：去评价
function gotoReview(appointmentId) {
  router.push(`/service/review/${appointmentId}`)
}

// 新增：加载更多预约记录
async function loadMoreAppointments() {
  // 因为使用 getAllUserAppointments 一次性获取所有数据
  // 所以不需要加载更多功能了
  alert('已经显示所有预约记录')
}

// 修改：从数据库获取真实预约次数
async function getAppointmentCountForDate(dateStr) {
  try {
    if (!dateStr || !userStore.isLoggedIn) {
      return 0
    }

    // 如果缓存中有数据，直接返回
    if (appointmentCountsCache.value[dateStr] !== undefined) {
      return appointmentCountsCache.value[dateStr]
    }

    // 调用API获取指定日期的预约次数
    const response = await userStore.getAppointmentCountByDate(dateStr)

    if (response && response.count !== undefined) {
      // 缓存结果
      appointmentCountsCache.value[dateStr] = response.count
      return response.count
    }

    return 0
  } catch (error) {
    console.error('获取预约次数失败:', error)
    return 0
  }
}

// 新增：获取当前月份的预约次数
async function fetchMonthAppointmentCounts() {
  try {
    if (!userStore.isLoggedIn) return

    const today = new Date()
    const year = today.getFullYear()
    const month = today.getMonth()

    // 获取当月第一天和最后一天
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)

    const startDate = formatDateForInput(firstDay)
    const endDate = formatDateForInput(lastDay)

    // 调用API获取当月每日预约次数
    const dailyCounts = await userStore.getDailyAppointmentCounts(startDate, endDate)

    // 更新缓存
    if (dailyCounts && Array.isArray(dailyCounts)) {
      dailyCounts.forEach(item => {
        if (item.appointment_date && item.count !== undefined) {
          appointmentCountsCache.value[item.appointment_date] = item.count
        }
      })
    }
  } catch (error) {
    console.error('获取当月预约次数失败:', error)
  }
}

async function fetchPets() {
  if (loading.value) loading.value.pets = true
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
    if (loading.value) loading.value.pets = false
  }
}

async function fetchServices() {
  if (loading.value) loading.value.services = true
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
    services.value = []
  } finally {
    if (loading.value) loading.value.services = false
  }
}

// 新增：获取护理项目
async function fetchCareItems() {
  if (loading.value) loading.value.careItems = true
  try {
    console.log('开始获取护理项目...')

    // 调用新增的API方法获取护理项目
    const careItemsData = await userStore.getCareItems()
    console.log('获取到护理项目原始数据:', careItemsData)

    if (Array.isArray(careItemsData)) {
      // 处理数据，确保字段匹配
      careItems.value = careItemsData.map(item => ({
        id: item.id,
        careName: item.careName || '未命名护理',
        careCategory: item.careCategory || '其他',
        description: item.description || '',
        unitPrice: item.unitPrice || 0,
        isSinglePurchase: item.isSinglePurchase || false
      }))
      console.log('格式化后护理项目:', careItems.value)
    } else {
      console.warn('护理项目数据不是数组:', careItemsData)
      careItems.value = []
    }

  } catch (error) {
    console.error('获取护理项目失败:', error)
    careItems.value = []
  } finally {
    if (loading.value) loading.value.careItems = false
  }
}

async function fetchAvailableTimeSlots(date) {
  if (!date) return

  if (loading.value) loading.value.timeSlots = true
  try {
    availableTimeSlots.value = await userStore.getAvailableTimeSlots(date)
  } catch (error) {
    console.error('获取可用时间段失败:', error)
    // 默认所有时间段都可用
    availableTimeSlots.value = timeSlots.value
  } finally {
    if (loading.value) loading.value.timeSlots = false
  }
}

async function fetchRecentAppointments() {
  if (loading.value) loading.value.appointments = true
  try {
    // 使用原有的 getAllUserAppointments 方法
    const appointmentsData = await userStore.getAllUserAppointments()

    if (Array.isArray(appointmentsData)) {
      // 只显示最近4条记录
      recentAppointments.value = appointmentsData.slice(0, 4).map(app => ({
        id: app.id,
        orderNo: app.orderNo || `ORD${app.id.toString().padStart(6, '0')}`,
        petName: app.petName || app.pet?.name || '未命名',
        serviceName: app.serviceName || app.service?.itemName || '未知服务',
        appointmentDate: app.appointmentDate || app.appointment_date,
        timeSlot: app.timeSlot || app.time_slot || '09:00',
        orderTotalAmount: app.orderTotalAmount || app.total_amount || 0,
        status: app.status || 'pending',
        reviewed: app.reviewed || app.has_review || false
      }))
    } else {
      recentAppointments.value = []
    }

  } catch (error) {
    console.error('获取预约记录失败:', error)
    recentAppointments.value = []
  } finally {
    if (loading.value) loading.value.appointments = false
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

// 新增：切换护理项目选择
function toggleCareItem(item) {
  const index = selectedCareItems.value.indexOf(item.id)
  if (index === -1) {
    selectedCareItems.value.push(item.id)
  } else {
    selectedCareItems.value.splice(index, 1)
  }

  // 更新表单中的护理项
  formData.value.careItems = [...selectedCareItems.value]
}

function selectCalendarDay(day) {
  if (day.isCurrentMonth && !isDateDisabled(day.date)) {
    formData.value.appointmentDate = formatDateForInput(day.date)
    fetchAvailableTimeSlots(formData.value.appointmentDate)
  }
}

function prevMonth() {
  const newDate = new Date(currentDate.value)
  newDate.setMonth(newDate.getMonth() - 1)
  currentDate.value = newDate
  // 切换月份时重新获取预约数据
  fetchMonthAppointmentCounts()
}

function nextMonth() {
  const newDate = new Date(currentDate.value)
  newDate.setMonth(newDate.getMonth() + 1)
  currentDate.value = newDate
  // 切换月份时重新获取预约数据
  fetchMonthAppointmentCounts()
}

function goToToday() {
  currentDate.value = new Date()
  const todayStr = formatDateForInput(new Date())
  formData.value.appointmentDate = todayStr
  fetchAvailableTimeSlots(todayStr)
  // 重新获取当月预约数据
  fetchMonthAppointmentCounts()
}

// 新增：处理取消预约
async function handleCancelAppointment(appointment) {
  if (!confirm(`确定要取消预约 ${appointment.orderNo} 吗？`)) {
    return
  }

  try {
    // 调用取消预约API
    await userStore.cancelAppointment(appointment.id, '用户主动取消')
    alert('预约已取消')

    // 更新本地状态
    const index = recentAppointments.value.findIndex(a => a.id === appointment.id)
    if (index !== -1) {
      recentAppointments.value[index].status = 'cancelled'
    }

    // 刷新日历数据
    await fetchMonthAppointmentCounts()
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
      careItems: formData.value.careItems || [] // 新增：包含护理项
    }

    console.log('提交的预约数据:', appointmentData)

    const result = await userStore.createAppointment(appointmentData)

    console.log('预约结果:', result)

    // 显示成功弹窗
    successData.value = {
      orderNo: result.orderNo,
      appointmentDate: formData.value.appointmentDate,
      timeSlot: formData.value.timeSlot,
      totalAmount: result.totalAmount || totalPrice.value || 0
    }
    showSuccessModal.value = true

    // 重置表单
    if (pets.value && pets.value.length > 0) {
      formData.value.petId = pets.value[0].id.toString()
    } else {
      formData.value.petId = ''
    }

    // 设置默认日期为今天
    const today = new Date()
    formData.value.appointmentDate = formatDateForInput(today)

    formData.value.timeSlot = ''
    formData.value.serviceId = ''
    formData.value.specialNotes = ''
    formData.value.careItems = []
    selectedService.value = null
    selectedCareItems.value = []

    // 刷新数据
    await fetchRecentAppointments()
    await fetchAvailableTimeSlots(formData.value.appointmentDate)
    // 刷新日历数据
    await fetchMonthAppointmentCounts()

  } catch (error) {
    console.error('预约失败:', error)
    alert(error.message || '预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
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

// 监听登录状态变化，重新加载数据
watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    fetchPets()
    fetchRecentAppointments()
    fetchMonthAppointmentCounts()
  } else {
    pets.value = []
    recentAppointments.value = []
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

    // 获取护理项目（不需要登录）
    await fetchCareItems()

    // 获取近期预约（需要登录）
    if (isLoggedIn) {
      await fetchRecentAppointments()
      // 获取当月预约次数统计
      await fetchMonthAppointmentCounts()
    } else {
      recentAppointments.value = []
    }

    // 设置默认日期为今天
    const today = new Date()
    formData.value.appointmentDate = formatDateForInput(today)

    // 获取可用时间段（可能不需要登录）
    await fetchAvailableTimeSlots(formData.value.appointmentDate)

  } catch (error) {
    console.error('初始化失败:', error)
  }
})

// 新增：组件卸载前清理
import { onUnmounted } from 'vue'

onUnmounted(() => {
  // 清理缓存数据
  appointmentCountsCache.value = {}
})
</script>

<style scoped>
/* 修改容器布局 */
.appointment-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

/* 双栏布局 */
.appointment-main-dual {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 30px;
  margin-bottom: 40px;
}

.left-section,
.right-section {
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 25px rgba(255, 159, 67, 0.15);
  position: relative;
  overflow: hidden;
}

.left-section {
  border-top: 8px solid #FF9F43;
}

.right-section {
  border-top: 8px solid #FF9F43;
}

.middle-section {
  grid-column: 1 / span 2;
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 25px rgba(255, 159, 67, 0.15);
  border-top: 8px solid #FFD166;
}

/* 左侧区域样式 */
.left-section .appointment-form {
  border: none;
  padding: 0;
  box-shadow: none;
  border-top: none;
}

.left-section .appointment-form::before {
  display: none;
}

/* 右侧区域样式 */
.right-section .appointment-form {
  border: none;
  padding: 0;
  box-shadow: none;
  border-top: none;
}

.right-section .appointment-form::before {
  display: none;
}

/* 调整表单部分间距 */
.form-section {
  margin-bottom: 30px;
}

/* 调整日历容器大小 */
.calendar-container-main {
  margin-bottom: 15px;
}

/* 调整服务选项网格 - 与护理项目保持一致 */
.service-options-main {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin-top: 10px;
}

/* 调整护理项目网格 */
.care-items-grid-main {
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
}

/* 调整时间槽位 */
.time-slots-main {
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

/* 中间区域样式 */
.middle-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

/* 价格汇总样式调整 */
.price-summary-main {
  width: 100%;
  max-width: 500px;
  margin: 0 auto 30px auto;
}

/* 提交按钮样式调整 */
.btn-submit-main {
  max-width: 300px;
  margin: 0 auto;
}

/* 预约历史记录模块样式 */
.appointment-history-section {
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 25px rgba(255, 159, 67, 0.15);
  border-top: 8px solid #FF9F43;
}

/* 历史记录双栏布局 */
.history-dual-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.history-left-column,
.history-right-column {
  display: flex;
  flex-direction: column;
}

/* 紧凑型预约卡片样式 */
.appointments-history-list.compact {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.appointment-history-card.compact-card {
  padding: 15px;
  border-radius: 12px;
  transform: scale(0.9);
  transform-origin: top center;
  margin: 0 auto;
  width: 90%;
}

.appointment-history-card.compact-card:hover {
  transform: scale(0.92) translateY(-2px);
}

.appointment-history-header .compact-time {
  font-size: 0.8rem;
  margin-top: 5px;
}

.service-history-info.compact-service {
  gap: 10px;
}

.service-history-info.compact-service i {
  font-size: 1.5rem;
}

.service-history-details h4 {
  font-size: 0.95rem;
  margin-bottom: 5px;
}

.pet-history-info {
  font-size: 0.8rem;
}

.price-history-info.compact-price .price-history-amount {
  font-size: 1rem;
}

.appointment-history-footer.compact-footer {
  gap: 8px;
  margin-top: 10px;
}

.btn-view-detail.compact-btn,
.btn-cancel-appointment.compact-btn,
.btn-review.compact-btn {
  padding: 5px 10px;
  font-size: 0.8rem;
  border-radius: 15px;
}

/* 加载更多按钮 */
.load-more-container {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #FFEAA7;
  grid-column: 1 / span 2;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .appointment-main-dual {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
  }

  .middle-section {
    grid-column: 1;
  }

  .service-options-main {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }

  .care-items-grid-main {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }

  .history-dual-layout {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .appointment-container {
    padding: 10px;
  }

  .left-section,
  .right-section,
  .middle-section {
    padding: 20px;
  }

  .service-options-main {
    grid-template-columns: 1fr;
  }

  .care-items-grid-main {
    grid-template-columns: 1fr;
  }

  .time-slots-main {
    grid-template-columns: repeat(2, 1fr);
  }

  .appointment-history-card.compact-card {
    width: 100%;
    transform: scale(1);
  }
}

/* 基础表单样式 */
.appointment-form {
  width: 100%;
  background-color: transparent;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
  border-top: none;
  position: relative;
  overflow: hidden;
}

.appointment-form h2 {
  color: #E17055;
  margin-bottom: 30px;
  font-size: 1.8rem;
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 15px;
  border-bottom: 3px solid #FFEAA7;
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

.form-control-textarea {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #FFEAA7;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background-color: #FFFCF5;
  color: #5A4B3A;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.form-control-textarea:focus {
  outline: none;
  border-color: #FF9F43;
  box-shadow: 0 0 0 4px rgba(255, 159, 67, 0.15);
  background-color: white;
}

/* 日历组件样式 */
.calendar-container-main {
  background-color: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 2px solid #FFEAA7;
  margin-bottom: 15px;
}

.calendar-header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.calendar-header-main h3 {
  color: #E17055;
  font-size: 1.4rem;
  margin: 0;
  font-weight: 600;
}

.calendar-nav-main {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nav-btn {
  background-color: #FFEAA7;
  border: none;
  border-radius: 50%;
  width: 38px;
  height: 38px;
  cursor: pointer;
  font-size: 1rem;
  color: #8B5A2B;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-btn:hover {
  background-color: #FFD166;
  transform: scale(1.1);
}

.today-btn {
  background-color: #FF9F43;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
  margin: 0 5px;
}

.today-btn:hover {
  background-color: #E17055;
  transform: translateY(-1px);
}

.calendar-weekdays-main {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 10px;
  text-align: center;
  font-weight: 600;
  color: #E17055;
  font-size: 0.95rem;
  padding: 10px 0;
  border-bottom: 1px solid #FFEAA7;
}

.weekday-cell {
  padding: 8px;
}

.calendar-days-main {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day-main {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #FFF9E6;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 10px;
  position: relative;
  border: 2px solid transparent;
  min-height: 70px;
}

.calendar-day-main:hover:not(.other-month):not(.disabled) {
  background-color: #FFEAA7;
  transform: translateY(-2px);
}

.calendar-day-main.other-month {
  color: #ccc;
  opacity: 0.5;
  cursor: default;
}

.calendar-day-main.today:not(.selected) {
  background-color: #FFD166;
  font-weight: bold;
  border-color: #FF9F43;
}

.calendar-day-main.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #E17055;
}

.calendar-day-main.disabled {
  background-color: #f5f5f5;
  color: #aaa;
  cursor: not-allowed;
  opacity: 0.6;
}

.day-number-main {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 4px;
}

.appointment-indicator-main {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 0.7rem;
}

.appointment-dot {
  width: 6px;
  height: 6px;
  background-color: #FF6B6B;
  border-radius: 50%;
}

.appointment-count {
  color: #FF6B6B;
  font-weight: 600;
}

.today-indicator-main {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: #E17055;
  color: white;
  font-size: 0.7rem;
  padding: 1px 4px;
  border-radius: 4px;
  font-weight: bold;
}

.selected-date-display {
  text-align: center;
  padding: 12px;
  background-color: #FFF9E6;
  border-radius: 10px;
  color: #E17055;
  font-weight: 600;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;
}

/* 时间段选择样式 */
.time-slots-main {
  display: grid;
  gap: 12px;
  margin-top: 10px;
}

.time-slot-main {
  background-color: #FFF9E6;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  padding: 15px;
}

.time-slot-main:hover:not(.disabled) {
  background-color: #FFEAA7;
  transform: translateY(-2px);
}

.time-slot-main.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
}

.time-slot-main.disabled {
  background-color: #f5f5f5;
  color: #aaa;
  cursor: not-allowed;
  opacity: 0.7;
}

.time-slot-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.time-text {
  font-weight: 500;
  font-size: 1.1rem;
}

.slot-status {
  font-size: 0.8rem;
  color: #FF4757;
  font-weight: bold;
  margin-left: auto;
}

.selected-icon {
  color: white;
  font-size: 1.1rem;
}

/* 服务选项样式 - 调整为与护理项目卡片相同大小 */
.service-option-main {
  background-color: #FFF9E6;
  border-radius: 12px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.service-option-main:hover {
  background-color: #FFEAA7;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 159, 67, 0.15);
}

.service-option-main.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
}

.service-option-main.recommended {
  border-left: 4px solid #FF4757;
}

.service-icon-main {
  font-size: 1.8rem;
  color: #E17055;
  margin-bottom: 10px;
  text-align: center;
}

.service-option-main.selected .service-icon-main {
  color: white;
}

.service-info-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.service-header h4 {
  margin: 0;
  font-size: 1rem;
  color: #5A4B3A;
  flex: 1;
  line-height: 1.3;
}

.service-option-main.selected .service-header h4 {
  color: white;
}

.recommended-tag {
  background-color: #FF4757;
  color: white;
  padding: 3px 8px;
  border-radius: 10px;
  font-size: 0.7rem;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.service-desc {
  font-size: 0.85rem;
  color: #8B5A2B;
  margin: 0 0 10px 0;
  line-height: 1.4;
  flex: 1;
}

.service-option-main.selected .service-desc {
  color: rgba(255, 255, 255, 0.9);
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.original-price-main {
  text-decoration: line-through;
  color: #999;
  font-size: 0.8rem;
}

.final-price-main {
  font-weight: bold;
  color: #E17055;
  font-size: 1.1rem;
}

.service-option-main.selected .final-price-main {
  color: white;
}

.discount-badge-main {
  background-color: #FF4757;
  color: white;
  padding: 3px 6px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: bold;
}

.duration-section {
  font-size: 0.85rem;
  color: #8B5A2B;
  display: flex;
  align-items: center;
  gap: 4px;
}

.service-option-main.selected .duration-section {
  color: rgba(255, 255, 255, 0.8);
}

/* 护理项目样式 */
.care-items-container-main {
  margin-top: 10px;
}

.care-category-title-main {
  color: #E17055;
  font-size: 1.1rem;
  margin: 20px 0 10px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #FFEAA7;
}

.care-items-grid-main {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.care-item-main {
  background-color: #FFF9E6;
  border-radius: 12px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.care-item-main:hover {
  background-color: #FFEAA7;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 159, 67, 0.15);
}

.care-item-main.selected {
  background-color: #FF9F43;
  color: white;
  border-color: #FF9F43;
}

.care-item-main.free {
  border-left: 4px solid #51cf66;
}

.care-item-header-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.care-item-header-main h5 {
  font-size: 1rem;
  margin: 0;
  color: #5A4B3A;
  flex: 1;
  line-height: 1.3;
}

.care-item-main.selected .care-item-header-main h5 {
  color: white;
}

.care-price-main {
  font-weight: bold;
  color: #FF6B6B;
  font-size: 1.1rem;
}

.care-free-main {
  font-weight: bold;
  color: #51cf66;
  font-size: 0.85rem;
  background-color: #ebfbee;
  padding: 3px 8px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.care-item-main.selected .care-price-main,
.care-item-main.selected .care-free-main {
  color: white;
}

.care-description-main {
  font-size: 0.85rem;
  color: #8B5A2B;
  margin: 0 0 10px 0;
  line-height: 1.4;
  flex: 1;
}

.care-item-main.selected .care-description-main {
  color: rgba(255, 255, 255, 0.9);
}

.care-item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.single-purchase-tag {
  background-color: #339af0;
  color: white;
  font-size: 0.7rem;
  padding: 3px 8px;
  border-radius: 8px;
  font-weight: bold;
}

.selection-indicator {
  color: #FF9F43;
  font-size: 1.2rem;
}

.care-item-main.selected .selection-indicator {
  color: white;
}

.selected-care-summary-main {
  text-align: center;
  padding: 12px;
  background-color: #FFF9E6;
  border-radius: 10px;
  color: #8B5A2B;
  font-size: 0.95rem;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-weight: 500;
}

.care-total-price {
  font-weight: bold;
  color: #E17055;
  margin-left: 10px;
}

/* 字符计数 */
.char-count {
  text-align: right;
  font-size: 0.85rem;
  color: #8B5A2B;
  margin-top: 5px;
}

/* 价格汇总 */
.price-summary-main {
  background-color: #FFF9E6;
  border-radius: 15px;
  padding: 20px;
  margin: 25px 0;
  border: 2px solid #FFEAA7;
}

.summary-header {
  margin-bottom: 15px;
}

.summary-header h4 {
  color: #E17055;
  font-size: 1.2rem;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.summary-content {
  background-color: white;
  border-radius: 10px;
  padding: 15px;
}

.summary-item-main {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #5A4B3A;
  font-size: 1rem;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.summary-item-main:last-child {
  border-bottom: none;
}

.summary-total-main {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 2px solid #FFEAA7;
  font-size: 1.2rem;
  font-weight: bold;
  color: #E17055;
}

.total-price-main {
  font-size: 1.5rem;
  color: #FF6B6B;
}

/* 提交按钮 */
.btn-submit-main {
  width: 100%;
  background: linear-gradient(135deg, #FF9F43 0%, #E17055 100%);
  color: white;
  padding: 20px;
  font-size: 1.3rem;
  font-weight: bold;
  border: none;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  letter-spacing: 1px;
  position: relative;
  overflow: hidden;
}

.btn-submit-main:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(255, 159, 67, 0.4);
}

.btn-submit-main:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}


/* 查看预约历史记录模块样式 */
.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #FFEAA7;
}

.history-header h3 {
  color: #E17055;
  font-size: 1.5rem;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.view-all-link {
  color: #FF9F43;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.view-all-link:hover {
  color: #E17055;
  transform: translateX(3px);
}

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 40px;
  color: #8B5A2B;
}

.loading-container i {
  font-size: 1.5rem;
  margin-right: 10px;
}

/* 空状态 */
.empty-history {
  text-align: center;
  padding: 50px 20px;
  background-color: #FFF9E6;
  border-radius: 15px;
  margin: 20px 0;
  grid-column: 1 / span 2;
}

.empty-icon {
  font-size: 4rem;
  color: #FFD166;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-text {
  font-size: 1.2rem;
  color: #E17055;
  margin-bottom: 10px;
  font-weight: 600;
}

.empty-subtext {
  font-size: 0.95rem;
  color: #8B5A2B;
  opacity: 0.8;
}

/* 预约历史列表 */
.appointments-history-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.appointment-history-card {
  background-color: #FFFCF5;
  border-radius: 15px;
  padding: 20px;
  border-left: 5px solid #FFD166;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.appointment-history-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.appointment-history-card.status-completed {
  border-left-color: #4CAF50;
}

.appointment-history-card.status-cancelled {
  border-left-color: #f44336;
  opacity: 0.8;
}

.appointment-history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 1px solid #FFEAA7;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-id {
  font-weight: bold;
  color: #E17055;
  font-size: 1rem;
  font-family: monospace;
  background-color: #FFEAA7;
  padding: 4px 10px;
  border-radius: 8px;
}

.status-badge {
  padding: 5px 14px;
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

.appointment-history-time {
  font-size: 0.9rem;
  color: #8B5A2B;
  display: flex;
  align-items: center;
  gap: 6px;
}

.appointment-history-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.service-history-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.service-history-info i {
  font-size: 2rem;
  color: #E17055;
}

.service-history-details h4 {
  margin: 0 0 8px 0;
  color: #5A4B3A;
  font-size: 1.1rem;
}

.pet-history-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #8B5A2B;
  font-size: 0.9rem;
}

.price-history-info {
  text-align: right;
}

.price-history-amount {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.2rem;
  font-weight: bold;
  color: #E17055;
}

.price-history-amount i {
  font-size: 1rem;
}

.appointment-history-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-view-detail,
.btn-cancel-appointment,
.btn-review {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-view-detail {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.btn-view-detail:hover {
  background-color: #FFD166;
  transform: translateY(-2px);
}

.btn-cancel-appointment {
  background-color: #FFCDD2;
  color: #C62828;
}

.btn-cancel-appointment:hover {
  background-color: #FFB3B8;
  transform: translateY(-2px);
}

.btn-review {
  background-color: #FFEAA7;
  color: #E17055;
}

.btn-review:hover {
  background-color: #FFD166;
  transform: translateY(-2px);
}

/* 加载更多按钮 */
.load-more-container {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #FFEAA7;
}

.btn-load-more {
  background-color: #FFEAA7;
  color: #8B5A2B;
  border: none;
  padding: 10px 25px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-load-more:hover:not(:disabled) {
  background-color: #FFD166;
  transform: translateY(-2px);
}

.btn-load-more:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

.btn-primary {
  background: linear-gradient(135deg, #FF9F43 0%, #E17055 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 159, 67, 0.3);
}
</style>
