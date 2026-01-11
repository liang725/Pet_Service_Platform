<template>
  <div class="my-orders-section">
    <div class="content-header">
      <h2>我的订单</h2>
      <p>查看和管理您的所有订单</p>
    </div>

    <!-- 订单状态筛选栏 -->
    <div class="order-filter-tabs">
      <div
        class="filter-tab"
        :class="{ active: orderFilter === 'all' }"
        @click="setOrderFilter('all')"
      >
        全部订单
      </div>
      <div
        class="filter-tab"
        :class="{ active: orderFilter === 'pending' }"
        @click="setOrderFilter('pending')"
      >
        待付款
        <span v-if="getPendingCount() > 0" class="filter-badge">{{ getPendingCount() }}</span>
      </div>
      <div
        class="filter-tab"
        :class="{ active: orderFilter === 'processing' }"
        @click="setOrderFilter('processing')"
      >
        待收货/使用
      </div>
      <div
        class="filter-tab"
        :class="{ active: orderFilter === 'delivered' }"
        @click="setOrderFilter('delivered')"
      >
        待评价
        <span v-if="getDeliveredCount() > 0" class="filter-badge">{{ getDeliveredCount() }}</span>
      </div>
      <div
        class="filter-tab"
        :class="{ active: orderFilter === 'cancelled' }"
        @click="setOrderFilter('cancelled')"
      >
        订单回收站
      </div>
      <!-- 清空所有订单按钮 - 放在订单回收站旁边 -->
      <div v-if="getCancelledCount() > 0" class="clear-all-btn" @click="clearTrash">
        <Icon icon="mdi:delete-sweep" />
        清空所有订单
      </div>
    </div>

    <!-- 订单列表 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner">
        <Icon icon="mdi:loading" class="spin" />
        <span>加载中...</span>
      </div>
    </div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">
        <Icon icon="mdi:clipboard-list" />
      </div>
      <div class="empty-title">暂无订单</div>
      <div class="empty-description">您还没有任何订单记录</div>
      <button class="empty-action" @click="$emit('continue-shopping')">
        <Icon icon="mdi:shopping" /> 去购物
      </button>
    </div>
    <div v-else class="orders-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <div class="order-info-item">
              <div class="order-info-label">订单号</div>
              <div class="order-info-value">{{ order.orderNo || order.id }}</div>
            </div>
            <div class="order-info-item">
              <div class="order-info-label">下单时间</div>
              <div class="order-info-value">{{ formatDateTime(order.createdAt || order.date) }}</div>
            </div>
            <div class="order-info-item">
              <div class="order-info-label">订单金额</div>
              <div class="order-info-value">¥{{ (order.finalAmount || order.total || 0).toFixed(2) }}</div>
            </div>
          </div>
          <div class="order-status-section">
            <div class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </div>
            <!-- 未付款订单显示剩余支付时间 -->
            <div v-if="order.status === 'pending' && !isOrderExpired(order)" class="payment-countdown-badge" :class="{ 'countdown-warning': getPaymentTimeLeft(order) <= 180 }">
              <Icon icon="mdi:clock-outline" />
              <span>{{ formatPaymentTime(getPaymentTimeLeft(order)) }}</span>
            </div>
            <div v-else-if="order.status === 'pending' && isOrderExpired(order)" class="payment-expired-badge">
              <Icon icon="mdi:clock-alert" />
              <span>已超时</span>
            </div>
          </div>
        </div>

        <!-- 订单商品预览 -->
        <div v-if="order.items && order.items.length > 0" class="order-preview">
          <div class="preview-product" @click="$emit('go-to-product', order.items[0].productId)">
            <div class="product-image">
              <img v-if="order.items[0].image" :src="order.items[0].image" :alt="order.items[0].name">
              <Icon v-else icon="mdi:image" />
            </div>
            <div class="product-info">
              <h4>{{ order.items[0].name }}</h4>
              <p>{{ order.items[0].spec }}</p>
            </div>
          </div>
          <div class="order-summary">
            <p>共 {{ order.items.length }} 件商品</p>
            <h3>实付：¥{{ (order.finalAmount || order.total || 0).toFixed(2) }}</h3>
          </div>
        </div>

        <!-- 订单操作按钮 -->
        <div class="order-actions">
          <button class="action-btn action-btn-secondary" @click="toggleOrderDetail(order)">
            <Icon icon="mdi:eye" />
            {{ order.showDetail ? '收起详情' : '查看详情' }}
          </button>
          <button v-if="order.status !== 'pending' && order.items && order.items.length > 0" class="action-btn action-btn-primary" @click="$emit('reorder', order)">
            <Icon icon="mdi:refresh" />
            再次购买
          </button>
          <button v-if="order.status === 'pending' && !isOrderExpired(order)" class="action-btn action-btn-primary" @click="$emit('pay-order', order)">
            <Icon icon="mdi:credit-card" />
            立即支付
          </button>
          <button v-if="order.status === 'pending' && !isOrderExpired(order)" class="action-btn action-btn-danger" @click="$emit('cancel-order', order)">
            <Icon icon="mdi:close" />
            取消订单
          </button>
          <button v-if="order.status === 'cancelled'" class="action-btn action-btn-delete" @click="$emit('delete-order', order)">
            <Icon icon="mdi:delete" />
            删除订单
          </button>
          <div v-if="order.status === 'pending' && isOrderExpired(order)" class="expired-order-notice">
            <Icon icon="mdi:clock-alert" />
            <span>订单已超时，无法支付</span>
          </div>
        </div>

        <!-- 订单详情展开 -->
        <div v-if="order.showDetail" class="order-detail-expanded">
          <div class="detail-section">
            <h4>商品清单</h4>
            <div class="detail-products">
              <div v-for="item in order.items" :key="item.id" class="detail-product-item" @click="$emit('go-to-product', item.productId)">
                <div class="product-image">
                  <img v-if="item.image" :src="item.image" :alt="item.name">
                  <Icon v-else icon="mdi:image" />
                </div>
                <div class="product-details">
                  <h5>{{ item.name }}</h5>
                  <p>{{ item.spec }}</p>
                  <div class="price-quantity">
                    <span class="price">¥{{ item.price.toFixed(2) }}</span>
                    <span class="quantity">数量：{{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="order.summary" class="detail-section">
            <h4>费用明细</h4>
            <div class="order-summary-detail">
              <div class="summary-row">
                <span>商品总价</span>
                <span>¥{{ order.summary.subtotal.toFixed(2) }}</span>
              </div>
              <div class="summary-row">
                <span>运费</span>
                <span>¥{{ order.summary.shipping.toFixed(2) }}</span>
              </div>
              <div v-if="order.summary.coupon > 0" class="summary-row">
                <span>优惠券折扣</span>
                <span>-¥{{ order.summary.coupon.toFixed(2) }}</span>
              </div>
              <div v-if="order.summary.discount > 0" class="summary-row">
                <span>会员折扣</span>
                <span>-¥{{ order.summary.discount.toFixed(2) }}</span>
              </div>
              <div class="summary-row total">
                <span>实付金额</span>
                <span>¥{{ order.summary.total.toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Icon } from '@iconify/vue'

// Props
const props = defineProps({
  orders: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['continue-shopping', 'go-to-product', 'reorder', 'pay-order', 'cancel-order', 'clear-trash', 'delete-order'])

// 订单筛选状态
const orderFilter = ref('all')

// 订单支付超时时间（10分钟 = 600秒）
const PAYMENT_TIMEOUT = 600

// 当前时间（用于实时更新倒计时）
const currentTime = ref(Date.now())

// 定时器
let timer = null

// 启动定时器，每秒更新一次当前时间
onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = Date.now()
  }, 1000)
})

