<template>
  <div class="checkout-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>
          <Icon icon="mdi:credit-card" />
          确认订单
        </h1>
        <p>请确认您的订单信息并选择支付方式</p>
      </div>

      <div class="checkout-content">
        <!-- 左侧主要内容 -->
        <div class="checkout-main">
          <!-- 收货地址 -->
          <div class="checkout-section">
            <div class="section-header">
              <h3>
                <Icon icon="mdi:map-marker" />
                收货地址
              </h3>
              <button class="btn-change" @click="showAddressSelector = true">
                <Icon icon="mdi:pencil" />
                更换地址
              </button>
            </div>

            <div v-if="selectedAddress" class="address-card selected">
              <div class="address-info">
                <div class="recipient-info">
                  <span class="name">{{ selectedAddress.recipientName }}</span>
                  <span class="phone">{{ selectedAddress.phone }}</span>
                  <span v-if="selectedAddress.isDefault" class="default-tag">默认</span>
                </div>
                <div class="address-detail">
                  <Icon icon="mdi:map-marker" />
                  {{ selectedAddress.fullAddress }}
                </div>
              </div>
            </div>

            <div v-else class="no-address">
              <Icon icon="mdi:map-marker-off" />
              <span>请选择收货地址</span>
              <button class="btn-add-address" @click="showAddressSelector = true">
                添加地址
              </button>
            </div>
          </div>

          <!-- 商品清单 -->
          <div class="checkout-section">
            <div class="section-header">
              <h3>
                <Icon icon="mdi:package-variant" />
                商品清单
              </h3>
              <span class="item-count">共 {{ totalItems }} 件商品</span>
            </div>

            <div class="product-list">
              <div v-for="item in orderItems" :key="item.id" class="product-item">
                <div class="product-image">
                  <img v-if="item.image" :src="item.image" :alt="item.name">
                  <Icon v-else icon="mdi:image" />
                </div>
                <div class="product-info">
                  <h4>{{ item.name }}</h4>
                  <p v-if="item.spec">{{ item.spec }}</p>
                  <div class="product-meta">
                    <span class="price">¥{{ item.price.toFixed(2) }}</span>
                    <span class="quantity">x{{ item.quantity }}</span>
                  </div>
                </div>
                <div class="item-total">
                  ¥{{ (item.price * item.quantity).toFixed(2) }}
                </div>
              </div>
            </div>
          </div>

          <!-- 配送方式 -->
          <div class="checkout-section">
            <div class="section-header">
              <h3>
                <Icon icon="mdi:truck" />
                配送方式
              </h3>
            </div>

            <div class="delivery-options">
              <label class="delivery-option" :class="{ active: deliveryMethod === 'standard' }">
                <input type="radio" v-model="deliveryMethod" value="standard">
                <div class="option-content">
                  <div class="option-info">
                    <span class="option-name">标准配送</span>
                    <span class="option-desc">3-5个工作日送达</span>
                  </div>
                  <span class="option-price">免费</span>
                </div>
              </label>

              <label class="delivery-option" :class="{ active: deliveryMethod === 'express' }">
                <input type="radio" v-model="deliveryMethod" value="express">
                <div class="option-content">
                  <div class="option-info">
                    <span class="option-name">快速配送</span>
                    <span class="option-desc">1-2个工作日送达</span>
                  </div>
                  <span class="option-price">¥15.00</span>
                </div>
              </label>
            </div>
          </div>

          <!-- 优惠券 -->
          <div class="checkout-section">
            <div class="section-header">
              <h3>
                <Icon icon="mdi:tag" />
                优惠券
              </h3>
            </div>

            <div class="coupon-section">
              <div class="coupon-input">
                <input
                  type="text"
                  v-model="couponCode"
                  placeholder="请输入优惠券代码"
                  class="coupon-field"
                >
                <button class="btn-apply-coupon" @click="applyCoupon">
                  使用
                </button>
              </div>

              <div v-if="appliedCoupon" class="applied-coupon">
                <Icon icon="mdi:check-circle" />
                <span>{{ appliedCoupon.name }}</span>
                <span class="discount">-¥{{ appliedCoupon.discount.toFixed(2) }}</span>
                <button class="btn-remove-coupon" @click="removeCoupon">
                  <Icon icon="mdi:close" />
                </button>
              </div>

              <div v-if="couponMessage" class="coupon-message" :class="couponMessageType">
                {{ couponMessage }}
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧订单汇总 -->
        <div class="checkout-sidebar">
          <div class="order-summary">
            <h3>订单汇总</h3>

            <div class="summary-details">
              <div class="summary-row">
                <span>商品总价</span>
                <span>¥{{ subtotal.toFixed(2) }}</span>
              </div>

              <div class="summary-row">
                <span>配送费用</span>
                <span>{{ deliveryFee > 0 ? `¥${deliveryFee.toFixed(2)}` : '免费' }}</span>
              </div>

              <div v-if="appliedCoupon" class="summary-row discount">
                <span>优惠券折扣</span>
                <span>-¥{{ appliedCoupon.discount.toFixed(2) }}</span>
              </div>

              <div class="summary-divider"></div>

              <div class="summary-row total">
                <span>应付总额</span>
                <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
              </div>
            </div>

            <button
              class="btn-checkout"
              @click="proceedToPayment"
              :disabled="!canCheckout"
            >
              <Icon icon="mdi:credit-card" />
              立即支付
            </button>

            <div class="payment-security">
              <Icon icon="mdi:shield-check" />
              <span>支付安全保障</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址选择对话框 -->
    <div v-if="showAddressSelector" class="dialog-overlay" @click="closeAddressSelector">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>选择收货地址</h3>
          <button class="dialog-close-btn" @click="closeAddressSelector">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <div class="dialog-content">
          <div v-if="addresses.length === 0" class="no-addresses">
            <Icon icon="mdi:map-marker-off" />
            <p>暂无收货地址</p>
            <button class="btn-add-address" @click="addNewAddress">
              添加新地址
            </button>
          </div>

          <div v-else class="address-list">
            <div
              v-for="address in addresses"
              :key="address.id"
              class="address-option"
              :class="{ selected: selectedAddress?.id === address.id }"
              @click="selectAddress(address)"
            >
              <div class="address-info">
                <div class="recipient-info">
                  <span class="name">{{ address.recipientName }}</span>
                  <span class="phone">{{ address.phone }}</span>
                  <span v-if="address.isDefault" class="default-tag">默认</span>
                </div>
                <div class="address-detail">
                  {{ address.fullAddress }}
                </div>
              </div>
              <Icon v-if="selectedAddress?.id === address.id" icon="mdi:check-circle" class="selected-icon" />
            </div>
          </div>
        </div>

        <div class="dialog-actions">
          <button class="btn-cancel" @click="closeAddressSelector">取消</button>
          <button class="btn-confirm" @click="confirmAddressSelection">确认</button>
        </div>
      </div>
    </div>

    <!-- 支付对话框 -->
    <div v-if="showPaymentDialog" class="dialog-overlay payment-overlay" @click="closePaymentDialog">
      <div class="dialog-container payment-dialog compact-payment" @click.stop>
        <div class="dialog-header">
          <h3>支付订单</h3>
          <button class="dialog-close-btn" @click="closePaymentDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <div class="payment-content compact-content">
          <!-- 订单信息和支付金额 -->
          <div class="payment-summary">
            <div class="order-info">
              <span class="order-label">订单号：</span>
              <span class="order-number">{{ orderId }}</span>
            </div>
            <div class="payment-amount">
              <span class="amount-label">支付金额</span>
              <span class="amount-value">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
          </div>

          <!-- 支付方式和二维码在同一行 -->
          <div class="payment-main" v-if="paymentStatus === 'waiting'">
            <!-- 左侧：支付方式选择 -->
            <div class="payment-methods-section">
              <h4>支付方式</h4>
              <div class="payment-options compact-options">
                <label class="payment-option compact-option" :class="{ active: selectedPaymentMethod === 'wechat' }">
                  <input type="radio" v-model="selectedPaymentMethod" value="wechat">
                  <div class="payment-icon wechat">
                    <Icon icon="mdi:wechat" />
                  </div>
                  <span>微信支付</span>
                </label>

                <label class="payment-option compact-option" :class="{ active: selectedPaymentMethod === 'alipay' }">
                  <input type="radio" v-model="selectedPaymentMethod" value="alipay">
                  <div class="payment-icon alipay">
                    <Icon icon="simple-icons:alipay" />
                  </div>
                  <span>支付宝</span>
                </label>

                <label class="payment-option compact-option" :class="{ active: selectedPaymentMethod === 'unionpay' }">
                  <input type="radio" v-model="selectedPaymentMethod" value="unionpay">
                  <div class="payment-icon unionpay">
                    <Icon icon="mdi:credit-card" />
                  </div>
                  <span>银联支付</span>
                </label>
              </div>
            </div>

            <!-- 右侧：二维码和说明 -->
            <div class="qr-code-section compact-qr">
              <div class="qr-code">
                <Icon icon="mdi:qrcode" />
              </div>
              <p class="qr-instruction">
                使用{{ getPaymentMethodName(selectedPaymentMethod) }}扫码支付
              </p>
              <div class="payment-timer compact-timer" :class="{ 'timer-warning': paymentTimeLeft <= 180 }">
                <Icon icon="mdi:clock-outline" />
                <span>{{ formatTime(paymentTimeLeft) }}</span>
                <span v-if="paymentTimeLeft <= 180" class="warning-text">即将超时</span>
              </div>
            </div>
          </div>

          <!-- 支付成功状态 -->
          <div v-if="paymentStatus === 'success'" class="payment-success">
            <div class="success-icon">
              <Icon icon="mdi:check-circle" />
            </div>
            <h4>支付成功！</h4>
            <p>订单已生成，我们将尽快为您处理</p>
          </div>

          <!-- 支付提示 -->
          <div class="payment-tips compact-tips" v-if="paymentStatus === 'waiting'">
            <div class="tips-content">
              <Icon icon="mdi:information-outline" />
              <span>请在10分钟内完成支付，支付完成后点击"确认已支付"</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="payment-actions compact-actions">
            <div class="button-group">
              <button
                v-if="paymentStatus === 'waiting'"
                class="btn-confirm-payment"
                @click="confirmPayment"
                :disabled="!selectedPaymentMethod"
              >
                <Icon icon="mdi:check" />
                确认已支付
              </button>

              <button
                v-if="paymentStatus === 'success'"
                class="btn-view-order"
                @click="goToOrderDetail"
              >
                <Icon icon="mdi:eye" />
                查看订单
              </button>

              <button
                v-if="paymentStatus === 'waiting'"
                class="btn-cancel-payment"
                @click="cancelPayment"
              >
                <Icon icon="mdi:close" />
                取消支付
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { getAddresses } from '@/api/address'
import { createOrder, payOrder } from '@/api/order'

