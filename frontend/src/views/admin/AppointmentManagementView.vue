<!-- src/views/admin/AppointmentManagementView.vue -->
<template>
  <div class="appointment-management">
    <div class="page-header">
      <h1>预约管理</h1>
      <p>管理所有用户的预约订单</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card total" @click="filterByStatus('all')" :class="{ active: selectedStatus === 'all' }">
        <Icon icon="mdi:calendar-multiple" class="stat-icon" />
        <div class="stat-content">
          <div class="stat-number">{{ stats.all || 0 }}</div>
          <div class="stat-label">全部预约</div>
        </div>
      </div>
      <div class="stat-card pending" @click="filterByStatus('pending')" :class="{ active: selectedStatus === 'pending' }">
        <Icon icon="mdi:clock-outline" class="stat-icon" />
        <div class="stat-content">
          <div class="stat-number">{{ stats.pending || 0 }}</div>
          <div class="stat-label">待确认</div>
        </div>
      </div>
      <div class="stat-card confirmed" @click="filterByStatus('confirmed')" :class="{ active: selectedStatus === 'confirmed' }">
        <Icon icon="mdi:calendar-check" class="stat-icon" />
        <div class="stat-content">
          <div class="stat-number">{{ stats.confirmed || 0 }}</div>
          <div class="stat-label">已确认</div>
        </div>
      </div>
      <div class="stat-card completed" @click="filterByStatus('completed')" :class="{ active: selectedStatus === 'completed' }">
        <Icon icon="mdi:check-circle-outline" class="stat-icon" />
        <div class="stat-content">
          <div class="stat-number">{{ stats.completed || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card cancelled" @click="filterByStatus('cancelled')" :class="{ active: selectedStatus === 'cancelled' }">
        <Icon icon="mdi:close-circle-outline" class="stat-icon" />
        <div class="stat-content">
          <div class="stat-number">{{ stats.cancelled || 0 }}</div>
          <div class="stat-label">已取消</div>
        </div>
      </div>
    </div>

        <!-- 操作栏 -->
    <div class="toolbar">
      <div class="search-box">
        <Icon icon="mdi:magnify" class="search-icon" />
        <input
          v-model="searchKeyword"
          placeholder="搜索订单号、用户名、宠物名..."
          @keyup.enter="searchAppointments"
        />
        <button class="search-btn" @click="searchAppointments">搜索</button>
      </div>
      <div class="filter-group">
        <!-- 当日日期筛选 -->
        <div class="filter-item">
          <label class="checkbox-label">
            <input type="checkbox" v-model="filterToday" @change="handleTodayFilter" />
            <Icon icon="mdi:calendar-today" class="checkbox-icon" />
            只看今日预约
          </label>
        </div>
        
        <!-- 动物种类筛选 -->
        <div class="filter-item">
          <label>动物种类：</label>
          <select v-model="filterPetType" @change="handlePetTypeFilter">
            <option value="">全部</option>
            <option value="猫">猫</option>
            <option value="狗">狗</option>
          </select>
        </div>
        
        <button class="clear-filter-btn" @click="clearFilters">
          <Icon icon="mdi:filter-remove" /> 清除筛选
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <Icon icon="mdi:loading" class="spin" />
      <span>加载中...</span>
    </div>

    <!-- 空状态 -->
    <div v-else-if="appointments.length === 0" class="empty-state">
      <Icon icon="mdi:calendar-blank" class="empty-icon" />
      <h3>暂无预约</h3>
      <p v-if="hasFilters">当前筛选条件下没有预约记录</p>
      <p v-else>暂无用户预约记录</p>
    </div>

    <!-- 预约列表 -->
    <div v-else class="table-container">
      <table class="appointment-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>用户信息</th>
            <th>宠物信息</th>
            <th>预约时间</th>
            <th>服务项目</th>
            <th>金额</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="appointment in appointments" :key="appointment.id">
            <td class="order-no">
              <span>{{ appointment.order_no }}</span>
              <small>{{ formatDate(appointment.created_at) }}</small>
            </td>
            <td>
              <div class="user-info">
                <Icon icon="mdi:account-circle" class="user-icon" />
                <div>
                  <div class="user-name">{{ appointment.user_name || '用户' + appointment.user_id }}</div>
                  <div v-if="appointment.user_phone" class="user-phone">{{ appointment.user_phone }}</div>
                  <div v-else class="user-id">ID: {{ appointment.user_id }}</div>
                </div>
              </div>
            </td>
            <td>
              <div class="pet-info">
                <Icon :icon="appointment.pet_type === '狗' ? 'mdi:dog' : 'mdi:cat'" class="pet-icon" />
                <div>
                  <div class="pet-name">{{ appointment.pet_name || '未命名宠物' }}</div>
                  <div class="pet-type">{{ appointment.pet_type || '未知' }}</div>
                </div>
              </div>
            </td>
            <td>
              <div class="time-info">
                <div class="appointment-date">{{ formatDate(appointment.appointment_date) }}</div>
                <div class="time-slot">{{ appointment.time_slot }}</div>
              </div>
            </td>
            <td>
              <div class="service-info">
                <div class="service-name">{{ appointment.service_name || '洗护服务' }}</div>
                <div v-if="appointment.special_notes" class="special-notes" :title="appointment.special_notes">
                  <Icon icon="mdi:note-text" /> 有备注
                </div>
              </div>
            </td>
            <td class="amount">¥{{ formatAmount(appointment.order_total_amount) }}</td>
            <td>
              <span class="status-badge" :class="appointment.status">
                <Icon :icon="getStatusIcon(appointment.status)" />
                {{ getStatusText(appointment.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button class="action-btn view" @click="viewDetails(appointment.id)" title="查看详情">
                  <Icon icon="mdi:eye" />
                </button>
                <button class="action-btn edit" @click="changeStatus(appointment)" title="修改状态">
                  <Icon icon="mdi:pencil" />
                </button>
                <button
                  class="action-btn delete"
                  @click="deleteAppointment(appointment)"
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
          <option :value="5">5 条</option>
          <option :value="10">10 条</option>
          <option :value="20">20 条</option>
          <option :value="50">50 条</option>
        </select>
      </div>
      <div class="page-controls">
        <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
        <span class="page-info">第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
        <button :disabled="page >= totalPages" @click="changePage(page + 1)">下一页</button>
      </div>
    </div>

    <!-- 查看详情对话框 -->
    <div v-if="showDetailDialog" class="dialog-overlay" @click="closeDetailDialog">
      <div class="dialog large" @click.stop>
        <div class="dialog-header">
          <h3>预约详情</h3>
          <button class="close-btn" @click="closeDetailDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <div v-if="detailLoading" class="dialog-loading">
          <Icon icon="mdi:loading" class="spin" />
          <span>加载中...</span>
        </div>
        <div v-else-if="currentDetail" class="dialog-body">
          <!-- 基本信息 -->
          <div class="detail-section">
            <h4><Icon icon="mdi:information-outline" /> 基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <label>订单号：</label>
                <span>{{ currentDetail.orderNo }}</span>
              </div>
              <div class="info-item">
                <label>预约状态：</label>
                <span class="status-badge" :class="currentDetail.status">
                  {{ getStatusText(currentDetail.status) }}
                </span>
              </div>
              <div class="info-item">
                <label>用户ID：</label>
                <span>{{ currentDetail.userId }}</span>
              </div>
              <div class="info-item">
                <label>预约时间：</label>
                <span>{{ formatDate(currentDetail.appointmentDate) }} {{ currentDetail.timeSlot }}</span>
              </div>
              <div class="info-item">
                <label>创建时间：</label>
                <span>{{ formatDateTime(currentDetail.createdAt) }}</span>
              </div>
              <div class="info-item">
                <label>最后更新：</label>
                <span>{{ formatDateTime(currentDetail.updatedAt) }}</span>
              </div>
            </div>
          </div>

          <!-- 宠物信息 -->
          <div class="detail-section">
            <h4><Icon icon="mdi:paw" /> 宠物信息</h4>
            <div class="pet-detail">
              <div class="pet-avatar">
                <Icon :icon="currentDetail.petType === '狗' ? 'mdi:dog' : 'mdi:cat'" />
              </div>
              <div class="pet-info-detail">
                <div class="pet-name">{{ currentDetail.petName || '未命名宠物' }}</div>
                <div class="pet-details">
                  <span>{{ currentDetail.petType || '未知' }}</span>
                  <span v-if="currentDetail.breed">品种：{{ currentDetail.breed }}</span>
                  <span v-if="currentDetail.personality">性格：{{ currentDetail.personality }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 服务项目 -->
          <div class="detail-section">
            <h4><Icon icon="mdi:spa" /> 服务项目</h4>
            <div v-for="service in currentDetail.services" :key="service.serviceId" class="service-item">
              <div class="service-header">
                <div class="service-name">{{ service.serviceName }}</div>
                <div class="service-price">¥{{ formatAmount(service.finalPrice) }}</div>
              </div>
              <div v-if="service.description" class="service-desc">{{ service.description }}</div>
              <div class="service-meta">
                <span>分类：{{ service.category }}</span>
                <span>时长：{{ service.duration }}分钟</span>
                <span v-if="service.discountPercentage > 0">折扣：{{ service.discountPercentage }}%</span>
              </div>
            </div>
          </div>

          <!-- 护理项目 -->
          <div v-if="currentDetail.careItems && currentDetail.careItems.length > 0" class="detail-section">
            <h4><Icon icon="mdi:heart-pulse" /> 护理项目</h4>
            <div class="care-items">
              <div v-for="care in currentDetail.careItems" :key="care.id" class="care-item">
                <div class="care-header">
                  <div class="care-name">{{ care.careName }}</div>
                  <div class="care-price">¥{{ formatAmount(care.unitPrice) }}</div>
                </div>
                <div v-if="care.description" class="care-desc">{{ care.description }}</div>
                <div class="care-category">分类：{{ care.careCategory }}</div>
              </div>
            </div>
          </div>

          <!-- 金额汇总 -->
          <div class="detail-section">
            <h4><Icon icon="mdi:cash-multiple" /> 金额汇总</h4>
            <div class="amount-summary">
              <div class="amount-item">
                <span>基础服务费：</span>
                <span>¥{{ formatAmount(currentDetail.itemsBaseTotal) }}</span>
              </div>
              <div class="amount-item">
                <span>服务折扣：</span>
                <span>-¥{{ formatAmount(currentDetail.itemsDiscountTotal) }}</span>
              </div>
              <div class="amount-item">
                <span>护理项目：</span>
                <span>¥{{ formatAmount(currentDetail.careItemsTotal) }}</span>
              </div>
              <div class="amount-item total">
                <span>订单总金额：</span>
                <span>¥{{ formatAmount(currentDetail.orderTotalAmount) }}</span>
              </div>
            </div>
          </div>

          <!-- 备注信息 -->
          <div v-if="currentDetail.specialNotes || currentDetail.cancellationReason" class="detail-section">
            <h4><Icon icon="mdi:note-text" /> 备注信息</h4>
            <div v-if="currentDetail.specialNotes" class="notes">
              <label>特殊要求：</label>
              <p>{{ currentDetail.specialNotes }}</p>
            </div>
            <div v-if="currentDetail.cancellationReason" class="notes">
              <label>取消原因：</label>
              <p>{{ currentDetail.cancellationReason }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改状态对话框 -->
    <div v-if="showStatusDialog" class="dialog-overlay" @click="closeStatusDialog">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>修改预约状态</h3>
          <button class="close-btn" @click="closeStatusDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <div class="dialog-body">
          <div class="appointment-info">
            <div class="info-row">
              <label>订单号：</label>
              <span>{{ currentAppointment?.order_no }}</span>
            </div>
            <div class="info-row">
              <label>当前状态：</label>
              <span class="status-badge" :class="currentAppointment?.status">
                {{ getStatusText(currentAppointment?.status) }}
              </span>
            </div>
          </div>

          <div class="form-group">
            <label>新状态 <span class="required">*</span></label>
            <select v-model="newStatus" required>
              <option value="pending">待确认</option>
              <option value="confirmed">已确认</option>
              <option value="completed">已完成</option>
              <option value="cancelled">已取消</option>
            </select>
          </div>

          <div v-if="newStatus === 'cancelled'" class="form-group">
            <label>取消原因 <span class="required">*</span></label>
            <textarea
              v-model="cancelReason"
              rows="3"
              placeholder="请输入取消原因..."
              required
            ></textarea>
          </div>

          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeStatusDialog">取消</button>
            <button type="button" class="submit-btn" @click="saveStatusChange" :disabled="saving">
              <Icon v-if="saving" icon="mdi:loading" class="spin" />
              {{ saving ? '保存中...' : '确认修改' }}
            </button>
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

// 数据
const appointments = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 筛选条件
const searchKeyword = ref('')
const selectedStatus = ref('all')
const filterToday = ref(false)  // 当日筛选
const filterPetType = ref('')    // 动物种类筛选


// 统计信息
const stats = ref({
  all: 0,
  pending: 0,
  confirmed: 0,
  completed: 0,
  cancelled: 0
})

// 对话框状态
const showDetailDialog = ref(false)
const showStatusDialog = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)
const currentAppointment = ref(null)
const newStatus = ref('')
const cancelReason = ref('')
const saving = ref(false)

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
const hasFilters = computed(() => {
  return searchKeyword.value || selectedStatus.value !== 'all' || 
         filterToday.value || filterPetType.value !== ''
})

onMounted(() => {
  loadStats()
  loadAppointments()
})

// 加载统计信息
async function loadStats() {
  try {
    const res = await request({
      url: '/api/admin/appointments/stats',
      method: 'GET'
    })
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 加载预约列表
async function loadAppointments() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    if (selectedStatus.value !== 'all') {
      params.status = selectedStatus.value
    }
    
    // 当日筛选
    if (filterToday.value) {
      const today = new Date().toISOString().split('T')[0]
      params.today = today
    }
    
    // 动物种类筛选
    if (filterPetType.value) {
      params.petType = filterPetType.value
    }
    
    const res = await request({
      url: '/api/admin/appointments',
      method: 'GET',
      params
    })
    
    if (res.code === 200) {
      appointments.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
    alert('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索预约
function searchAppointments() {
  page.value = 1
  loadAppointments()
}

// 筛选状态
function filterByStatus(status) {
  selectedStatus.value = status
  page.value = 1
  loadAppointments()
}

// 当日筛选处理
function handleTodayFilter() {
  loadAppointments()
}

// 动物种类筛选处理
function handlePetTypeFilter() {
  loadAppointments()
}

// 清除筛选
function clearFilters() {
  searchKeyword.value = ''
  selectedStatus.value = 'all'
  filterToday.value = false
  filterPetType.value = ''
  page.value = 1
  loadAppointments()
}

// 分页
function changePage(newPage) {
  page.value = newPage
  loadAppointments()
}

function changePageSize() {
  page.value = 1
  loadAppointments()
}

// 查看详情
async function viewDetails(id) {
  showDetailDialog.value = true
  detailLoading.value = true
  try {
    const res = await request({
      url: `/api/admin/appointments/${id}`,
      method: 'GET'
    })
    if (res.code === 200) {
      currentDetail.value = res.data
    } else {
      alert(res.message || '获取详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    alert('获取详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 修改状态
function changeStatus(appointment) {
  currentAppointment.value = appointment
  newStatus.value = appointment.status
  cancelReason.value = appointment.cancellation_reason || ''
  showStatusDialog.value = true
}

// 保存状态修改
async function saveStatusChange() {
  if (!newStatus.value) {
    alert('请选择状态')
    return
  }
  
  if (newStatus.value === 'cancelled' && !cancelReason.value.trim()) {
    alert('取消预约必须填写原因')
    return
  }
  
  saving.value = true
  try {
    const data = { status: newStatus.value }
    if (newStatus.value === 'cancelled') {
      data.reason = cancelReason.value.trim()
    }
    
    const res = await request({
      url: `/api/admin/appointments/${currentAppointment.value.id}/status`,
      method: 'PUT',
      data
    })
    
    if (res.code === 200) {
      alert('状态修改成功')
      closeStatusDialog()
      loadStats()
      loadAppointments()
    } else {
      alert(res.message || '状态修改失败')
    }
  } catch (error) {
    console.error('状态修改失败:', error)
    alert('状态修改失败')
  } finally {
    saving.value = false
  }
}

// 删除预约
async function deleteAppointment(appointment) {
  if (!confirm(`确定要删除预约订单"${appointment.order_no}"吗？此操作不可恢复！`)) {
    return
  }
  
  try {
    const res = await request({
      url: `/api/admin/appointments/${appointment.id}`,
      method: 'DELETE'
    })
    
    if (res.code === 200) {
      alert('删除成功')
      loadStats()
      loadAppointments()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}

// 关闭对话框
function closeDetailDialog() {
  showDetailDialog.value = false
  currentDetail.value = null
}

function closeStatusDialog() {
  showStatusDialog.value = false
  currentAppointment.value = null
  newStatus.value = ''
  cancelReason.value = ''
}

// 工具函数
function getStatusIcon(status) {
  switch(status) {
    case 'pending': return 'mdi:clock-outline'
    case 'confirmed': return 'mdi:calendar-check'
    case 'completed': return 'mdi:check-circle-outline'
    case 'cancelled': return 'mdi:close-circle-outline'
    default: return 'mdi:help-circle'
  }
}

function getStatusText(status) {
  switch(status) {
    case 'pending': return '待确认'
    case 'confirmed': return '已确认'
    case 'completed': return '已完成'
    case 'cancelled': return '已取消'
    default: return '未知'
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

function formatDateTime(dateString) {
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

function formatAmount(amount) {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

</script>

<style scoped>
.appointment-management {
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

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 2px solid transparent;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card.active {
  border-color: #e17055;
}

.stat-icon {
  font-size: 36px;
  opacity: 0.8;
}

.total .stat-icon { color: #3498db; }
.pending .stat-icon { color: #f39c12; }
.confirmed .stat-icon { color: #2ecc71; }
.completed .stat-icon { color: #27ae60; }
.cancelled .stat-icon { color: #e74c3c; }

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #666;
}

/* 操作栏 */
.toolbar {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-box {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 0 12px;
  margin-bottom: 16px;
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
  padding: 12px 8px;
  font-size: 14px;
  background: transparent;
}

.search-btn {
  padding: 10px 24px;
  background: #e17055;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  margin-left: 8px;
}

.search-btn:hover {
  background: #d35400;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
}

.filter-item input {
  padding: 8px 12px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.filter-item input:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.clear-filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f8f9fa;
  color: #666;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
}

.clear-filter-btn:hover {
  background: #e9ecef;
  color: #333;
}

/* 加载状态 */
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
}

.appointment-table {
  width: 100%;
  border-collapse: collapse;
}

.appointment-table thead {
  background: #f8f9fa;
}

.appointment-table th {
  padding: 14px 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  font-size: 13px;
  border-bottom: 2px solid #dee2e6;
}

.appointment-table td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #333;
}

.appointment-table tbody tr:hover {
  background: #f8f9fa;
}

.order-no {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no small {
  color: #999;
  font-size: 11px;
}

.user-info,
.pet-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-icon,
.pet-icon {
  font-size: 28px;
  color: #adb5bd;
}

.user-name,
.pet-name {
  font-weight: 500;
  color: #333;
}

.user-phone,
.user-id,
.pet-type {
  color: #666;
  font-size: 12px;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.appointment-date {
  color: #333;
  font-weight: 500;
}

.time-slot {
  color: #666;
  font-size: 12px;
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
}

.service-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.service-name {
  color: #333;
  font-weight: 500;
}

.special-notes {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #e17055;
  font-size: 11px;
}

.amount {
  font-weight: bold;
  color: #e17055;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.confirmed {
  background: #d1ecf1;
  color: #0c5460;
}

.status-badge.completed {
  background: #d4edda;
  color: #155724;
}

.status-badge.cancelled {
  background: #f8d7da;
  color: #721c24;
}

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

/* 分页 */
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

.page-size-selector select:hover,
.page-size-selector select:focus {
  border-color: #e17055;
  outline: none;
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

/* 对话框样式 */
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
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog.large {
  max-width: 800px;
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

.dialog-loading {
  padding: 60px 20px;
  text-align: center;
  color: #666;
}

.dialog-body {
  padding: 20px;
  overflow-y: auto;
  max-height: calc(90vh - 80px);
}

/* 详情对话框样式 */
.detail-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px 0;
  color: #333;
  font-size: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.info-item label {
  font-size: 13px;
  color: #666;
  min-width: 80px;
}

.info-item span {
  color: #333;
}

.pet-detail {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.pet-avatar {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #e17055;
}

.pet-info-detail {
  flex: 1;
}

.pet-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.pet-details {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #666;
}

.service-item,
.care-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 8px;
}

.service-header,
.care-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.service-name,
.care-name {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.service-price,
.care-price {
  color: #e17055;
  font-weight: bold;
}

.service-desc,
.care-desc {
  color: #666;
  font-size: 12px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.service-meta,
.care-category {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 11px;
  color: #999;
}

.amount-summary {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e9ecef;
}

.amount-item:last-child {
  border-bottom: none;
}

.amount-item.total {
  font-weight: bold;
  color: #e17055;
  font-size: 16px;
}

.notes {
  margin-bottom: 12px;
}

.notes label {
  display: block;
  margin-bottom: 4px;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.notes p {
  margin: 0;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

/* 状态修改对话框 */
.appointment-info {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row label {
  font-size: 13px;
  color: #666;
  min-width: 70px;
}

.info-row span {
  color: #333;
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

.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
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
</style>