// 清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

// 计算属性：过滤后的订单
const filteredOrders = computed(() => {
  if (orderFilter.value === 'all') {
    return props.orders
  } else if (orderFilter.value === 'pending') {
    // 待付款：只显示10分钟内的订单
    return props.orders.filter(order => {
      if (order.status !== 'pending') return false
      return !isOrderExpired(order)
    })
  } else if (orderFilter.value === 'processing') {
    return props.orders.filter(order => order.status === 'paid' || order.status === 'shipped')
  } else if (orderFilter.value === 'delivered') {
    return props.orders.filter(order => order.status === 'delivered')
  } else if (orderFilter.value === 'cancelled') {
    return props.orders.filter(order => order.status === 'cancelled' || (order.status === 'pending' && isOrderExpired(order)))
  }
  return props.orders
})

// 方法
const setOrderFilter = (filter) => {
  orderFilter.value = filter
}

const getPendingCount = () => {
  return props.orders.filter(order => order.status === 'pending' && !isOrderExpired(order)).length
}

const getDeliveredCount = () => {
  return props.orders.filter(order => order.status === 'delivered').length
}

const getCancelledCount = () => {
  return props.orders.length
}

const clearTrash = () => {
  emit('clear-trash')
}

// 判断订单是否超时（超过10分钟）
const isOrderExpired = (order) => {
  if (order.status !== 'pending') return false

  const createdAt = new Date(order.createdAt || order.date)
  const now = new Date()
  const diffSeconds = Math.floor((now - createdAt) / 1000)

  return diffSeconds > PAYMENT_TIMEOUT
}

// 获取剩余支付时间（秒）- 使用 currentTime 确保实时更新
const getPaymentTimeLeft = (order) => {
  const createdAt = new Date(order.createdAt || order.date)
  const diffSeconds = Math.floor((currentTime.value - createdAt.getTime()) / 1000)
  const timeLeft = PAYMENT_TIMEOUT - diffSeconds

  return timeLeft > 0 ? timeLeft : 0
}

