<!-- src/views/ServiceAppointmentDetailView.vue -->
<template>
  <div class="appointment-detail">
    <!-- 返回按钮 -->
    <div class="back-nav">
      <button class="btn-back" @click="goBack">
        <i class="fas fa-arrow-left"></i> 返回
      </button>
      <router-link to="/service/orders" class="btn-all-orders">
        <i class="fas fa-list"></i> 所有预约
      </router-link>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner">
        <i class="fas fa-spinner fa-spin"></i>
      </div>
      <p>加载预约详情中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <div class="error-icon">
        <i class="fas fa-exclamation-circle"></i>
      </div>
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button class="btn-retry" @click="fetchAppointmentDetail">
        <i class="fas fa-redo"></i> 重试
      </button>
    </div>

    <!-- 详情内容 -->
    <div v-else-if="appointment" class="detail-content">
      <!-- 头部信息 -->
      <div class="detail-header">
        <div class="header-main">
          <div class="order-title">
            <h1>预约详情</h1>
            <div :class="['status-badge', appointment.status]">
              {{ getStatusText(appointment.status) }}
            </div>
          </div>
          <div class="order-no">
            <i class="fas fa-receipt"></i>
            订单号：{{ appointment.orderNo }}
          </div>
        </div>
        <div class="header-actions" v-if="appointment.status === 'pending'">
          <button class="btn-action reschedule" @click="rescheduleAppointment">
            <i class="fas fa-calendar-alt"></i> 改期
          </button>
          <button class="btn-action cancel" @click="confirmCancel">
            <i class="fas fa-times"></i> 取消预约
          </button>
        </div>
      </div>

      <!-- 主要内容 -->
      <div class="detail-body">
        <!-- 左侧：预约信息 -->
        <div class="detail-left">
          <!-- 基本信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <i class="fas fa-info-circle"></i>
              <h3>预约信息</h3>
            </div>
            <div class="card-body">
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-label">
                    <i class="far fa-calendar"></i> 预约日期
                  </div>
                  <div class="info-value">
                    {{ formatDate(appointment.appointmentDate) }}
                  </div>
                </div>
                <div class="info-item">
                  <div class="info-label">
                    <i class="far fa-clock"></i> 预约时间
                  </div>
                  <div class="info-value">
                    {{ appointment.timeSlot }}
                  </div>
                </div>
                <div class="info-item">
                  <div class="info-label">
                    <i class="fas fa-clock"></i> 预计时长
                  </div>
                  <div class="info-value">
                    {{ estimateDuration }}分钟
                  </div>
                </div>
                <div class="info-item">
                  <div class="info-label">
                    <i class="fas fa-yen-sign"></i> 预约金额
                  </div>
                  <div class="info-value price">
                    ¥{{ appointment.orderTotalAmount }}
                  </div>
                </div>
                <div class="info-item">
                  <div class="info-label">
                    <i class="far fa-calendar-check"></i> 创建时间
                  </div>
                  <div class="info-value">
                    {{ formatDateTime(appointment.createdAt) }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 宠物信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <i class="fas fa-paw"></i>
              <h3>宠物信息</h3>
            </div>
            <div class="card-body">
              <div class="pet-profile">
                <div class="pet-avatar">
                  <i :class="getPetIcon(appointment.petType)"></i>
                </div>
                <div class="pet-details">
                  <h4>{{ appointment.petName }}</h4>
                  <div class="pet-meta">
                    <span class="pet-type">{{ appointment.petType }}</span>
                    <span class="pet-breed" v-if="appointment.breed">{{ appointment.breed }}</span>
                  </div>
                  <p class="pet-personality" v-if="appointment.personality">
                    <i class="fas fa-heart"></i> {{ appointment.personality }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- 服务信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <i class="fas fa-spa"></i>
              <h3>服务信息</h3>
            </div>
            <div class="card-body">
              <div class="service-detail">
                <div class="service-icon">
                  <i :class="getServiceIcon(appointment.serviceName)"></i>
                </div>
                <div class="service-content">
                  <h4>{{ appointment.serviceName }}</h4>
                  <p class="service-desc">{{ appointment.serviceDescription || '专业宠物洗护服务' }}</p>
                  <div class="service-features">
                    <span class="feature-tag" v-for="feature in serviceFeatures" :key="feature">
                      {{ feature }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：操作和备注 -->
        <div class="detail-right">
          <!-- 特殊要求 -->
          <div class="info-card special-notes-card" v-if="appointment.specialNotes">
            <div class="card-header">
              <i class="fas fa-sticky-note"></i>
              <h3>特殊要求</h3>
            </div>
            <div class="card-body">
              <div class="notes-content">
                {{ appointment.specialNotes }}
              </div>
            </div>
          </div>

          <!-- 操作指南 -->
          <div class="info-card instructions-card">
            <div class="card-header">
              <i class="fas fa-list-ol"></i>
              <h3>预约须知</h3>
            </div>
            <div class="card-body">
              <ul class="instructions-list">
                <li>
                  <i class="fas fa-check-circle"></i>
                  请提前15分钟到达门店，以便准备工作
                </li>
                <li>
                  <i class="fas fa-check-circle"></i>
                  请携带宠物免疫证明（如有）
                </li>
                <li>
                  <i class="fas fa-check-circle"></i>
                  预约前2小时可免费取消或改期
                </li>
                <li>
                  <i class="fas fa-check-circle"></i>
                  如有特殊情况无法按时到达，请及时联系我们
                </li>
                <li>
                  <i class="fas fa-check-circle"></i>
                  服务完成后请及时评价，帮助我们改进服务
                </li>
              </ul>
            </div>
          </div>

          <!-- 门店信息 -->
          <div class="info-card store-card">
            <div class="card-header">
              <i class="fas fa-store"></i>
              <h3>服务门店</h3>
            </div>
            <div class="card-body">
              <div class="store-info">
                <h4>宠物之家旗舰店</h4>
                <div class="store-details">
                  <p><i class="fas fa-map-marker-alt"></i> 北京市朝阳区宠物之家大厦</p>
                  <p><i class="fas fa-phone"></i> 400-123-4567</p>
                  <p><i class="far fa-clock"></i> 营业时间：09:00 - 20:00</p>
                </div>
                <div class="store-actions">
                  <button class="btn-store contact">
                    <i class="fas fa-phone"></i> 联系门店
                  </button>
                  <button class="btn-store navigate">
                    <i class="fas fa-map-marked-alt"></i> 导航前往
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 状态时间线 -->
          <div class="info-card timeline-card">
            <div class="card-header">
              <i class="fas fa-history"></i>
              <h3>状态时间线</h3>
            </div>
            <div class="card-body">
              <div class="timeline">
                <div :class="['timeline-item', { active: true }]">
                  <div class="timeline-icon">
                    <i class="fas fa-calendar-plus"></i>
                  </div>
                  <div class="timeline-content">
                    <div class="timeline-title">预约创建</div>
                    <div class="timeline-time">{{ formatDateTime(appointment.createdAt) }}</div>
                  </div>
                </div>
                <div :class="['timeline-item', { active: appointment.status !== 'pending' }]">
                  <div class="timeline-icon">
                    <i class="fas fa-check-circle"></i>
                  </div>
                  <div class="timeline-content">
                    <div class="timeline-title">预约确认</div>
                    <div class="timeline-time" v-if="appointment.status !== 'pending'">
                      {{ getStatusTime('confirmed') }}
                    </div>
                    <div class="timeline-time" v-else>待确认</div>
                  </div>
                </div>
                <div :class="['timeline-item', { active: appointment.status === 'completed' }]">
                  <div class="timeline-icon">
                    <i class="fas fa-check-double"></i>
                  </div>
                  <div class="timeline-content">
                    <div class="timeline-title">服务完成</div>
                    <div class="timeline-time" v-if="appointment.status === 'completed'">
                      {{ getStatusTime('completed') }}
                    </div>
                    <div class="timeline-time" v-else>待完成</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="detail-footer">
        <div class="footer-info">
          <p>如有任何问题，请联系客服：400-123-4567</p>
        </div>
        <div class="footer-actions">
          <button class="btn-secondary" @click="printDetail">
            <i class="fas fa-print"></i> 打印预约单
          </button>
          <button class="btn-secondary" @click="shareDetail">
            <i class="fas fa-share-alt"></i> 分享
          </button>
          <button 
            v-if="appointment.status === 'completed'"
            class="btn-primary"
            @click="addReview"
          >
            <i class="fas fa-star"></i> 评价服务
          </button>
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
          <p>确定要取消预约 <strong>{{ appointment?.orderNo }}</strong> 吗？</p>
          <div class="cancel-details">
            <p><i class="fas fa-paw"></i> 宠物：{{ appointment?.petName }}</p>
            <p><i class="fas fa-spa"></i> 服务：{{ appointment?.serviceName }}</p>
            <p><i class="far fa-calendar"></i> 时间：{{ formatDate(appointment?.appointmentDate) }} {{ appointment?.timeSlot }}</p>
          </div>
          <div class="cancel-reason">
            <label for="cancelReasonDetail">
              <i class="fas fa-comment-dots"></i> 取消原因（可选）：
            </label>
            <textarea 
              id="cancelReasonDetail"
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const props = defineProps({
  id: {
    type: [String, Number],
    required: true
  }
})

// 状态
const loading = ref(true)
const error = ref(null)
const appointment = ref(null)
const showCancelModal = ref(false)
const cancelReason = ref('')

// 计算属性
const estimateDuration = computed(() => {
  // 根据服务类型估算时长
  if (!appointment.value?.serviceName) return 60
  
  const service = appointment.value.serviceName
  if (service.includes('基础')) return 60
  if (service.includes('精致')) return 90
  if (service.includes('美容')) return 120
  if (service.includes('SPA')) return 150
  return 60
})

const serviceFeatures = computed(() => {
  if (!appointment.value?.serviceName) return []
  
  const service = appointment.value.serviceName
  const features = []
  
  if (service.includes('洗澡')) {
    features.push('专业洗浴', '毛发吹干', '基础护理')
  }
  if (service.includes('美容')) {
    features.push('造型设计', '专业修剪', '毛发打理')
  }
  if (service.includes('SPA')) {
    features.push('精油按摩', '深层清洁', '放松护理')
  }
  if (service.includes('驱虫')) {
    features.push('药浴驱虫', '健康检查', '防护处理')
  }
  
  return features.slice(0, 3) // 最多显示3个
})

// 方法
function getStatusText(status) {
  const statusMap = {
    pending: '待服务',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

function getPetIcon(petType) {
  return petType === '狗' ? 'fas fa-dog' : 'fas fa-cat'
}

function getServiceIcon(serviceName) {
  if (serviceName.includes('洗澡')) return 'fas fa-shower'
  if (serviceName.includes('美容')) return 'fas fa-cut'
  if (serviceName.includes('SPA') || serviceName.includes('护理')) return 'fas fa-spa'
  if (serviceName.includes('驱虫')) return 'fas fa-bug'
  return 'fas fa-paw'
}

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  return `${year}年${month}月${day}日 (周${weekday})`
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

function getStatusTime(status) {
  // 这里应该从后端获取实际的状态变更时间
  // 暂时返回固定值
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

async function fetchAppointmentDetail() {
  loading.value = true
  error.value = null
  
  try {
    console.log('开始获取预约详情，ID:', props.id)
    
    const data = await userStore.getAppointmentDetail(props.id)
    console.log('预约详情原始数据:', data)
    
    // 处理可能的嵌套结构
    let detailData = data
    if (data && typeof data === 'object' && data.data) {
      detailData = data.data
    }
    
    // 格式化数据
    appointment.value = {
      id: parseInt(detailData.id) || props.id,
      orderNo: detailData.orderNo || `ORD-${new Date().getTime()}`,
      petName: detailData.petName || detailData.pet?.name || '未命名',
      petType: detailData.petType || detailData.pet?.type || '未知',
      breed: detailData.breed || detailData.pet?.breed || '',
      personality: detailData.personality || detailData.pet?.personality || '',
      serviceName: detailData.serviceName || detailData.service?.name || '未知服务',
      serviceDescription: detailData.serviceDescription || detailData.service?.description || '',
      appointmentDate: detailData.appointmentDate || new Date().toISOString().split('T')[0],
      timeSlot: detailData.timeSlot || '09:00',
      orderTotalAmount: parseFloat(detailData.orderTotalAmount) || parseFloat(detailData.totalAmount) || 0,
      specialNotes: detailData.specialNotes || '',
      status: detailData.status || 'pending',
      createdAt: detailData.createdAt || new Date().toISOString()
    }
    
    console.log('格式化后详情数据:', appointment.value)
    
  } catch (err) {
    console.error('获取预约详情失败:', err)
    error.value = err.message || '加载预约详情失败'
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

function rescheduleAppointment() {
  // 跳转到改期页面或显示改期弹窗
  alert('改期功能开发中...')
}

function confirmCancel() {
  cancelReason.value = ''
  showCancelModal.value = true
}

async function cancelAppointment() {
  if (!appointment.value) return
  
  try {
    await userStore.cancelAppointment(
      appointment.value.id, 
      cancelReason.value || '用户主动取消'
    )
    showCancelModal.value = false
    alert('预约已取消')
    // 刷新数据
    await fetchAppointmentDetail()
  } catch (err) {
    console.error('取消预约失败:', err)
    alert(err.message || '取消预约失败')
  }
}

function addReview() {
  // 跳转到评价页面
  router.push(`/service/review/${appointment.value.id}`)
}

function printDetail() {
  window.print()
}

function shareDetail() {
  if (navigator.share) {
    navigator.share({
      title: `宠物之家预约 - ${appointment.value.orderNo}`,
      text: `我在宠物之家预约了${appointment.value.serviceName}服务，时间：${formatDate(appointment.value.appointmentDate)} ${appointment.value.timeSlot}`,
      url: window.location.href
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href)
    alert('链接已复制到剪贴板')
  }
}

// 生命周期
onMounted(() => {
  fetchAppointmentDetail()
})
</script>

<style scoped>
.appointment-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

/* 返回导航 */
.back-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 15px 0;
  border-bottom: 2px solid #FFEAA7;
}

.btn-back {
  background-color: #FFEAA7;
  color: #8B5A2B;
  border: none;
  padding: 10px 20px;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background-color: #FFD166;
  transform: translateY(-2px);
}

.btn-all-orders {
  background-color: white;
  color: #FF9F43;
  border: 2px solid #FF9F43;
  padding: 8px 20px;
  border-radius: 25px;
  text-decoration: none;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-all-orders:hover {
  background-color: #FF9F43;
  color: white;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 80px 20px;
}

.spinner {
  font-size: 3.5rem;
  color: #FF9F43;
  margin-bottom: 20px;
}

.loading-state p {
  color: #8B5A2B;
  font-size: 1.1rem;
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 80px 20px;
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.error-icon {
  font-size: 4rem;
  color: #FF4757;
  margin-bottom: 20px;
}

.error-state h3 {
  color: #E17055;
  margin-bottom: 15px;
  font-size: 1.5rem;
}

.error-state p {
  color: #8B5A2B;
  margin-bottom: 30px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.btn-retry {
  background: linear-gradient(135deg, #FF9F43 0%, #E17055 100%);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 auto;
  transition: all 0.3s ease;
}

.btn-retry:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 159, 67, 0.3);
}

/* 详情内容 */
.detail-header {
  background: linear-gradient(135deg, #FFEAA7 0%, #FFD166 100%);
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(255, 200, 100, 0.3);
}

.header-main {
  flex: 1;
}

.order-title {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.order-title h1 {
  color: #E17055;
  font-size: 2rem;
  margin: 0;
}

.status-badge {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 1rem;
  font-weight: bold;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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

.order-no {
  color: #8B5A2B;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: monospace;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.btn-action {
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-action.reschedule {
  background-color: #C8E6C9;
  color: #2E7D32;
}

.btn-action.cancel {
  background-color: #FFCDD2;
  color: #C62828;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.btn-action.reschedule:hover {
  background-color: #A5D6A7;
}

.btn-action.cancel:hover {
  background-color: #EF9A9A;
}

/* 详情主体 */
.detail-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 40px;
}

@media (max-width: 992px) {
  .detail-body {
    grid-template-columns: 1fr;
  }
}

/* 信息卡片通用样式 */
.info-card {
  background-color: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 25px;
}

.card-header {
  background: linear-gradient(135deg, #FFF9E6 0%, #FFEAA7 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 2px solid #FFEAA7;
}

.card-header i {
  font-size: 1.5rem;
  color: #FF9F43;
}

.card-header h3 {
  color: #E17055;
  font-size: 1.3rem;
  margin: 0;
}

.card-body {
  padding: 25px;
}

/* 基本信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-item {
  padding: 15px;
  background-color: #FFFCF5;
  border-radius: 10px;
  border-left: 4px solid #FF9F43;
}

.info-label {
  color: #8B5A2B;
  font-size: 0.9rem;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-value {
  color: #5A4B3A;
  font-size: 1.1rem;
  font-weight: 500;
}

.info-value.price {
  color: #E17055;
  font-size: 1.3rem;
  font-weight: bold;
}

/* 宠物信息 */
.pet-profile {
  display: flex;
  align-items: center;
  gap: 20px;
}

.pet-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #FFEAA7 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  color: #E17055;
  flex-shrink: 0;
}

.pet-details h4 {
  color: #5A4B3A;
  font-size: 1.4rem;
  margin: 0 0 10px 0;
}

.pet-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.pet-type {
  background-color: #FFEAA7;
  color: #8B5A2B;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 0.9rem;
  font-weight: 500;
}

.pet-breed {
  background-color: #E8F5E9;
  color: #2E7D32;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 0.9rem;
}

.pet-personality {
  color: #8B5A2B;
  margin: 10px 0 0 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.95rem;
}

.pet-personality i {
  color: #FF6B6B;
}

/* 服务信息 */
.service-detail {
  display: flex;
  align-items: center;
  gap: 20px;
}

.service-icon {
  width: 70px;
  height: 70px;
  background-color: #FFF9E6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: #FF9F43;
  flex-shrink: 0;
}

.service-content {
  flex: 1;
}

.service-content h4 {
  color: #5A4B3A;
  font-size: 1.3rem;
  margin: 0 0 10px 0;
}

.service-desc {
  color: #8B5A2B;
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.service-features {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.feature-tag {
  background-color: #FFEAA7;
  color: #8B5A2B;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 500;
}

/* 特殊要求卡片 */
.special-notes-card .notes-content {
  background-color: #FFF9E6;
  padding: 20px;
  border-radius: 10px;
  color: #5A4B3A;
  line-height: 1.6;
  border-left: 4px solid #FF9F43;
  font-size: 0.95rem;
}

/* 操作指南 */
.instructions-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.instructions-list li {
  padding: 12px 0;
  color: #5A4B3A;
  display: flex;
  align-items: flex-start;
  gap: 15px;
  border-bottom: 1px solid #FFEAA7;
}

.instructions-list li:last-child {
  border-bottom: none;
}

.instructions-list li i {
  color: #4CAF50;
  margin-top: 2px;
  flex-shrink: 0;
}

/* 门店信息 */
.store-info h4 {
  color: #5A4B3A;
  font-size: 1.2rem;
  margin: 0 0 15px 0;
}

.store-details {
  background-color: #FFF9E6;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.store-details p {
  color: #8B5A2B;
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.store-details i {
  color: #FF9F43;
  width: 20px;
}

.store-actions {
  display: flex;
  gap: 15px;
}

.btn-store {
  flex: 1;
  padding: 12px;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-store.contact {
  background-color: #FFEAA7;
  color: #8B5A2B;
}

.btn-store.navigate {
  background-color: #C8E6C9;
  color: #2E7D32;
}

.btn-store:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn-store.contact:hover {
  background-color: #FFD166;
}

.btn-store.navigate:hover {
  background-color: #A5D6A7;
}

/* 时间线 */
.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #FFEAA7;
}

.timeline-item {
  position: relative;
  margin-bottom: 25px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-icon {
  position: absolute;
  left: -30px;
  top: 0;
  width: 40px;
  height: 40px;
  background-color: #FFF9E6;
  border: 2px solid #FFEAA7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  color: #8B5A2B;
  z-index: 1;
}

.timeline-item.active .timeline-icon {
  background-color: #FF9F43;
  border-color: #FF9F43;
  color: white;
}

.timeline-content {
  background-color: #FFFCF5;
  padding: 15px;
  border-radius: 10px;
  border-left: 4px solid #FFEAA7;
}

.timeline-item.active .timeline-content {
  border-left-color: #FF9F43;
}

.timeline-title {
  color: #5A4B3A;
  font-weight: 500;
  margin-bottom: 5px;
  font-size: 1rem;
}

.timeline-time {
  color: #8B5A2B;
  font-size: 0.9rem;
}

/* 底部操作栏 */
.detail-footer {
  background-color: white;
  border-radius: 15px;
  padding: 25px;
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border-top: 4px solid #FFEAA7;
}

.footer-info p {
  color: #8B5A2B;
  margin: 0;
  font-size: 0.95rem;
}

.footer-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.btn-secondary {
  background-color: #FFEAA7;
  color: #8B5A2B;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
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
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 159, 67, 0.3);
}

/* 模态框样式（复用ServiceOrdersView的样式，略作调整） */
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

/* 打印样式 */
@media print {
  .back-nav,
  .header-actions,
  .detail-footer,
  .btn-store,
  .btn-secondary,
  .btn-primary {
    display: none !important;
  }
  
  .detail-body {
    grid-template-columns: 1fr;
  }
  
  .info-card {
    break-inside: avoid;
    box-shadow: none;
    border: 1px solid #ddd;
  }
  
  .appointment-detail {
    padding: 0;
  }
}
</style>