export default {
  name: 'CheckoutView',
  setup() {
    const cartStore = useCartStore()
    const userStore = useUserStore()
    return {
      cartStore,
      userStore
    }
  },
  data() {
    return {
      // 订单商品
      orderItems: [],
      orderType: 'cart', // 'cart' 或 'direct'

      // 地址相关
      addresses: [],
      selectedAddress: null,
      showAddressSelector: false,

      // 配送方式
      deliveryMethod: 'standard',

      // 优惠券
      couponCode: '',
      appliedCoupon: null,
      couponMessage: '',
      couponMessageType: 'info',

      // 支付相关
      showPaymentDialog: false,
      paymentStatus: 'waiting', // 'waiting', 'success'
      selectedPaymentMethod: 'wechat', // 'wechat', 'alipay', 'unionpay'
      paymentTimeLeft: 600, // 10分钟倒计时（秒）
      paymentTimer: null,
      orderId: null,

      loading: false
    }
  },
  computed: {
    // 商品总数
    totalItems() {
      return this.orderItems.reduce((sum, item) => sum + item.quantity, 0)
    },

    // 商品小计
    subtotal() {
      return this.orderItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    },

    // 配送费用
    deliveryFee() {
      return this.deliveryMethod === 'express' ? 15 : 0
    },

    // 总金额
    totalAmount() {
      let total = this.subtotal + this.deliveryFee
      if (this.appliedCoupon) {
        total -= this.appliedCoupon.discount
      }
      return Math.max(0, total)
    },

    // 是否可以结算
    canCheckout() {
      return this.selectedAddress && this.orderItems.length > 0
    }
  },
  async mounted() {
    await this.initCheckout()
  },
  methods: {
    async initCheckout() {
      try {
        this.loading = true

        // 获取订单商品（从路由参数或购物车）
        await this.loadOrderItems()

        // 加载收货地址
        await this.loadAddresses()

        // 选择默认地址
        this.selectDefaultAddress()

      } catch (error) {
        console.error('初始化结算页面失败:', error)
        this.showNotification('加载失败，请重试')
      } finally {
        this.loading = false
      }
    },

    async loadOrderItems() {
      const query = this.$route.query

      if (query.type === 'direct' && query.productId) {
        // 立即购买
        this.orderType = 'direct'
        const product = JSON.parse(query.product || '{}')
        this.orderItems = [{
          id: product.id,
          name: product.name,
          image: product.image,
          price: product.price,
          spec: product.selectedSpec || '',
          quantity: parseInt(query.quantity) || 1
        }]
      } else {
        // 购物车结算
        this.orderType = 'cart'

        // 不重新加载，直接使用store中已有的选中状态
        // 如果store中没有数据才加载
        if (this.cartStore.cartItems.length === 0) {
          await this.cartStore.loadCart()
        }

        // 只选择已选中的商品进行结算
        const selectedItems = this.cartStore.cartItems.filter(item => item.selected)

        this.orderItems = selectedItems.map(item => ({
          id: item.productId, // 使用商品ID而不是购物车项ID
          name: item.name,
          image: item.image,
          price: item.price,
          spec: item.spec,
          quantity: item.quantity
        }))
      }

      if (this.orderItems.length === 0) {
        this.showNotification('没有要结算的商品')
        this.$router.push('/user-center')
      }
    },

    async loadAddresses() {
      try {
        const response = await getAddresses()
        if (response.success) {
          this.addresses = response.data
        }
      } catch (error) {
        console.error('获取地址列表失败:', error)
      }
    },

    selectDefaultAddress() {
      const defaultAddress = this.addresses.find(addr => addr.isDefault)
      if (defaultAddress) {
        this.selectedAddress = defaultAddress
      } else if (this.addresses.length > 0) {
        this.selectedAddress = this.addresses[0]
      }
    },

    selectAddress(address) {
      this.selectedAddress = address
    },

    confirmAddressSelection() {
      this.closeAddressSelector()
    },

    closeAddressSelector() {
      this.showAddressSelector = false
    },

    addNewAddress() {
      // 跳转到地址管理页面
      this.$router.push('/user-center?tab=address')
    },

    async applyCoupon() {
      if (!this.couponCode.trim()) {
        this.showCouponMessage('请输入优惠券代码', 'error')
        return
      }

      // 模拟优惠券验证
      const mockCoupons = {
        'WELCOME10': { name: '新用户优惠券', discount: 10 },
        'SAVE20': { name: '满减优惠券', discount: 20 },
        'VIP50': { name: 'VIP专享券', discount: 50 }
      }

      const coupon = mockCoupons[this.couponCode.toUpperCase()]
      if (coupon) {
        this.appliedCoupon = coupon
        this.couponCode = ''
        this.showCouponMessage('优惠券使用成功', 'success')
      } else {
        this.showCouponMessage('优惠券代码无效', 'error')
      }
    },

    removeCoupon() {
      this.appliedCoupon = null
      this.showCouponMessage('已移除优惠券', 'info')
    },

    showCouponMessage(message, type) {
      this.couponMessage = message
      this.couponMessageType = type
      setTimeout(() => {
        this.couponMessage = ''
      }, 3000)
    },

    async proceedToPayment() {
      if (!this.canCheckout) {
        this.showNotification('请完善订单信息')
        return
      }

      try {
        this.loading = true

        // 创建订单
        const orderData = {
          items: this.orderItems,
          addressId: this.selectedAddress.id,
          deliveryMethod: this.deliveryMethod,
          couponCode: this.appliedCoupon?.name,
          totalAmount: this.totalAmount
        }

        console.log('创建订单数据:', JSON.stringify(orderData, null, 2))

        const response = await createOrder(orderData)
        if (response.success) {
          this.orderId = response.data.orderId
          this.showPaymentDialog = true
          this.paymentStatus = 'waiting'
          this.startPaymentTimer() // 启动支付倒计时
        } else {
          this.showNotification('创建订单失败：' + response.message)
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        this.showNotification('创建订单失败，请重试')
      } finally {
        this.loading = false
      }
    },

    confirmPayment() {
      // 调用后端支付接口
      this.processPayment()
    },

    async processPayment() {
      try {
        const response = await payOrder(this.orderId, this.selectedPaymentMethod)

        if (response.success) {
          this.paymentStatus = 'success'
          this.stopPaymentTimer()

          // 如果是购物车结算，清空购物车中已选商品
          if (this.orderType === 'cart') {
            await this.cartStore.clearSelectedItems()
          }

          setTimeout(() => {
            this.showNotification('支付成功！')
          }, 1000)
        } else {
          this.showNotification('支付失败：' + response.message)
        }
      } catch (error) {
        console.error('支付失败:', error)
        this.showNotification('支付失败，请重试')
      }
    },

    getPaymentMethodName(method) {
      const names = {
        'wechat': '微信',
        'alipay': '支付宝',
        'unionpay': '银联'
      }
      return names[method] || '微信'
    },

    startPaymentTimer() {
      this.paymentTimeLeft = 600 // 10分钟
      this.paymentTimer = setInterval(() => {
        this.paymentTimeLeft--
        if (this.paymentTimeLeft <= 0) {
          this.stopPaymentTimer()
        }
      }, 1000)
    },

    stopPaymentTimer() {
      if (this.paymentTimer) {
        clearInterval(this.paymentTimer)
        this.paymentTimer = null
      }
    },

    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },

    async cancelPayment() {
      // 手动取消支付
      this.stopPaymentTimer()

      // 如果在10分钟内取消，询问用户是否要取消订单
      if (this.paymentTimeLeft > 0) {
        const shouldCancelOrder = confirm('是否要取消订单？\n选择"确定"将取消订单\n选择"取消"将保留订单，稍后可继续支付')

        if (shouldCancelOrder && this.orderId) {
          try {
            const { cancelOrder } = await import('@/api/order')
            const response = await cancelOrder(this.orderId)
            if (response.success) {
              this.showNotification('订单已取消')
            } else {
              this.showNotification('订单取消失败：' + response.message)
            }
          } catch (error) {
            console.error('取消订单失败:', error)
            this.showNotification('取消订单失败，请重试')
          }
        } else {
          this.showNotification('支付已取消，订单保留中')
        }
      } else {
        this.showNotification('支付已超时取消')
      }

      this.showPaymentDialog = false
    },

    closePaymentDialog() {
      if (this.paymentStatus === 'success') {
        this.goToOrderDetail()
      } else {
        this.showPaymentDialog = false
      }
    },

    goToOrderDetail() {
      this.$router.push(`/user-center?tab=orders`)
    },

    showNotification(message) {
      // 简单的通知实现
      alert(message)
    }
  }
}
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #2c3e50;
  font-size: 28px;
  margin: 0 0 8px 0;
}