// 格式化剩余支付时间
const formatPaymentTime = (seconds) => {
  if (seconds <= 0) return '已超时'

  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60

  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const toggleOrderDetail = (order) => {
  order.showDetail = !order.showDetail
}

const getStatusClass = (status) => {
  const statusMap = {
    'pending': 'status-pending',
    'paid': 'status-paid',
    'shipped': 'status-shipped',
    'delivered': 'status-delivered',
    'cancelled': 'status-cancelled'
  }
  return statusMap[status] || ''
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待付款',
    'paid': '待发货',
    'shipped': '待收货',
    'delivered': '待评价',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
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
/* 这里复制 UserCenterView.vue 中订单相关的样式 */
.my-orders-section {
  width: 100%;
}

.content-header {
  margin-bottom: 30px;
}

.content-header h2 {
  font-size: 28px;
  color: #5a4a42;
  margin-bottom: 8px;
}

.content-header p {
  color: #8d6e63;
  font-size: 14px;
}

/* 订单筛选标签 */
.order-filter-tabs {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
  border-bottom: 2px solid #ffecb3;
  padding-bottom: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-tab {
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
  color: #5a4a42;
  font-weight: 500;
  position: relative;
}

.filter-tab:hover {
  background-color: #fff8e1;
}

.filter-tab.active {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
}

.filter-badge {
  background-color: #ff6b6b;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 5px;
}

.clear-all-btn {
  padding: 8px 16px;
  background-color: #e0e0e0;
  color: #666;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  transition: all 0.3s;
  border: 1px solid #bdbdbd;
}

.clear-all-btn:hover {
  background-color: #bdbdbd;
  transform: translateY(-1px);
}

/* 订单列表 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e0e0;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-info-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.order-info-label {
  font-size: 11px;
  color: #999;
}

.order-info-value {
  font-size: 13px;
  color: #333;
  font-weight: 600;
}

.order-status-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background-color: #fff3e0;
  color: #ff9800;
}

.status-paid {
  background-color: #e3f2fd;
  color: #2196f3;
}

.status-shipped {
  background-color: #e8f5e9;
  color: #4caf50;
}

.status-delivered {
  background-color: #f3e5f5;
  color: #9c27b0;
}

.status-cancelled {
  background-color: #ffebee;
  color: #f44336;
}

.payment-countdown-badge {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 4px 12px;
  background-color: #e3f2fd;
  color: #2196f3;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.countdown-warning {
  background-color: #ffebee;
  color: #f44336;
  animation: pulse 1s infinite;
}

.payment-expired-badge {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 4px 12px;
  background-color: #ffebee;
  color: #f44336;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

.order-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.preview-product {
  display: flex;
  gap: 12px;
  cursor: pointer;
  flex: 1;
}

.preview-product:hover .product-info h4 {
  color: #1976d2;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info h4 {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  transition: color 0.3s;
}

.product-info p {
  font-size: 12px;
  color: #999;
}

.order-summary {
  text-align: right;
}

.order-summary p {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.order-summary h3 {
  font-size: 16px;
  color: #f44336;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.action-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.action-btn-primary {
  background: #1976d2;
  color: white;
}

.action-btn-primary:hover {
  background: #1565c0;
  transform: translateY(-1px);
}

.action-btn-secondary {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
}

.action-btn-secondary:hover {
  background-color: #eeeeee;
}

.action-btn-danger {
  background-color: #ffebee;
  color: #f44336;
}

.action-btn-danger:hover {
  background-color: #f44336;
  color: white;
}

.action-btn-delete {
  background-color: #fff3e0;
  color: #ff9800;
}

.action-btn-delete:hover {
  background-color: #ff9800;
  color: white;
}

.expired-order-notice {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 16px;
  background-color: #ffebee;
  color: #f44336;
  border-radius: 8px;
  font-size: 14px;
}

/* 订单详情展开 */
.order-detail-expanded {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ffecb3;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  font-size: 16px;
  color: #5a4a42;
  margin-bottom: 15px;
}

.detail-products {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-product-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.detail-product-item:hover {
  background-color: #fff8e1;
}

.product-details h5 {
  font-size: 15px;
  color: #5a4a42;
  margin-bottom: 5px;
}

.product-details p {
  font-size: 13px;
  color: #8d6e63;
  margin-bottom: 8px;
}

.price-quantity {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.price {
  color: #e69500;
  font-weight: 600;
}

.quantity {
  color: #8d6e63;
}

.order-summary-detail {
  background-color: white;
  padding: 15px;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #5a4a42;
}

.summary-row.total {
  border-top: 2px solid #ffecb3;
  margin-top: 10px;
  padding-top: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #e69500;
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  color: #8d6e63;
}

.spin {
  animation: spin 1s linear infinite;
  font-size: 40px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.empty-icon {
  font-size: 80px;
  color: #ffecb3;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 20px;
  color: #5a4a42;
  margin-bottom: 10px;
  font-weight: 600;
}

.empty-description {
  font-size: 14px;
  color: #8d6e63;
  margin-bottom: 25px;
}

.empty-action {
  padding: 12px 30px;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.empty-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 179, 71, 0.4);
}
</style>