.page-header p {
  color: #6c757d;
  font-size: 16px;
  margin: 0;
}

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 30px;
}

.checkout-main {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.checkout-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2c3e50;
  font-size: 18px;
  margin: 0;
}

.btn-change {
  background: none;
  border: 1px solid #dee2e6;
  color: #6c757d;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.btn-change:hover {
  border-color: #ff9800;
  color: #ff9800;
}

.item-count {
  color: #6c757d;
  font-size: 14px;
}

/* 地址卡片 */
.address-card {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
}

.address-card.selected {
  border-color: #ff9800;
  background: #fff8f0;
}

.recipient-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.recipient-info .name {
  font-weight: 600;
  color: #2c3e50;
}

.recipient-info .phone {
  color: #6c757d;
}

.default-tag {
  background: #ff9800;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.address-detail {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #495057;
}

.no-address {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.no-address svg {
  font-size: 48px;
  margin-bottom: 16px;
}

.btn-add-address {
  background: #ff9800;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 12px;
}

/* 商品列表 */
.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image svg {
  font-size: 24px;
  color: #ccc;
}

.product-info {
  flex: 1;
}

.product-info h4 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 16px;
}

.product-info p {
  margin: 0 0 8px 0;
  color: #6c757d;
  font-size: 14px;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-meta .price {
  color: #ff9800;
  font-weight: 600;
}

.product-meta .quantity {
  color: #6c757d;
}

.item-total {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 配送方式 */
.delivery-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.delivery-option {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.delivery-option.active {
  border-color: #ff9800;
  background: #fff8f0;
}

.delivery-option input[type="radio"] {
  margin-right: 12px;
}

.option-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.option-name {
  font-weight: 600;
  color: #2c3e50;
}

.option-desc {
  color: #6c757d;
  font-size: 14px;
  margin-top: 4px;
}

.option-price {
  font-weight: 600;
  color: #ff9800;
}

/* 优惠券 */
.coupon-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coupon-input {
  display: flex;
  gap: 12px;
}

.coupon-field {
  flex: 1;
  padding: 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
}

.btn-apply-coupon {
  background: #ff9800;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.applied-coupon {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  color: #155724;
}

.applied-coupon .discount {
  margin-left: auto;
  font-weight: 600;
}

.btn-remove-coupon {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 4px;
}

.coupon-message {
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.coupon-message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.coupon-message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.coupon-message.info {
  background: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

/* 订单汇总 */
.checkout-sidebar {
  position: sticky;
  top: 20px;
  height: fit-content;
}

.order-summary {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.order-summary h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.summary-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-row.discount {
  color: #28a745;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.total-price {
  color: #ff9800;
}

.summary-divider {
  height: 1px;
  background: #e9ecef;
  margin: 8px 0;
}

.btn-checkout {
  width: 100%;
  background: #ff9800;
  color: white;
  border: none;
  padding: 16px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
  margin-bottom: 16px;
}

.btn-checkout:hover:not(:disabled) {
  background: #f57c00;
  transform: translateY(-2px);
}

.btn-checkout:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.payment-security {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #28a745;
  font-size: 14px;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-container {
  background: white;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header h3 {
  margin: 0;
  color: #2c3e50;
}

.dialog-close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #6c757d;
  padding: 4px;
}

.dialog-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e9ecef;
}

.btn-cancel,
.btn-confirm {
  flex: 1;
  padding: 12px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-cancel {
  background: white;
  border: 1px solid #dee2e6;
  color: #6c757d;
}

.btn-confirm {
  background: #ff9800;
  border: none;
  color: white;
}

/* 地址选择 */
.no-addresses {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.no-addresses svg {
  font-size: 48px;
  margin-bottom: 16px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-option.selected {
  border-color: #ff9800;
  background: #fff8f0;
}

.selected-icon {
  color: #ff9800;
  font-size: 20px;
}

/* 支付对话框 */
.payment-overlay {
  backdrop-filter: blur(4px);
}

.payment-dialog {
  max-width: 450px;
  max-height: 90vh;
  overflow-y: auto;
}

/* 紧凑型支付对话框 */
.compact-payment {
  max-width: 600px;
  max-height: 85vh;
}

.compact-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 支付摘要信息 */
.payment-summary {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-label {
  font-size: 12px;
  color: #6c757d;
}

.order-number {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  font-family: monospace;
}

.payment-amount {
  text-align: right;
}

.amount-label {
  display: block;
  color: #6c757d;
  font-size: 12px;
  margin-bottom: 4px;
}

.amount-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff9800;
}

/* 支付主要内容区域 */
.payment-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

/* 支付方式选择区域 */
.payment-methods-section h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
  text-align: left;
}

.compact-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.compact-option {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.compact-option.active {
  border-color: #ff9800;
  background: #fff8f0;
}

.compact-option input[type="radio"] {
  display: none;
}

.compact-option .payment-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 16px;
  flex-shrink: 0;
}

.compact-option .payment-icon.wechat {
  background: #07c160;
  color: white;
}

.compact-option .payment-icon.alipay {
  background: #1677ff;
  color: white;
}

.compact-option .payment-icon.unionpay {
  background: #e60012;
  color: white;
}

.compact-option span {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

/* 二维码区域 */
.compact-qr {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.compact-qr .qr-code {
  width: 120px;
  height: 120px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  background: #f8f9fa;
}

.compact-qr .qr-code svg {
  font-size: 80px;
  color: #ccc;
}

.compact-qr .qr-instruction {
  color: #6c757d;
  font-size: 13px;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.compact-timer {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  color: #856404;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.compact-timer.timer-warning {
  background: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
  animation: pulse 1s infinite;
}

.compact-timer .warning-text {
  margin-left: 4px;
  font-size: 11px;
  color: #dc3545;
}

/* 支付成功状态 */
.payment-success {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  font-size: 64px;
  color: #28a745;
  margin-bottom: 16px;
}

.payment-success h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 20px;
}

.payment-success p {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

/* 支付提示 */
.compact-tips {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 12px;
}

.tips-content {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6c757d;
  font-size: 13px;
  line-height: 1.4;
}

.tips-content svg {
  flex-shrink: 0;
  color: #17a2b8;
}

/* 操作按钮区域 - 修改为并排布局 */
.compact-actions {
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.button-group {
  display: flex;
  gap: 12px;
  width: 100%;
}

.button-group .btn-confirm-payment,
.button-group .btn-view-order {
  flex: 2;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.button-group .btn-confirm-payment {
  background: #ff9800;
  color: white;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
}

.button-group .btn-confirm-payment:hover:not(:disabled) {
  background: #f57c00;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.4);
}

.button-group .btn-confirm-payment:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.button-group .btn-view-order {
  background: #28a745;
  color: white;
  box-shadow: 0 2px 8px rgba(40, 167, 69, 0.3);
}

.button-group .btn-view-order:hover {
  background: #218838;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
}

.button-group .btn-cancel-payment {
  flex: 1;
  background: #6c757d;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

.button-group .btn-cancel-payment:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

/* 响应式设计 - 移动端优化 */
@media (max-width: 768px) {
  .button-group {
    flex-direction: row; /* 保持并排布局 */
    flex-wrap: wrap;
  }

  .button-group .btn-confirm-payment,
  .button-group .btn-view-order {
    flex: 1;
    min-width: 60%;
  }

  .button-group .btn-cancel-payment {
    flex: 1;
    min-width: 35%;
  }
}

/* 支付成功时，查看订单按钮单独显示 */
.button-group:has(.btn-view-order:only-child) {
  justify-content: center;
}

.button-group:has(.btn-view-order:only-child) .btn-view-order {
  flex: none;
  width: 200px;
}

/* 支付成功时，查看订单按钮单独显示 */
.payment-actions:has(.btn-view-order:only-child) {
  justify-content: center;
}

.payment-actions:has(.btn-view-order:only-child) .btn-view-order {
  flex: none;
  width: 200px;
}

/* 响应式设计 - 移动端优化 */
@media (max-width: 768px) {
  .compact-payment {
    max-width: 95%;
    max-height: 95vh;
  }

  .payment-main {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .payment-summary {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .compact-actions {
    flex-direction: column;
  }

  .compact-actions .btn-confirm-payment,
  .compact-actions .btn-view-order,
  .compact-actions .btn-cancel-payment {
    flex: none;
  }
}

/* 支付方式选择 */
.payment-methods {
  margin-bottom: 30px;
}

.payment-methods h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 16px;
  text-align: left;
}

.payment-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.payment-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.payment-option.active {
  border-color: #ff9800;
  background: #fff8f0;
}

.payment-option input[type="radio"] {
  display: none;
}

.payment-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  font-size: 20px;
}

.payment-icon.wechat {
  background: #07c160;
  color: white;
}

.payment-icon.alipay {
  background: #1677ff;
  color: white;
}

.payment-icon.unionpay {
  background: #e60012;
  color: white;
}

.payment-option span {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.payment-content {
  text-align: center;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

.payment-tips {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 16px;
  margin-top: 16px;
  text-align: left;
}

.payment-tips p {
  margin: 0 0 8px 0;
  color: #6c757d;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.payment-tips ul {
  margin: 0;
  padding-left: 20px;
  color: #6c757d;
  font-size: 13px;
}

.payment-tips li {
  margin-bottom: 4px;
}

.payment-timer {
  margin-top: 8px;
  padding: 8px 12px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  color: #856404;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.payment-timer.timer-warning {
  background: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
  animation: pulse 1s infinite;
}

.warning-text {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #dc3545;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.btn-cancel-payment {
  background: #6c757d;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 600;
}

.success-message {
  color: #6c757d;
  font-size: 14px;
  margin: 8px 0 0 0;
}

.payment-amount {
  margin-bottom: 30px;
}

.amount-label {
  display: block;
  color: #6c757d;
  font-size: 14px;
  margin-bottom: 8px;
}

.amount-value {
  font-size: 32px;
  font-weight: 700;
  color: #ff9800;
}

.qr-code-section {
  margin-bottom: 30px;
}

.qr-code {
  width: 200px;
  height: 200px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  background: #f8f9fa;
}

.qr-code svg {
  font-size: 120px;
  color: #ccc;
}

.qr-instruction {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

.payment-status {
  margin-bottom: 20px;
}

.status-waiting,
.status-success {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border-radius: 6px;
  font-weight: 600;
}

.status-waiting {
  background: #fff3cd;
  color: #856404;
}

.status-success {
  background: #d4edda;
  color: #155724;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.payment-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: auto;
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.btn-confirm-payment,
.btn-view-order {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.btn-confirm-payment {
  background: #ff9800;
  color: white;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
}

.btn-confirm-payment:hover:not(:disabled) {
  background: #f57c00;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.4);
}

.btn-confirm-payment:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-view-order {
  background: #28a745;
  color: white;
  box-shadow: 0 2px 8px rgba(40, 167, 69, 0.3);
}

.btn-view-order:hover {
  background: #218838;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
}

.btn-cancel-payment {
  background: #6c757d;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.btn-cancel-payment:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .checkout-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .checkout-sidebar {
    order: -1;
    position: static;
  }

  .container {
    padding: 0 16px;
  }

  .checkout-section {
    padding: 16px;
  }

  .product-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .product-meta {
    width: 100%;
    justify-content: space-between;
  }

  .delivery-option {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .option-content {
    width: 100%;
  }
}
</style>
